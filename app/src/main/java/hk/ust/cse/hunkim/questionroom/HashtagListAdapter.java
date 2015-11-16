package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.hashtag.Hashtag;

/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */

//Here control how the question is listed.
public class HashtagListAdapter extends FirebaseListAdapter<Hashtag> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    public static final String ROOM_NAME = "ROOMNAME";
    private String roomName;
    private String filterStr;
    SearchMainActivity activity;

    //Without filter
    public HashtagListAdapter(Query ref, Activity activity, int layout, String roomName) {
        this(ref, activity, layout, roomName, null);
    }
    //Filter function
    public HashtagListAdapter(Query ref, Activity activity, int layout, String roomName, String filterStr) {
        super(ref, Hashtag.class, layout, activity, filterStr);

        //Originally only MainActivity is allowed.
        //However, I want to reuse this class for search function. Therefore, we use instanceof here
        //Check whether activity is a class of MainActivity / SearchResultActivity by using instanceof keyword
        //By Peter Yeung 13/11/2015
        this.roomName = roomName;
        this.filterStr = filterStr;
    }




    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view     A view instance corresponding to the layout we passed to the constructor.
     * @param hashtag An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, Hashtag hashtag) {


        // Map a Chat object to an entry in our listview

        TextView hashtagText = (TextView) view.findViewById(R.id.name);
        TextView timesText = (TextView) view.findViewById((R.id.times));

        hashtagText.setText(""  + hashtag.getName());
        timesText.setText("There are currently " + hashtag.getUsed() + " questions related in this room.");

        view.setTag(hashtag.getKey());  // store key in the view
    }

    private String getRoomName(){
        return roomName;
    }

    @Override
    protected void sortModels(List<Hashtag> mModels) {
        Collections.sort(mModels, new Comparator<Hashtag>() {
            public int compare(Hashtag hashtag1, Hashtag hashtag2) {
                return -hashtag1.getUsed() + hashtag2.getUsed();
            }
        });
    }

    @Override
    protected void setKey(String key, Hashtag model) {
        model.setKey(key);
    }

    protected boolean IsContainString(String filterStr, Hashtag model)
    {
        return true;
    }
}
