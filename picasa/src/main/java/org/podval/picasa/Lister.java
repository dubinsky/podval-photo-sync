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

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;

import org.podval.picasa.model.AlbumEntry;
import org.podval.picasa.model.PhotoEntry;
import org.podval.picasa.model.UserFeed;

import java.io.File;
import java.io.IOException;

import java.net.URL;


/**
 *
 * @author dub
 */
public final class Lister {

    private AlbumEntry postAlbum(final UserFeed feed)
        throws IOException {
        System.out.println();
        AlbumEntry newAlbum = new AlbumEntry();
        newAlbum.access = "private";
        newAlbum.title = "A new album";
        newAlbum.summary = "My favorite photos";
        AlbumEntry album = feed.insertAlbum(transport, newAlbum);
/////        showAlbum(album);
        return album;
    }


    private PhotoEntry postPhoto(final AlbumEntry album)
        throws IOException {
        String fileName = "picasaweblogo-en_US.gif";
        String photoUrlString = "http://www.google.com/accounts/lh2/" + fileName;
        InputStreamContent content = new InputStreamContent();
        content.inputStream = new URL(photoUrlString).openStream();
        content.type = "image/jpeg";
        PhotoEntry photo = PhotoEntry.executeInsert(
            transport, album.getFeedLink(), content, fileName);
        System.out.println("Posted photo: " + photo.title);
        return photo;
    }


    private PhotoEntry postVideo(final AlbumEntry album)
        throws IOException {
        InputStreamContent imageContent = new InputStreamContent();
        // NOTE: this video is not included in the sample
        File file = new File("myvideo.3gp");
        imageContent.setFileInput(file);
        imageContent.type = "video/3gpp";
        PhotoEntry video = new PhotoEntry();
        video.title = file.getName();
        video.summary = "My video";
        PhotoEntry result = video.executeInsertWithMetadata(
            transport, album.getFeedLink(), imageContent);
        System.out.println("Posted video (pending processing): " + result.title);
        return result;
    }


    private AlbumEntry getUpdatedAlbum(AlbumEntry album) throws IOException {
        album = AlbumEntry.executeGet(transport, album.getSelfLink());
/////        showAlbum(album);
        return album;
    }


    private AlbumEntry updateTitle(AlbumEntry album) throws IOException {
        AlbumEntry patched = album.clone();
        patched.title = "My favorite web logos";
        album = patched.executePatchRelativeToOriginal(transport, album);
/////        showAlbum(album);
        return album;
    }


    private void deleteAlbum(final AlbumEntry album) throws IOException {
        album.executeDelete(transport);
        System.out.println();
        System.out.println("Album deleted.");
    }


    private final HttpTransport transport = null;
}
