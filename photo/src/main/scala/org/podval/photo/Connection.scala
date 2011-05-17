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


abstract class Connection(connector: Connector) {

    // TODO: check that login is present if required

    type T


    type C <: Connection


    type F <: Folder[C]


    final type R = F with RootFolder[C]


    type P <: Photo[C]


    final def scheme: String = connector.scheme


    def enableLowLevelLogging(): Unit


    // TODO: throws
    final def open(loginVal: Option[String], password: Option[String]) {
        if (rootFolderVar.isDefined) {
            throw new PhotoException("Connection is already open!")
        }

        loginVar = loginVal

        if (isLoginRequired && loginVal.isEmpty) {
            throw new PhotoException(scheme + " requires a login to be specified!")
        }

        transportVar = Some(createTransport())

        if (password.isDefined) {
            login(login, password.get)
        }

        rootFolderVar = Some(createRootFolder())
    }


    protected def createTransport(): T


    def transport: T = transportVar.get


    def isLoginRequired: Boolean


    final def login: String = loginVar.get


    protected def login(login: String, password: String): Unit


    protected def createRootFolder(): R


    final def rootFolder: R = {
        if (rootFolderVar.isEmpty) {
            throw new PhotoException("Connection is not open - no root folder!")
        }

        rootFolderVar.get
    }


    private var transportVar: Option[T] = None


    private var loginVar: Option[String] = None


    private var rootFolderVar: Option[R] = None
}



object Connection {

    val LOG = "org.podval.photo"
}
