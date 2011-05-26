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

import com.google.api.client.http.{HttpRequest, HttpRequestFactory, GenericUrl, AbstractInputStreamContent}

import com.google.api.client.util.Key

import java.io.{OutputStream, IOException}


final class PhotoEntry extends Entry {

    @Key
    var category: Category = Category("photo")


    @Key("media:group")
    var mediaGroup: MediaGroup = null


    @Key("gphoto:timestamp")
    var timestamp: Long = 0


    @Key("gphoto:size")
    var size: Int = 0


    @Key("gphoto:rotation")
    var rotation: Int = 0


    final def getOriginalUrl(): String = {
        val content: MediaContent =
            if (mediaGroup == null) null else if (mediaGroup.content == null) null else mediaGroup.content

        if (content.typeV == PhotoEntry.MEDIA_TYPE) content.url else null
    }
}



object PhotoEntry {
    
    val MEDIA_TYPE = "image/jpeg"


    @throws(classOf[IOException])
    def download(
        transport: HttpRequestFactory,
        url: String,
        out: OutputStream)
    {
        // TODO: something changed at 1.4!
        val request: HttpRequest = transport.buildGetRequest(new GenericUrl(url))
        AbstractInputStreamContent.copy(request.execute().getContent(), out)
    }
}
