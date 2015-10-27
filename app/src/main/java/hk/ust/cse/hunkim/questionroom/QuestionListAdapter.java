package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
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

        // Map a Chat object to an entry in our listview
        int like = question.getLike();
        int dislike = question.getDislike();
        int score = like - dislike;
        ImageButton likeButton = (ImageButton) view.findViewById(R.id.like);
        ImageButton dislikeButton = (ImageButton) view.findViewById(R.id.dislike);
        ImageButton replyButton = (ImageButton) view.findViewById(R.id.reply);
        TextView scoreText = (TextView) view.findViewById(R.id.score);
        TextView timeText = (TextView) view.findViewById((R.id.timetext));

        scoreText.setText("" + (score));
        if (score < 0)
            scoreText.setTextColor(Color.parseColor("#ae0000"));
        else if(score > 0)
            scoreText.setTextColor(Color.parseColor("#42dfd8"));


        timeText.setText("created: " + getDate(question.getTimestamp()));
        
        likeButton.setTag(question.getKey()); // Set tag for button
        dislikeButton.setTag(question.getKey());
        replyButton.setTag(question.getKey());

        likeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();
                        m.updateLike((String) view.getTag());
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
                        intent.putExtra(ROOM_NAME, getRoomName());
                        view.getContext().startActivity(intent);
                    }
                }
        );

        String msgString = "";


        //msgString += "<B>" + question.getHead() + "</B>" + question.getDesc();
        msgString += question.getDesc();
        ((TextView) view.findViewById(R.id.head_desc)).setText(Html.fromHtml(msgString));

        // check if we already clicked
        boolean clickable = !dbUtil.contains(question.getKey());

        likeButton.setClickable(clickable);
        likeButton.setEnabled(clickable);
        dislikeButton.setClickable(clickable);
        dislikeButton.setEnabled(clickable);
        view.setClickable(clickable);


        // http://stackoverflow.com/questions/8743120/how-to-grey-out-a-button
        // grey out our button
        if (clickable) {
            likeButton.getBackground().setColorFilter(null);
            dislikeButton.getBackground().setColorFilter(null);
        } else {
            likeButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);
            dislikeButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);
        }


        view.setTag(question.getKey());  // store key in the view
    }

    private void keepRoomName(String rn){
        roomName = rn;
    }

    private String getRoomName(){
        return roomName;
    }

    private String getDate(long timestamp)
    {
        //"Thu Oct 22 2015 11:17:20 GMT+0800 (HKT)"
        DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z");
        Date date = (new Date(timestamp));
        return df.format(date);
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
