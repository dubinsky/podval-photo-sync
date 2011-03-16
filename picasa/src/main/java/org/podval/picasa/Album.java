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
        this.album = album;
    }


    @Override
    public String getName() {
        return album.title;
    }


    private static List<Folder<PicasaThing>> EMPTY = new LinkedList<Folder<PicasaThing>>();


    @Override
    public Collection<Folder<PicasaThing>> getFolders() throws ThingsException {
        return EMPTY;
    }


    @Override
    public Folder<PicasaThing> getFolder(final String name) throws ThingsException {
        return null;
    }


    @Override
    public List<PicasaThing> getThings() throws ThingsException {
        ensureIsPopulated();
        return things;
    }


    @Override
    public PicasaThing getThing(final String name) throws ThingsException {
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
    protected void populate() throws ThingsException {
        try {
            if (album.numPhotos != 0) {
                final PicasaUrl url = new PicasaUrl(album.getFeedLink());
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
            throw new ThingsException(e);
        }
    }


    @Override
    public boolean canHaveFolders() {
        return false;
    }


    @Override
    public boolean canHaveThings() {
        return true;
    }


    @Override
    protected void checkFolderType(boolean canHaveFolders, boolean canHaveThings) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    protected Folder<PicasaThing> doCreateFolder(String name, boolean canHaveFolders, boolean canHaveThings) throws ThingsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    protected Folder<PicasaThing> doCreateFakeFolder(String name, boolean canHaveFolders, boolean canHaveThings) throws ThingsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    protected void doAddFile(String name, File file) throws ThingsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private final Picasa picasa;


    private final AlbumEntry album;


    private final List<PicasaThing> things = new LinkedList<PicasaThing>();
}
