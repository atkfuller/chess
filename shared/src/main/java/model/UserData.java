package model;

import java.util.Objects;

public record UserData(String username, String pasword, String email){
    public boolean checkPassword(String p){
        if(!Objects.equals(p, pasword)){
            return false;
        }
        return true;
    }
}
