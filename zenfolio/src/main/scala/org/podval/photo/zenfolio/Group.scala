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

import org.podval.photo.{AlbumList, PhotoException}

import com.zenfolio.www.api._1_1.{ArrayOfChoice1Choice, GroupElement, GroupUpdater, PhotoSet, PhotoSetType, PhotoSetUpdater, Group => ZGroup}

import java.rmi.RemoteException

import java.io.File


/* package */ class Group(element: ZGroup) extends ZenfolioFolder[ZGroup](element) with AlbumList {

    protected final override def retrieveFolders(): Seq[F] = {
        if ((element.getElements() != null) && (element.getElements().getArrayOfChoice1Choice() != null)) {
            val choices: Array[ArrayOfChoice1Choice] = element.getElements().getArrayOfChoice1Choice()
            choices.map(toFolder)
        } else {
            Seq[F]()
        }
    }


    private def toFolder(what: ArrayOfChoice1Choice) =
        if (what.getGroup != null) new NonRootGroup(this, what.getGroup) else new Gallery(this, what.getPhotoSet())

    
//    @Override
//    public GroupLike<?> doCreateFolder(
//        final String name,
//        final FolderType folderType) throws PhotoException
//    {
//        final GroupLike<?> result;
//
//        try {
//            if (folderType.canHaveFolders()) {
//                final GroupUpdater updater = new GroupUpdater();
//                updater.setTitle(name);
//                result = new Group(getConnection(), getConnection().getConnection().createGroup(getElement().getId(), updater));
//            } else {
//                final PhotoSetUpdater updater = new PhotoSetUpdater();
//                updater.setTitle(name);
//                return new Gallery(getConnection(), getConnection().getConnection().createPhotoSet(getElement().getId(), PhotoSetType.Gallery, updater));
//            }
//        } catch (final RemoteException e) {
//            throw new PhotoException(e);
//        }
//
//        return result;
//    }
//
//
//    @Override
//    public GroupLike<?> doCreateFakeFolder(
//        final String name,
//        final FolderType folderType)
//    {
//        final GroupLike<?> result;
//
//        if (folderType.canHaveFolders()) {
//            final com.zenfolio.www.api._1_1.Group newGroup = new com.zenfolio.www.api._1_1.Group();
//            newGroup.setTitle(name);
//            result = new Group(getConnection(), newGroup);
//        } else {
//            final PhotoSet gallery = new PhotoSet();
//            gallery.setTitle(name);
//            gallery.setType(PhotoSetType.Gallery);
//            return new Gallery(getConnection(), gallery);
//        }
//
//        return result;
//    }
//
//
//    @Override
//    protected void doAddFile(final String name, final File file) {
//        // @todo implement
//        // @todo checks in the base class?
//        throw new UnsupportedOperationException("Not implemented yet!!!");
//    }
//
//
//    @Override
//    protected void checkFolderType(final FolderType folderType) {
//        folderType.checkNotMixed();
//    }
//
//
//    @Override
//    public void updateIfChanged() throws PhotoException {
//        // TODO
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
}
