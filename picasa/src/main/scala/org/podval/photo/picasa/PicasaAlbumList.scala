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

import org.podval.photo.picasa.model.{PicasaUrl, UserFeed, Link, AlbumEntry}

import scala.collection.mutable.ListBuffer

import scala.collection.JavaConversions._

import java.io.IOException


final class PicasaAlbumList(override val connection: Picasa) extends PicasaFolder with
    RootAlbumList[Picasa, PicasaFolder, PicasaPhoto] {

    override def public = true


    override def public_=(value: Boolean) {
        if (!value) {
            throw new PhotoException("Picasa album list is always public!")
        }
    }


    protected override def retrieveFolders(): Seq[PicasaFolder] = {
        val result = Util.readFeed[UserFeed, AlbumEntry](
            feedUrl,
            (url => executeGetUserFeed(url, 100)),
            (chunk => chunk.albums))

        
        result map (new PicasaAlbum(_))
    }


    private def feedUrl = PicasaUrl.relativeToRoot("feed/api/user/" + connection.login)


    @throws(classOf[IOException])
    private def executeGetUserFeed(url: PicasaUrl, maxResults: Int): UserFeed = {
        url.kinds = "album"
        url.maxResults = maxResults
        connection.executeGetFeed(url, classOf[UserFeed])
    }


    protected override def doCreateFolder(name: String, folderType: FolderType): PicasaFolder = {
        // TODO check type...
        try {
            val result = new PicasaAlbum()
            result.name = name
            result.parent = this
            result.insert(getFeed)
            result
        } catch {
            case e: IOException => throw new PhotoException(e)
        }
    }


    private def getFeed: UserFeed = {
        if (feed.isEmpty) {
            feed = Some(executeGetUserFeed(feedUrl, 0))
        }

        feed.get
    }


    private var feed: Option[UserFeed] = None
}
