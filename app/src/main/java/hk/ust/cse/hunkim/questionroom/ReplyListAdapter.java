package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.graphics.Color;
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
        int like = reply.getLike();
        int dislike = reply.getDislike();
        ImageButton likeButton = (ImageButton) view.findViewById(R.id.like);
        ImageButton dislikeButton = (ImageButton) view.findViewById(R.id.dislike);

        likeButton.setTag(reply.getKey()); // Set tag for button
        dislikeButton.setTag(reply.getKey());

        likeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReplyActivity m = (ReplyActivity) view.getContext();

                    }
                }

        );

        dislikeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReplyActivity m = (ReplyActivity) view.getContext();
//                        m.updateDislike((String) view.getTag());
                    }
                }

        );

        String msgString = "";
        msgString += reply.getWholeMsg();

        ((TextView) view.findViewById(R.id.replyMsg)).setText(Html.fromHtml(msgString));
    }

    @Override
    protected void sortModels(List<Reply> mModels) {

    }

    @Override
    protected void setKey(String key, Reply model) {
        model.setKey(key);
    }
}
