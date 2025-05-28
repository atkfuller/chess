package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

public class MemoryGameDAO implements GameDAO {
    private ArrayList<GameData> games=new ArrayList<GameData>();
    private static Set<Integer> gameIDs= new HashSet<>();
    private static final Random RAND = new Random();
    @Override
    public void clear(){
        games.clear();
    }
    @Override
    public ArrayList<GameData> listGames(){
        return games;
    }
    @Override
    public void addGame(GameData data){
        games.add(data);
    }
    @Override
    public GameData getGame(int id){
        for(GameData data: games){
            if(data.gameID()==id){
                return data;
            }
        }
        return null;
    }

    @Override
    public void joinGame(String color, GameData game, String username) throws DataAccessException{
        GameData data;
        if(Objects.equals(color, "BLACK")){
            if(game.blackUsername() != null){
                throw new DataAccessException(403, "Error: already taken");
            }
            data= new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(),game.game());
        }
        else{
            if(game.whiteUsername() != null){
                throw new DataAccessException(403, "Error: already taken");
            }
            data= new GameData(game.gameID(), username, game.blackUsername(), game.gameName(),game.game());
        }
        setGame(game.gameID(), data);
    }
    @Override
    public void updateGame(int gameID, ChessGame updatedGame) throws DataAccessException {
        GameData existing = getGame(gameID);
        if (existing == null) {
            throw new DataAccessException(400, "Error: bad request");
        }

        GameData updated = new GameData(gameID, existing.whiteUsername(), existing.blackUsername(), existing.gameName(), updatedGame);

        setGame(gameID, updated);
    }
    @Override
    public void setGame(int id, GameData game){
        for(GameData data: games){
            if(data.gameID()==id){
                games.remove(data);
                games.add(game);
            }
        }

    }
    @Override
    public GameData createGame(String gameName){
        GameData data= new GameData(generateUniqueID(), null, null, gameName, new ChessGame());
        games.add(data);
        return data;
    }
    public static int generateUniqueID() {
        int id;
        do {
            id = MemoryGameDAO.RAND.nextInt(9000);
        } while (MemoryGameDAO.gameIDs.contains(id));
        MemoryGameDAO.gameIDs.add(id);
        return id;
    }
}
