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


trait NoPhotos extends Folder {

    override final def canHavePhotos(): Boolean = false


    override final def hasPhotos(): Boolean = false


    override final def getPhotos(): Seq[P] = photos


    override final def getPhoto(name: String): Option[P] = scala.None // TODO WTF do I need to prefix this?!


    private val photos = List[P]() // TODO make "static""!
}
