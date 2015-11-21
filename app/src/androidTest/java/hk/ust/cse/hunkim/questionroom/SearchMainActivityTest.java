package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.client.Firebase;

import junit.framework.TestCase;

import hk.ust.cse.hunkim.questionroom.question.Question;

/**
 * Created by CAI on 21/11/2015.
 */
public class SearchMainActivityTest extends ActivityInstrumentationTestCase2<SearchMainActivity> {
    private String roomBaseUrl =  "https://cmkquestionsdb.firebaseio.com//rooms/TestRoom/";

    private ImageButton searchBtn;
    private EditText searchInput;



    public SearchMainActivityTest() {
        super(SearchMainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Intent mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra("ROOM_NAME", "TestRoom");
        mStartIntent.putExtra("ROOM_BASE_URL", roomBaseUrl);
        setActivityIntent(mStartIntent);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSearchWithoutInput(){
        Firebase.setAndroidContext(getActivity());
        Firebase testQuestionUrl = new Firebase(roomBaseUrl).child("questions");
        Question question1 = new Question("Question 1 q1", "");
        Question question2 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question3 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        Question question4 = new Question("Question 1 q1", "");
        Question question5 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question6 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        testQuestionUrl.push().setValue(question1);
        testQuestionUrl.push().setValue(question2);
        testQuestionUrl.push().setValue(question3);
        testQuestionUrl.push().setValue(question4);
        testQuestionUrl.push().setValue(question5);
        testQuestionUrl.push().setValue(question6);

        searchBtn = (ImageButton) getActivity().findViewById(R.id.searchButton);
        searchInput = (EditText) getActivity().findViewById(R.id.searchInput);
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, searchBtn);
        getInstrumentation().waitForIdleSync();


        Firebase mFirebaseRef = new Firebase(roomBaseUrl);
        mFirebaseRef.removeValue();
    }

    public void testSearchWithNormalInput(){
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SearchMainActivity.class.getName(), null, false);
        Firebase.setAndroidContext(getActivity());
        Firebase testQuestionUrl = new Firebase(roomBaseUrl).child("questions");
        Question question1 = new Question("Question 1 q1", "");
        Question question2 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question3 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        Question question4 = new Question("Question 1 q1", "");
        Question question5 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question6 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        testQuestionUrl.push().setValue(question1);
        testQuestionUrl.push().setValue(question2);
        testQuestionUrl.push().setValue(question3);
        testQuestionUrl.push().setValue(question4);
        testQuestionUrl.push().setValue(question5);
        testQuestionUrl.push().setValue(question6);

        searchBtn = (ImageButton) getActivity().findViewById(R.id.searchButton);
        searchInput = (EditText) getActivity().findViewById(R.id.searchInput);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                searchInput.requestFocus();
            }
        });

        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendStringSync("Question");
        getInstrumentation().waitForIdleSync();

        String actualText = searchInput.getText().toString();
        while(actualText==null){
            actualText = searchInput.getText().toString();
        }
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, searchBtn);
        getInstrumentation().waitForIdleSync();

        SearchResultActivity nextActivity;

        do{
            nextActivity= (SearchResultActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5);
        }while(nextActivity==null);

        nextActivity.finish();

        Firebase mFirebaseRef = new Firebase(roomBaseUrl);
        mFirebaseRef.removeValue();
    }

    public void testSearchWithWrongInput(){
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SearchMainActivity.class.getName(), null, false);
        Firebase.setAndroidContext(getActivity());
        Firebase testQuestionUrl = new Firebase(roomBaseUrl).child("questions");
        Question question1 = new Question("Question 1 q1", "");
        Question question2 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question3 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        Question question4 = new Question("Question 1 q1", "");
        Question question5 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question6 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        testQuestionUrl.push().setValue(question1);
        testQuestionUrl.push().setValue(question2);
        testQuestionUrl.push().setValue(question3);
        testQuestionUrl.push().setValue(question4);
        testQuestionUrl.push().setValue(question5);
        testQuestionUrl.push().setValue(question6);

        searchBtn = (ImageButton) getActivity().findViewById(R.id.searchButton);
        searchInput = (EditText) getActivity().findViewById(R.id.searchInput);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                searchInput.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendStringSync("KaStion");
        getInstrumentation().waitForIdleSync();

        String actualText = searchInput.getText().toString();
        while(actualText==null){
            actualText = searchInput.getText().toString();
        }
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, searchBtn);
        getInstrumentation().waitForIdleSync();

        SearchResultActivity nextActivity;
        do{
            nextActivity= (SearchResultActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5);
        }while(nextActivity==null);

        nextActivity.finish();


        Firebase mFirebaseRef = new Firebase(roomBaseUrl);
        mFirebaseRef.removeValue();
    }

    public void testSearchWithHashTagInTitle(){
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SearchMainActivity.class.getName(), null, false);
        Firebase.setAndroidContext(getActivity());
        Firebase testQuestionUrl = new Firebase(roomBaseUrl).child("questions");
        Question question1 = new Question("Question 1 q1", "");
        Question question2 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question3 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        Question question4 = new Question("Question 1 q1", "");
        Question question5 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question6 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        testQuestionUrl.push().setValue(question1);
        testQuestionUrl.push().setValue(question2);
        testQuestionUrl.push().setValue(question3);
        testQuestionUrl.push().setValue(question4);
        testQuestionUrl.push().setValue(question5);
        testQuestionUrl.push().setValue(question6);

        searchBtn = (ImageButton) getActivity().findViewById(R.id.searchButton);
        searchInput = (EditText) getActivity().findViewById(R.id.searchInput);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                searchInput.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendStringSync("#q2");
        getInstrumentation().waitForIdleSync();

        String actualText = searchInput.getText().toString();
        while(actualText==null){
            actualText = searchInput.getText().toString();
        }
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, searchBtn);
        getInstrumentation().waitForIdleSync();

        SearchResultActivity nextActivity;
        do{
            nextActivity= (SearchResultActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5);
        }while(nextActivity==null);

        nextActivity.finish();


        Firebase mFirebaseRef = new Firebase(roomBaseUrl);
        mFirebaseRef.removeValue();
    }

    public void testSearchWithHashTagInDesc(){
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SearchMainActivity.class.getName(), null, false);
        Firebase.setAndroidContext(getActivity());
        Firebase testQuestionUrl = new Firebase(roomBaseUrl).child("questions");
        Question question1 = new Question("Question 1 q1", "");
        Question question2 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question3 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        Question question4 = new Question("Question 1 q1", "");
        Question question5 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question6 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        testQuestionUrl.push().setValue(question1);
        testQuestionUrl.push().setValue(question2);
        testQuestionUrl.push().setValue(question3);
        testQuestionUrl.push().setValue(question4);
        testQuestionUrl.push().setValue(question5);
        testQuestionUrl.push().setValue(question6);

        searchBtn = (ImageButton) getActivity().findViewById(R.id.searchButton);
        searchInput = (EditText) getActivity().findViewById(R.id.searchInput);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                searchInput.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendStringSync("#abc");
        getInstrumentation().waitForIdleSync();

        String actualText = searchInput.getText().toString();
        while(actualText==null){
            actualText = searchInput.getText().toString();
        }
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, searchBtn);
        getInstrumentation().waitForIdleSync();

        SearchResultActivity nextActivity;
        do{
            nextActivity= (SearchResultActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5);
        }while(nextActivity==null);

        nextActivity.finish();


        Firebase mFirebaseRef = new Firebase(roomBaseUrl);
        mFirebaseRef.removeValue();
    }

    public void testSearchWithHashTagNoResult(){
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SearchMainActivity.class.getName(), null, false);
        Firebase.setAndroidContext(getActivity());
        Firebase testQuestionUrl = new Firebase(roomBaseUrl).child("questions");
        Question question1 = new Question("Question 1 q1", "");
        Question question2 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question3 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        Question question4 = new Question("Question 1 q1", "");
        Question question5 = new Question("Question 2 #q2 ", "This is question 2 \n # dealwith it haha.");
        Question question6 = new Question("Question 3 #  q3 ", "question 3 at here #abc look out");
        testQuestionUrl.push().setValue(question1);
        testQuestionUrl.push().setValue(question2);
        testQuestionUrl.push().setValue(question3);
        testQuestionUrl.push().setValue(question4);
        testQuestionUrl.push().setValue(question5);
        testQuestionUrl.push().setValue(question6);

        searchBtn = (ImageButton) getActivity().findViewById(R.id.searchButton);
        searchInput = (EditText) getActivity().findViewById(R.id.searchInput);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                searchInput.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        getInstrumentation().sendStringSync("#awasome");
        getInstrumentation().waitForIdleSync();

        String actualText = searchInput.getText().toString();
        while(actualText==null){
            actualText = searchInput.getText().toString();
        }
        getInstrumentation().waitForIdleSync();
        TouchUtils.clickView(this, searchBtn);
        getInstrumentation().waitForIdleSync();

        SearchResultActivity nextActivity;
        do{
            nextActivity= (SearchResultActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5);
        }while(nextActivity==null);

        nextActivity.finish();


        Firebase mFirebaseRef = new Firebase(roomBaseUrl);
        mFirebaseRef.removeValue();
    }

}