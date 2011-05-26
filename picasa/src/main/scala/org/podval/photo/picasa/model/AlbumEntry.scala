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


final class AlbumEntry extends Entry {

    @Key("gphoto:access")
    var access: String = null


    @Key
    var category: Category = Category("album")


    @Key("gphoto:numphotos")
    var numPhotos: Int = 0


    override def clone(): AlbumEntry = super.clone().asInstanceOf[AlbumEntry]
}
