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

import org.podval.photo.{ConnectionFactoryNg, ConnectionNG, ConnectionDescriptorNg}

import java.io.File


final class FilesConnection(descriptor: ConnectionDescriptorNg) extends ConnectionNG {

    type F = FilesFolder


    private val rootFolder: F =  new RootFilesFolder(this, new File(descriptor.path))


    override def getScheme() = FilesConnection.SCHEME


    override def enableLowLevelLogging() {
    }


    override def open() {
    }


    override def getRootFolder(): F = rootFolder
}



object FilesConnection {

    val SCHEME = "file"
}


final class FilesFactory extends ConnectionFactoryNg {

    override def createConnection(descriptor: ConnectionDescriptorNg) = new FilesConnection(descriptor)


    override def getScheme() = FilesConnection.SCHEME
}
