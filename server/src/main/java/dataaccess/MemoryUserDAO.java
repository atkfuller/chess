package dataaccess;

import model.UserData;
import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {
    private ArrayList<UserData> users=new ArrayList<UserData>();
    @Override
    public void clear(){
        users.clear();
    }
    @Override
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
    @Override
    public void createUser(UserData user)throws DataAccessException{
        users.add(user);
    }


    public ArrayList<UserData> getUsers() {
        return users;
    }
}
