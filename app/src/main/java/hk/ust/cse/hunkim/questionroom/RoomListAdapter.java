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
import hk.ust.cse.hunkim.questionroom.room.Room;

/**
 * Created by CAI on 21/10/2015.
 */
public class RoomListAdapter extends FirebaseListAdapter<Room> {
    RoomActivity activity;
    public RoomListAdapter(Query mRef, Activity activity, int mLayout) {
        super(mRef, Room.class, mLayout, activity);
        assert (activity instanceof RoomActivity);
        this.activity = (RoomActivity) activity;
    }

    @Override
    protected void populateView(View v, Room model) {

    }

    @Override
    protected void sortModels(List<Room> mModels) {

    }

    @Override
    protected void setKey(String key, Room model) {

    }

    @Override
    protected boolean IsContainString(String filterStr, Room model) {
        return false;
    }
}