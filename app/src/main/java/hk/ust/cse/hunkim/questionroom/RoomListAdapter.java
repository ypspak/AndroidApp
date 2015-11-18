package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import hk.ust.cse.hunkim.questionroom.room.Room;

/**
 * Created by CAI on 21/10/2015.
 */
public class RoomListAdapter extends FirebaseListAdapter<Room> {
    JoinActivity activity;
    public RoomListAdapter(Query mRef, Activity activity, int mLayout) {
        super(mRef, Room.class, mLayout, activity);
        assert (activity instanceof JoinActivity);
        this.activity = (JoinActivity) activity;
    }

    @Override
    protected void populateView(View view, final Room model) {
        ImageView isPrivate = (ImageView) view.findViewById(R.id.is_private);
        if(!model.getIsPrivate())
            isPrivate.setVisibility(View.INVISIBLE);
        else
            isPrivate.setVisibility(View.VISIBLE);

        TextView roomName = (TextView) view.findViewById(R.id.room_name);
        roomName.setText(model.getKey());

        Button joinRoom = (Button) view.findViewById(R.id.join_button);
        joinRoom.setTag(model.getKey());
        joinRoom.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JoinActivity m = (JoinActivity) view.getContext();
                        m.tryJoin((String) view.getTag(), model);
                    }
                }

        );
    }

    @Override
    protected void sortModels(List<Room> mModels) {

    }

    @Override
    protected void setKey(String key, Room model) {
        model.setKey(key);
    }

    @Override
    protected boolean IsContainString(String filterStr, Room model) {
        return true;
    }

    private void updateRoomName(){

    }
}