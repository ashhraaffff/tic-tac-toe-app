package androidsamples.java.tictactoe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OpenGamesAdapter extends RecyclerView.Adapter<OpenGamesAdapter.ViewHolder> {
  private ArrayList<GameModel> list;
  private NavController navController;

  public OpenGamesAdapter(ArrayList<GameModel> list, NavController navController) {
    this.list = list;
    this.navController = navController;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    holder.populate(list.get(position).getNickname() +"'s Game", list.get(position).getGameId(), position + 1);
  }

  @Override
  public int getItemCount() {
    return 0; // FIXME
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mIdView = view.findViewById(R.id.item_number);
      mContentView = view.findViewById(R.id.content);
    }

    @NonNull
    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }

    public void populate (String gameNick, String gameId, int i) {
      mContentView.setText(gameNick);
      mIdView.setText("#" + i);
      mView.setOnClickListener(v -> {
        NavDirections action = DashboardFragmentDirections.actionGame("Two-Player", gameId);
        Navigation.findNavController(mView).navigate(action);
      });
    }
  }
}