package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
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
import hk.ust.cse.hunkim.questionroom.reply.Reply;

/**
 * Created by CAI on 21/10/2015.
 */
public class ReplyListAdapter extends FirebaseListAdapter<Reply> {
    ReplyActivity activity;

    public ReplyListAdapter(Query mRef, Activity activity, int mLayout) {
        super(mRef, Reply.class, mLayout, activity);
        assert (activity instanceof ReplyActivity);
        this.activity = (ReplyActivity) activity;
    }

    @Override
    protected void populateView(View view, Reply reply) {
        DBUtil dbUtil = activity.getDbutil();
        int order = reply.getOrder();

        ImageButton likeButton = (ImageButton) view.findViewById(R.id.ReplyLike);
        ImageButton dislikeButton = (ImageButton) view.findViewById(R.id.ReplyDislike);

        TextView scoreText = (TextView) view.findViewById(R.id.order);
        TextView timeText = (TextView) view.findViewById(R.id.timetext);

        //Set the color of the score according to the like/dislike
        scoreText.setText("" + order);
        if (order < 0)
            scoreText.setTextColor(view.getResources().getColor(R.color.ReplyOrderDislike));
        else if(order > 0)
            scoreText.setTextColor(view.getResources().getColor(R.color.ReplyOrderLike));
        else
            scoreText.setTextColor(view.getResources().getColor(R.color.ReplyOrderNeutral));

        //Set the text of the timestamp
        timeText.setText("" + getDate(reply.getTimestamp()));
        likeButton.setTag(reply.getKey()); // Set tag for button
        dislikeButton.setTag(reply.getKey());

        likeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReplyActivity m = (ReplyActivity) view.getContext();
                        m.updateOrder((String) view.getTag(), 1);
                    }
                }

        );

        dislikeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReplyActivity m = (ReplyActivity) view.getContext();
                        m.updateOrder((String) view.getTag(), -1);
                    }
                }

        );

        String msgString = "";
        msgString += reply.getDesc();

        ((TextView) view.findViewById(R.id.replyMsg)).setText(Html.fromHtml(msgString));

        // check if we already clicked
        boolean clickable = !dbUtil.contains(reply.getKey());

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


        view.setTag(reply.getKey());  // store key in the view
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
    protected void sortModels(List<Reply> mModels) {
        Collections.sort(mModels, new Comparator<Reply>(){
            public int compare(Reply reply1, Reply reply2) {
                if (reply1.getOrder() >= reply2.getOrder()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    @Override
    protected void setKey(String key, Reply model) {
        model.setKey(key);
    }
}
