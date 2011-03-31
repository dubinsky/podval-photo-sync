/*
 *  Copyright 2011 Leonid Dubinsky dub@podval.org.
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

import org.podval.photo.Folder;
import org.podval.photo.FolderType;
import org.podval.photo.PhotoException;

import org.podval.picasa.model.PicasaUrl;
import org.podval.picasa.model.AlbumEntry;
import org.podval.picasa.model.AlbumFeed;
import org.podval.picasa.model.PhotoEntry;
import org.podval.picasa.model.Link;

import java.io.File;
import java.io.IOException;


/**
 *
 * @author Leonid Dubinsky dub@podval.org
 */
public final class Album extends Folder<Picasa, PicasaPhoto> {

    public Album(final Picasa picasa, final String name) {
        this(picasa, new AlbumEntry());

        this.albumEntry.title = name;
    }


    public Album(final Picasa picasa, final AlbumEntry album) {
        super(picasa);

        this.albumEntry = album;
    }


    /* package */ AlbumEntry getAlbumEntry() {
        return albumEntry;
    }


    @Override
    public String getName() {
        return albumEntry.title;
    }


    @Override
    public FolderType getFolderType() {
        return FolderType.Photos;
    }


    private static final String PUBLIC_ACCESS = "public";


    private static final String PRIVATE_ACCESS = "private";


    @Override
    public boolean isPublic() {
        return albumEntry.access.equals(PUBLIC_ACCESS);
    }


    @Override
    public void setPublic(final boolean value) {
        if (isPublic() != value) {
            ensureOriginalSaved();
            albumEntry.access = (value) ? PUBLIC_ACCESS : PRIVATE_ACCESS;
        }
    }


    @Override
    protected void populate() throws PhotoException {
        try {
            if ((albumEntry != null) && (albumEntry.numPhotos != 0)) {
                final PicasaUrl url = new PicasaUrl(albumEntry.getFeedLink());
                PicasaUrl nextUrl = url;

                while (nextUrl != null) {
                    final AlbumFeed feed = AlbumFeed.executeGet(getConnection().getTransport(), nextUrl);
                    for (final PhotoEntry photo : feed.photos) {
                        register(new PicasaPhoto(this, photo));
                    }

                    final String next = Link.find(feed.links, "next");
                    nextUrl = (next == null) ? null : new PicasaUrl(next);
                }
            }
        } catch (final IOException e) {
            throw new PhotoException(e);
        }
    }


    @Override
    protected void checkFolderType(final FolderType folderType) {
        // TODO?
    }


    @Override
    protected Album doCreateFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    protected Album doCreateFakeFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    protected void doAddFile(final String name, final File file) throws PhotoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private void ensureOriginalSaved() {
        if (originalAlbumEntry == null) {
            originalAlbumEntry = albumEntry.clone();
        }
    }


    @Override
    public void updateIfChanged() throws PhotoException {
        if (originalAlbumEntry != null) {
            try {
                albumEntry.executePatchRelativeToOriginal(getConnection().getTransport(), originalAlbumEntry);
            } catch (final IOException e) {
                throw new PhotoException(e);
            }
        }
    }


    private final AlbumEntry albumEntry;


    private AlbumEntry originalAlbumEntry;
}
