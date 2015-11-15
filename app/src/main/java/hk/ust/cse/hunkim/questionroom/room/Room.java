package hk.ust.cse.hunkim.questionroom.room;

/**
 * Created by CAI on 15/11/2015.
 */
public class Room {
    private String roomName;
    private boolean isPrivate;
    private String password;

    private Room(){}
    public Room(String roomName, boolean isPrivate, String password){
        this.roomName = roomName;
        this.isPrivate = isPrivate;
        this.password = password;
    }

    public String getRoomName(){return roomName;}
    public boolean getIfPrivate(){ return isPrivate;}
    public String getPassword(){return password;}
}
