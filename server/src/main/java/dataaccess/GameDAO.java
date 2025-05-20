package dataaccess;

import model.GameData;
import model.UserData;
import java.util.ArrayList;

public class GameDAO {
    private ArrayList<GameData> games=new ArrayList<GameData>();
    public void clear(){
        games.clear();
    }
}
