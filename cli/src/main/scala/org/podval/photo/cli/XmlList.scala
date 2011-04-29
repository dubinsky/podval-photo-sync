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

import org.podval.photo.{Folder, Photo, Rotation}

import scala.xml.{Elem, Text, PrettyPrinter}


final class XmlList(folder: Folder) {

    def run() {
        val prettyPrinter = new PrettyPrinter(120, 4)
        val xml = listFolder(folder)
        Console.println(prettyPrinter.formatNodes(xml))
    }


    private def listFolder(folder: Folder): Elem =
        <folder>
           <name>{folder.name}</name>
           {folder.folders map (listFolder(_))}
           {folder.photos map (listPhoto(_))}
        </folder>


    private def listPhoto(photo: Photo): Elem = {
        val rotationAttribute = if (photo.rotation == Rotation.None) None else Some(Text(photo.rotation.toString))

        <photo
            rotation={rotationAttribute}
            size={photo.size.toString}
            date={photo.timestamp.toString}
            name={photo.name}
        />
    }
}
