package hk.ust.cse.hunkim.questionroom.room;

/**
 * Created by CAI on 15/11/2015.
 */
public class Room {
    private boolean isPrivate;
    private String password;
    private String key; //it stores the name of the room
    private Room(){}
    public Room(boolean isPrivate, String password){
        this.isPrivate = isPrivate;
        this.password = password;
    }

    public boolean getIsPrivate(){ return isPrivate;}
    public String getPassword(){return password;}
    public String getKey() {return key; }
    //modifier of each attribute, timestamp is supposed to be unable to modified
    public void setKey(String key) {
        this.key = key;
    }
}
