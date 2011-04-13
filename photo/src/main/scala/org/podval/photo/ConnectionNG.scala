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


abstract class ConnectionNG {

    type F <: FolderNG


    def getScheme(): String


    def enableLowLevelLogging(): Unit


    def open() // TODO @throws


    def getRootFolder(): F


    var isReadOnly: Boolean = false


//    protected final <C extends Connection<P>> Folder<C, P> getSubFolderByPath(final Folder<C, P> folder, final String path) throws PhotoException {
//        Folder<C, P> result = folder;
//
//        if (path != null) {
//            for (final String name : path.split("/")) {
//                if (!name.isEmpty()) {
//                    result.getFolderType().checkCanHaveFolders(result);
//                    result = result.getFolder(name);
//                }
//            }
//        }
//
//        return result;
//    }
}


object ConnectionNG {

    val LOG = "org.podval.photo"
}
