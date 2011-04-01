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

import java.util.List;

import java.io.File;
import java.io.IOException;


/**
 *
 * @author dub
 */
public final class RootFolder extends Folder<Picasa, PicasaPhoto> {

    public RootFolder(final Picasa picasa) {
        super(picasa);
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
    public void setPublic(final boolean value) throws PhotoException {
        if (!value) {
            throw new PhotoException("Picasa album list is always public!");
        }
    }


    @Override
    protected void populate() throws PhotoException {
        try {
            final PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/" + getConnection().getLogin());

            PicasaUrl nextUrl = url;
            do {
                final UserFeed chunk = UserFeed.executeGet(getConnection().getTransport(), nextUrl);

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
                register(new Album(getConnection(), album));
            }
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
        final Album result;
        ensureIsPopulated();
        try {
            result = new Album(getConnection(), name);
            feed.insertAlbum(getConnection().getTransport(), result.getAlbumEntry());
        } catch (final IOException e) {
            throw new PhotoException(e);
        }

        return result;
    }


    @Override
    protected Album doCreateFakeFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        return new Album(getConnection(), name);
    }


    @Override
    protected void doAddFile(final String name, final File file) throws PhotoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public void updateIfChanged() throws PhotoException {
        // TODO
    }


    private UserFeed feed;
}
