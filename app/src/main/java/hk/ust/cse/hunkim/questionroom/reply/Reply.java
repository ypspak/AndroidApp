package hk.ust.cse.hunkim.questionroom.reply;

import java.util.Date;

/**
 * Created by CAI on 23/10/2015.
 */
public class Reply{
    private String desc;
    private long timestamp;
    private int order;

    private Reply(){

    }

    public Reply(String reply){
        this.order = 0;
        this.desc = reply;
        this.timestamp = new Date().getTime();
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

    //modifier of each attribute, timestamp is supposed to be unable to modified
    public void setDesc(String newReply){
        this.desc= newReply;
    }

    public void setOrder(int newOrder){
        this.order = newOrder;
    }
}
