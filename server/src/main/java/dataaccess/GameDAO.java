package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameDAO {
    private ArrayList<GameData> games=new ArrayList<GameData>();
    private static Set<Integer> gameIDs= new HashSet<>();
    private static final Random rand = new Random();
    public void clear(){
        games.clear();
    }
    public ArrayList<GameData> listGames(){
        return games;
    }
    public void addGame(GameData data){
        games.add(data);
    }
    public GameData createGame(String gameName){
        GameData data= new GameData(generateUniqueID(), null, null, gameName, new ChessGame());
        games.add(data);
        return data;
    }
    public static int generateUniqueID() {
        int id;
        do {
            id = rand.nextInt(9000);
        } while (gameIDs.contains(id));
        gameIDs.add(id);
        return id;
    }
}
