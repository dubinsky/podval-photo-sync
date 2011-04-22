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


trait Folders extends Folder {

    override final def canHaveFolders(): Boolean = true


    override final def hasFolders(): Boolean = !getFolders().isEmpty


    override final def getFolder(name: String): Option[F] = getFolders().find(_.name == name)


    override final def getFolders(): Seq[F] = {
        if (!isPopulated) {
            folders ++= retrieveFolders()
            isPopulated = true
        }

        folders
    }


    private var isPopulated: Boolean = false


    protected def retrieveFolders(): Seq[F]


    private val folders: ListBuffer[F] = new ListBuffer[F]()
}
