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

package org.podval.photo.files

import org.podval.photo.{MixedFolder, FolderType, PhotoException}

import java.io.File

import scala.collection.mutable


abstract class FilesFolder extends MixedFolder[FilesConnection, FilesFolder, FilesPhoto] {

    // TODOD: tighter access?
    private[files] final def directory: File = new File(connection.transport, path)


    final override def public = true


    final override def public_=(value: Boolean) {
        if (!value) {
            throw new PhotoException("File system folders are always public!")
        }
    }


    final override def coverPhoto: Option[FilesPhoto] = {
        throw new UnsupportedOperationException(); // TODO implement
        // TODO: implement, using XML metadata
    }


    protected final override def setCoverPhoto(value: FilesPhoto) {
        throw new UnsupportedOperationException(); // TODO implement
        // TODO: implement, using XML metadata
    }


    protected final override def retrieveFolders(): Seq[FilesFolder] = {
        val result = directory.listFiles().filter(_.isDirectory).map(file => new NonRootFilesFolder(file.getName))
        result.foreach(_.parent = this)
        result
    }


    final override def doCreateFolder(name: String, folderType: FolderType): FilesFolder = {
        val subdirectory = new File(directory, name)

        if (!subdirectory.mkdir()) {
            throw new PhotoException("Failed to create folder " + name)
        }

        val result = new NonRootFilesFolder(name)
        result.parent = this

        result
    }


    protected final override def retrievePhotos(): Seq[FilesPhoto] = {
        // TODO: use grouping of tuples from a list...

        val bunches = mutable.Map.empty[String, mutable.Map[String, File]]

        directory.listFiles() filter(_.isFile) foreach(register(bunches, _))

        val result = bunches.keys.map(name => {
                val result = new FilesPhoto(Map.empty ++ bunches(name))
                result.name = name
                result
            }).toSeq

        result.foreach(_.parent = this)

        result
    }


    // TODO: introduce builder
    private def register(bunches: mutable.Map[String, mutable.Map[String, File]], file: File) {
        val (name, extension) = splitName(file)

        if (!bunches.contains(name)) {
            val newBunch= mutable.Map.empty[String, File]
            bunches += (name -> newBunch)
        }

        bunches(name) += (extension -> file)  // TODO duplicates?
    }


    // TODO: look for standard function
    private def splitName(file: File): (String, String) = {
        val filename = file.getName()
        val dot = filename.lastIndexOf('.')
        if (dot == -1) (filename, "") else (filename.substring(0, dot), filename.substring(dot+1))
    }
}
