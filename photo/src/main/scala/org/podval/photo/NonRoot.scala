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


trait NonRoot extends Folder {

    // TODO When I use abstract type members instead of type parameters,
    // I can't figure out a way to avoid this cast.
    // I hope that Scala type system has a way to eliminate the cast - I just
    // don't know it yet ;)
    final override def connection: C = getParentFolder.connection.asInstanceOf[C]


    final override def parent: Option[F] = Some(getParentFolder)


    final override def parent_=(value: F) {
        if (value.connection != this.connection) {
            throw new PhotoException("Can't move a folder to a different connection")
        }

        val newValue = Some (value)
        if (newValue != parentFolder) {
            if (parentFolder != null) {
                moveToParent(value);

                // TODO: remove from parentFolder
            }

            parentFolder = newValue

            // TODO: add to new parentFolder
        }
    }


    protected final def getParentFolder = {
        if (parentFolder.isEmpty) {
            throw new PhotoException("Parent has not been set after creation?")
        }

        parentFolder.get
    }


    protected def moveToParent(value: F)


    private var parentFolder: Option[F] = None


    final override def path: String = getParentFolder.path + name + "/"


    final override def delete {
        deleteFolder

        // TODO: remove from parentFolder
    }


    protected def deleteFolder


    final override def root: C#R = getParentFolder.root.asInstanceOf[C#R]
}
