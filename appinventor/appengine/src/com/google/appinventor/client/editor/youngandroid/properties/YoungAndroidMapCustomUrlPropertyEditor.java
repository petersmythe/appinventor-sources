// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2017 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.client.editor.youngandroid.properties;

import com.google.appinventor.client.widgets.properties.TextPropertyEditor;

import static com.google.appinventor.client.Ode.MESSAGES;

/**
 * Property editor for Map custom URL matching a particular format.
 */
public class YoungAndroidMapCustomUrlPropertyEditor extends TextPropertyEditor {

  public YoungAndroidMapCustomUrlPropertyEditor() {
  }

  @Override
  protected void validate(String text) throws InvalidTextException {
    if (/*!(text.startsWith("https://") || text.startsWith("http://"))*/ false
    || !text.contains("{x}")
    || !text.contains("{y}")
    || !text.contains("{z}")) {
      throw new InvalidTextException(MESSAGES.customUrlNoPlaceholders(text, "{x}, {y} and {z}"));
    }
    //Another test: replace 3x placeholders with 0, then request URL, highlight 404, 403 errors (or any non-200 status)
  }
}
