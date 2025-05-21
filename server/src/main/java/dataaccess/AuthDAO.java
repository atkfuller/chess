package dataaccess;

import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AuthDAO {
    private ArrayList<AuthData> authencation=new ArrayList<>();
    public void clear(){
        authencation.clear();
    }
    public void createAuth(AuthData autho){
        authencation.add(autho);
    }
    public AuthData getAuth(String authToken){
        for(AuthData data: authencation){
            if(Objects.equals(data.authToken(), authToken)){
                return data;
            }
        }
        return null;
    }
    public AuthData getAuthByUsername(String username){
        for(AuthData data: authencation){
            if(data.username()==username){
                return data;
            }
        }
        return null;
    }
    public void deleteAuth(AuthData aData){
        authencation.remove(aData);
    }
    public ArrayList<AuthData> getAuthencation() {
        return authencation;
    }
}
