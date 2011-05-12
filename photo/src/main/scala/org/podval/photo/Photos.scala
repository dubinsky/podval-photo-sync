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

import scala.collection.mutable.ListBuffer


trait Photos extends Folder {

    override final def canHavePhotos: Boolean = true


    override final def hasPhotos: Boolean = !photos.isEmpty


    override final def getPhoto(name: String): Option[P] = photos.find(_.name == name)


    override final def photos: Seq[P] = {
        if (!isPopulated) {
            photosList ++= retrievePhotos()
            isPopulated = true
        }

        photosList
    }


    override def coverPhoto: Option[P]


    final override def coverPhoto_=(value: P) {
        if (value.parent != this) {
            throw new PhotoException("Cover photo for an album has to be in the album!")
        }

        setCoverPhoto(value)
    }


    protected def setCoverPhoto(value: P)


    private var isPopulated: Boolean = false


    protected def retrievePhotos(): Seq[P]


    private val photosList: ListBuffer[P] = new ListBuffer[P]()
}
