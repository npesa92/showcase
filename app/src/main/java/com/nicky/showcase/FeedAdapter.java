package com.nicky.showcase;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nicholas.myapplication.backend.postApi.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Nicholas on 10/1/2015.
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Post> dataSet;
    Context context;
    Calendar calendar;
    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};

    public FeedAdapter(ArrayList<Post> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
        calendar = new GregorianCalendar();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new CustomView(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Post post = dataSet.get(position);
        ((CustomView) viewHolder).setPost(post);

        Picasso.with(context).load(post.getServingUrl()).into(((CustomView) viewHolder).itemImage);
        ((CustomView) viewHolder).textLabel.setText(post.getUserEmail());

        calendar.setTimeInMillis(post.getTimeInMillis());
        String d = months[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.DAY_OF_MONTH);
        ((CustomView) viewHolder).date.setText(d);

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class CustomView extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textLabel, date;
        public CardView itemCard;
        public ImageView itemImage;
        public Post item;
        public CustomView(View v) {
            super(v);
            textLabel = (TextView) v.findViewById(R.id.textLabel);
            itemCard = (CardView) v.findViewById(R.id.itemCard);
            itemImage = (ImageView) v.findViewById(R.id.itemImage);
            date = (TextView) v.findViewById(R.id.date);

            itemCard.setOnClickListener(this);
        }

        public void setPost(Post item) {
            this.item = item;
        }

        public Bundle getBundledPostDetails() {
            Bundle postDetails = new Bundle();
            postDetails.putString("userEmail", item.getUserEmail());
            postDetails.putString("servingUrl", item.getServingUrl());
            postDetails.putString("postDescription", item.getJsonDetails());
            postDetails.putLong("date", item.getTimeInMillis());
            return postDetails;
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.itemCard:
                    //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, new Pair(itemImage, "itemImage"));
                    Intent i = new Intent();
                    i.setClass(context, DetailActivity.class);
                    i.putExtra("bundledPostDetails", getBundledPostDetails());
                    context.startActivity(i);
                    break;
            }
        }
    }
}
