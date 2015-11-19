package hk.ust.cse.hunkim.questionroom.hashtag;

import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import hk.ust.cse.hunkim.questionroom.R;
import hk.ust.cse.hunkim.questionroom.SearchResultActivity;

/**
 * Created by PakShing on 19/11/2015.
 */
public class Hashtag_processor {

    private static final String FIREBASE_URL = "https://cmkquestionsdb.firebaseio.com/";
    private static final String ROOM_NAME = "ROOM_NAME"; //This is used as VARIABLE name for sending value of variable through intent, i.e. the left part of Map<string, int/string/double>
    private static final String m_FirebaseURL = "FIREBASE_URL"; //This is used as VARIABLE name for sending value of variable through intent
    private static final String INPUT = "INPUT";

    private View view;
    private TextView hashtagText;
    private String roomName;
    private String[] Hashtags;
    private int NumTagsShown = 0;


    public Hashtag_processor(View view, TextView hashtagText, String roomName, String[] Hashtags, int NumTagsShown)
    {
        this.view = view;
        this.hashtagText = hashtagText;
        this.roomName = roomName;
        this.Hashtags = Hashtags;

        if (Hashtags != null)
            this.NumTagsShown = Math.min(NumTagsShown, Hashtags.length);

    }

    public void HashtagTextJoin()
    {
        if (Hashtags == null)
        {
            hashtagText.setText("None");
            return;
        }

        SpannableStringBuilder sb = new SpannableStringBuilder();
        int PreviousPosition = 0;
        int CurrentPosition = 0;
        for (int i = 0; i < NumTagsShown; i++)
        {
            final String SingleHashtags = Hashtags[i];

            //Set the behavior of clicking it
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SearchResultActivity.class);
                    intent.putExtra(ROOM_NAME, roomName);
                    intent.putExtra(m_FirebaseURL, FIREBASE_URL);
                    intent.putExtra(INPUT, SingleHashtags);
                    view.getContext().startActivity(intent);
                }
            };

            //Set the color of the text
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(view.getResources().getColor(R.color.SpannableHashtagReply));

            CurrentPosition += Hashtags[i].length();
            sb.append(Hashtags[i]);
            sb.setSpan(clickableSpan, PreviousPosition, CurrentPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(foregroundColorSpan, PreviousPosition, CurrentPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(" ");
            CurrentPosition++;
            PreviousPosition = CurrentPosition; //Increment 1 for delimiter
        }

        hashtagText.setText(sb);
        hashtagText.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
