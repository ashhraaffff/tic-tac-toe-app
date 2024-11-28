package androidsamples.java.tictactoe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;


public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<GameModel>> gameList = new MutableLiveData<>();

    public LiveData<ArrayList<GameModel>> getGameList() {
        return gameList;
    }

    public void setGameList(ArrayList<GameModel> gameList) {
        this.gameList.setValue(gameList);
    }
}
