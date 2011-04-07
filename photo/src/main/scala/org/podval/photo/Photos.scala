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


trait Photos extends FolderNG {

    override final def canHavePhotos(): Boolean = true


    override final def hasPhotos(): Boolean = !photos.isEmpty


    override final def getPhoto(name: String): Option[P] = photos.find(_.name == name)


    override final def getPhotos(): Seq[P] = {
        if (!isPopulated) {
            photos ++= retrievePhotos()
            isPopulated = true
        }

        photos
    }


    private var isPopulated: Boolean = false


    protected def retrievePhotos(): Seq[P]


    private val photos: ListBuffer[P] = new ListBuffer[P]()
}
