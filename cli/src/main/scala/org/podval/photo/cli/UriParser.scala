/*
 * Copyright 2011 Podval Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.podval.photo.cli

import org.podval.photo.ConnectionDescriptor
import org.podval.photo.files.FilesConnector

import org.kohsuke.args4j.CmdLineException

import java.net.{URI, URISyntaxException}


object UriParser {

    @throws(classOf[CmdLineException])
    def fromUri(uriStr: String, suffix: String): ConnectionDescriptor = {
        val uri: URI = toUri(uriStr)

        val (login: Option[String], password: Option[String]) = splitUserInfo(getUserInfo(uri))

        new ConnectionDescriptor(
            defaultScheme(uri.getScheme()),
            login,
            password,
            uri.getHost(),
            addSuffix(uri.getPath(), suffix));
    }


    private def toUri(uriStr: String) = {
        try {
            new URI(uriStr)
        } catch {
            case e: URISyntaxException =>  throw new CmdLineException(e.getMessage())
        }
    }


    private def getUserInfo(uri: URI): String = {
        val userInfo = uri.getUserInfo()
        val host = uri.getHost()
        val authority = uri.getAuthority()

        if ((host == null) && (authority != null)) {
            if (authority.endsWith("@")) {
                authority.substring(0, authority.length()-1)
            } else {
                authority
            }
        } else {
            userInfo;
        }
    }


    // TODO look for standard function
    private def splitUserInfo(userInfo: String): (Option[String], Option[String]) = {
        if (userInfo == null) {
            (None, None)
        } else {
            val colon = userInfo.indexOf(':')
            if (colon == -1) {
                (Some(userInfo), None)
            } else {
                (Some(userInfo.substring(0, colon)), Some(userInfo.substring(colon+1)))
            }
        }
    }


    private def defaultScheme(scheme: String): String =
        if (scheme == null) FilesConnector.SCHEME else scheme


    private def addSuffix(what: String, suffix: String) =
        if ((what == null) || (suffix == null)) what else what + "/" + suffix
}
