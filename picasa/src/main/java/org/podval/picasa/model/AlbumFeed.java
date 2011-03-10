/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.podval.picasa.model;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.util.List;

/**
 * @author Yaniv Inbar
 */
public class AlbumFeed extends Feed {

  @Key("entry")
  public List<PhotoEntry> photos;

  public static AlbumFeed executeGet(HttpTransport transport, PicasaUrl url)
      throws IOException {
    url.kinds = "photo";
    url.maxResults = 5;
    return (AlbumFeed) Feed.executeGet(transport, url, AlbumFeed.class);
  }
}
