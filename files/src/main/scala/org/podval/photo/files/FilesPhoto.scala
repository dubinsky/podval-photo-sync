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

import org.podval.photo.{Photo, PhotoException}

import java.io.File

import java.util.Date


final class FilesPhoto(files: Map[String, File]) extends Photo[FilesConnection, FilesFolder, FilesPhoto] {

    protected override def setParent(value: FilesFolder) = {
        // TODO move all the files
        throw new UnsupportedOperationException() // TODO
    }


    override def name = {
        if (nameVar.isEmpty) {
            throw new PhotoException("Name not set?!")
        }

        nameVar.get
    }


    override def name_=(value: String) = {
        val newValue = Some(value)

        if (nameVar != newValue) {
            // TODO rename all the files with the name nameVar in my directory
            throw new UnsupportedOperationException() // TODO
            nameVar = newValue
        }
    }


    private var nameVar: Option[String] = None


    protected override def doDelete = {
        throw new UnsupportedOperationException() // TODO
        // TODO delete all the files!
    }


    override def timestamp = new Date(originalFile().lastModified())


    override def size = originalFile().length().toInt


    override def rotation = throw new UnsupportedOperationException("Not supported yet.") // TODO


    def originalFile() = get("jpg").get


    def get(extension: String): Option[File] = files.get(extension)


    def exists(extension: String): Boolean = get(extension).isDefined


    override def isPersistent: Boolean = {
        // TODO
        throw new UnsupportedOperationException("")
    }


    protected override def doInsert {
        // TODO
        throw new UnsupportedOperationException("")
    }
}
