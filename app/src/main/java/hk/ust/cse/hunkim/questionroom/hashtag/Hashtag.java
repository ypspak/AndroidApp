package hk.ust.cse.hunkim.questionroom.hashtag;

/**
 * Created by PakShing on 16/11/2015.
 */
public class Hashtag implements Comparable<Hashtag> {

    private String key;
    private String name;    //name of the hashtag, e.g. #yolo
    private int used;       //Number of time this tag is used.

    private Hashtag() {}

    public Hashtag (String s)
    {
        this.name = s;
        this.used = 1;
    }

    public String getKey() { return key; }
    public String getName() { return name; }
    public int getUsed() { return used; }

    public void setKey(String key) { this.key = key; }
    public void setName(String name) { this.name = name;}
    public void setUsed(int used) { this.used = used; }

    @Override
    public int compareTo(Hashtag other) {


        /*** Added by Peter Yeung, 2015/10/30
         *      We can't implement any method unless a sorting algorithm/method is implemented.
         */
        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Hashtag)) {
            return false;
        }
        Hashtag other = (Hashtag)o;
        return key.equals(other.key);
    }
}
