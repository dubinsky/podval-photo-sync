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

package org.podval.photo.zenfolio

import org.podval.photo.{AlbumList, FolderType, PhotoException}

import com.zenfolio.www.api._1_1.{ArrayOfChoice1Choice, GroupUpdater, PhotoSetType, PhotoSetUpdater, Group => ZGroup}

import java.rmi.RemoteException


/* package */ abstract class Group(element: ZGroup) extends ZenfolioFolder[ZGroup](element) with AlbumList {

    protected final override def retrieveFolders(): Seq[F] = {
        if ((element.getElements() != null) && (element.getElements().getArrayOfChoice1Choice() != null)) {
            val choices: Array[ArrayOfChoice1Choice] = element.getElements().getArrayOfChoice1Choice()
            choices.map(toFolder)
        } else {
            Seq[F]()
        }
    }


    private def toFolder(what: ArrayOfChoice1Choice) = {
        val result = if (what.getGroup != null) new NonRootGroup(what.getGroup) else new Gallery(what.getPhotoSet())
        result.parent = this
        result
    }


    @throws(classOf[PhotoException])
    protected override def doCreateFolder(name: String, folderType: FolderType): ZenfolioFolder[_] = {
        try {
            val result = if (canHaveFolders) {
                val updater = new GroupUpdater()
                updater.setTitle(name)
                new NonRootGroup(connection.transport.createGroup(element.getId(), updater))
            } else {
                val updater = new PhotoSetUpdater()
                updater.setTitle(name)
                new Gallery(connection.transport.createPhotoSet(element.getId(), PhotoSetType.Gallery, updater))
            }

            result.parent = this
            result

        } catch {
            case e: RemoteException => throw new PhotoException(e)
        }
    }
}
