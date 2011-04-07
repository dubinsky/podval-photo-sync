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

import org.podval.photo.Mix

import java.io.File

import scala.collection.mutable


abstract class FilesFolder(directory: File) extends Mix {

    type C = FilesConnection


    type F = FilesFolder


    type P = FilesPhoto


    if (!directory.exists()) {
        throw new IllegalArgumentException("Does not exist: " + directory);
    }

    if (!directory.isDirectory()) {
        throw new IllegalArgumentException("Not a directory: " + directory);
    }


    override def name() = directory.getName()


    protected final override def retrieveFolders(): Seq[FilesFolder] = {
        directory.listFiles() filter(_.isDirectory) map(file => new NonRootFilesFolder(this, file))
    }


    protected final override def retrievePhotos(): Seq[FilesPhoto] = {
        val bunches = mutable.Map.empty[String, mutable.Map[String, File]]

        directory.listFiles() filter(_.isFile) foreach(register(bunches, _))

        bunches.keys.map(name => new FilesPhoto(this, name, Map.empty ++ bunches(name))).toSeq
    }


    private def register(bunches: mutable.Map[String, mutable.Map[String, File]], file: File) {
        val (name, extension) = splitName(file)

        if (!bunches.contains(name)) {
            val newBunch= mutable.Map.empty[String, File]
            bunches += (name -> newBunch)
        }

        bunches(name) += (extension -> file)  // TODO duplicates?
    }


    private def splitName(file: File): (String, String) = {
        val filename = file.getName()
        val dot = filename.lastIndexOf('.')
        if (dot == -1) (filename, "") else (filename.substring(0, dot), filename.substring(dot+1))
    }
}
