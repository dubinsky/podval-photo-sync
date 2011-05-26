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


trait NoFoldersFolder[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]] extends Folder[C,F,P] {

    final override def canHaveFolders: Boolean = false


    final override def hasFolders: Boolean = false


    final override def folders: Seq[F] = List[F]()


    final override def addFolder(value: F) = noFolders


    final override def removeFolder(value: F) = noFolders


    final override def getFolder(name: String): Option[F] = None


    final override def createFolder(name: String, folderType: FolderType): F = noFolders


    private def noFolders = throw new PhotoException("Folder can not contain folders")
}
