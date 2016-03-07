package edu.dartmouth.cs.together;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class ResetPasswordDialog extends DialogFragment {
    private EditText newPwd;
    private EditText repeatNewPwd;

    public ResetPasswordDialog() {
    }

    public static DialogFragment ResetPasswordDialog(){
        Bundle args = new Bundle();
        QuestionReplyDialogFragment fragment = new QuestionReplyDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        final Activity parent = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        LayoutInflater inflater = parent.getLayoutInflater();
        final Bundle extras = getArguments().getBundle("extras");

        View view = inflater.inflate(R.layout.fragment_question_reply_dialog,null);
        newPwd = (EditText) view.findViewById(R.id.new_password);
        repeatNewPwd = (EditText) view.findViewById(R.id.new_password_confirm);

//        builder.setView(view)
//                .setTitle("Reset Password")
//                        // Add action buttons
//                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        extras.putString("answer", mAnswerEditor.getText().toString());
//                        AnswerOperationAsyncTask answerTask = new AnswerOperationAsyncTask();
//                        answerTask.doInBackground(extras);
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        //save draft in shared preference
//                        QuestionReplyDialogFragment.this.getDialog().cancel();
//                    }
//                });

        return builder.create();


    }
}
