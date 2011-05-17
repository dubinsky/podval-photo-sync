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

import org.podval.photo.NonRootFolder

import java.io.File


final class NonRootFilesFolder(private var nameVar: String) extends FilesFolder with NonRootFolder[FilesConnection] {

    override def name = nameVar


    // TODO push this method up (in part)
    override def name_=(value: String) = {
        if (nameVar != value) {
            val from = directory
            nameVar = value
            val to = directory

            Files.rename(from, to)
        }
    }


    protected override def setParent(value: FilesFolder) = Files.rename(directory, new File(parent.directory, name))


    protected override def doDelete = Files.delete(directory)
}
