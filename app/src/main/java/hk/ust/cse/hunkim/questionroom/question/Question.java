package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;

import hk.ust.cse.hunkim.questionroom.reply.Reply;

/**
 * Created by hunkim on 7/16/15.
 */
public class Question implements Comparable<Question> {

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
	order: double, // a value used for ordering
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
    private String tags;
    private double order;
    private String wholeMsgReply; //unused
    private int replies;


    // Required default constructor for Firebase object mapping
    //unused
    private Question() {
    }

    /**
     * Set question from a String message
     * @param message string message
     */
    public Question(String message) {
        this.like = 0;
        this.dislike = 0;
        this.completed = false;
        this.head = getFirstSentence(message).trim();
        this.desc = message;
        this.tags = "";
        this.timestamp = new Date().getTime();
    }

    public Question(String title, String body) {
        this.like = 0;
        this.dislike = 0;
        this.completed = false;
        this.head = title;
        this.desc = body;
        this.tags = "";
        this.timestamp = new Date().getTime();
    }

    /**
     * Get first sentence from a message
     * @param message
     * @return
     */
    public static String getFirstSentence(String message) {
        String[] tokens = {". ", "? ", "! "};

        int index = -1;

        for (String token : tokens) {
            int i = message.indexOf(token);
            if (i == -1) {
                continue;
            }

            if (index == -1) {
                index = i;
            } else {
                index = Math.min(i, index);
            }
        }

        if (index == -1) {
            return message;
        }

        return message.substring(0, index+1);
    }

    /* -------------------- Getters ------------------- */
    public String getHead() { return head; }

    public String getDesc() { return desc; }

    public int getLike() { return like; }

    public int getDislike() {return dislike; }

    public boolean isCompleted() { return completed; }

    public long getTimestamp() {return timestamp; }

    public double getOrder() { return order; }

    public String getKey() {return key; }

    public int getReplies() { return replies; }

    public String getTags() { return tags; }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * New one/high like goes bottom
     * @param other other chat
     * @return order
     */
    @Override
    public int compareTo(Question other) {


        if (this.like == other.like) {
            if (other.timestamp == this.timestamp) {
                return 0;
            }
            return other.timestamp > this.timestamp ? -1 : 1;
        }
        return this.like - other.like;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Question)) {
            return false;
        }
        Question other = (Question)o;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
