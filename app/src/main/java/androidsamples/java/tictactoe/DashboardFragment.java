package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DashboardFragment extends Fragment {

  private static final String TAG = "DashboardFragment";
  private NavController mNavController;

  private FirebaseAuth mAuth;
  private TextView wonText, lostText, infoText;
  private DatabaseReference gamesReference, userReference;
  private RecyclerView rv;
  private ProgressDialog pd;
  private DashboardViewModel viewModel;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public DashboardFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setHasOptionsMenu(true); // Needed to display the action menu for this fragment
    gamesReference = FirebaseDatabase.getInstance("https://boomboomtictactoe-default-rtdb.firebaseio.com/").getReference("games");

    viewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_dashboard, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mNavController = Navigation.findNavController(view);

    rv = view.findViewById(R.id.list);
    wonText = view.findViewById(R.id.won_score);
    lostText = view.findViewById(R.id.lost_score);
    infoText = view.findViewById(R.id.open_display);

    pd = new ProgressDialog(getContext());
    pd.setMessage("Fetching Open Games...");
    pd.setTitle("Please Wait");


    // TODO if a user is not logged in, go to LoginFragment
    mAuth = FirebaseAuth.getInstance();
    if (mAuth.getCurrentUser() == null) {
      mNavController.navigate(R.id.action_need_auth);
      return;
    }

    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    userReference = FirebaseDatabase.getInstance("https://boomboomtictactoe-default-rtdb.firebaseio.com/")
            .getReference("users")
            .child(userId);

    // Fetch win/loss data from Firebase
    userReference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        // Handle null values gracefully
        String won = snapshot.child("won").exists() ? snapshot.child("won").getValue().toString() : "0";
        String lost = snapshot.child("lost").exists() ? snapshot.child("lost").getValue().toString() : "0";

        wonText.setText(won);
        lostText.setText(lost);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e("Firebase", "Failed to fetch win/loss data: " + error.getMessage());
      }
    });

    // Observe the game list from ViewModel
    viewModel.getGameList().observe(getViewLifecycleOwner(), gameList -> {
      if (getView() != null) {
        rv.setAdapter(new OpenGamesAdapter(gameList, mNavController));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        pd.dismiss();
        infoText.setText(gameList.isEmpty() ? "NO OPEN GAMES YET :(" : "OPEN GAMES");
      }
    });

    // Create new game logic
    view.findViewById(R.id.fab_new_game).setOnClickListener(v -> {
      DialogInterface.OnClickListener listener = (dialog, which) -> {
        if (which == DialogInterface.BUTTON_POSITIVE) {
          showInputDialog();
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
          String gameType = getString(R.string.one_player);
          String gameId = "Single Game ID";
          NavDirections action = DashboardFragmentDirections.actionGame(gameType, gameId);
          mNavController.navigate(action);
        }
      };

      AlertDialog dialog = new AlertDialog.Builder(requireActivity())
              .setTitle(R.string.new_game)
              .setMessage(R.string.new_game_dialog_message)
              .setPositiveButton(R.string.two_player, listener)
              .setNegativeButton(R.string.one_player, listener)
              .setNeutralButton(R.string.cancel, (d, which) -> d.dismiss())
              .create();
      dialog.show();
    });

    // Load open games from Firebase
    pd.show();
    gamesReference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        ArrayList<GameModel> gameList = new ArrayList<>();
        for (DataSnapshot shot : snapshot.getChildren()) {
          GameModel game = shot.getValue(GameModel.class);
          if (game.getIsOpen()) gameList.add(game);
        }
        // Update ViewModel with new game list
        viewModel.setGameList(gameList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e("Firebase", "Failed to fetch open games: " + error.getMessage());
      }
    });


  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
  }

  private void showInputDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
    View view = getLayoutInflater().inflate(R.layout.nickname_layout, null);
    final EditText editText = view.findViewById(R.id.nicknameText);

    builder.setView(view)
            .setPositiveButton("OK", (dialog, which) -> {
              String userInput = editText.getText().toString();
              String gameType = getString(R.string.two_player);
              String gameId = gamesReference.push().getKey();

              assert gameId != null;
              gamesReference.child(gameId).setValue(new GameModel(FirebaseAuth.getInstance().getCurrentUser().getUid(), gameId, userInput));

              NavDirections action = DashboardFragmentDirections.actionGame(gameType, gameId);
              if (gameId != null && gameType != null) {
                mNavController.navigate(action);
              } else {
                Log.e("Navigation", "Missing gameId or gameType");
              }
            });
    AlertDialog dialog = builder.create();
    dialog.show();
  }
}