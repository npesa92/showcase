package com.nicky.showcase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView itemImage;
    Toolbar toolbar;
    FloatingActionButton fab;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView userEmailTV, postDescriptionTV;
    CardView descriptionCard;

    Bundle bundledPostDetails;
    String userEmail, servingUrl, postDescription;
    long date;
    int baseCardColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initInstances();
        setUpPost();
    }

    public void initInstances() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        itemImage = (ImageView) findViewById(R.id.itemImage);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        userEmailTV = (TextView) findViewById(R.id.userEmail);
        postDescriptionTV = (TextView) findViewById(R.id.postDescription);
        descriptionCard = (CardView) findViewById(R.id.descriptionCard);

        collapsingToolbarLayout.setTitle("testTitle");

        baseCardColor = getResources().getColor(android.R.color.white);

        setSupportActionBar(toolbar);
        fab.setOnClickListener(this);

        Intent i = getIntent();
        bundledPostDetails = i.getBundleExtra("bundledPostDetails");

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpPost() {
        userEmail = bundledPostDetails.getString("userEmail");
        servingUrl = bundledPostDetails.getString("servingUrl");
        postDescription = bundledPostDetails.getString("postDescription");
        date = bundledPostDetails.getLong("date");

        Picasso.with(this).load(servingUrl).into(imageTarget);
        userEmailTV.setText(userEmail);

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        }
    }

    Target imageTarget = new Target() {

        @Override
        public void onPrepareLoad(Drawable drawable) {

        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            itemImage.setImageBitmap(bitmap);
            Palette.from(bitmap).generate(paletteAsyncListener);
            //descriptionCard.setBackgroundColor(p.getLightVibrantColor(baseCardColor));
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {

        }
    };

    Palette.PaletteAsyncListener paletteAsyncListener = new Palette.PaletteAsyncListener() {
        @Override
        public void onGenerated(Palette palette) {
            Palette.Swatch swatch = palette.getVibrantSwatch();
            try {
                descriptionCard.setBackgroundColor(swatch.getRgb());
                userEmailTV.setTextColor(swatch.getTitleTextColor());
                postDescriptionTV.setTextColor(swatch.getBodyTextColor());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
