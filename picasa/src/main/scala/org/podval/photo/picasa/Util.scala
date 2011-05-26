/*
 * Copyright 2011 Podval Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.podval.photo.picasa

import org.podval.photo.PhotoException

import org.podval.photo.picasa.model.{Feed, Entry, PicasaUrl, Link}

import scala.collection.mutable.ListBuffer

import java.util.List

import scala.collection.JavaConversions.collectionAsScalaIterable

import java.io.IOException


object Util {

    def readFeed[F <: Feed, E <: Entry](
        url: PicasaUrl,
        getFeed: (PicasaUrl => F),
        getEntries: (F => List[E])): Seq[E] =
    {
        try {
            val result = new ListBuffer[E]()

            var nextUrl = url
            do {
                val chunk = getFeed(nextUrl)

                val entries: List[E] = getEntries(chunk)
                if (entries != null) {
                    result ++= entries
                }

                val next = Link.find(chunk.links, "next")
                nextUrl = if (next == null) null else new PicasaUrl(next) // TODO standard function?
            } while (nextUrl != null)

            result
        } catch {
            case e: IOException => throw new PhotoException(e)
        }
    }
}
