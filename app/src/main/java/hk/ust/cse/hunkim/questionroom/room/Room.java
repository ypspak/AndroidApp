package hk.ust.cse.hunkim.questionroom.room;

/**
 * Created by CAI on 15/11/2015.
 */
public class Room {
    private boolean isPrivate;
    private String password;

    private Room(){}
    public Room(boolean isPrivate, String password){
        this.isPrivate = isPrivate;
        this.password = password;
    }

    public boolean getIsPrivate(){ return isPrivate;}
    public String getPassword(){return password;}
}
