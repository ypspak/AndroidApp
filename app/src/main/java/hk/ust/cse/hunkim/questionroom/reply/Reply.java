package hk.ust.cse.hunkim.questionroom.reply;

import java.util.Date;

/**
 * Created by CAI on 23/10/2015.
 */
public class Reply implements Comparable<Reply>{
    private String key;
    private String wholeMsg;
    private long timestamp;
    //private int like;
    //private int dislike;
    //private int order;

    public Reply() {
        this.wholeMsg = "";
    }
    public Reply(String reply){
        this.wholeMsg = reply;
        //this.like = 0;
        //this.dislike = 0;
        this.timestamp = new Date().getTime();
    }

    public void setKey(String key) {
        this.key = key;
    }

    /*public int getLike() {
        return like;
    }

    public int getDislike() {return dislike; }*/

    public String getWholeMsg() {
        return wholeMsg;
    }

    public long getTimestamp() {
        return timestamp;
    }
/*
    public int getOrder() {
        return order;
    }*/

    public String getKey() {
        return key;
    }
    //getters

    public int compareTo(Reply other) {

        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Reply)) {
            return false;
        }
        Reply other = (Reply)o;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}

