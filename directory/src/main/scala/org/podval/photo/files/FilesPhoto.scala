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

import org.podval.photo.PhotoNG

import java.io.File

import java.util.Date


final class FilesPhoto(parent: FilesFolder, name: String, files: Map[String, File])
    extends PhotoNG[FilesConnection, FilesFolder, FilesPhoto](parent)
{

    override def name() = name


    override def timestamp() = new Date(getOriginalFile().lastModified())


    override def size() = getOriginalFile().length().toInt


    override def rotation() = throw new UnsupportedOperationException("Not supported yet.") // TODO


    def getOriginalFile() = get("jpg").get


    def get(extension: String): Option[File] = files.get(extension)


    def exists(extension: String): Boolean = get(extension).isDefined
}
