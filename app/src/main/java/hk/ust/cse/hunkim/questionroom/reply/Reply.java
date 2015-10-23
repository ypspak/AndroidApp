package hk.ust.cse.hunkim.questionroom.reply;

import java.util.Date;

/**
 * Created by CAI on 23/10/2015.
 */
public class Reply{
    private String key;
    private String wholeMsg;
    private boolean completed;
    private long timestamp;
    private int like;
    private int dislike;
    private int order;

    public Reply(String reply){
        this.wholeMsg = reply;
        this.like = 0;
        this.dislike = 0;
        this.timestamp = new Date().getTime();
    }

    public int getLike() {
        return like;
    }

    public int getDislike() {return dislike; }

    public String getWholeMsg() {
        return wholeMsg;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getOrder() {
        return order;
    }
    //getters
}
