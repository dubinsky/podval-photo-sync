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
import org.podval.directory.FileConnection

import org.kohsuke.args4j.CmdLineException

import java.net.{URI, URISyntaxException}


object UriParser {

    def fromUri(uriStr: String, suffix: String): ConnectionDescriptor = { // throws CmdLineException

        val uri: URI = toUri(uriStr)


        private def toUri(uriStr: String) = {
            try {
                uri = new URI(uriStr)
            } catch {
                case e: URISyntaxException =>  throw new CmdLineException(e.getMessage())
            }
        }

        val userInfo: String = getUserInfo(uri)

        final String login;
        final String password;

        if (userInfo == null) {
            login = null;
            password = null;
        } else {
            final int colon = userInfo.indexOf(':');
            if (colon == -1) {
                login = userInfo;
                password = null;
            } else {
                login = userInfo.substring(0, colon);
                password = userInfo.substring(colon+1);
            }
        }

        return new ConnectionDescriptor(
            defaultScheme(uri.getScheme()),
            login,
            password,
            uri.getHost(),
            addSuffix(uri.getPath(), suffix));
    }


    private static String getUserInfo(final URI uri) {
        final String result;

        final String userInfo = uri.getUserInfo();
        final String host = uri.getHost();
        final String authority = uri.getAuthority();
        if ((host == null) && (authority != null)) {
            result = (authority.endsWith("@")) ?
                authority.substring(0, authority.length()-1) :
                authority;
        } else {
            result = userInfo;
        }

        return result;
    }


    private static String defaultScheme(final String scheme) {
        return (scheme == null) ? FileConnection.SCHEME : scheme;
    }


    private static String addSuffix(final String what, final String suffix) {
        return ((what == null) || (suffix == null)) ? what : what + "/" + suffix;
    }
}
