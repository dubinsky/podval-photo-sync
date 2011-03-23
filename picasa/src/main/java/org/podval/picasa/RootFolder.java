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

import org.podval.things.Folder;
import org.podval.things.ThingsException;

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
public final class RootFolder extends Folder<PicasaThing> {

    public RootFolder(final Picasa picasa) {
        this.picasa = picasa;
    }


    @Override
    public String getName() {
        return "/";
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
    public Collection<Folder<PicasaThing>> getFolders() throws ThingsException {
        ensureIsPopulated();
        return folders;
    }


    @Override
    public Folder<PicasaThing> getFolder(final String name) throws ThingsException {
        Folder<PicasaThing> result = null;

        for (final Folder<PicasaThing> folder : getFolders()) {
            if (folder.getName().equals(name)) {
                result = folder;
                break;
            }
        }

        return result;
    }


    private static final List<PicasaThing> EMPTY = new LinkedList<PicasaThing>();


    @Override
    public List<PicasaThing> getThings() throws ThingsException {
        return EMPTY;
    }


    @Override
    public PicasaThing getThing(final String name) throws ThingsException {
        return null;
    }


    @Override
    protected void populate() throws ThingsException {
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
            throw new ThingsException(e);
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
    public boolean canHaveFolders() {
        return true;
    }


    @Override
    public boolean canHaveThings() {
        return false;
    }


    @Override
    protected void checkFolderType(boolean canHaveFolders, boolean canHaveThings) {
        // TODO?
    }


    @Override
    protected Folder<PicasaThing> doCreateFolder(
        final String name,
        final boolean canHaveFolders,
        final boolean canHaveThings) throws ThingsException
    {
        final Album result;
        ensureIsPopulated();
        try {
            result = new Album(picasa, name);
            feed.insertAlbum(picasa.getTransport(), result.getAlbumEntry());
        } catch (final IOException e) {
            throw new ThingsException(e);
        }

        return result;
    }


    @Override
    protected Folder<PicasaThing> doCreateFakeFolder(String name, boolean canHaveFolders, boolean canHaveThings) throws ThingsException {
        return new Album(picasa, name);
    }


    @Override
    protected void doAddFile(String name, File file) throws ThingsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private final Picasa picasa;


    private UserFeed feed;


    private final List<Folder<PicasaThing>> folders = new LinkedList<Folder<PicasaThing>>();
}
