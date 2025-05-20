package dataaccess;

import model.AuthData;
import model.UserData;
import java.util.ArrayList;

public class AuthDAO {
    private ArrayList<AuthData> authencation=new ArrayList<AuthData>();
    public void clear(){
        authencation.clear();
    }
    public void createAuth(AuthData autho){
        authencation.add(autho);
    }

    public ArrayList<AuthData> getAuthencation() {
        return authencation;
    }
}
