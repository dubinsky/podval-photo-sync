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

import java.io.IOException


object Util {

    def readFeed[F <: Feed, E <: Entry](
        url: PicasaUrl,
        getFeed: (PicasaUrl => F),
        getEntries: (F => Seq[E])): Seq[E] =
    {
        try {
            unfold[PicasaUrl, F, Seq[E]](getFeed, getEntries, nextUrl, url).flatten
        } catch {
            case e: IOException => throw new PhotoException(e)
        }
    }


    private def nextUrl(feed: Feed): Option[PicasaUrl] = {
        val next = Link.find(feed.links, "next")
        if (next == null) None else Some(new PicasaUrl(next))
    }


    def unfold[N,F,R](
        f: N => F,
        result: F => R,
        next: F => Option[N],
        seed: N): List[R] =
    {
        val feed = f(seed)

        result(feed) ::
            (next(feed) match {
                case Some(n) => unfold(f, result, next, n)
                case None => Nil
            })
    }
}
