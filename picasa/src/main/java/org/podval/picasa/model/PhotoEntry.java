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

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.MultipartRelatedContent;
import com.google.api.client.util.Key;
import com.google.api.client.xml.atom.AtomContent;

import java.io.IOException;
import java.io.OutputStream;


/**
 * @author Yaniv Inbar
 */
public final class PhotoEntry extends Entry {

    @Key
    public Category category = Category.newKind("photo");


    @Key("media:group")
    public MediaGroup mediaGroup;


    @Key("gphoto:timestamp")
    public long timestamp;


    @Key("gphoto:size")
    public int size;


    @Key("gphoto:rotation")
    public int rotation;


    public static PhotoEntry executeInsert(
        final HttpTransport transport,
        final String albumFeedLink,
        final InputStreamContent content,
        final String fileName) throws IOException
    {
        final HttpRequest request = transport.buildPostRequest();
        request.setUrl(albumFeedLink);
//        final GoogleHeaders headers = (GoogleHeaders) request.headers;
//        headers.setSlugFromFileName(fileName);
        request.content = content;
        return request.execute().parseAs(PhotoEntry.class);
    }


//    private PhotoEntry postPhoto(final AlbumEntry album)
//        throws IOException {
//        InputStreamContent content = new InputStreamContent();
//        content.inputStream = new URL(photoUrlString).openStream();
//        content.type = "image/jpeg";
//        PhotoEntry photo = PhotoEntry.executeInsert(
//            transport, album.getFeedLink(), content, fileName);
//        return photo;
//    }


    public PhotoEntry executeInsertWithMetadata(
        final HttpTransport transport,
        final String albumFeedLink,
        final InputStreamContent content) throws IOException
    {
        final HttpRequest request = transport.buildPostRequest();
        request.setUrl(albumFeedLink);
        final AtomContent atomContent = new AtomContent();
        atomContent.namespaceDictionary = Namespaces.DICTIONARY;
        atomContent.entry = this;
        final MultipartRelatedContent multiPartContent =
            MultipartRelatedContent.forRequest(request);
        multiPartContent.parts.add(atomContent);
        multiPartContent.parts.add(content);
        request.content = multiPartContent;
        return request.execute().parseAs(PhotoEntry.class);
    }


    private static final String MEDIA_TYPE = "image/jpeg";


    public String getOriginalUrl() {
        final MediaContent content = (mediaGroup == null) ? null :
            ((mediaGroup.content == null) ? null : mediaGroup.content);

        return (content.type.equals(MEDIA_TYPE)) ? content.url : null;
    }


    public static void download(
        final HttpTransport transport,
        final String url,
        final OutputStream out) throws IOException
    {
        final HttpRequest request = transport.buildGetRequest();
        request.setUrl(url);
        InputStreamContent.copy(request.execute().getContent(), out);
    }
}
