package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import hk.ust.cse.hunkim.questionroom.question.Question;


/**
 * Created by hunkim on 7/15/15.
 */

public class QuestionTest  extends TestCase {
    Question q;



    protected void setUp() throws Exception {
        super.setUp();

        q = new Question("Hello?","This is very nice");
    }

    @SmallTest
    public void testHead() {
        assertEquals("Head", "Hello?", q.getHead());
    }
    public void testBody() {
        assertEquals("Body", "This is very nice", q.getDesc());
    }

}
