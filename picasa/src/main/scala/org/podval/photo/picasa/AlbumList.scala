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

import org.podval.photo.{FolderNG, PhotoException}

import org.podval.picasa.model.{PicasaUrl, UserFeed, Link}

import scala.collection.mutable.ListBuffer

import java.io.IOException


final class AlbumList extends org.podval.photo.AlbumList with org.podval.photo.Root {

    override def name(): String = "/"


    override def retrieveFolders(): Seq[FolderNG] = {
        val result = new ListBuffer[FolderNG]()

        try {
            val url = PicasaUrl.relativeToRoot("feed/api/user/" + getConnection().getLogin());

            var nextUrl = url;
            do {
                val chunk = UserFeed.executeGet(getConnection().getTransport(), nextUrl);

                if (feed == null) {
                    feed = chunk;
                }

                val albums = chunk.albums

                if (albums != null) {
                    result ++=(albums map (new Album(getConnection(), _)))
                }

                val next = Link.find(chunk.links, "next");
                nextUrl = if (next == null) null else new PicasaUrl(next);
            } while (nextUrl != null);
        } catch {
            case e: IOException => throw new PhotoException(e)
        }

        result
    }


    private var feed: UserFeed = null
}
