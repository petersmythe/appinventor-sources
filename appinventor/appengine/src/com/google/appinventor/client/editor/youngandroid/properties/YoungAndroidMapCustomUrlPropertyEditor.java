// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2024 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.client.editor.youngandroid.properties;

import com.google.appinventor.client.widgets.properties.TextPropertyEditor;
import static com.google.appinventor.client.Ode.MESSAGES;
import com.google.gwt.http.client.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Property editor for Map custom URL matching a particular format.
 */
public class YoungAndroidMapCustomUrlPropertyEditor extends TextPropertyEditor {

  public YoungAndroidMapCustomUrlPropertyEditor() {
  }

  int responseCode;

  @Override
  protected void validate(String text) throws InvalidTextException {
    if (!(text.startsWith("https://") || text.startsWith("http://"))
        || !text.contains("{x}")
        || !text.contains("{y}")
        || !text.contains("{z}")) {
      throw new InvalidTextException(MESSAGES.customUrlNoPlaceholders(text, "{x}, {y} and {z}"));
    }

    // Try to request a single tile from the custom URL source as a final validation
    String urlString = text.replace("{x}", "0")
        .replace("{y}", "0")
        .replace("{z}", "0");

    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, urlString);
    responseCode = 0;
    CountDownLatch latch = new CountDownLatch(1);

    try {
      builder.sendRequest(null, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          responseCode = response.getStatusCode();
          latch.countDown();
        }

        @Override
        public void onError(Request request, Throwable exception) {
          responseCode = -1;
          latch.countDown();
        }
      });
    } catch (RequestException e) {
      throw new InvalidTextException(MESSAGES.customUrlException(urlString, e.getMessage()));
    }

    try {
      latch.await();
      // // Wait for the latch to be counted down (i.e., for the asynchronous request to
      // // complete)
      // if (!latch.await(20, TimeUnit.SECONDS)) {
      //   // Timeout occurred
      //   throw new InvalidTextException("Request timed out after 20 seconds");
      // }
    } catch (InterruptedException e) {
      // Ignore interruption
    }

    if (responseCode == 0) {
      throw new InvalidTextException("ResponseCode still zero " + text);
    } else if (responseCode == 401 || responseCode == 403) {
      throw new InvalidTextException(MESSAGES.customUrlBadAuthentication(urlString, responseCode));
    } else if (responseCode >= 400) {
      throw new InvalidTextException(MESSAGES.customUrlBadStatusCode(urlString, responseCode));
    } else {
      throw new InvalidTextException("ResponseCode: " + responseCode);
    }
  }
}