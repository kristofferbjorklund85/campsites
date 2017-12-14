package com.example.sombra.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sombra on 2017-11-28.
 */

public class CommentLoader {

    private Context context;
    private VolleyHandler vh;
    private ListView commentsListView;
    private CampsiteModel cm;
    private boolean waiting = false;

    public CommentLoader(Context context, ListView listview, CampsiteModel cm) {
        vh = new VolleyHandler();
        this.context = context;
        commentsListView = listview;
        this.cm = cm;

    }

    private class loadCommentsList extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            vh.getComments(context);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            /*cml = (ArrayList<CampsiteModel>) vh.getCampList();
            for(CampsiteModel cm : cml) {
                Log.d("List ", cm.location);
            }
            Log.d("starting: ", "Maps");
            Intent intent = new Intent(LandingActivity.this, MapsActivity.class);
            intent.putParcelableArrayListExtra("cmList", cml);
            startActivity(intent);*/



            waiting = true;
        }
    }


    public boolean loadComments() {

        Comment[] commentsArray2 = {
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

        Log.d("COMMENLOADER: ", "execute");
        new loadCommentsList().execute();
        Log.d("COMMENLOADER: ", "waiting");
        while(!waiting) {}

        List<Comment> list = vh.getCommentList();
        Log.d("COMMENTLOADER", "Getting: CommentList");
        Comment[] commentsArray = new Comment[list.size()];

        for(int i = 0; i < list.size(); i++) {
            commentsArray[i] = list.get(i);
        }

        ListView comments = commentsListView;
        CommentAdapter adapter = new CommentAdapter(context,
                R.layout.comments_listitem, commentsArray);
        comments.setAdapter(adapter);
        justifyListViewHeightBasedOnChildren(comments, adapter);
        justifyListViewHeightBasedOnChildren(comments, adapter);

        Log.d("COMMENLOADER: ", "return from loadcomments");
        return true;
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
