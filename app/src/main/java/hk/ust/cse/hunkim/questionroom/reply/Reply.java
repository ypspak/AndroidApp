package hk.ust.cse.hunkim.questionroom.reply;

import java.util.Date;

/**
 * Created by CAI on 23/10/2015.
 */
public class Reply{
    private String key;
    private String desc;
    private long timestamp;
    private int order;

    private Reply(){

    }

    public Reply(String reply){
        this.desc = reply;
        this.timestamp = new Date().getTime();
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getOrder() {
        return order;
    }

    public String getKey() {
        return key;
    }
    //getters
}
