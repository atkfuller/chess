package dataaccess;

import model.GameData;
import model.UserData;
import java.util.ArrayList;

public class GameDAO {
    private ArrayList<GameData> games=new ArrayList<GameData>();
    public void clear(){
        games.clear();
    }
    public ArrayList<GameData> listGames(){
        return games;
    }
    public void addGame(GameData data){
        games.add(data);
    }
}
