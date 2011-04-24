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


abstract class Connection[T](connector: Connector, descriptor: ConnectionDescriptor) {

    // TODO: check that login is present if required


    type F <: Folder


    type R = F with Root


    if (isLoginRequired && descriptor.login.isEmpty) {
        throw new PhotoException(scheme + " requires a login to be specified!")
    }

    if (!isHierarchySupported) {
        val path = descriptor.path
        if ((path != null) &&  !path.isEmpty() && !path.equals("/")) {
          // TODO not really; one step is still allowed...
            throw new PhotoException(scheme + " does not support hierarchy; path must be empty!")
        }
    }


    final def scheme: String = connector.scheme


    def isLoginRequired: Boolean


    def isHierarchySupported: Boolean


    def enableLowLevelLogging(): Unit


    final def login: String = descriptor.login.get


    val transport: T = createTransport()


    protected def createTransport(): T


    final def open() { // TODO: throws
        if (descriptor.password.isDefined) {
            login(login, descriptor.password.get)
        }

        root = Some(createRootFolder())
    }


    protected def login(login: String, password: String)


    final def realRootFolder: R = {
        if (root.isEmpty) {
            throw new PhotoException("Connection is not open - no root folder!")
        }

        root.get
    }


    private var root: Option[R] = _


    protected def createRootFolder(): R


    final def rootFolder: F =
        if (isPathToRoot)
            getSubFolderByPath(realRootFolder, descriptor.path) else
            realRootFolder


    protected def isPathToRoot: Boolean


    private final def getSubFolderByPath(folder: F, path: String): F = {
        var result = folder

        if (path != null) {
            for (name <- path.split("/")) {
                if (!name.isEmpty()) {
                  // TODO checks
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
