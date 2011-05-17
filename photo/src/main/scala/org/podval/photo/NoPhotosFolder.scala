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

package org.podval.photo


trait NoPhotosFolder[C <: Connection] extends Folder[C] {

    final override def canHavePhotos: Boolean = false


    final override def hasPhotos: Boolean = false


    final override def photos: Seq[P] = List[P]() // TODO make statisc...


    final override def addPhoto(value: P) = noPhotos


    final override def removePhoto(value: P) = noPhotos


    final override def getPhoto(name: String): Option[P] = scala.None // TODO WTF do I need to prefix this?!


    private def noPhotos = throw new PhotoException("Folder can not contain photos")


    final override def coverPhoto: Option[P] = None


    final def coverPhoto_=(value: P) = throw new PhotoException("Cover photo can not be set for a folder without photos!")
}
