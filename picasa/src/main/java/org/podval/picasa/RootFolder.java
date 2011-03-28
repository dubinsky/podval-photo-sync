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

import org.podval.picasa.model.AlbumEntry;
import org.podval.picasa.model.Link;
import org.podval.picasa.model.PicasaUrl;
import org.podval.picasa.model.UserFeed;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import java.io.File;
import java.io.IOException;


/**
 *
 * @author dub
 */
public final class RootFolder extends Folder<PicasaPhoto> {

    public RootFolder(final Picasa picasa) {
        this.picasa = picasa;
    }


    @Override
    public String getName() {
        return "/";
    }


    @Override
    public FolderType getFolderType() {
        return FolderType.Folders;
    }


    @Override
    public boolean isPublic() {
        return true;
    }


    @Override
    public void setPublic(final boolean value) {
        // TODO block setting root folder to private
    }


    @Override
    public Collection<Folder<PicasaPhoto>> getFolders() throws PhotoException {
        ensureIsPopulated();
        return folders;
    }


    @Override
    public Folder<PicasaPhoto> getFolder(final String name) throws PhotoException {
        Folder<PicasaPhoto> result = null;

        for (final Folder<PicasaPhoto> folder : getFolders()) {
            if (folder.getName().equals(name)) {
                result = folder;
                break;
            }
        }

        return result;
    }


    private static final List<PicasaPhoto> EMPTY = new LinkedList<PicasaPhoto>();


    @Override
    public List<PicasaPhoto> getPhotos() throws PhotoException {
        return EMPTY;
    }


    @Override
    public PicasaPhoto getPhoto(final String name) throws PhotoException {
        return null;
    }


    @Override
    protected void populate() throws PhotoException {
        try {
            final PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/" + picasa.getLogin());

            PicasaUrl nextUrl = url;
            do {
                final UserFeed chunk = UserFeed.executeGet(picasa.getTransport(), nextUrl);

                if (feed == null) {
                    feed = chunk;
                }

                populate(chunk.albums);

                final String next = Link.find(chunk.links, "next");
                nextUrl = (next == null) ? null : new PicasaUrl(next);
            } while (nextUrl != null);
        } catch (final IOException e) {
            throw new PhotoException(e);
        }
    }


    private void populate(final List<AlbumEntry> albums) throws IOException {
        if (albums != null) {
            for (final AlbumEntry album : albums) {
                folders.add(new Album(picasa, album));
            }
        }
    }


    @Override
    protected void checkFolderType(final FolderType folderType) {
        // TODO?
    }


    @Override
    protected Folder<PicasaPhoto> doCreateFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        final Album result;
        ensureIsPopulated();
        try {
            result = new Album(picasa, name);
            feed.insertAlbum(picasa.getTransport(), result.getAlbumEntry());
        } catch (final IOException e) {
            throw new PhotoException(e);
        }

        return result;
    }


    @Override
    protected Folder<PicasaPhoto> doCreateFakeFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        return new Album(picasa, name);
    }


    @Override
    protected void doAddFile(final String name, final File file) throws PhotoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public void updateIfChanged() throws PhotoException {
        // TODO
    }


    private final Picasa picasa;


    private UserFeed feed;


    private final List<Folder<PicasaPhoto>> folders = new LinkedList<Folder<PicasaPhoto>>();
}
