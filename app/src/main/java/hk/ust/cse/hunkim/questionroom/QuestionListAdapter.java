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
    private String filterStr;
    private int sortMethod;
    Activity activity;

    //Without filter
    public QuestionListAdapter(Query ref, Activity activity, int layout, String roomName) {
        this(ref, activity, layout, roomName, null);
    }
    //Filter function
    public QuestionListAdapter(Query ref, Activity activity, int layout, String roomName, String filterStr) {
        super(ref, Question.class, layout, activity, filterStr);

        //Originally only MainActivity is allowed.
        //However, I want to reuse this class for search function. Therefore, we use instanceof here
        //Check whether activity is a class of MainActivity / SearchResultActivity by using instanceof keyword
        //By Peter Yeung 13/11/2015
        this.roomName = roomName;
        this.filterStr = filterStr;
        if (activity instanceof MainActivity) {
            this.activity = (MainActivity) activity;
        }
        else if (activity instanceof SearchResultActivity)
            this.activity = (SearchResultActivity) activity;

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


        final int numLike = question.getLike();
        final int numDislike = question.getDislike();
        final int numReply = question.getReplies();
        final String qHead = question.getHead();
        final String qDesc = question.getDesc().equals("") ? "Empty message." : question.getDesc();
        final Long qTimestamp = question.getTimestamp();

        // Map a Chat object to an entry in our listview
        ImageButton likeButton = (ImageButton) view.findViewById(R.id.QuestionLike);
        ImageButton dislikeButton = (ImageButton) view.findViewById(R.id.QuestionDislike);
        ImageButton replyButton = (ImageButton) view.findViewById(R.id.QuestionReply);

        TextView timeText = (TextView) view.findViewById((R.id.timetext));
        TextView likeNumText = (TextView) view.findViewById((R.id.likenumber));
        TextView dislikeNumText = (TextView) view.findViewById((R.id.dislikenumber));
        TextView replyNumText = (TextView) view.findViewById((R.id.replynumber));

        timeText.setText("Last updated: " + getDate(question.getLastTimestamp()));
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
                        if (view.getContext() instanceof MainActivity) {
                            MainActivity m = (MainActivity) view.getContext();
                            m.updateLike((String) view.getTag());
                        }
                        else if (view.getContext() instanceof SearchResultActivity)
                        {
                            SearchResultActivity m = (SearchResultActivity) view.getContext();
                            m.updateLike((String) view.getTag());
                        }
                    }
                }

        );

        dislikeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getContext() instanceof MainActivity) {
                            MainActivity m = (MainActivity) view.getContext();
                            m.updateDislike((String) view.getTag());
                        }
                        else if (view.getContext() instanceof SearchResultActivity)
                        {
                            SearchResultActivity m = (SearchResultActivity) view.getContext();
                            m.updateDislike((String) view.getTag());
                        }
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
                        intent.putExtra("NUM_LIKE", numLike);
                        intent.putExtra("NUM_DISLIKE", numDislike);
                        intent.putExtra("NUM_REPLY", numReply);
                        intent.putExtra("HEAD", qHead);
                        intent.putExtra("DESC", qDesc);
                        intent.putExtra("TIMESTAMP", qTimestamp);
                        view.getContext().startActivity(intent);
                    }
                }
        );



        //Initialize the dbUtil, and check whether activity is a class of MainActivity/SearchResultActivity. If so, get the dbUtil.
        //Moreover, if the class is SearchResultActivity, I want to enable the multiline of the description
        //By Peter Yeung 13/11/2015
        DBUtil dbUtil = null;
        if (activity instanceof MainActivity) {
            dbUtil = ((MainActivity) activity).getDbutil();
            ((TextView) view.findViewById(R.id.head)).setText(Html.fromHtml("" + question.getHead()));
        }
        else if (activity instanceof SearchResultActivity) {
            dbUtil = ((SearchResultActivity) activity).getDbutil();
            if (question.getDesc().equals(""))
                ((TextView) view.findViewById(R.id.desc)).setText(Html.fromHtml("Empty message."));
            else
                ((TextView) view.findViewById(R.id.desc)).setText(Html.fromHtml("" + replaceFilterStr(question.getDesc(), filterStr)));

            ((TextView) view.findViewById(R.id.head)).setText(Html.fromHtml("" + replaceFilterStr(question.getHead(), filterStr)));

        }

        //If the dbUtil is null, then probably the activity is neither MainActivity or SearchResultActivity, i.e. exception. So we do assertion here
        //By Peter Yeung 13/11/2015
        assert(dbUtil != null);

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

    private String getRoomName(){
        return roomName;
    }


    private String replaceFilterStr(String text, String filterStr) {
        String prependText = "<b><font color=\"red\">";
        String endText = "</font></b>";
        String loweredText = text.toLowerCase();
        String loweredfilterStr = filterStr.toLowerCase();
        int initalPosition = 0;
        int lengthIncrement = prependText.length() + endText.length();
        int lengthfilterStr = filterStr.length();
        initalPosition = loweredText.indexOf(loweredfilterStr, initalPosition);

        while (initalPosition > -1)
        {
            text = text.substring(0, initalPosition) + prependText + text.substring(initalPosition, initalPosition + lengthfilterStr) + endText + text.substring(initalPosition + lengthfilterStr);
            loweredText = text.toLowerCase();
            initalPosition = loweredText.indexOf(loweredfilterStr, initalPosition + lengthIncrement);

        }

        return text;
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

    protected boolean IsContainString(String filterStr, Question model)
    {

        //Check header and then description
        if (filterStr == null)
            return true;
        else if (model.getHead().toLowerCase().contains(filterStr.toLowerCase()))
            return true;
        else if (model.getDesc().toLowerCase().contains(filterStr.toLowerCase()))
            return true;

        return false;
    }

    private int difCompareWay(Question question1, Question question2){
        int method = this.getSortMethod();
        if(method==0){ //newest
            return question1.getTimestamp() < question2.getTimestamp() ? 1 : -1;
        }else if(method==1){ //hot
            if ((question1.getTimestamp()+7200000*(3*question1.getLike()+2*question1.getReplies()+question1.getDislike()))
                    == (question2.getTimestamp()+7200000*(3*question2.getLike()+2*question2.getReplies()+question2.getDislike()))) {
                return question1.getTimestamp() < question2.getTimestamp() ? 1 : -1;
            }
            return (question1.getTimestamp()+7200000*(3*question1.getLike()+2*question1.getReplies()+question1.getDislike()))
                    < (question2.getTimestamp()+7200000*(3*question2.getLike()+2*question2.getReplies()+question2.getDislike()))? 1:-1;
        }else if(method == 2) {//like
            if (question1.getLike() == question2.getLike()) {
                return question1.getTimestamp() < question2.getTimestamp() ? 1 : -1;
            }
            return question2.getLike() - question1.getLike();
        }else if(method == 3){//dislike
            if (question1.getDislike() == question2.getDislike()) {
                return question1.getTimestamp() < question2.getTimestamp() ? 1 : -1;
            }
            return question2.getDislike() - question1.getDislike();
        }else if (method == 4){//latest reply
            return question1.getLastTimestamp() < question2.getLastTimestamp() ? 1 : -1;
        }
        return 0;
    }
}
