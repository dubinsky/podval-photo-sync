/*
 *  Copyright 2011 Leonid Dubinsky <dub@podval.org>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.podval.photo


trait AlbumList[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]] extends FoldersFolder[C,F,P] with NoPhotosFolder[C,F,P]


trait Album[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]] extends NoFoldersFolder[C,F,P] with PhotosFolder[C,F,P]


trait MixedFolder[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]] extends FoldersFolder[C,F,P] with PhotosFolder[C,F,P]


trait RootFolder[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]] extends Folder[C,F,P] with RootThing[C,F,P]


trait NonRootAlbum[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]] extends Album[C,F,P] with NonRootFolder[C,F,P]


trait RootAlbumList[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]] extends AlbumList[C,F,P] with RootFolder[C,F,P]


trait NonRootAlbumList[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]] extends AlbumList[C,F,P] with NonRootFolder[C,F,P]
