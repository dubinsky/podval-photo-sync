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

import org.podval.photo.{PhotoNG, RotationNG}

import org.podval.picasa.model.PhotoEntry

import java.util.Date


final class PicasaPhoto(parentArg: PicasaAlbum, entry: PhotoEntry) extends PhotoNG {

    type F = PicasaAlbum


    protected val parent = parentArg


    override def name() = entry.title


    override def timestamp() = new Date(entry.timestamp)


    override def size() = entry.size


    override def rotation() = entry.rotation match {
        // TODO deal with null...
        case   0 => RotationNG.None
        case  90 => RotationNG.Right
        case 180 => RotationNG.R180
        case 270 => RotationNG.Left
    }
}
