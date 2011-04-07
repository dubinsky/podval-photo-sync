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

import org.podval.photo.{ConnectionNG, ConnectionDescriptor, PhotoException}
import org.podval.picasa.model.Namespaces

import com.google.api.client.googleapis.{GoogleTransport, GoogleHeaders}
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin
import com.google.api.client.http.{HttpTransport, HttpResponseException}
import com.google.api.client.xml.atom.AtomParser

import java.io.IOException


final class Picasa(descriptor: ConnectionDescriptor) extends ConnectionNG {

    type F = PicasaFolder


    private val path = descriptor.getPath
    if ((path != null) &&  !path.isEmpty() && !path.equals("/")) {
        throw new PhotoException("Picasa does not support hierarchy; path must be empty!")
    }


    def getLogin(): String = descriptor.getLogin()


    private val rootFolder: F = new PicasaAlbumList(this)


    override def getRootFolder(): F = rootFolder


    val transport: HttpTransport = createTransport("Podval-PicasaSync/1.0")


    private def createTransport(applicationName: String): HttpTransport = {
        val result = GoogleTransport.create()

        val headers = result.defaultHeaders.asInstanceOf[GoogleHeaders]
        headers.setApplicationName(applicationName)
        headers.gdataVersion = "2"

        val parser = new AtomParser()
        parser.namespaceDictionary = Namespaces.DICTIONARY
        result.addParser(parser)

        result
    }


    override def open() = if (descriptor.getPassword != null) { login() }


    private def login() {
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
        authenticator.password = descriptor.getPassword()
        authenticator.authenticate().setAuthorizationHeader(transport)
    }
}
