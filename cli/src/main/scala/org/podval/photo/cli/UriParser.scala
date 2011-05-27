/*
 *  Copyright 2011 Leonid Dubinsky <dub@podval.org>.
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

import org.podval.photo.{Connector, Folder, PhotoException}
import org.podval.photo.files.{FilesConnector, FilesConnection}

import org.kohsuke.args4j.CmdLineException

import java.net.{URI, URISyntaxException}


object UriParser {

    @throws(classOf[CmdLineException])
    def uri2folder(uriStr: String, enableLowLevelLogging: Boolean, suffix: String): Folder[_,_,_] = {
        val uri: URI = 
            try {
                new URI(uriStr)
            } catch {
                case e: URISyntaxException =>  throw new CmdLineException(e.getMessage())
            }

        val scheme = defaultScheme(uri.getScheme())

        val connector = Connector.get(scheme)
        if (connector.isEmpty) {
            throw new CmdLineException("Unknown scheme: " + scheme)
        }

        val connection = connector.get.connect()

        if (enableLowLevelLogging) {
            connection.enableLowLevelLogging
        }

        val isFilesConnection = connection.isInstanceOf[FilesConnection]

        val (login: Option[String], password: Option[String]) = split(getUserInfo(uri), ':')

        val (pathToRoot, pathAfterRoot) = split(uri.getPath(), '|')

        if (pathAfterRoot.isDefined && !isFilesConnection) {
            throw new CmdLineException("Only files connection can have path-to-root!")
        }

        val path = if (isFilesConnection) pathAfterRoot else pathToRoot

        if (connection.isInstanceOf[FilesConnection]) {
            connection.asInstanceOf[FilesConnection].open(pathToRoot.get, login, password)
        } else {
            connection.open(login, password)
        }

        getSubFolderByPath(connection.rootFolder, addSuffix(path.get, suffix))
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
    private def split(what: String, where: Char): (Option[String], Option[String]) = {
        if (what == null) {
            (None, None)
        } else {
            val index = what.indexOf(where)
            if (index == -1) {
                (Some(what), None)
            } else {
                (Some(what.substring(0, index)), Some(what.substring(index+1)))
            }
        }
    }


    private def defaultScheme(scheme: String): String =
        if (scheme == null) FilesConnector.SCHEME else scheme


    private def addSuffix(what: String, suffix: String) =
        if ((what == null) || (suffix == null)) what else what + "/" + suffix


    private final def getSubFolderByPath[F <: Folder[_,F,_]](folder: F, path: String): F = {
        var result = folder

        if (path != null) {
            for (name <- path.split("/")) {
                if (!name.isEmpty()) {
                    if (!result.canHaveFolders) {
                        throw new PhotoException("Folder " + result + " can not have subfoldrs!")
                    }

                    result = result.getFolder(name).get
                }
            }
        }

        result
    }
}
