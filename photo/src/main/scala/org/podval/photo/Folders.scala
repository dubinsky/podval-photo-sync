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


trait Folders extends Folder {

    final override def canHaveFolders: Boolean = true


    final override def hasFolders: Boolean = !folders.isEmpty


    final override def getFolder(name: String): Option[F] = folders.find(_.name == name)


    final override def folders: Seq[F] = {
        if (!isPopulated) {
            foldersList ++= retrieveFolders()
            isPopulated = true
        }

        foldersList
    }


    final override def createFolder(
        name: String,
        canHaveFolders: Boolean,
        canHavePhotos: Boolean): F = 
    {
        val result = doCreateFolder(name, canHaveFolders, canHavePhotos)
        foldersList += result
        result
    }


    private var isPopulated: Boolean = false


    protected def retrieveFolders(): Seq[F]


    protected def doCreateFolder(
        name: String,
        canHaveFolders: Boolean,
        canHavePhotos: Boolean): F


    private val foldersList: ListBuffer[F] = new ListBuffer[F]()
}
