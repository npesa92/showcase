package com.nicky.showcase;


import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.example.nicholas.myapplication.backend.postApi.PostApi;
import com.example.nicholas.myapplication.backend.postApi.model.CollectionResponsePost;
import com.example.nicholas.myapplication.backend.postApi.model.Post;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Nicholas on 10/9/2015.
 */
public class NetworkUtil {

    private static final String UPLOAD_URL = "http://showcasebackend.appspot.com/blob/upload";
    private static final String DELETE_URL = "http://showcasebackend.appspot.com/blob/delete?blobString=";

    HttpClient httpClient;
    PostApi service;
    GoogleAccountCredential credential;

    OnNetworkUtilInteraction listener;

    Context context;

    public NetworkUtil(GoogleAccountCredential credential, OnNetworkUtilInteraction listener) {
        httpClient = new DefaultHttpClient();
        this.credential = credential;
        service = AppConstants.getApiHandle(credential);
        this.listener = listener;
    }

    public void blobAndStorePost(String jsonPostDetails) {
        new BlobAndStoreAsync().execute(jsonPostDetails);
    }

    public void deletePost(Long postId) {
        new DeleteAsync().execute(postId);
    }

    public void getPostsList() {
        new GetPostsListAsync().execute();
    }

    private class BlobAndStoreAsync extends AsyncTask<String, Void, Post> {
        String userEmail, postDescription, imagePath, blobKey, servingUrl;
        long timeInMillis;

        @Override
        public Post doInBackground(String... params) {
            try {
                JSONObject jsonDetails = new JSONObject(params[0]);
                userEmail = jsonDetails.getString("userEmail");
                postDescription = jsonDetails.getString("postDescription");
                imagePath = jsonDetails.getString("imagePath");
                timeInMillis = jsonDetails.getLong("timeInMillis");

                String uploadUrl = getUploadUrl();
                String uploadDetails = addPOST(imagePath, uploadUrl);
                jsonDetails = new JSONObject(uploadDetails);
                blobKey = jsonDetails.getString("blobKey");
                servingUrl = jsonDetails.getString("servingUrl");

                Post post = getTempPostForUpload(userEmail, postDescription, blobKey, servingUrl, timeInMillis);
                return service.insert(post).execute();


            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(Post result) {
            listener.blobAndStoreGettingResponse(result);
        }
    }

    private class DeleteAsync extends AsyncTask<Long, Void, Boolean> {

        @Override
        public Boolean doInBackground(Long... params) {
            return null;
        }

        @Override
        public void onPostExecute(Boolean result) {

        }
    }

    private class GetPostsListAsync extends AsyncTask<Void, Void, CollectionResponsePost> {

        @Override
        public CollectionResponsePost doInBackground(Void... params) {
            try {
                return service.list().execute();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(CollectionResponsePost result) {
            listener.fetchingListGettingResponse((ArrayList)result.getItems());
        }
    }

    private Post getTempPostForUpload(String userEmail, String postDescription,
                                      String blobKey, String servingUrl, long timeInMillis) {
        Post result =  new Post();
        result.setUserEmail(userEmail);
        result.setJsonDetails(postDescription);
        result.setBlobKey(blobKey);
        result.setServingUrl(servingUrl);
        result.setTimeInMillis(timeInMillis);
        return result;
    }

    public interface OnNetworkUtilInteraction {
        void blobAndStoreGettingResponse(Post result);
        void deletePostGettingResponse(Boolean result);
        void fetchingListGettingResponse(@Nullable ArrayList<Post> result);
    }

    private String getUploadUrl() {
        HttpGet httpGet = new HttpGet(UPLOAD_URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            InputStream input = response.getEntity().getContent();
            return convertStreamToString(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String addPOST(String imagePath, String uploadUrl) {
        HttpPost httpPost = new HttpPost(uploadUrl);

        File f = new File(imagePath);
        FileBody fileBody = new FileBody(f);

        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("myFile", fileBody);

        httpPost.setEntity(reqEntity);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            InputStream iStream = response.getEntity().getContent();
            return convertStreamToString(iStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private String deleteGET(String blobKey) {
        HttpGet httpGet = new HttpGet(DELETE_URL + blobKey);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            InputStream iStream = response.getEntity().getContent();
            return convertStreamToString(iStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    public String convertStreamToString(InputStream iStream) {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream));
            String result = "";
            String line = "";
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
