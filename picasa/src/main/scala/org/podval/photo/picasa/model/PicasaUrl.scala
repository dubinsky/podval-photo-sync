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

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.util.Key;


final class PicasaUrl(url: String) extends GoogleUrl(url) {

    prettyprint = PicasaUrl.isLoggingEnabled


    @Key("max-results")
    var maxResults: java.lang.Integer = null


    @Key
    var kinds: String = null
}



object PicasaUrl {
    
    /**
     * Constructs a new Picasa Web Albums URL based on the given relative path.
     *
     * @param relativePath encoded path relative to the {@link #ROOT_URL}
     * @return new Picasa URL
     */
    def relativeToRoot(relativePath: String): PicasaUrl = new PicasaUrl(ROOT_URL + relativePath)


    val ROOT_URL = "https://picasaweb.google.com/data/"


    var isLoggingEnabled: Boolean = false
}