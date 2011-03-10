/*
 *  Copyright 2011 dub.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.podval.picasa;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
//import com.google.api.client.http.InputStreamContent;
import com.google.api.client.xml.atom.AtomParser;

import org.podval.picasa.model.AlbumEntry;
import org.podval.picasa.model.AlbumFeed;
import org.podval.picasa.model.PhotoEntry;
import org.podval.picasa.model.PicasaUrl;
import org.podval.picasa.model.UserFeed;
import org.podval.picasa.model.Util;

//import java.io.File;
import java.io.IOException;
//import java.net.URL;


/**
 *
 * @author dub
 */
public class Main {

    public static void main(final String[] args) throws IOException {
        Util.enableLogging();

        try {
            final HttpTransport transport = createTransport();
            authenticate(transport);
            final UserFeed feed = showAlbums(transport);
//            AlbumEntry album = postAlbum(transport, feed);
//            postPhoto(transport, album);
//            // postVideo(transport, album);
//            album = getUpdatedAlbum(transport, album);
//            album = updateTitle(transport, album);
//            deleteAlbum(transport, album);
        } catch (final HttpResponseException e) {
            System.err.println(e.response.parseAsString());
            throw e;
        }
    }


    private static HttpTransport createTransport() {
        final HttpTransport result = GoogleTransport.create();
        final GoogleHeaders headers = (GoogleHeaders) result.defaultHeaders;
        headers.setApplicationName("Podval-PicasaSync/1.0");
        headers.gdataVersion = "2";
        final AtomParser parser = new AtomParser();
        parser.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
        result.addParser(parser);
        return result;
    }


    private static void authenticate(final HttpTransport transport) throws HttpResponseException, IOException {
        final ClientLogin authenticator = new ClientLogin();
        authenticator.authTokenType = "ndev";
        authenticator.username = "...";
        authenticator.password = "...";
        authenticator.authenticate().setAuthorizationHeader(transport);
    }


    private static UserFeed showAlbums(final HttpTransport transport) throws IOException {
        // build URL for the default user feed of albums
        final PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
        // execute GData request for the feed
        final UserFeed feed = UserFeed.executeGet(transport, url);
        System.out.println("User: " + feed.author.name);
        System.out.println("Total number of albums: " + feed.totalResults);
        // show albums
        if (feed.albums != null) {
            for (final AlbumEntry album : feed.albums) {
                showAlbum(transport, album);
            }
        }
        return feed;
    }


    private static void showAlbum(final HttpTransport transport, final AlbumEntry album)  throws IOException {
        System.out.println();
        System.out.println("-----------------------------------------------");
        System.out.println("Album title: " + album.title);
        System.out.println("Updated: " + album.updated);
        System.out.println("Album ETag: " + album.etag);
        if (album.summary != null) {
            System.out.println("Description: " + album.summary);
        }
        if (album.numPhotos != 0) {
            System.out.println("Total number of photos: " + album.numPhotos);
            final PicasaUrl url = new PicasaUrl(album.getFeedLink());
            final AlbumFeed feed = AlbumFeed.executeGet(transport, url);
            for (final PhotoEntry photo : feed.photos) {
                System.out.println();
                System.out.println("Photo title: " + photo.title);
                if (photo.summary != null) {
                    System.out.println("Photo description: " + photo.summary);
                }
                System.out.println("Image MIME type: " + photo.mediaGroup.content.type);
                System.out.println("Image URL: " + photo.mediaGroup.content.url);
            }
        }
    }


//    private static AlbumEntry postAlbum(HttpTransport transport, UserFeed feed)
//        throws IOException {
//        System.out.println();
//        AlbumEntry newAlbum = new AlbumEntry();
//        newAlbum.access = "private";
//        newAlbum.title = "A new album";
//        newAlbum.summary = "My favorite photos";
//        AlbumEntry album = feed.insertAlbum(transport, newAlbum);
//        showAlbum(transport, album);
//        return album;
//    }
//
//
//    private static PhotoEntry postPhoto(HttpTransport transport, AlbumEntry album)
//        throws IOException {
//        String fileName = "picasaweblogo-en_US.gif";
//        String photoUrlString = "http://www.google.com/accounts/lh2/" + fileName;
//        InputStreamContent content = new InputStreamContent();
//        content.inputStream = new URL(photoUrlString).openStream();
//        content.type = "image/jpeg";
//        PhotoEntry photo = PhotoEntry.executeInsert(
//            transport, album.getFeedLink(), content, fileName);
//        System.out.println("Posted photo: " + photo.title);
//        return photo;
//    }
//
//
//    private static PhotoEntry postVideo(HttpTransport transport, AlbumEntry album)
//        throws IOException {
//        InputStreamContent imageContent = new InputStreamContent();
//        // NOTE: this video is not included in the sample
//        File file = new File("myvideo.3gp");
//        imageContent.setFileInput(file);
//        imageContent.type = "video/3gpp";
//        PhotoEntry video = new PhotoEntry();
//        video.title = file.getName();
//        video.summary = "My video";
//        PhotoEntry result = video.executeInsertWithMetadata(
//            transport, album.getFeedLink(), imageContent);
//        System.out.println("Posted video (pending processing): " + result.title);
//        return result;
//    }
//
//
//    private static AlbumEntry getUpdatedAlbum(
//        HttpTransport transport, AlbumEntry album) throws IOException {
//        album = AlbumEntry.executeGet(transport, album.getSelfLink());
//        showAlbum(transport, album);
//        return album;
//    }
//
//
//    private static AlbumEntry updateTitle(
//        HttpTransport transport, AlbumEntry album) throws IOException {
//        AlbumEntry patched = album.clone();
//        patched.title = "My favorite web logos";
//        album = patched.executePatchRelativeToOriginal(transport, album);
//        showAlbum(transport, album);
//        return album;
//    }
//
//
//    private static void deleteAlbum(HttpTransport transport, AlbumEntry album)
//        throws IOException {
//        album.executeDelete(transport);
//        System.out.println();
//        System.out.println("Album deleted.");
//    }
}
