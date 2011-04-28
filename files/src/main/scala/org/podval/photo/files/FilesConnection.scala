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

import org.podval.photo.{Connector, Connection, PhotoException}

import java.io.File


final class FilesConnection(connector: FilesConnector) extends Connection[File](connector) {

    type F = FilesFolder


    override def enableLowLevelLogging() {
    }


    def open(path: String, loginVal: Option[String], password: Option[String]) {
        pathVar = Some(path)
    }


    protected override def createTransport(): File = {
        if (pathVar.isEmpty) {
            throw new PhotoException("Unknown path to root. Did you call the right open()?")
        }

        new File(pathVar.get)
    }


    override def isLoginRequired: Boolean = false


    protected override def login(login: String, password: String) {
    }


    protected override def createRootFolder(): R =  new RootFilesFolder(this, transport)


    private var pathVar: Option[String] = None
}



final class FilesConnector extends Connector(FilesConnector.SCHEME) {

    override def connect() = new FilesConnection(this)
}



object FilesConnector {

    val SCHEME = "file"
}