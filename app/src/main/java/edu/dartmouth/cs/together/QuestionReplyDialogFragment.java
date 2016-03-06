package edu.dartmouth.cs.together;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import edu.dartmouth.cs.together.cloud.PostEventIntentService;
import edu.dartmouth.cs.together.cloud.QaIntentService;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.data.Message;
import edu.dartmouth.cs.together.data.Qa;
import edu.dartmouth.cs.together.data.QaDataSource;
import edu.dartmouth.cs.together.utils.Globals;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionReplyDialogFragment extends DialogFragment {

    private TextView mEventText;
    private TextView mMsgTimeText;
    private TextView mQuestionText;
    private EditText mAnswerEditor;
    private String eventDesc;
    private String question;
    private String time;


    public QuestionReplyDialogFragment() {
        // Required empty public constructor

    }

    public static QuestionReplyDialogFragment newInstance(Bundle extras) {
        Bundle args = new Bundle();
        QuestionReplyDialogFragment fragment = new QuestionReplyDialogFragment();
        fragment.setArguments(args);
        args.putBundle("extras", extras);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        final Activity parent = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        LayoutInflater inflater = parent.getLayoutInflater();
        final Bundle extras = getArguments().getBundle("extras");

        eventDesc = extras.getString("eventDesc");
        question = extras.getString("question");
        time = extras.getString("time");

        View view = inflater.inflate(R.layout.fragment_question_reply_dialog,null);

        mEventText = (TextView)view.findViewById(R.id.dialog_event_name);
        mEventText.setText(eventDesc);
        mMsgTimeText = (TextView) view.findViewById(R.id.dialog_msg_time);
        mMsgTimeText.setText(time);
        mQuestionText = (TextView) view.findViewById(R.id.dialog_msg_question);
        mQuestionText.setText(question);
        mAnswerEditor = (EditText) view.findViewById(R.id.dialog_reply_editor);

        builder.setView(view)
                .setTitle("Reply")
                        // Add action buttons
                .setPositiveButton("Reply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        extras.putString("answer", mAnswerEditor.getText().toString());
                        AnswerOperationAsyncTask answerTask = new AnswerOperationAsyncTask();
                        answerTask.doInBackground(extras);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //save draft in shared preference
                        QuestionReplyDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();


    }

    class AnswerOperationAsyncTask extends AsyncTask<Bundle,Void,Void> {
        @Override
        protected Void doInBackground(Bundle... bundles) {
            Bundle extras = bundles[0];

            long qaId = extras.getLong("qaId");
            long eventId = extras.getLong("eventId");
            String answer =  extras.getString("answer");
            String question = extras.getString("question");

            Intent answerIntent = new Intent(getActivity(), QaIntentService.class);
            answerIntent.putExtra(Qa.ID_KEY, qaId);
            answerIntent.putExtra(Qa.A_KEY,answer);
            answerIntent.putExtra(Qa.Q_KEY, question);
            answerIntent.putExtra(Event.ID_KEY, eventId );
            getActivity().startService(answerIntent);

            return null;
        }
    }
}
