/*
 *  Copyright 2011 Leonid Dubinsky <dub@podval.org>.
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

package org.podval.photo.picasa

import org.podval.photo.{NonRootAlbum, PhotoException}

import org.podval.picasa.model.{PicasaUrl, AlbumEntry, AlbumFeed, PhotoEntry, Link}

import scala.collection.mutable.ListBuffer

import scala.collection.JavaConversions._

import java.io.IOException


final class PicasaAlbum(parentArg: PicasaFolder, private var entry: AlbumEntry) extends PicasaFolder with NonRootAlbum {

    protected val parent = parentArg


//    public Album(final Picasa picasa, final String name) {
//        this(picasa, new AlbumEntry());
//
//        this.albumEntry.title = name;
//    }


    override def name(): String = entry.title


    override def isPublic() = PicasaAlbum.access(entry.access)


    override def setPublic(value: Boolean) {
        if (isPublic() != value) {
            ensureOriginalSaved()
            entry.access = PicasaAlbum.access(value)
        }
    }


    protected override def retrievePhotos(): Seq[PicasaPhoto] = {
        val result = new ListBuffer[PicasaPhoto]()

        try {
            if ((entry != null) && (entry.numPhotos != 0)) {
                val url = new PicasaUrl(entry.getFeedLink())

                var nextUrl = url
                do {
                    val feed = AlbumFeed.executeGet(getConnection().transport, nextUrl)

                    val photos: Seq[PhotoEntry] = feed.photos
                    result ++= (photos map (new PicasaPhoto(this, _)))

                    val next = Link.find(feed.links, "next")
                    nextUrl = if (next == null) null else new PicasaUrl(next) // TODO standard function?
                } while (nextUrl != null)
            }
        } catch {
            case e: IOException => throw new PhotoException(e)
        }

        result
    }


    private def ensureOriginalSaved() {
        if (originalEntry == null) {
            originalEntry = entry.clone()
        }
    }


    override def updateIfChanged() {
        if (originalEntry != null) {
            try {
                entry.executePatchRelativeToOriginal(getConnection().transport, originalEntry)
            } catch {
                case e: IOException => throw new PhotoException(e)
            }
        }
    }


    private var originalEntry: AlbumEntry = null


//    @Override
//    protected void checkFolderType(final FolderType folderType) {
//        // TODO?
//    }
//
//
//    @Override
//    protected Album doCreateFolder(
//        final String name,
//        final FolderType folderType) throws PhotoException
//    {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//
//    @Override
//    protected Album doCreateFakeFolder(
//        final String name,
//        final FolderType folderType) throws PhotoException
//    {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//
//    @Override
//    protected void doAddFile(final String name, final File file) throws PhotoException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//
}


object PicasaAlbum {

    private val PUBLIC_ACCESS = "public"


    private val PRIVATE_ACCESS = "private"


    def access(value: Boolean): String = if (value) PUBLIC_ACCESS else PRIVATE_ACCESS


    def access(value: String): Boolean = value == PUBLIC_ACCESS
}
