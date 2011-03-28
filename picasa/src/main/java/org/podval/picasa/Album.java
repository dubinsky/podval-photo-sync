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

import org.podval.photo.Folder;
import org.podval.photo.FolderType;
import org.podval.photo.PhotoException;

import org.podval.picasa.model.PicasaUrl;
import org.podval.picasa.model.AlbumEntry;
import org.podval.picasa.model.AlbumFeed;
import org.podval.picasa.model.PhotoEntry;
import org.podval.picasa.model.Link;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import java.io.File;
import java.io.IOException;


/**
 *
 * @author dub
 */
public final class Album extends Folder<PicasaThing> {

    public Album(final Picasa picasa, final AlbumEntry album) {
        this.picasa = picasa;
        this.albumEntry = album;
    }


    public Album(final Picasa picasa, final String name) {
        this.picasa = picasa;
        this.albumEntry = new AlbumEntry();
        this.albumEntry.title = name;
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
        return FolderType.Things;
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


    private static List<Folder<PicasaThing>> EMPTY = new LinkedList<Folder<PicasaThing>>();


    @Override
    public Collection<Folder<PicasaThing>> getFolders() throws PhotoException {
        return EMPTY;
    }


    @Override
    public Folder<PicasaThing> getFolder(final String name) throws PhotoException {
        return null;
    }


    @Override
    public List<PicasaThing> getThings() throws PhotoException {
        ensureIsPopulated();
        return things;
    }


    @Override
    public PicasaThing getThing(final String name) throws PhotoException {
        PicasaThing result = null;

        for (final PicasaThing thing : getThings()) {
            if (thing.getName().equals(name)) {
                result = thing;
                break;
            }
        }

        return result;
    }


    @Override
    protected void populate() throws PhotoException {
        try {
            if ((albumEntry != null) && (albumEntry.numPhotos != 0)) {
                final PicasaUrl url = new PicasaUrl(albumEntry.getFeedLink());
                PicasaUrl nextUrl = url;

                while (nextUrl != null) {
                    final AlbumFeed feed = AlbumFeed.executeGet(picasa.getTransport(), nextUrl);
                    for (final PhotoEntry photo : feed.photos) {
                        things.add(new PicasaThing(picasa, photo));
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
    protected Folder<PicasaThing> doCreateFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    protected Folder<PicasaThing> doCreateFakeFolder(
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
                albumEntry.executePatchRelativeToOriginal(picasa.getTransport(), originalAlbumEntry);
            } catch (final IOException e) {
                throw new PhotoException(e);
            }
        }
    }


    private final Picasa picasa;


    private final AlbumEntry albumEntry;


    private AlbumEntry originalAlbumEntry;


    private final List<PicasaThing> things = new LinkedList<PicasaThing>();
}
