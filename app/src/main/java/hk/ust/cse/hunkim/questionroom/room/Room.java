package hk.ust.cse.hunkim.questionroom.room;

/**
 * Created by CAI on 15/11/2015.
 */
public class Room {
    private String name;
    private boolean isPrivate;
    private String password;

    private Room(){}
    public Room(String name, boolean isPrivate, String password){
        this.name = name;
        this.isPrivate = isPrivate;
        this.password = password;
    }

    public boolean checkPassword(String password){
        return this.password.equals(password);
    }
    public String getName(){ return name;}
    public boolean getIfPrivate(){ return isPrivate;}
}
