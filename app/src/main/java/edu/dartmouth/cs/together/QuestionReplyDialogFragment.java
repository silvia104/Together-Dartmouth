package edu.dartmouth.cs.together;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import edu.dartmouth.cs.together.data.Message;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionReplyDialogFragment extends DialogFragment {

    TextView mEventText;
    TextView mMsgTimeText;
    TextView mQuestionText;
    EditText mAnswerEditor;




    public QuestionReplyDialogFragment() {
        // Required empty public constructor

    }

    public static QuestionReplyDialogFragment newInstance(ArrayList<String> msgFields) {
        Bundle args = new Bundle();
        QuestionReplyDialogFragment fragment = new QuestionReplyDialogFragment();
        fragment.setArguments(args);
        //evetId msgTime, QaId -- 0,1,2
        args.putStringArrayList("messageFields", msgFields);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        final Activity parent = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        LayoutInflater inflater = parent.getLayoutInflater();
        ArrayList<String> msgFields = getArguments().getStringArrayList("messageFields");

        View view = inflater.inflate(R.layout.fragment_question_reply_dialog,null);

        mEventText = (TextView)view.findViewById(R.id.dialog_event_name);
        mEventText.setText(msgFields.get(0));
        mMsgTimeText = (TextView) view.findViewById(R.id.dialog_msg_time);
        mMsgTimeText.setText(msgFields.get(1));
        mQuestionText = (TextView) view.findViewById(R.id.dialog_msg_question);
        mQuestionText.setText(msgFields.get(2));
        mAnswerEditor = (EditText) view.findViewById(R.id.dialog_reply_editor);

        builder.setView(view)
                .setTitle("Reply")
                        // Add action buttons
                .setPositiveButton("Reply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

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

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_question_reply_dialog, container, false);
//    }

}
