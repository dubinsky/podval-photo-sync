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

import org.podval.photo.{Connector, Connection, PhotoException}
import org.podval.picasa.model.{Namespaces, PicasaUrl}

import com.google.api.client.googleapis.{GoogleTransport, GoogleHeaders}
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin
import com.google.api.client.http.{HttpTransport, HttpResponseException}
import com.google.api.client.xml.atom.AtomParser

import java.util.logging.{Logger, Level}

import java.io.IOException


final class Picasa(connector: PicasaConnector) extends Connection[HttpTransport](connector) {

    type F = PicasaFolder


    override def enableLowLevelLogging() {
        val logger = Logger.getLogger("com.google.api.client")
        logger.setLevel(Level.CONFIG);

        PicasaUrl.isLoggingEnabled = true;
    }


    override def isLoginRequired: Boolean = true


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


    @throws(classOf[PhotoException])
    protected override def login(login: String, password: String) {
        try {
            val authenticator = new ClientLogin()
            authenticator.authTokenType = "lh2" //"ndev";
            authenticator.username = login
            authenticator.password = password
            authenticator.authenticate().setAuthorizationHeader(transport)
        } catch {
            case e: HttpResponseException => throw new PhotoException(e)
            case e: IOException => throw new PhotoException(e)
        }
    }


    protected override def createRootFolder(): R = new PicasaAlbumList(this)
}



final class PicasaConnector extends Connector("picasa") {

    def connect() = new Picasa(this)
}
