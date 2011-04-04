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


trait NoFolders[C <: ConnectionNG[C, F], F <: FolderNG[C, F]] extends FolderNG[C, F] {

    override final def canHaveFolders(): Boolean = false


    override final def hasFolders(): Boolean = false


    override final def getFolders(): Seq[F] = folders


    override final def getFolder(name: String): Option[F] = scala.None // TODO WTF do I need to prefix this?!


    private val folders = List[F]() // TODO make "static"!
}



trait YesFolders[C <: ConnectionNG[C, F], F <: FolderNG[C, F]] extends FolderNG[C, F] {

    override final def canHaveFolders(): Boolean = true


    override final def hasFolders(): Boolean = !getFolders().isEmpty


    override final def getFolders(): Seq[F] = folders


    override final def getFolder(name: String): Option[F] = getFolders().find(_.name == name)


    protected final def populateFolders() { folders ++= retrieveFolders() }


    protected def retrieveFolders(): Seq[F]


    private val folders: ListBuffer[F] = new ListBuffer[F]()
}



trait NoPhotos[C <: ConnectionNG[C, F], F <: FolderNG[C, F]] extends FolderNG[C, F] {

    override final def canHavePhotos(): Boolean = false


    override final def hasPhotos(): Boolean = false


    override final def getPhotos(): Seq[PhotoNG] = photos


    override final def getPhoto(name: String): Option[PhotoNG] = scala.None // TODO WTF do I need to prefix this?!


    private val photos = List[PhotoNG]() // TODO make "static""!
}



trait YesPhotos[C <: ConnectionNG[C, F], F <: FolderNG[C, F]] extends FolderNG[C, F] {

    override final def canHavePhotos(): Boolean = true


    override final def hasPhotos(): Boolean = !photos.isEmpty


    override final def getPhotos(): Seq[PhotoNG] = photos


    override final def getPhoto(name: String): Option[PhotoNG] = photos.find(_.name == name)


    protected final def populatePhotos() { photos ++= retrievePhotos() }


    protected def retrievePhotos(): Seq[PhotoNG]


    private val photos: ListBuffer[PhotoNG] = new ListBuffer[PhotoNG]()
}


trait Root[C <: ConnectionNG[C, F], F <: FolderNG[C, F]] extends FolderNG[C, F] {

    override final def getConnection(): C = connection


    override final def getParent(): Option[F] = scala.None


    protected val connection: C
}



trait NotRoot[C <: ConnectionNG[C, F], F <: FolderNG[C, F]] extends FolderNG[C, F] {

    override final def getConnection(): C = getParent().get.getConnection()


    override final def getParent(): Option[F] = Some(parent)


    protected val parent: F
}
