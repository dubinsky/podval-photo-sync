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


trait NonRoot extends FolderNG {

    // TODO When I use abstract type members instead of type parameters,
    // I can't figure out a way to avoid this cast.
    // I hope that Scala type system has a way to eliminate the cast - I just
    // don't know it yet ;)
    override final def getConnection(): C = getParent().get.getConnection().asInstanceOf[C]


    override final def getParent(): Option[F] = Some(parent)


    protected val parent: F
}
