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


trait Folder[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]] extends Thing[C,F,P] with FolderType {

    def public: Boolean


    def public_=(value: Boolean)


    def coverPhoto: Option[P]


    def coverPhoto_=(value: P)


    final def isEmpty = !hasFolders && !hasPhotos


    def hasFolders: Boolean


    // TODO tighten access rights on the add/remove Folder/Photo
    def addFolder(value: F)


    def removeFolder(value: F)


    def folders: Seq[F]


    def getFolder(name: String): Option[F]


    // TODO: do I even want it here, and not on Folders?
    def createFolder(name: String, folderType: FolderType): F


    def hasPhotos: Boolean


    def photos: Seq[P]


    def addPhoto(value: P)


    def removePhoto(value: P)


    def getPhoto(name: String): Option[P]


    final def getPhotos(id: PhotoId): Seq[P] =
        photos.filter(id.isIdentifiedBy) ++ folders.flatMap(_.getPhotos(id)).asInstanceOf[Seq[P]]
}
