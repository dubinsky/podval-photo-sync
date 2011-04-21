package org.podval.zenfolio;

import org.podval.photo.{AlbumList, PhotoException}

import com.zenfolio.www.api._1_1.{ArrayOfChoice1Choice, GroupElement, GroupUpdater, PhotoSet, PhotoSetType, PhotoSetUpdater}

import java.rmi.RemoteException;

import java.io.File;


/* package */ final class Group(element: com.zenfolio.www.api._1_1.Group) extends ZenfolioFolder[com.zenfolio.www.api._1_1.Group](element) with AlbumList {

    protected override def retrieveFolders(): Seq[F] = {
        if ((getElement().getElements() != null) && (getElement().getElements().getArrayOfChoice1Choice() != null)) {
            for (element <- getElement().getElements().getArrayOfChoice1Choice()
                 subGroup = element.getGroup()

                 folder =
                    if (subGroup != null)
                    new Group(subGroup.asInstanceOf[com.zenfolio.www.api._1_1.Group]) else
                    new Gallery(element.asInstanceOf[PhotoSet].getPhotoSet())
            ) yield folder
        }
    }


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
