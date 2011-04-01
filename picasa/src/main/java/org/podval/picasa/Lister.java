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

import java.io.IOException;

import java.net.URL;


/**
 *
 * @author dub
 */
public final class Lister {

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


    private AlbumEntry getUpdatedAlbum(AlbumEntry album) throws IOException {
        album = AlbumEntry.executeGet(transport, album.getSelfLink());
/////        showAlbum(album);
        return album;
    }


    private final HttpTransport transport = null;
}
