package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by CAI on 2/11/2015.
 */
public class ReplyActivityTest extends ActivityInstrumentationTestCase2<ReplyActivity> {
    private EditText replyInput;
    private ImageButton sendButton;
    private ImageButton parentLike;
    private ImageButton parentDislike;
    private TextView timeText;
    private Button titleText;
    private TextView descText;
    private TextView likeText;
    private TextView dislikeText;



    private Intent mStartIntent;
    private ReplyActivity activity;
    private static final int TIMEOUT_IN_MS = 5000;

    public ReplyActivityTest() {
        super(ReplyActivity.class);
    }

    protected void setUp() throws Exception{
        super.setUp();
        mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra(QuestionListAdapter.ROOM_NAME, "TestRoom");
        mStartIntent.putExtra(QuestionListAdapter.REPLIED_QEUSTION, "TestQuestionKey");
        setActivityIntent(mStartIntent);
        replyInput = (EditText) getActivity().findViewById(R.id.replyInput);
        sendButton = (ImageButton) getActivity().findViewById(R.id.sendButton);
        parentDislike=(ImageButton) getActivity().findViewById(R.id.dislikeParentQuestion);
        parentLike=(ImageButton) getActivity().findViewById(R.id.likeParentQuestion);
        timeText = (TextView) getActivity().findViewById((R.id.timetext));
        titleText = (Button) getActivity().findViewById((R.id.head_reply));
        descText = (TextView) getActivity().findViewById((R.id.desc));
        likeText = (TextView) getActivity().findViewById((R.id.likeText));
        dislikeText = (TextView) getActivity().findViewById(R.id.dislikeText);
    }

    public void testPrecondition(){
        assertNotNull("ReplyActivity is null", getActivity());
        assertNotNull("ReplyMessage field is null", replyInput);
        assertNotNull("ReplySending Button is null", sendButton);
        assertNotNull("Parent Question has no dislike button", parentDislike);
        assertNotNull("Parent Question has no like button", parentLike);
        assertNotNull(timeText);
        assertNotNull(titleText);
        assertNotNull(descText);
        assertNotNull(likeText);
        assertNotNull(dislikeText);
    }

    public void testReplyWithoutMessage() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                replyInput.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendStringSync("");
        getInstrumentation().waitForIdleSync();

        String actualText = replyInput.getText().toString();
        while(actualText==null){
            actualText = replyInput.getText().toString();
        }

        TouchUtils.clickView(this, sendButton);
        getInstrumentation().waitForIdleSync();
        assertEquals("There s not error message from the reply input", "This field is required", replyInput.getError());
    }

    public void testReplyWithMessage() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                replyInput.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendStringSync("Testing Reply");
        getInstrumentation().waitForIdleSync();

        String actualText = replyInput.getText().toString();
        while(actualText==null){
            actualText = replyInput.getText().toString();
        }

        TouchUtils.clickView(this, sendButton);
        getInstrumentation().waitForIdleSync();
        assertEquals("There s not error message from the reply input", null, replyInput.getError());
    }
}
