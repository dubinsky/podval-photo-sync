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


/**
 * @author Yaniv Inbar
 */
public final class AlbumEntry extends Entry {

    @Key("gphoto:access")
    public String access;


    @Key
    public Category category = Category.newKind("album");


    @Key("gphoto:numphotos")
    public int numPhotos;


    @Override
    public AlbumEntry clone() {
        return (AlbumEntry) super.clone();
    }


    public static AlbumEntry executeGet(
        final HttpTransport transport,
        final String link) throws IOException
    {
        final PicasaUrl url = new PicasaUrl(link);
        return (AlbumEntry) Entry.executeGet(transport, url, AlbumEntry.class);
    }


    public AlbumEntry executePatchRelativeToOriginal(
        final HttpTransport transport,
        final AlbumEntry original) throws IOException
    {
        return (AlbumEntry) super.executePatchRelativeToOriginal(transport, original);
    }
}
