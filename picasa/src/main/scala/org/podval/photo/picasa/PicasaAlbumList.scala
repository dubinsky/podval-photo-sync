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


final class PicasaAlbumList(connectionArg: Picasa) extends PicasaFolder with RootAlbumList {

    protected val connection = connectionArg


    override def name(): String = "/"


    protected override def retrieveFolders(): Seq[PicasaFolder] = {
        val result = new ListBuffer[PicasaFolder]()

        try {
            val url = PicasaUrl.relativeToRoot("feed/api/user/" + getConnection().getLogin())

            var nextUrl = url
            do {
                val chunk = UserFeed.executeGet(getConnection().transport, nextUrl)

                if (feed == null) {
                    feed = chunk
                }

                val albums: Seq[AlbumEntry] = chunk.albums

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


    private var feed: UserFeed = null
}
