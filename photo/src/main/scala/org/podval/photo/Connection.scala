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


abstract class Connection(descriptor: ConnectionDescriptor) {

    // TODO: check that login is present if required


    type F <: Folder


    def scheme: String


    def enableLowLevelLogging(): Unit


    final def open() { // TODO: throws
        if (descriptor.password.isDefined) {
            login()
        }
    }


    protected def login()


    def rootFolder: F


    protected final def getSubFolderByPath(folder: F, path: String): F = {
        var result = folder

        if (path != null) {
            for (name <- path.split("/")) {
                if (!name.isEmpty()) {
////                    result.getFolderType().checkCanHaveFolders(result);
                    result = result.getFolder(name).get.asInstanceOf[F];
                }
            }
        }

        result
    }
}


object Connection {

    val LOG = "org.podval.photo"
}
