package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GameFragment extends Fragment {
  private static final String TAG = "GameFragment";
  private static final int GRID_SIZE = 9;

  private final ImageView[] mImageViews = new ImageView[GRID_SIZE];
  private ImageView player1Icon, player2Icon;
  private Button quitButton;
  private LinearLayout player1Layout, player2Layout;
  private TextView player1TV, player2TV;
  private String player1TV_val, player2TV_val;

  private NavController mNavController;

  private String[] inputText = new String[]{"", "", "", "", "", "", "", "", ""};
  private boolean gameEnded = false;
  private boolean isSinglePlayer = true;
  private boolean isHost = true;
  private boolean myTurn = true;
  private String myChar = "X";
  private String otherChar = "O";
  private DatabaseReference gameReference, userReference;
  private GameModel game;

  private ValueEventListener gameInitializerListener, gameStateUpdateListener,
          userForfeitUpdateListener , userQuitUpdateListener,  userWinUpdateListener, userLossUpdateListener;

  private boolean enableMockAI = false; // Static field to persist mock state
  private List<Integer> mockAIMoves;
  private int currentAIMoveIndex = 0;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    // Extract the argument passed with the action in a type-safe way
    GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
    Log.d(TAG, "New game type = " + args.getGameType());

    isSinglePlayer = (args.getGameType().equals("One-Player"));
    userReference = FirebaseDatabase.getInstance("https://boomboomtictactoe-default-rtdb.firebaseio.com/").getReference("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

    if(!isSinglePlayer){
      gameReference = FirebaseDatabase.getInstance("https://boomboomtictactoe-default-rtdb.firebaseio.com/").getReference("games").child(args.getGameId());
      gameInitializerListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          if(isAdded() && !isDetached()){
            game = snapshot.getValue(GameModel.class);
            assert game != null;

            Log.d(TAG, "Game Host UID: " + game.getHost());
            Log.d(TAG, "Current User UID: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
            Log.d(TAG, "Game Turn: " + game.getTurn());

            inputText = (game.getGameArray()).toArray(new String[9]);
            if (game.getTurn() == 1) {
              if (game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                isHost = true;
                myTurn = true;
                myChar = "X";
                otherChar = "O";
              }
              else {
                isHost = false;
                myTurn = false;
                myChar = "O";
                otherChar = "X";
                gameReference.child("isOpen").setValue(false);
              }
            } else {
              if (!game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                myTurn = true;
                myChar = "O";
                otherChar = "X";
                isHost = false;
                gameReference.child("isOpen").setValue(false);
              } else {
                isHost = true;
                myTurn = false;
                myChar = "X";
                otherChar = "O";
              }
            }

            requireActivity().runOnUiThread(() -> {
              Log.d(TAG, "Setting icons in Firebase listener - isHost: " + isHost + ", myChar: " + myChar);
              if (!isHost) {
                player1Icon.setImageResource(R.drawable.tic_tac_toe_circle_final);
                player2Icon.setImageResource(R.drawable.tic_tac_toe_cross_final);
              } else {
                player1Icon.setImageResource(R.drawable.tic_tac_toe_cross_final);
                player2Icon.setImageResource(R.drawable.tic_tac_toe_circle_final);
              }
            });
          }else{
            Log.w("GameFragment", "onDataChange called, but Fragment is no longer attached!");

          }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
          Log.e("Game setup error", error.getMessage());
        }
      };
      gameReference.addListenerForSingleValueEvent(gameInitializerListener);

      player1TV_val = "You";
      player2TV_val = "Opponent";
    }else{
      player1TV_val = "You";
      player2TV_val = "Computer";
    }

    // Handle the back press by adding a confirmation dialog
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        Log.d(TAG, "Back pressed");

        // TODO show dialog only when the game is still in progress
        if (!gameEnded) {
          AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                  .setTitle(R.string.confirm)
                  .setMessage(R.string.forfeit_game_dialog_message)
                  .setPositiveButton(R.string.yes, (d, which) -> {
                    userForfeitUpdateListener = new ValueEventListener(){
                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {
                        if (isAdded() && !isDetached()) {
                          int value = Integer.parseInt(dataSnapshot.child("lost").getValue().toString());
                          value = value + 1;
                          dataSnapshot.getRef().child("lost").setValue(value);
                        }else{
                          Log.w("GameFragment", "onDataChange called, but Fragment is no longer attached!");
                        }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {}
                    };

                    userReference.addListenerForSingleValueEvent(userForfeitUpdateListener);


                    if(!isSinglePlayer){
                      gameReference.child("hasForfeit").setValue(true);
                      gameEnded = true;
                    }
                    mNavController.popBackStack();
                  })
                  .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                  .create();
          dialog.show();
        } else{
          assert getParentFragment() != null;
          NavHostFragment.findNavController(getParentFragment()).navigateUp();
        }
      }
    };
    requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_game_new, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    player1Layout = view.findViewById(R.id.player1Layout);
    player2Layout = view.findViewById(R.id.player2Layout);
    player1TV = view.findViewById(R.id.player1TV);
    player2TV = view.findViewById(R.id.player2TV);
    player1Icon = view.findViewById(R.id.player1_icon);
    player2Icon = view.findViewById(R.id.player2_icon);

    if (!isSinglePlayer) {
      setupPlayerNames(player1TV_val,player2TV_val);

      boolean check = false;
      for (String s : inputText) {
        if (!s.isEmpty()) {
          check = true;
          break;
        }
      }
      if (!check) {
        updateTurnIndicator(false);
        waitForTurn();
      }
    } else {
      setupPlayerNames(player1TV_val, player2TV_val);
      updateTurnIndicator(true);
    }

    mNavController = Navigation.findNavController(view);

    mImageViews[0] = view.findViewById(R.id.image1);
    mImageViews[1] = view.findViewById(R.id.image2);
    mImageViews[2] = view.findViewById(R.id.image3);
    mImageViews[3] = view.findViewById(R.id.image4);
    mImageViews[4] = view.findViewById(R.id.image5);
    mImageViews[5] = view.findViewById(R.id.image6);
    mImageViews[6] = view.findViewById(R.id.image7);
    mImageViews[7] = view.findViewById(R.id.image8);
    mImageViews[8] = view.findViewById(R.id.image9);

    if(isSinglePlayer){
      if(savedInstanceState != null) {
        inputText = savedInstanceState.getStringArray(TAG);
        updateUI();
      }
    }

    quitButton = view.findViewById(R.id.quit_btn);
    //quitButtonListener
    quitButton.setOnClickListener(v -> {
      if (!gameEnded) {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm)
                .setMessage(R.string.forfeit_game_dialog_message)
                .setPositiveButton(R.string.yes, (d, which) -> {
                  userQuitUpdateListener = new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                      if (isAdded()) {
                        int value = Integer.parseInt(dataSnapshot.child("lost").getValue().toString());
                        dataSnapshot.getRef().child("lost").setValue(value + 1);
                      }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                  };

                  userReference.addListenerForSingleValueEvent(userQuitUpdateListener);

                  if (!isSinglePlayer) {
                    gameReference.child("hasForfeit").setValue(true);
                    gameEnded = true;
                  }

                  if (isAdded()) {
                    mNavController.popBackStack();
                  }
                })
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .create();

        if (isAdded()) {
          dialog.show();
        }
      } else {
        if (isAdded()) {
          mNavController.navigateUp();
        }
      }
    });

    for (int i = 0; i < mImageViews.length; i++) {
      int finalI = i;
      mImageViews[i].setOnClickListener(v -> {
        Log.d(TAG, "Image " + finalI + " clicked");
        if(myTurn){
          setMove(mImageViews[finalI], myChar);
          inputText[finalI] = myChar;
          mImageViews[finalI].setClickable(false);

          if (!isSinglePlayer) {
            updateDB();
            myTurn = updateTurn(game.getTurn());
          }

          myTurn = !myTurn;
          updateTurnIndicator(myTurn);

          if(isSinglePlayer){
            computerMove();
          } else {
            waitForTurn();
          }
        } else {
          Toast.makeText(getContext(), "Please wait for your turn!", Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
  }

  public void setBoard(String[] temp){
    inputText=temp;
  }

  public void setAIManually(boolean enableMockAI, List<Integer> moves){
    this.enableMockAI=enableMockAI;
    this.mockAIMoves=moves;
  }

  private void computerMove() {
    // Check if the game has ended (win or draw)
    int win = checkWin();
    if (win == 1 || win == -1) {
      endGame(win);
      return;
    }

    if (checkDraw()) {
      endGame(0);
      return;
    }

    if (!gameEnded) {
      // For manual control during tests, use a flag to determine if we should simulate a move
      if (enableMockAI) {
        int x = currentAIMoveIndex+3;
        currentAIMoveIndex++;
        // Ensure the spot is empty before making a move
        if (inputText[x].isEmpty()) {
          inputText[x] = otherChar;
          setMove(mImageViews[x], otherChar);
          mImageViews[x].setClickable(false);
          myTurn = !myTurn;
          updateTurnIndicator(true);

          // Check for win immediately after AI move
          win = checkWin();
          if (win == 1 || win == -1) {
            endGame(win);
            return;
          }
        }

        return;
      }

      // Random AI move if not in manual mode
      Random rand = new Random();
      int x = rand.nextInt(9);
      while (!inputText[x].isEmpty()) {
        x = rand.nextInt(9);
      }
      inputText[x] = otherChar;
      setMove(mImageViews[x], otherChar);
      mImageViews[x].setClickable(false);
      myTurn = !myTurn;
      updateTurnIndicator(true);

      // Check for win immediately after AI move
      win = checkWin();
      if (win == 1 || win == -1) {
        endGame(win);
        return;
      }
    }

    if (checkDraw()) {
      endGame(0);
    }
  }

  private void waitForTurn() {
    int win = checkWin();
    if (win == 1 || win == -1) {
      endGame(win);
      return;
    }

    if (checkDraw()) {
      endGame(0);
      return;
    }

    gameStateUpdateListener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (isAdded() && !isDetached()) {
          GameModel g = snapshot.getValue(GameModel.class);
          game.updateGameArray(g);
          inputText = (game.getGameArray()).toArray(new String[9]);
          updateUI();
          myTurn = updateTurn(game.getTurn());
          if (myTurn) {
            updateTurnIndicator(true);
          } else {
            updateTurnIndicator(false);
          }
          int win = checkWin();
          if (win == 1 || win == -1) {
            if (!gameEnded)
              endGame(win);
            return;
          }
          if (checkDraw()) {
            if (!gameEnded)
              endGame(0);
            return;
          }

          if (snapshot.getValue(GameModel.class).getHasForfeit()) {
            if (!gameEnded) {
              endGame(1);
            }
          }
        } else{
          Log.w("GameFragment", "onDataChange called, but Fragment is no longer attached!");
        }
      }


      @Override
      public void onCancelled(@NonNull DatabaseError error) {}
    };

    gameReference.addValueEventListener(gameStateUpdateListener);
  }

  private void updateTurnIndicator(boolean isMyTurn) {
    if (isMyTurn) {
      player1Layout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_black_dark_blue_stoke, null));
      player2Layout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_back_dark_blue_20, null));
    } else {
      player1Layout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_back_dark_blue_20, null));
      player2Layout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_black_dark_blue_stoke, null));
    }
  }

  private void setMove(ImageView imageView, String player) {
    if (player.equals("X")) {
      imageView.setImageResource(R.drawable.tic_tac_toe_cross_final);
    } else {
      imageView.setImageResource(R.drawable.tic_tac_toe_circle_final);
    }
  }

  private void setupPlayerNames(String player1Name, String player2Name) {
    if (myChar.equals("X")) {
      player1TV.setText(player1Name);
      player2TV.setText(player2Name);
    } else {
      player1TV.setText(player2Name);
      player2TV.setText(player1Name);
    }
  }

  private boolean updateTurn (int turn) {
    return (turn == 1) == isHost;
  }

  private void updateUI() {
    for (int i = 0; i < 9; i++) {
      String v = inputText[i];
      if (!v.isEmpty()) {
        setMove(mImageViews[i],v);
        mImageViews[i].setClickable(false);
      }
    }
  }

  private void updateDB() {
    gameReference.child("gameArray").setValue(Arrays.asList(inputText));
    if (game.getTurn() == 1) {
      game.setTurn(2);
    } else {
      game.setTurn(1);
    }
    gameReference.child("turn").setValue(game.getTurn());
  }

  public int checkWin() {
    String winChar = "";
    if  (inputText[0].equals(inputText[1]) && inputText[1].equals(inputText[2]) && !inputText[0].isEmpty()) winChar = inputText[0];
    else if (inputText[3].equals(inputText[4]) && inputText[4].equals(inputText[5]) && !inputText[3].isEmpty()) winChar = inputText[3];
    else if (inputText[6].equals(inputText[7]) && inputText[7].equals(inputText[8]) && !inputText[6].isEmpty()) winChar = inputText[6];
    else if (inputText[0].equals(inputText[3]) && inputText[3].equals(inputText[6]) && !inputText[0].isEmpty()) winChar = inputText[0];
    else if (inputText[4].equals(inputText[1]) && inputText[1].equals(inputText[7]) && !inputText[1].isEmpty()) winChar = inputText[1];
    else if (inputText[2].equals(inputText[5]) && inputText[5].equals(inputText[8]) && !inputText[2].isEmpty()) winChar = inputText[2];
    else if (inputText[0].equals(inputText[4]) && inputText[4].equals(inputText[8]) && !inputText[0].isEmpty()) winChar = inputText[0];
    else if (inputText[6].equals(inputText[4]) && inputText[4].equals(inputText[2]) && !inputText[2].isEmpty()) winChar = inputText[2];
    else return 0;

    return (winChar.equals(myChar)) ? 1 : -1;
  }

  private boolean checkDraw() {
    if (checkWin() != 0) return false;
    for (int i = 0; i < 9; i++) {
      if (inputText[i].isEmpty()) {
        return false;
      }
    }
    return true;
  }

  private void endGame(int win) {
    AlertDialog dialog;
    switch (win) {
      case 1:
        userWinUpdateListener = new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            if (isAdded() && !isDetached()) {
              int value = Integer.parseInt(dataSnapshot.child("won").getValue().toString());
              value = value + 1;
              dataSnapshot.getRef().child("won").setValue(value);
            } else {
              Log.w("GameFragment", "onDataChange called, but Fragment is no longer attached!");
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {}
        };
        userReference.addListenerForSingleValueEvent(userWinUpdateListener);

        dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.congratulations)
                .setPositiveButton(R.string.ok, (d, which) -> {
                  mNavController.popBackStack();
                })
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .create();
        dialog.show();
        break;

      case -1:
        userLossUpdateListener = new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            if(isAdded() && !isDetached()){
              int value = Integer.parseInt(dataSnapshot.child("lost").getValue().toString());
              value = value + 1;
              dataSnapshot.getRef().child("lost").setValue(value);
            }else {
              Log.w("GameFragment", "onDataChange called, but Fragment is no longer attached!");
            }
          }
          @Override
          public void onCancelled(@NonNull DatabaseError error) {}
        };
        userReference.addListenerForSingleValueEvent(userLossUpdateListener);

        dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.sorry)
                .setPositiveButton(R.string.ok, (d, which) -> {
                  mNavController.popBackStack();
                })
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .create();
        dialog.show();
        break;

      case 0:
        dialog = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.draw)
                .setPositiveButton(R.string.ok, (d, which) -> {
                  mNavController.popBackStack();
                })
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .create();
        dialog.show();
        break;
    }

    for (int i = 0; i < 9; i++) {
      mImageViews[i].setClickable(false);
    }

    gameEnded = true;
    quitButton.setText(R.string.go_back);

    if(!isSinglePlayer)
      updateDB();
  }


  @Override
  public void onSaveInstanceState(@NonNull Bundle outState)
  {
    super.onSaveInstanceState(outState);
    outState.putStringArray(TAG, inputText);
  }

  public void onDestroy() {
    super.onDestroy();
    //remove all the listeners
    removeListener(gameReference, gameInitializerListener, "gameInitializerListener");
    removeListener(gameReference, gameStateUpdateListener, "gameStateUpdateListener");
    removeListener(userReference, userForfeitUpdateListener, "userForfeitUpdateListener");
    removeListener(userReference, userQuitUpdateListener, "userQuitUpdateListener");
    removeListener(userReference, userWinUpdateListener, "userWinUpdateListener");
    removeListener(userReference, userLossUpdateListener, "userLossUpdateListener");

    if (!isSinglePlayer) {
      gameReference.child("isOpen").setValue(false);
    }
  }

  private void removeListener(DatabaseReference reference, ValueEventListener listener, String name) {
    if (listener != null) {
      reference.removeEventListener(listener);
      Log.d("GameFragment", name + " removed.");
    }
  }


}