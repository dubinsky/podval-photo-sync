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


trait FoldersFolder[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]] extends Folder[C,F,P] {

    final override def canHaveFolders: Boolean = true


    final override def hasFolders: Boolean = !folders.isEmpty


    final override def addFolder(value: F) {
        // TODO check that the connection is the same
        // TODO check that this folder is already the parent
        foldersList += value
    }


    def removeFolder(value: F) {
        // TODO check that the folder is in this folder
        foldersList -= value
    }


    final override def getFolder(name: String): Option[F] = folders.find(_.name == name)


    final override def folders: Seq[F] = {
        if (!isPopulated) {
            foldersList ++= retrieveFolders()
            isPopulated = true
        }

        foldersList
    }


    final override def createFolder(name: String, folderType: FolderType): F = {
        val result = doCreateFolder(name, folderType)
        foldersList += result
        result
    }


    private var isPopulated: Boolean = false


    protected def retrieveFolders(): Seq[F]


    protected def doCreateFolder(name: String, folderType: FolderType): F


    private val foldersList: ListBuffer[F] = new ListBuffer[F]()
}
