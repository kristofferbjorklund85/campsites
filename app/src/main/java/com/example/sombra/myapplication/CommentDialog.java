package com.example.sombra.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

/**
 * Created by Sombra on 2017-12-05.
 */

public class CommentDialog extends DialogFragment {

    CommentLoader cl = null;
    Context context;
    CampsiteModel cm;

    public CommentDialog(Context context, CommentLoader cl, CampsiteModel cm) {
        this.context = context;
        this.cl = cl;
        this.cm = cm;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        builder.setView(inflater.inflate(R.layout.dialog_comment, null))
                .setMessage("Comment")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText comment = (EditText) getDialog().findViewById(R.id.comment_body);
                        String tempString = comment.getText().toString();
                        Comment c = new Comment(
                                        UUID.randomUUID().toString(),
                                        cm.id,
                                        "2017-12-24",
                                        User.getUsername(),
                                        tempString);
                        cl.postComment(context, c);

                        Log.d("submitted: ", User.getUsername() + "said: " + tempString);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        CommentDialog.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}