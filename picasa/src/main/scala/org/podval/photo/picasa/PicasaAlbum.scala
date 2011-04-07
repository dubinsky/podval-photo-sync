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


class PicasaAlbum(parentArg: PicasaFolder, entry: AlbumEntry) extends PicasaFolder with NonRootAlbum
{
    protected val parent = parentArg


    override def name(): String = entry.title


    override protected def retrievePhotos(): Seq[PicasaPhoto] = {
        val result = new ListBuffer[PicasaPhoto]()

        try {
            if ((entry != null) && (entry.numPhotos != 0)) {
                val url = new PicasaUrl(entry.getFeedLink())

                var nextUrl = url;
                do {
                    val feed = AlbumFeed.executeGet(getConnection().transport, nextUrl)

                    val photos: Seq[PhotoEntry] = feed.photos
                    result ++= (photos map (new PicasaPhoto(this, _)))

                    val next = Link.find(feed.links, "next")
                    nextUrl = if (next == null) null else new PicasaUrl(next) // TODO standard function?
                } while (nextUrl != null);
            }
        } catch {
            case e: IOException => throw new PhotoException(e)
        }

        result
    }
}
