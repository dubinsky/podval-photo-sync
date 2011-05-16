/*
 *  Copyright 2011 Leonid Dubinsky <dub@podval.org>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.podval.photo


trait NonRootThing extends Thing {

    // TODO When I use abstract type members instead of type parameters,
    // I can't figure out a way to avoid this cast.
    // I hope that Scala type system has a way to eliminate the cast - I just
    // don't know it yet ;)
    final override def connection: C = parent.connection.asInstanceOf[C]


    final override def isRoot: Boolean = false


    final override def parent: F = {
        if (parentVar.isEmpty) {
            throw new PhotoException("Parent has not been set after creation?")
        }

        parentVar.get
    }



    final override def parent_=(value: F) {
        val newValue = Some (value)
        if (newValue != parentVar) {
            if (parentVar.isDefined) {
                if (value.connection != this.connection) {
                    throw new PhotoException("Can't move a folder to a different connection")
                }

                setParent(value);

                // TODO: remove from parentFolder
            }

            parentVar = newValue

            // TODO: add to new parentFolder
        }
    }


    // TODO introduce (properly access-restricted) add/remove on the Folder; review "retrieve" (-> "populate"?)
    protected def setParent(value: F)


    private var parentVar: Option[F] = None


    final override def root: C#R = parent.root.asInstanceOf[C#R]


    final override def delete {
        doDelete

        // TODO: remove from parentFolder
    }


    protected def doDelete
}
