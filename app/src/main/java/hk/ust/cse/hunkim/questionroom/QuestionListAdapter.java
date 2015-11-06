package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Html;
import android.text.format.DateUtils;
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
    private int sortMethod;//0=sortByTimestamp, 1=sortByLike

    public QuestionListAdapter(Query ref, Activity activity, int layout, String roomName) {
        super(ref, Question.class, layout, activity);

        // Must be MainActivity
        assert (activity instanceof MainActivity);
        keepRoomName(roomName);
        this.activity = (MainActivity) activity;
        this.sortMethod = 0;
    }

    public int getSortMethod(){
        return sortMethod;
    }

    public void setSortMethod(int method){
        this.sortMethod = method;
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

        ImageButton likeButton = (ImageButton) view.findViewById(R.id.QuestionLike);
        ImageButton dislikeButton = (ImageButton) view.findViewById(R.id.QuestionDislike);
        ImageButton replyButton = (ImageButton) view.findViewById(R.id.QuestionReply);

        TextView timeText = (TextView) view.findViewById((R.id.timetext));
        TextView likeNumText = (TextView) view.findViewById((R.id.likenumber));
        TextView dislikeNumText = (TextView) view.findViewById((R.id.dislikenumber));
        TextView replyNumText = (TextView) view.findViewById((R.id.replynumber));

        timeText.setText("" + getDate(question.getTimestamp()));
        likeNumText.setText("" + question.getLike());
        dislikeNumText.setText("" + question.getDislike());
        replyNumText.setText("" + question.getReplies());

        likeButton.setTag(question.getKey()); // Set tag for button
        dislikeButton.setTag(question.getKey());
        replyButton.setTag(question.getKey());
        replyButton.getBackground().setColorFilter(null);

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

        String msgString = "" + question.getHead();
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
            likeButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_OVER);
            dislikeButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_OVER);
        }


        view.setTag(question.getKey());  // store key in the view
    }

    private void keepRoomName(String rn){
        roomName = rn;
    }

    private String getRoomName(){
        return roomName;
    }

    //If you want to know more about the function, plz visit here http://developer.android.com/reference/android/text/format/DateUtils.html
    private String getDate(long postTime)
    {
        long currentTime = new Date().getTime();
        long timeResolution = 0;
        long timeDiff = currentTime-postTime;
        if(timeDiff < DateUtils.SECOND_IN_MILLIS*5){
            return "Just now";
        }

        if(timeDiff/DateUtils.MINUTE_IN_MILLIS == 0){
            timeResolution = DateUtils.SECOND_IN_MILLIS;
        }else if(timeDiff/DateUtils.HOUR_IN_MILLIS == 0){
            timeResolution = DateUtils.MINUTE_IN_MILLIS;
        }else if(timeDiff/DateUtils.DAY_IN_MILLIS == 0){
            timeResolution = DateUtils.HOUR_IN_MILLIS;
        }else{
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd KK:mm aa");
            Date date = (new Date(postTime));
            return df.format(date);
        }
        return DateUtils.getRelativeTimeSpanString(postTime, currentTime, timeResolution).toString();
    }
    @Override
    protected void sortModels(List<Question> mModels) {
        Collections.sort(mModels, new Comparator<Question>(){
            public int compare(Question question1, Question question2) {
                return difCompareWay(question1, question2);
            }
        });
    }

    @Override
    protected void setKey(String key, Question model) {
        model.setKey(key);
    }


    private int difCompareWay(Question question1, Question question2){
        int method = this.getSortMethod();
        if(method==0){
            return question1.getTimestamp() > question2.getTimestamp() ? 1 : -1;
        }else if(method==1){
            if (question1.getLike() == question2.getLike()) {
                return question1.getTimestamp() > question2.getTimestamp() ? 1 : -1;
            }
            return question2.getLike() - question1.getLike();
        }
        return 0;
    }
}
