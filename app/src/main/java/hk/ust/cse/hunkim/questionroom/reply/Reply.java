package hk.ust.cse.hunkim.questionroom.reply;

import java.util.Date;

/**
 * Created by CAI on 23/10/2015.
 */
public class Reply{
    private String desc;
    private long timestamp;
    private int order;
    private String parentID;
    private String key;

    private Reply(){

    }

    public Reply(String reply, String ID){
        this.order = 0;
        this.desc = reply;
        this.timestamp = new Date().getTime();
        this.parentID = ID;
    }

    //getter of each attribute
    public String getDesc() {
        return desc;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getOrder() {
        return order;
    }

    public String getParentID() {return parentID;}

    public String getKey() {return key; }
    //modifier of each attribute, timestamp is supposed to be unable to modified
    public void setKey(String key) {
        this.key = key;
    }
}
