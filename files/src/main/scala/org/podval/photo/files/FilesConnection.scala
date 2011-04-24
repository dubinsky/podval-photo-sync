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

package org.podval.photo.files

import org.podval.photo.{Connector, ConnectionDescriptor, Connection}

import java.io.File


final class FilesConnection(connector: FilesConnector, descriptor: ConnectionDescriptor)
    extends Connection[File](connector, descriptor) {

    type F = FilesFolder


    override def isLoginRequired: Boolean = false


    override def isHierarchySupported: Boolean = true


    override def enableLowLevelLogging() {
    }


    protected override def createTransport(): File = new File(descriptor.path)


    protected override def login(login: String, password: String) {
    }


    protected override def createRootFolder(): R =  new RootFilesFolder(this, transport)


    protected override def isPathToRoot: Boolean = false
}



final class FilesConnector extends Connector(FilesConnector.SCHEME) {

    override def connect(descriptor: ConnectionDescriptor) = new FilesConnection(this, descriptor)
}



object FilesConnector {

    val SCHEME = "file"
}