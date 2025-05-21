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
    public GameData getGame(int id){
        for(GameData data: games){
            if(data.gameID()==id){
                return data;
            }
        }
        return null;
    }

    public void joinGame(String color, GameData game, String username) throws DataAccessException{
        GameData data;
        if(color=="BLACK"){
            if(game.blackUsername() == null){
                throw new DataAccessException(403, "Error: already taken");
            }
            data= new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(),game.game());
        }
        else{
            if(game.whiteUsername() == null){
                throw new DataAccessException(403, "Error: already taken");
            }
            data= new GameData(game.gameID(), username, game.blackUsername(), game.gameName(),game.game());
        }
        setGame(game.gameID(), data);
    }
    public void updateGame(int gameID, ChessGame updatedGame) throws DataAccessException {
        GameData existing = getGame(gameID);
        if (existing == null) {
            throw new DataAccessException(400, "Error: bad request");
        }

        GameData updated = new GameData(gameID, existing.whiteUsername(), existing.blackUsername(), existing.gameName(), updatedGame);

        setGame(gameID, updated);
    }
    public void setGame(int id, GameData game){
        for(GameData data: games){
            if(data.gameID()==id){
               games.add(games.indexOf(data), game);
            }
        }

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
