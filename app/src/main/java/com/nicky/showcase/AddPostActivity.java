package com.nicky.showcase;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nicholas.myapplication.backend.postApi.model.Post;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AddPostActivity extends AppCompatActivity implements NetworkUtil.OnNetworkUtilInteraction, View.OnClickListener {

    private int FAB_RIGHT_MARGIN;
    private int FAB_HALF_HEIGHT;

    Toolbar toolbar;
    EditText postDescription;
    RecyclerView imageSelectorView;
    TextView tempHolder;
    ImageView selectedImageView;
    FloatingActionButton imagesListFab;
    CoordinatorLayout rootLayout;

    String selectedImagePath, userEmail;
    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    final int PERMISSION_RESULT = 2;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    boolean isImagesListShown;

    Context context;

    NetworkUtil backendUtil;
    GoogleAccountCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        initInstances();

        prefs = getSharedPreferences("USER_PREFS", 0);
        edit = prefs.edit();
        userEmail = prefs.getString("userEmail", "");

        isImagesListShown = false;
        context = this;

        initNetworkUtil();
    }

    private void initInstances() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        postDescription = (EditText) findViewById(R.id.postDescription);
        imageSelectorView = (RecyclerView) findViewById(R.id.imageSelectorView);
        tempHolder = (TextView) findViewById(R.id.tempHolder);
        selectedImageView = (ImageView) findViewById(R.id.selectedImageView);
        imagesListFab = (FloatingActionButton) findViewById(R.id.imagesListFab);
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        toolbar.setTitle("Compose");

        GridLayoutManager gm = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        imageSelectorView.setLayoutManager(gm);

        imagesListFab.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);

        FAB_HALF_HEIGHT = (int) getResources().getDimension(R.dimen.fab_half_height);
        FAB_RIGHT_MARGIN = (int) getResources().getDimension(R.dimen.fab_margin);

        checkExternalStoragePermissions();
    }

    private void initNetworkUtil() {
        if (Strings.isNullOrEmpty(userEmail)) {
            Snackbar.make(rootLayout, "Please go back and Log-in", Snackbar.LENGTH_LONG).show();
        } else {
            credential = GoogleAccountCredential.usingAudience(this, AppConstants.AUDIENCE);
            credential.setSelectedAccountName(userEmail);
            backendUtil = new NetworkUtil(credential, this);
        }
    }

    private void checkExternalStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_RESULT);
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_RESULT);
            }*/
        } else {
            populateImageSelector();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_RESULT:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateImageSelector();
                }
        }
    }

    private void populateImageSelector() {
        ArrayList<String> deviceImages = (ArrayList) getCameraImagesFull(this);
        ImageSelectorAdapter adapter = new ImageSelectorAdapter(deviceImages, this);
        imageSelectorView.setAdapter(adapter);
    }

    public List<String> getCameraImagesFull(Context context) {
        Cursor cursor = null;
        String[] star = {"*"};
        ArrayList<String> result = new ArrayList<>();
        cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, star, null, null, MediaStore.MediaColumns.DATE_ADDED + " DESC");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    result.add(path);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return result;
    }

    public void setSelectedImageFromAdapter(String imagePath) {
        this.selectedImagePath = imagePath;
        Picasso.with(this).load(Uri.fromFile(new File(imagePath))).into(selectedImageView);
    }

    private String jsonPostFieldsForUpload() {
        JSONObject details = new JSONObject();
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        try {
            details.put("userEmail", userEmail);
            details.put("postDescription", postDescription.getText().toString());
            details.put("imagePath", selectedImagePath);
            details.put("timeInMillis", cal.getTimeInMillis());
            return details.toString();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.send_post) {
            if (Strings.isNullOrEmpty(selectedImagePath)) {
                showSnackBar("Select image before posting");
            } else if (Strings.isNullOrEmpty(postDescription.getText().toString())) {
                showSnackBar("Write description before posting");
            } else if (!Strings.isNullOrEmpty(selectedImagePath) &&
                    !Strings.isNullOrEmpty(postDescription.getText().toString())) {
                backendUtil.blobAndStorePost(jsonPostFieldsForUpload());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.imagesListFab:
                showAndHideImagesList();
                break;
        }
    }

    @Override
    public void blobAndStoreGettingResponse(Post result) {
        if (result != null) {
            savePostToPrefsAfterUpload(result);
            showSnackBar("Post Uploaded");
        } else {
            showSnackBar("Post error");
        }
    }
    @Override
    public void deletePostGettingResponse(Boolean result) {

    }
    @Override
    public void fetchingListGettingResponse(ArrayList<Post> result) {
        //NOT USED HERE EVER
    }

    private void savePostToPrefsAfterUpload(Post result) {
        edit.putLong("postId", result.getId());
        edit.putString("postDescription", result.getJsonDetails());
        edit.putString("blobKey", result.getBlobKey());
        edit.putString("servingUrl", result.getServingUrl());
        edit.putLong("timeInMillis", result.getTimeInMillis());
        edit.commit();
        showSnackBar("Post Not Null, Info Saved");
    }

    private void showSnackBar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showAndHideImagesList() {
        if (!isImagesListShown) {
            imagesListFab.animate().translationY(-(imageSelectorView.getHeight() - FAB_HALF_HEIGHT))
                    .rotation(180).setListener(rollUpListener);
            isImagesListShown = true;
        } else if (isImagesListShown) {
            imagesListFab.animate().translationY(0)
                    .rotation(0).setListener(rollUpListener);
            isImagesListShown = false;
        }
    }

    Animator.AnimatorListener rollUpListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            Animation s = isImagesListShown ? AnimationUtils.loadAnimation(context, R.anim.roll_up)
                    : AnimationUtils.loadAnimation(context, R.anim.roll_down);
            imageSelectorView.startAnimation(s);
            int visibility = isImagesListShown ? View.VISIBLE : View.INVISIBLE;
            imageSelectorView.setVisibility(visibility);
        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private class ImageSelectorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<String> dataSet;
        private Context context;

        public ImageSelectorAdapter(ArrayList<String> dataSet, Context context) {
            this.dataSet = dataSet;
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_selector_layout, parent, false);
            return new ImageSelectorView(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String imagePath = dataSet.get(position);
            ((ImageSelectorView)holder).setSelectedImagePath(imagePath);
            Picasso.with(context).load(Uri.fromFile(new File(imagePath))).resize(512, 384).centerCrop().into(((ImageSelectorView) holder).image);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        public class ImageSelectorView extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView image, checkbox;
            public String imagePath;

            boolean isImageChecked;

            public ImageSelectorView(View v) {
                super(v);
                image = (ImageView) v.findViewById(R.id.image);
                checkbox = (ImageView) v.findViewById(R.id.checkbox);

                checkbox.setVisibility(View.INVISIBLE);
                isImageChecked = false;

                v.setOnClickListener(this);
            }

            public void setSelectedImagePath(String imagePath) {
                this.imagePath = imagePath;
            }
            public String getSelectedImagePath() {
                return imagePath;
            }

            @Override
            public void onClick(View v) {
                setSelectedImageFromAdapter(imagePath);
                /*if (!isImageChecked) {
                    checkbox.setVisibility(View.VISIBLE);
                    setSelectedImagePathFromAdapter(this);
                    isImageChecked = true;
                } else {
                    checkbox.setVisibility(View.INVISIBLE);
                    clearSelectedImagePathFromAdapter();
                    isImageChecked = false;
                }*/

            }
        }
    }

    /*public void setSelectedImagePathFromAdapter(ImageSelectorAdapter.ImageSelectorView currentHolder) {
        if (previouslyCheckedViewHolder != null) {
            previouslyCheckedViewHolder.checkbox.setVisibility(View.INVISIBLE);
            previouslyCheckedViewHolder.isImageChecked = false;
            this.selectedImagePath = currentHolder.getSelectedImagePath();
            tempHolder.setText(selectedImagePath);
            previouslyCheckedViewHolder = currentHolder;
        } else {
            this.selectedImagePath = currentHolder.getSelectedImagePath();
            tempHolder.setText(selectedImagePath);
            previouslyCheckedViewHolder = currentHolder;
        }
    }

    private void clearSelectedImagePathFromAdapter() {
        this.selectedImagePath = "";
        tempHolder.setText(selectedImagePath);
        previouslyCheckedViewHolder = null;
    }*/
}
