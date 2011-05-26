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

package org.podval.photo.picasa.model

import com.google.api.client.util.Key

import java.util.List

import scala.collection.JavaConversions.collectionAsScalaIterable


final class Link {

    @Key("@href")
    var href: String = null


    @Key("@rel")
    var rel: String = null
}


object Link {

    def find(links: List[Link], rel: String): String = {
        val result: Option[Link] = if (links == null) None else links.find(_.rel == rel)
        if (result.isEmpty) null else result.get.href
    }
}
