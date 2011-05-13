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

import org.podval.photo.{RootAlbumList, FolderType, PhotoException}

import org.podval.picasa.model.{PicasaUrl, UserFeed, Link}

import scala.collection.mutable.ListBuffer

import scala.collection.JavaConversions._

import java.io.IOException


final class PicasaAlbumList(override val connection: Picasa) extends PicasaFolder with RootAlbumList {

    override def public = true


    override def public_=(value: Boolean) {
        if (!value) {
            throw new PhotoException("Picasa album list is always public!")
        }
    }


    protected override def retrieveFolders(): Seq[PicasaFolder] = {
        // TODO: abstract away the control structure to read a complete feed...
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
                    val picasaAlbums = albums map (new PicasaAlbum(_))
                    picasaAlbums.foreach(_.parent = this)
                    result ++= picasaAlbums
                }

                val next = Link.find(chunk.links, "next")
                nextUrl = if (next == null) null else new PicasaUrl(next) // TODO standard function?
            } while (nextUrl != null)
        } catch {
            case e: IOException => throw new PhotoException(e)
        }

        result
    }


    protected override def doCreateFolder(name: String, folderType: FolderType): PicasaFolder = {
        // TODO check type...
        try {
            val result = new PicasaAlbum()
            result.name = name
            result.parent = this
            result.insert(feed)
            result
        } catch {
            case e: IOException => throw new PhotoException(e)
        }
    }


    private var feed: UserFeed = null
}
