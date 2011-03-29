package org.podval.zenfolio;

import org.podval.photo.FolderType;
import org.podval.photo.PhotoException;

import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;
import com.zenfolio.www.api._1_1.GroupElement;
import com.zenfolio.www.api._1_1.GroupUpdater;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.PhotoSetType;
import com.zenfolio.www.api._1_1.PhotoSetUpdater;

import java.rmi.RemoteException;

import java.util.List;
import java.util.LinkedList;

import java.io.File;


/* package */ final class Group extends GroupLike<com.zenfolio.www.api._1_1.Group> {

    public Group(final Zenfolio zenfolio, final com.zenfolio.www.api._1_1.Group element) {
        super(zenfolio, element);
    }


    @Override
    public FolderType getFolderType() {
        return FolderType.Mix;
    }


    @Override
    protected void populate() {
        if ((getElement().getElements() != null) && (getElement().getElements().getArrayOfChoice1Choice() != null)) {
            for (final ArrayOfChoice1Choice element : getElement().getElements().getArrayOfChoice1Choice()) {
                GroupElement subGroup = element.getGroup();

                final GroupLike<?> subDirectory =
                        (subGroup != null)
                        ? new Group(getConnection(), (com.zenfolio.www.api._1_1.Group) subGroup)
                        : new Gallery(getConnection(), (PhotoSet) element.getPhotoSet());

                subFolders.add(subDirectory);
            }
        }
    }


    @Override
    public List<GroupLike<?>> getFolders() throws PhotoException {
        ensureIsPopulated();

        // @todo sort and immute?
        return subFolders;
    }


    @Override
    public GroupLike<?> getFolder(final String name) throws PhotoException {
        GroupLike<?> result = null;

        for (final GroupLike<?> subDirectory : getFolders()) {
            if (subDirectory.getName().equals(name)) {
                result = subDirectory;
                break;
            }
        }

        return result;
    }


    @Override
    public List<ZenfolioPhoto> getPhotos() {
        final List<ZenfolioPhoto> result = new LinkedList<ZenfolioPhoto>();

        return result;
    }


    @Override
    public ZenfolioPhoto getPhoto(final String name) {
        return null;
    }


    @Override
    public GroupLike<?> doCreateFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        final GroupLike<?> result;

        try {
            if (folderType.canHaveFolders()) {
                final GroupUpdater updater = new GroupUpdater();
                updater.setTitle(name);
                result = new Group(getConnection(), getConnection().getConnection().createGroup(getElement().getId(), updater));
            } else {
                final PhotoSetUpdater updater = new PhotoSetUpdater();
                updater.setTitle(name);
                return new Gallery(getConnection(), getConnection().getConnection().createPhotoSet(getElement().getId(), PhotoSetType.Gallery, updater));
            }
        } catch (final RemoteException e) {
            throw new PhotoException(e);
        }

        return result;
    }


    @Override
    public GroupLike<?> doCreateFakeFolder(
        final String name,
        final FolderType folderType)
    {
        final GroupLike<?> result;

        if (folderType.canHaveFolders()) {
            final com.zenfolio.www.api._1_1.Group newGroup = new com.zenfolio.www.api._1_1.Group();
            newGroup.setTitle(name);
            result = new Group(getConnection(), newGroup);
        } else {
            final PhotoSet gallery = new PhotoSet();
            gallery.setTitle(name);
            gallery.setType(PhotoSetType.Gallery);
            return new Gallery(getConnection(), gallery);
        }

        return result;
    }


    @Override
    protected void doAddFile(final String name, final File file) {
        // @todo implement
        // @todo checks in the base class?
        throw new UnsupportedOperationException("Not implemented yet!!!");
    }


    @Override
    protected void checkFolderType(final FolderType folderType) {
        folderType.checkNotMixed();
    }


    @Override
    public void updateIfChanged() throws PhotoException {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private final List<GroupLike<?>> subFolders = new LinkedList<GroupLike<?>>();
}
