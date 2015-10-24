package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.nfc.Tag;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.util.Collections;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;

/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */

//Here control how the question is listed.
public class QuestionListAdapter extends FirebaseListAdapter<Question> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private static final String FIREBASE_URL = "https://ypspakclassroom.firebaseio.com/";
    public static final String REPLIED_QEUSTION = "REPLIEDQ";
    public static final String ROOM_NAME = "ROOMNAME";
    private String roomName;
    MainActivity activity;

    public QuestionListAdapter(Query ref, Activity activity, int layout, String roomName) {
        super(ref, Question.class, layout, activity);

        // Must be MainActivity
        assert (activity instanceof MainActivity);
        keepRoomName(roomName);
        this.activity = (MainActivity) activity;
    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view     A view instance corresponding to the layout we passed to the constructor.
     * @param question An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, Question question) {
        DBUtil dbUtil = activity.getDbutil();

        Firebase mReplyFirebaseRef = null;
        ReplyListAdapter mReplyListAdapter = null;
        // Map a Chat object to an entry in our listview
        int echo = question.getEcho();
        int dislike = question.getDislike();
        Button echoButton = (Button) view.findViewById(R.id.echo);
        Button dislikeButton = (Button) view.findViewById(R.id.dislike);
        Button replyButton = (Button) view.findViewById(R.id.reply);
        echoButton.setText("" + echo);
        echoButton.setTextColor(Color.BLUE);
        dislikeButton.setText("" + dislike);
        dislikeButton.setTextColor(Color.RED);


        echoButton.setTag(question.getKey()); // Set tag for button
        dislikeButton.setTag(question.getKey());
        replyButton.setTag(question.getKey());

        echoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();
                        m.updateEcho((String) view.getTag());
                    }
                }

        );

        dislikeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();
                        m.updateDislike((String) view.getTag());
                    }
                }

        );

        replyButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), ReplyActivity.class);
                        intent.putExtra(REPLIED_QEUSTION, (String) view.getTag());
                        intent.putExtra(ROOM_NAME,getRoomName());
                        view.getContext().startActivity(intent);
                    }
                }
        );
        String msgString = "";

        question.updateNewQuestion();
        if (question.isNewQuestion()) {
            msgString += "<font color=red>NEW </font>";
        }

        msgString += "<B>" + question.getHead() + "</B>" + question.getDesc();

        ((TextView) view.findViewById(R.id.head_desc)).setText(Html.fromHtml(msgString));

        // check if we already clicked
        boolean clickable = !dbUtil.contains(question.getKey());

        echoButton.setClickable(clickable);
        echoButton.setEnabled(clickable);
        view.setClickable(clickable);


        // http://stackoverflow.com/questions/8743120/how-to-grey-out-a-button
        // grey out our button
        if (clickable) {
            echoButton.getBackground().setColorFilter(null);
        } else {
            echoButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        }


        view.setTag(question.getKey());  // store key in the view

        //final ListView listView = (ListView) view;
        mReplyFirebaseRef = new Firebase(FIREBASE_URL).child(roomName).child("replies").child(question.getKey());
        mReplyListAdapter = new ReplyListAdapter(mReplyFirebaseRef.orderByChild("like").limitToFirst(5), this.activity, R.layout.question);
        //listView.setAdapter(mReplyListAdapter);
    }

    void keepRoomName(String rn){
        roomName = rn;
    }
    String getRoomName(){
        return roomName;
    }
    @Override
    protected void sortModels(List<Question> mModels) {
        Collections.sort(mModels);
    }

    @Override
    protected void setKey(String key, Question model) {
        model.setKey(key);
    }
}
