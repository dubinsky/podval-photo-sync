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

import org.podval.photo.{RootAlbumList, PhotoException}

import org.podval.picasa.model.{PicasaUrl, UserFeed, AlbumEntry, Link}

import scala.collection.mutable.ListBuffer

import scala.collection.JavaConversions._

import java.io.IOException


final class PicasaAlbumList(override val connection: Picasa) extends PicasaFolder with RootAlbumList {

    override def name: String = "/"


    override def name_=(value: String) {
        if (name != value) {
            throw new PhotoException("Can not change the name of the root folder!")
        }
    }


    override def public = true


    override def public_=(value: Boolean) {
        if (!value) {
            throw new PhotoException("Picasa album list is always public!")
        }
    }


    protected override def retrieveFolders(): Seq[PicasaFolder] = {
        val result = new ListBuffer[PicasaFolder]()

        try {
            val url = PicasaUrl.relativeToRoot("feed/api/user/" + connection.login)

            var nextUrl = url
            do {
                val chunk = UserFeed.executeGet(transport, nextUrl)

                if (feed == null) {
                    feed = chunk
                }

                /* If 'albums' value is annotated with the Scala type Seq[AlbumEntry],
                 * implicit conversion takes place, and nullness check (next line)
                 * does not work: null is converted into a (empty?) collection...
                 */
                val albums = chunk.albums

                if (albums != null) {
                    result ++= (albums map (new PicasaAlbum(this, _)))
                }

                val next = Link.find(chunk.links, "next")
                nextUrl = if (next == null) null else new PicasaUrl(next) // TODO standard function?
            } while (nextUrl != null)
        } catch {
            case e: IOException => throw new PhotoException(e)
        }

        result
    }


    override def update() {
        // TODO
    }


    def createFolder(
        name: String,
        canHaveFolders: Boolean,
        canHavePhotos: Boolean): PicasaFolder = 
    {
        // TODO check type...
        try {
            val result = new PicasaAlbum(this)
            result.name = name
            result
        } catch {
            case e: IOException => throw new PhotoException(e)
        }
    }


    def insertAlbum(albumEntry: AlbumEntry) = feed.insertAlbum(transport, albumEntry)


    private var feed: UserFeed = null
}
