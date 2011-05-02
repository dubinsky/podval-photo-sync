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


trait Folder {

    type C <: Connection[_]


    type F = C#F


    type P <: Photo


    def connection: C


    def parent: Option[F]


    def root: C#R


    def name: String


    def name_=(value: String)


    // def path: String TODO: path


    def public: Boolean


    def public_=(value: Boolean)


    def canHaveFolders: Boolean


    def hasFolders: Boolean


    def folders: Seq[F]


    def getFolder(name: String): Option[F]


    def createFolder(
        name: String,
        canHaveFolders: Boolean,
        canHavePhotos: Boolean): F


    def canHavePhotos: Boolean


    def hasPhotos: Boolean


    def photos: Seq[P]


    def getPhoto(name: String): Option[P]


    final def getPhotos(id: PhotoId): Seq[P] =
        photos.filter(id.isIdentifiedBy) ++ folders.flatMap(_.getPhotos(id)).asInstanceOf[Seq[P]]
}
