package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by CAI on 21/10/2015.
 */
public class ReplyActivity extends ListActivity {

    private TextView questionContent;
    private String questionTitle;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        Intent intent = getIntent();
        assert (intent != null);

        //currently just for testing that I am entered the replying room corresponding to the question
        questionTitle = intent.getStringExtra(QuestionListAdapter.REPLIED_QEUSTION);
        questionContent = (TextView) findViewById(R.id.Question);
        questionContent.setText(questionTitle);



    }
    public void Close(View view) {
        finish();
    }
}
