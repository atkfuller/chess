package dataaccess;

import org.eclipse.jetty.server.Authentication;

import model.UserData;
import java.util.ArrayList;
import java.util.Objects;

public class UserDAO {
    private ArrayList<UserData> users=new ArrayList<UserData>();
    public void clear(){
        users.clear();
    }
    public UserData getUser(String username)throws DataAccessException{
        if(users.isEmpty()){
            return null;
        }
        for(UserData data: users){
            if(Objects.equals(data.username(), username)){
                return data;
            }
        }
        return null;
    }
    public void createUser(UserData user)throws DataAccessException{
        users.add(user);
    }


    public ArrayList<UserData> getUsers() {
        return users;
    }
}
