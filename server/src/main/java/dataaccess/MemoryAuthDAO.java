package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO {
    private ArrayList<AuthData> authencation=new ArrayList<>();
    @Override
    public void clear(){
        authencation.clear();
    }
    @Override
    public void createAuth(AuthData autho){
        authencation.add(autho);
    }
    @Override
    public AuthData getAuth(String authToken){
        for(AuthData data: authencation){
            if(Objects.equals(data.authToken(), authToken)){
                return data;
            }
        }
        return null;
    }
    @Override
    public AuthData getAuthByUsername(String username){
        for(AuthData data: authencation){
            if(data.username()==username){
                return data;
            }
        }
        return null;
    }
    @Override
    public void deleteAuth(AuthData aData){
        authencation.remove(aData);
    }
    public ArrayList<AuthData> getAuthencation() {
        return authencation;
    }
}
