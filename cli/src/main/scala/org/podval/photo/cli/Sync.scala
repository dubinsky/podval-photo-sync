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


class Sync {

//    def syncFolderTo(toFolder: FolderNG) {
//        getLog().debug("Synchronizing " + name());
//
//        syncProperties(toFolder)
//        syncContentTo(toFolder)
//        syncFoldersTo(toFolder)
//
//        syncBackwards(toFolder)
//    }
//
//
//    private def syncProperties(toFolder: FolderNG) {
//        toFolder.setPublic(isPublic())
//
//        toFolder.updateIfChanged()
//    }
//
//
//    private def syncBackwards[OF <: FolderNG](toFolder: OF) {
////        for (final Folder<D, O> toSubFolder : toFolder.getFolders()) {
////            final String name = toSubFolder.getName();
////            final Folder<C, P> fromSubFolder = getFolder(name);
////            if (fromSubFolder == null) {
////                getLog().info("No file for the element: " + name);
////            }
////        }
//    }
//
//
//    private def syncContentTo[OF <: FolderNG](toFolder: OF) {
//        if (hasFolders) {
//            getPhotos().foreach(photo => getLog().info("Skipping " + photo + " on the folder level"))
//        } else {
//            getPhotos().filter(photo => toFolder.getPhoto(photo.name()) == null).foreach(photo => toFolder.addPhoto(photo))
//        }
//    }
//
//
//    private def addPhoto[OP <: PhotoNG](photo: OP) {
//        val doIt = !getConnection().isReadOnly
//
//        val name = photo.name()
//
//        // TODO distinguish between "exists" and "available as local file"...
//        val file: File = photo.getOriginalFile()
//        if (file != null) {
//            val message = (if (doIt) "adding" else "'adding'") + " photo" + " " + name
//            getLog().debug(message)
//
//            if (doIt) {
//                try {
//                    addFile(file.name(), file)
//                } catch {
//                    case e: PhotoException => getLog().error(e.getMessage())
//                }
//            }
//
//        } else {
//            getLog().info("Raw conversions are not yet implemented. Can not add " + name)
//        }
//    }
//
//
//    private def syncFoldersTo(toFolder: FolderNG) {
//        // @todo skip the collections!
//
//        for (val fromSubFolder <- getFolders()) {
//            val toSubFolder = toFolder.getElementForSubDirectory(fromSubFolder)
//
//            if (toSubFolder != null) {
//                fromSubFolder.syncFolderTo(toSubFolder)
//            }
//        }
//    }
//
//
//    private <D extends Connection<O>, O extends Photo> Folder<C, P> getElementForSubDirectory(
//        final Folder<D, O> toFolder) throws PhotoException
//    {
//        Folder<C, P> result = null;
//
//        final boolean doIt = !getConnection().isReadOnly();
//
//        final String name = toFolder.getName();
//
//        final boolean shouldHaveFolders = toFolder.hasFolders();
//
//        final FolderType folderType = (shouldHaveFolders) ?
//            FolderType.Folders :
//            FolderType.Photos;
//
//        Folder<C, P> toSubFolder = getFolder(name);
//
//        if (toSubFolder == null) {
//            final String message =
//                ((doIt) ? "creating" : "'creating'") + " " +
//                folderType + " " + name;
//
//            getLog().debug(message);
//
//            // TODO: set properties when creating, so that we do not have to update it immediately!
//            toSubFolder = (doIt) ?
//                createFolder(name, folderType) :
//                createFakeFolder(name, folderType);
//        }
//
//        final boolean canHaveFolders = toSubFolder.getFolderType().canHaveFolders();
//
//        if (canHaveFolders && !shouldHaveFolders) {
//            getLog().info("Can have folders, but should't: " + name);
//        } if (!canHaveFolders && shouldHaveFolders) {
//            getLog().info("Can't have folders, but should: " + name);
//        } else {
//            result = toSubFolder;
//        }
//
//        return result;
//    }
//
//
//    protected final void register(final P photo) {
//        // TODO Map<String, P> name2photo?
//        photos.add(photo);
//    }
//
//
//    protected final void register(final  Folder<C, P> folder) {
//        // TODO Map<String, P> name2folder?
//        folders.add(folder);
//    }
//
//
//    final def createFolder(
//        final String name,
//        final FolderType folderType): F
//    {
//        checkFolderCreation(folderType);
//
//        return doCreateFolder(name, folderType);
//    }
//
//
//    public final Folder<C, P> createFakeFolder(
//        final String name,
//        final FolderType folderType) throws PhotoException
//    {
//        checkFolderCreation(folderType);
//
//        return doCreateFakeFolder(name, folderType);
//    }
//
//
//    private void checkFolderCreation(final FolderType folderType) {
//        getFolderType().checkCanHaveFolders(this);
//
//        checkFolderType(folderType);
//    }
//
//
//    protected abstract void checkFolderType(final FolderType folderType);
//
//
//    protected abstract Folder<C, P> doCreateFolder(
//        final String name,
//        final FolderType folderType) throws PhotoException;
//
//
//    protected abstract Folder<C, P> doCreateFakeFolder(
//        final String name,
//        final FolderType folderType) throws PhotoException;
//
//
//    public final void addFile(final String name, final File file) throws PhotoException {
//        getFolderType().checkCanHaveFolders(this);
//
//        doAddFile(name, file);
//    }
//
//
//    protected abstract void doAddFile(final String name, final File file) throws PhotoException;


//    private def getLog(): Log = LogFactory.getLog(Connection.LOG)
}
