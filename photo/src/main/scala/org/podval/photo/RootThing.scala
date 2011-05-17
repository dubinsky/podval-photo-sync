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


trait RootThing[C <: Connection] extends Thing[C] {

    override val connection: C


    final override def isRoot: Boolean = true


    final override def parent: F = throw new PhotoException("Root does not have a parent!")


    final override def parent_=(value: F) = throw new PhotoException("Root does not have a parent!")


    final override def root: C#R = this.asInstanceOf[C#R]


    final override def name: String = "/"


    final override def name_=(value: String) {
        if (name != value) {
            throw new PhotoException("Can not change the name of the root!")
        }
    }


    final override def path: String = name


    final override def delete = throw new PhotoException("Root can not be deleted!")
}
