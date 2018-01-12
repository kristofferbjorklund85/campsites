package com.example.sombra.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import java.util.UUID;

/**
 * A dialog containing a textfield where the user can type and post a comment.
 */

public class CommentDialog extends DialogFragment {

    CommentLoader cl = null;
    Context context;
    CampsiteModel cm;

    /**
     * Initialize the dialogfragment.
     *
     * @param context the context of the CampsiteAcitivty.
     * @param cl class containing the necessary methods needed to post a comment.
     * @param cm the campsite object to which the comment is posted.
     */
    public CommentDialog(Context context, CommentLoader cl, CampsiteModel cm) {
        this.context = context;
        this.cl = cl;
        this.cm = cm;
    }

    /**
     * Creates a dialog with a textfield, a cancel and a submit-button. If submit is clicked, a
     * CommentModel-object will be created and posted to the API.
     *
     * @param savedInstanceState not used here.
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        builder.setView(inflater.inflate(R.layout.dialog_comment, null))
                .setMessage("CommentModel")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText comment = (EditText) getDialog().findViewById(R.id.comment_body);
                        String tempString = comment.getText().toString();
                        CommentModel c = new CommentModel(
                                        UUID.randomUUID().toString(),
                                        cm.id.toString(),
                                        "2017-12-24",
                                        SessionSingleton.getId(),
                                        SessionSingleton.getUsername(),
                                        tempString);

                        cl.postComment(context, c);
                        Log.d("submitted: ", SessionSingleton.getUsername() + "said: " + tempString);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // SessionSingleton cancelled the dialog
                        CommentDialog.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
