package dataaccess;

import org.eclipse.jetty.server.Authentication;

import java.UserData;
import java.util.ArrayList;

public class UserDAO {
    private ArrayList<UserData> users;
    public void clear(){
        users.clear();
    }
    public UserData getUser(String username)throws DataAccessException{
        for(UserData data: users){
            if(data.username()==username){
                return data;
            }
        }
        return null;
    }
    public void createUser(UserData user)throws DataAccessException{
        users.add(user);
    }



}
