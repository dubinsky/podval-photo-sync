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

import scala.xml.{Elem}


trait FolderNG[C <: ConnectionNG[C, F], F <: FolderNG[C, F]] {
    
    def getConnection(): C


    def getParent(): Option[F]


    def name(): String


    def canHaveFolders(): Boolean


    def hasFolders(): Boolean


    def getFolders(): Seq[F]


    def getFolder(name: String): Option[F]


    def canHavePhotos(): Boolean


    def hasPhotos(): Boolean


    def getPhotos(): Seq[PhotoNG]


    def getPhoto(name: String): Option[PhotoNG]


    protected final def ensurePopulated() {
        if (!isPopulated) {
            populate()
            isPopulated = true
        }
    }


    protected def populate()


    private var isPopulated: Boolean = false


    def list(): Elem =
        <folder>
           <name>{name()}</name>
           {getFolders() map (_.list())}
           {getPhotos() map (_.list())}
        </folder>
}
