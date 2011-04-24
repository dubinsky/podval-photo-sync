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

package org.podval.photo.picasa

import org.podval.photo.{Connector, ConnectionDescriptor, Connection, PhotoException}
import org.podval.picasa.model.{Namespaces, PicasaUrl}

import com.google.api.client.googleapis.{GoogleTransport, GoogleHeaders}
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin
import com.google.api.client.http.{HttpTransport, HttpResponseException}
import com.google.api.client.xml.atom.AtomParser

import java.util.logging.{Logger, Level}

import java.io.IOException


final class Picasa(connector: PicasaConnector, descriptor: ConnectionDescriptor)
    extends Connection[HttpTransport](connector, descriptor) {

    type F = PicasaFolder


    override def isLoginRequired: Boolean = true


    override def isHierarchySupported: Boolean = false


    override def enableLowLevelLogging() {
        val logger = Logger.getLogger("com.google.api.client")
        logger.setLevel(Level.CONFIG);

        PicasaUrl.isLoggingEnabled = true;
    }


    protected override def createTransport(): HttpTransport = {
        val result = GoogleTransport.create()

        val headers = result.defaultHeaders.asInstanceOf[GoogleHeaders]
        headers.setApplicationName("Podval-PicasaSync/1.0")
        headers.gdataVersion = "2"

        val parser = new AtomParser()
        parser.namespaceDictionary = Namespaces.DICTIONARY
        result.addParser(parser)

        result
    }


    def getLogin(): String = descriptor.login.get


    override val rootFolder: F = new PicasaAlbumList(this)


    protected override def login() {
        try {
            authenticate()
        } catch {
            case e: HttpResponseException => throw new PhotoException(e)
            case e: IOException => throw new PhotoException(e)
        }
    }


    private def authenticate() {
        val authenticator = new ClientLogin()
        authenticator.authTokenType = "lh2" //"ndev";
        authenticator.username = getLogin()
        authenticator.password = descriptor.password.get
        authenticator.authenticate().setAuthorizationHeader(transport)
    }
}



final class PicasaConnector extends Connector("picasa") {

    def connect(descriptor: ConnectionDescriptor) = new Picasa(this, descriptor)
}
