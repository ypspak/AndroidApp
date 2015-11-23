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
import hk.ust.cse.hunkim.questionroom.timemanager.TimeManager;

/**
 * Created by CAI on 21/10/2015.
 */
public class ReplyListAdapter extends FirebaseListAdapter<Reply> {
    Activity activity;

    public ReplyListAdapter(Query mRef, Activity activity, int mLayout) {
        super(mRef, Reply.class, mLayout, activity);
        this.activity = (ReplyActivity) activity;
    }

    @Override
    protected void populateView(View view, Reply reply) {
        DBUtil dbUtil = ((ReplyActivity)activity).getDbutil();
        int order = reply.getOrder();

        ImageButton likeButton = (ImageButton) view.findViewById(R.id.reply_like_button);
        ImageButton dislikeButton = (ImageButton) view.findViewById(R.id.reply_dislike_button);

        TextView scoreText = (TextView) view.findViewById(R.id.reply_order);
        TextView timeText = (TextView) view.findViewById(R.id.reply_time_text);
        scoreText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_OVER);
        //Set the color of the score according to the like/dislike
        scoreText.setText("" + order);
        if (order < 0)
            scoreText.setTextColor(view.getResources().getColor(R.color.ReplyOrderDislike));
        else if(order > 0)
            scoreText.setTextColor(view.getResources().getColor(R.color.ReplyOrderLike));
        else
            scoreText.setTextColor(view.getResources().getColor(R.color.ReplyOrderNeutral));

        //Set the text of the timestamp
        timeText.setText("" + new TimeManager(reply.getTimestamp()).getDate());
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

//        ((TextView) view.findViewById(R.id.replyMsg)).setText(Html.fromHtml(msgString));
        ((TextView) view.findViewById(R.id.reply_content)).setText(Html.fromHtml(msgString));
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

    @Override
    protected void sortModels(List<Reply> mModels) {
        Collections.sort(mModels, new Comparator<Reply>(){
            public int compare(Reply reply1, Reply reply2) {
                if (reply1.getOrder() == reply2.getOrder()) {
                    return reply1.getTimestamp() < reply2.getTimestamp() ? 1 : -1;
                }
                return reply2.getOrder() - reply1.getOrder();
            }
        });
    }

    @Override
    protected void setKey(String key, Reply model) {
        model.setKey(key);
    }

    protected boolean IsContainString(String filterStr, Reply model)
    {
        return true; //Currently reply function does not need filter function, so just return true anyway
    }
}
