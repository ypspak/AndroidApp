package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Query;

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
        ImageButton likeButton = (ImageButton) view.findViewById(R.id.like);
        ImageButton dislikeButton = (ImageButton) view.findViewById(R.id.dislike);
        TextView scoreText = (TextView) view.findViewById(R.id.order);

        scoreText.setText("" + order);
        if (order < 0)
            scoreText.setTextColor(view.getResources().getColor(R.color.ReplyOrderDislike));
        else if(order > 0)
            scoreText.setTextColor(view.getResources().getColor(R.color.ReplyOrderLike));
        else
            scoreText.setTextColor(view.getResources().getColor(R.color.ReplyOrderNeutral));

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
            likeButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);
            dislikeButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);
        }


        view.setTag(reply.getKey());  // store key in the view
    }

    @Override
    protected void sortModels(List<Reply> mModels) {

    }

    @Override
    protected void setKey(String key, Reply model) {
        model.setKey(key);
    }
}
