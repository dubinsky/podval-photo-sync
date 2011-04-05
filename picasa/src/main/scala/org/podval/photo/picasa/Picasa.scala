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

import org.podval.photo.ConnectionNG
import org.podval.picasa.model.Namespaces

import com.google.api.client.googleapis.{GoogleTransport, GoogleHeaders}
import com.google.api.client.http.HttpTransport;
import com.google.api.client.xml.atom.AtomParser;


final class Picasa(login: String) extends ConnectionNG[Picasa, PicasaFolder, PicasaPhoto] {

    def getLogin() = login


    val transport: HttpTransport = createTransport("Podval-PicasaSync/1.0")


    private def createTransport(applicationName: String): HttpTransport = {
        val result = GoogleTransport.create()

        val headers = result.defaultHeaders.asInstanceOf[GoogleHeaders]
        headers.setApplicationName(applicationName)
        headers.gdataVersion = "2"

        val parser = new AtomParser()
        parser.namespaceDictionary = Namespaces.DICTIONARY;
        result.addParser(parser);

        result;
    }
}
