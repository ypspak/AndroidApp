package hk.ust.cse.hunkim.questionroom.question;

import android.util.Log;

import java.util.Date;
import java.util.HashMap;

import hk.ust.cse.hunkim.questionroom.hashtag_extracter.Hashtag_extracter;
import hk.ust.cse.hunkim.questionroom.reply.Reply;

/**
 * Created by hunkim on 7/16/15.
 */
public class Question{

    /**
     * Must be synced with firebase JSON structure
     * Each must have getters
     */

    /*
    question in questions
	head: string, // the title
	desc: string, // the main text
	like: integer, // counter of likes
	dislike: integer, // counter of dislikes
	completed: bool, // Was the question solved?
	timestamp: date, // posting time of the question
	tags: string, // string with all hashtags,e.g.: "#first #second"
	wholeMsgReply: string, // hack to store reply input (to be dropped)
	replies: integer // number of replies
     */
    private String key;
    private String head;
    private String desc;
    private int like;
    private int dislike;
    private boolean completed;
    private long timestamp;
    private long lastTimestamp;
    private String[] tags = null;
    private String wholeMsgReply; //todo: unused, gonna to delete
    private int replies;


    // Required default constructor for Firebase object mapping
    //unused
    private Question() {
    }

    public Question(String title, String body) {
        this.like = 0;
        this.dislike = 0;
        this.completed = false;
        this.head = title;
        this.desc = body;
        this.tags = (new Hashtag_extracter(title + " " + body).getArray());
        this.timestamp = this.lastTimestamp = new Date().getTime();
        /* Added by Peter Yeung, 2015/10/30
                    this.timestamp has to be changed to the following comment-out code (apply negative value)
                    since there is no way to reverse order in firebase query(default behaviour: obtain list in ascending order of timestamp)
                    as a result this.timestamp has to be multipled by -1.
                */

        //this.timestamp = (new Date().getTime())*-1;
        this.wholeMsgReply = ""; //todo: will be dropped if web team no long use this attribute
    }


    /* -------------------- Getters ------------------- */
    public String getHead() { return head; }

    public String getDesc() { return desc; }

    public int getLike() { return like; }

    public int getDislike() {return dislike; }

    public boolean isCompleted() { return completed; }

    public long getTimestamp() {return timestamp; }

    public long getLastTimestamp(){return lastTimestamp;}

    public String getKey() {return key; }

    public int getReplies() { return replies; }

    public String[] getTags() { return tags; }

    public String getWholeMsgReply(){return wholeMsgReply;}//todo: can delete later

    public void setKey(String key) {
        this.key = key;
    }

//Todo: currently I cant find the useage of the function below, so I hide it.
//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof Question)) {
//            return false;
//        }
//        Question other = (Question)o;
//        return key.equals(other.key);
//    }
//
//    @Override
//    public int hashCode() {
//        return key.hashCode();
//    }
}
