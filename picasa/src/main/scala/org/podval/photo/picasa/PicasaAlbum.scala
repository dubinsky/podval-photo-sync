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

import org.podval.photo.picasa.model.{PicasaUrl, UserFeed, AlbumEntry, AlbumFeed, PhotoEntry, Link}

import scala.collection.mutable.ListBuffer

import scala.collection.JavaConversions._

import java.io.IOException


final class PicasaAlbum(
    private var entry: AlbumEntry,
    private var isDetached: Boolean) // TODO: do I really need this isDetached?
extends PicasaFolder with NonRootAlbum[Picasa, PicasaFolder, PicasaPhoto] {

    def this() = this(new AlbumEntry(), true)


    def this(entry: AlbumEntry) = this(entry, false)


    override def name: String = entry.title


    override def name_=(value: String) {
        if (name != value) {
            ensureOriginalSaved()
            entry.title = name
        }
    }


    override def public = PicasaAlbum.access(entry.access)


    override def public_=(value: Boolean) {
        if (public != value) {
            ensureOriginalSaved()
            entry.access = PicasaAlbum.access(value)
        }
    }


    protected def setParent(value: PicasaFolder) {
        throw new PhotoException("Picasa folders can not be nested (bummer!)")
    }


    protected override def doDelete {
        throw new UnsupportedOperationException(); // TODO implement
    }


    override def isPersistent: Boolean = throw new UnsupportedOperationException(); // TODO implement


    protected override def doInsert {
        throw new UnsupportedOperationException(); // TODO implement
    }


    override def coverPhoto: Option[PicasaPhoto] = {
        throw new UnsupportedOperationException(); // TODO implement
    }


    protected def setCoverPhoto(value: PicasaPhoto) {
        throw new UnsupportedOperationException(); // TODO implement
    }


    protected override def retrievePhotos(): Seq[PicasaPhoto] = {
//        if ((entry != null) && (entry.numPhotos != 0)) { // TODO
        val result = Util.readFeed[AlbumFeed, PhotoEntry](
            new PicasaUrl(entry.getFeedLink()),
            (url => executeGetAlbumFeed(url)),
            (chunk => chunk.photos))

        result map (new PicasaPhoto(_))
    }


    @throws(classOf[IOException])
    private def executeGetAlbumFeed(url: PicasaUrl): AlbumFeed = {
        url.kinds = "photo"
        url.maxResults = 5
        connection.executeGetFeed(url, classOf[AlbumFeed])
    }


    private def ensureOriginalSaved() {
        // TODO only if already persistent!
        if (originalEntry == null) {
            originalEntry = entry.clone()
        }
    }


    @throws(classOf[IOException])
    def insert(feed: UserFeed) = connection.executeInsert(feed, entry).asInstanceOf[AlbumEntry]


    @throws(classOf[IOException])
    private def update() {
        if (originalEntry != null) {
            try {
                connection.executePatchEntryRelativeToOriginal(entry, originalEntry).asInstanceOf[AlbumEntry]
            } catch {
                case e: IOException => throw new PhotoException(e)
            }
        }
    }


    private var originalEntry: AlbumEntry = null
}



private object PicasaAlbum {

    private val PUBLIC_ACCESS = "public"


    private val PRIVATE_ACCESS = "private"


    def access(value: Boolean): String = if (value) PUBLIC_ACCESS else PRIVATE_ACCESS


    def access(value: String): Boolean = value == PUBLIC_ACCESS
}
