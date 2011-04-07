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


trait YesPhotos extends FolderNG {

    override final def canHavePhotos(): Boolean = true


    override final def hasPhotos(): Boolean = !photos.isEmpty


    override final def getPhotos(): Seq[P] = photos


    override final def getPhoto(name: String): Option[P] = photos.find(_.name == name)


    protected final def populatePhotos() { photos ++= retrievePhotos() }


    protected def retrievePhotos(): Seq[P]


    private val photos: ListBuffer[P] = new ListBuffer[P]()
}
