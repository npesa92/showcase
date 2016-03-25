/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-08-03 17:34:38 UTC)
 * on 2015-11-12 at 19:40:50 UTC 
 * Modify at your own risk.
 */

package com.example.nicholas.myapplication.backend.postApi.model;

/**
 * Model definition for Post.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the postApi. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Post extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String blobKey;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String jsonDetails;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String servingUrl;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long timeInMillis;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String userEmail;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getBlobKey() {
    return blobKey;
  }

  /**
   * @param blobKey blobKey or {@code null} for none
   */
  public Post setBlobKey(java.lang.String blobKey) {
    this.blobKey = blobKey;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Post setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getJsonDetails() {
    return jsonDetails;
  }

  /**
   * @param jsonDetails jsonDetails or {@code null} for none
   */
  public Post setJsonDetails(java.lang.String jsonDetails) {
    this.jsonDetails = jsonDetails;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getServingUrl() {
    return servingUrl;
  }

  /**
   * @param servingUrl servingUrl or {@code null} for none
   */
  public Post setServingUrl(java.lang.String servingUrl) {
    this.servingUrl = servingUrl;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getTimeInMillis() {
    return timeInMillis;
  }

  /**
   * @param timeInMillis timeInMillis or {@code null} for none
   */
  public Post setTimeInMillis(java.lang.Long timeInMillis) {
    this.timeInMillis = timeInMillis;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUserEmail() {
    return userEmail;
  }

  /**
   * @param userEmail userEmail or {@code null} for none
   */
  public Post setUserEmail(java.lang.String userEmail) {
    this.userEmail = userEmail;
    return this;
  }

  @Override
  public Post set(String fieldName, Object value) {
    return (Post) super.set(fieldName, value);
  }

  @Override
  public Post clone() {
    return (Post) super.clone();
  }

}