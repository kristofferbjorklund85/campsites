package com.example.sombra.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sombra on 2017-11-28.
 */

public class CommentLoader {

    public void loadComments(Context context, ListView listview, CampsiteModel cm) {

        Comment[] commentsArray = {
                new Comment("12", cm.id, "2017-12-24", User.getUsername(), "This is great campsite, many friendly people"),
                new Comment("12", cm.id, "2017-12-24", User.getUsername(), "Too many germans, 0/10"),
                new Comment("12", cm.id, "2017-12-24", User.getUsername(), "Too many germans, 0/10"),
                new Comment("12", cm.id, "2017-12-24", User.getUsername(), "Too many germans, 0/10"),
                new Comment("12", cm.id, "2017-12-24", User.getUsername(), "Too many germans, 0/10"),
                new Comment("12", cm.id, "2017-12-24", User.getUsername(), "Too many germans, 0/10"),
                new Comment("12", cm.id, "2017-12-24", User.getUsername(), "Too many germans, 0/10"),
                new Comment("12", cm.id, "2017-12-24", User.getUsername(), "Too many germans, 0/10"),
                new Comment("12", cm.id, "2017-12-24", User.getUsername(), "Too many germans, 0/10"),
                new Comment("12", cm.id, "2017-12-24", User.getUsername(), "BUY VIAGRA DELUXE NICE PRICE")};


        ListView comments = listview;
        CommentAdapter adapter = new CommentAdapter(context,
                R.layout.comments_listitem, commentsArray);
        comments.setAdapter(adapter);
        justifyListViewHeightBasedOnChildren(comments, adapter);
    }

    private class CommentAdapter extends ArrayAdapter<Comment> {

        Context context;
        int layoutResourceId;
        Comment data[] = null;


        public CommentAdapter(Context context, int layoutResourceId, Comment[] data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            CommentHolder holder = null;

            if(row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new CommentHolder();

                holder.user = (TextView)row.findViewById(R.id.comment_user);
                holder.comment = (TextView)row.findViewById(R.id.comment_textview);

                row.setTag(holder);

            } else {
                holder = (CommentHolder)row.getTag();
            }

            holder.user.setText(data[position].username + ":");
            holder.comment.setText(data[position].commentBody);

            return row;
        }

    }

    private class CommentHolder
    {
        TextView user;
        TextView comment;
    }

    public void justifyListViewHeightBasedOnChildren (ListView listView, CommentAdapter adapter) {

        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

}
