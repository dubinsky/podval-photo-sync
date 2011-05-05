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

import org.podval.photo.{NonRoot, PhotoException}


final class NonRootFilesFolder(protected val parentFolder: FilesFolder, namePar: String) extends FilesFolder with NonRoot {

    nameVar = namePar


    override def name = nameVar


    override def name_=(value: String) = {
        if (nameVar != value) {
            val from = directory
            nameVar = value
            val to = directory

            if (!from.renameTo(to)) {
                throw new PhotoException("Rename failed")
            }
        }
    }


    private var nameVar: String = null
}
