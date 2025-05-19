package dataaccess;

import java.AuthData;
import java.UserData;
import java.util.ArrayList;

public class AuthDAO {
    private ArrayList<AuthData> authencation;
    public void clear(){
        authencation.clear();
    }
    public void createAuth(AuthData autho){
        authencation.add(autho);
    }
}
