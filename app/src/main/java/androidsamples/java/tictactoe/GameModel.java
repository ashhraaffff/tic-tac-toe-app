package androidsamples.java.tictactoe;

import java.util.Arrays;
import java.util.List;

public class GameModel {

    private List<String> gameArray = null;
    private String host;
    private Boolean isOpen;
    private Boolean hasForfeit;
    private String gameId;
    private int turn;
    private String nickname;

    public GameModel(String host, String id, String nickname) {
        this.host = host;
        isOpen = true;
        gameArray = Arrays.asList("", "", "", "", "", "", "", "", "");
        this.gameId = id;
        turn = 1;
        this.nickname = nickname;
        hasForfeit = false;
    }

    public GameModel(){}

    public List<String> getGameArray() {
        return gameArray;
    }

    public void setGameArray(List<String> gameArray) {
        this.gameArray = (gameArray);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean open) {
        isOpen = open;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void updateGameArray(GameModel o) {
        gameArray = o.gameArray;
        turn = o.turn;
    }

    public int getTurn() {
        return turn;
    }
    public void setTurn(int turn) {
        this.turn = turn;
    }

    public String getNickname() {
        return this.nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getHasForfeit() {
        return hasForfeit;
    }
    public void setHasForfeit(Boolean hasForfeit) {
        this.hasForfeit = hasForfeit;
    }
}