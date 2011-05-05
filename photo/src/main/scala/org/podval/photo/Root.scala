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


trait Root extends Folder {

    override val connection: C


    final override def parent: Option[F] = scala.None


    final override def root: C#R = this.asInstanceOf[C#R]


    final override def name: String = "/"


    final override def name_=(value: String) {
        if (name != value) {
            throw new PhotoException("Can not change the name of the root folder!")
        }
    }


    final override def path: String = name
}
