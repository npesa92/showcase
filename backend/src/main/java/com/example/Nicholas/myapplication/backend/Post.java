package com.example.Nicholas.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by Nicholas on 10/3/2015.
 */

@Entity
public class Post {

    @Id
    Long id;

    @Index
    String userEmail;

    String blobKey;
    String servingUrl;

    String jsonDetails;

    @Index
    Long timeInMillis;

    public Long getId() {
        return id;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setBlobKey(String blobKey) {
        this.blobKey = blobKey;
    }

    public String getBlobKey() {
        return blobKey;
    }

    public void setServingUrl(String servingUrl) {
        this.servingUrl = servingUrl;
    }

    public String getServingUrl() {
        return servingUrl;
    }

    public void setJsonDetails(String jsonDetails) {
        this.jsonDetails = jsonDetails;
    }

    public String getJsonDetails() {
        return jsonDetails;
    }

    public void setTimeInMillis(Long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public Long getTimeInMillis() {
        return timeInMillis;
    }

}
