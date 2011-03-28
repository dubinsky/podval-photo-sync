package org.podval.zenfolio;

import org.podval.photo.Folder;
import org.podval.photo.FolderType;
import org.podval.photo.PhotoException;

import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;
import com.zenfolio.www.api._1_1.GroupElement;
import com.zenfolio.www.api._1_1.GroupUpdater;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.PhotoSetType;
import com.zenfolio.www.api._1_1.PhotoSetUpdater;
import com.zenfolio.www.api._1_1.AccessType;

import java.rmi.RemoteException;

import java.util.List;
import java.util.LinkedList;

import java.io.File;


/* package */ final class Group extends Folder<Zenfolio, ZenfolioPhoto> {

    public Group(final Zenfolio zenfolio, final com.zenfolio.www.api._1_1.Group group) {
        super(zenfolio);

        this.group = group;
    }


    @Override
    public String getName() {
        return group.getTitle();
    }


    @Override
    public FolderType getFolderType() {
        return FolderType.Mix;
    }


    @Override
    public boolean isPublic() {
        return group.getAccessDescriptor().getAccessType() == AccessType.Public;
    }


    @Override
    public void setPublic(final boolean value) {
        group.getAccessDescriptor().setAccessType((value) ? AccessType.Public : AccessType.Private );
    }


    @Override
    protected void populate() {
        if ((group.getElements() != null) && (group.getElements().getArrayOfChoice1Choice() != null)) {
            for (final ArrayOfChoice1Choice element : group.getElements().getArrayOfChoice1Choice()) {
                GroupElement subGroup = element.getGroup();

                final Folder<Zenfolio, ZenfolioPhoto> subDirectory =
                        (subGroup != null)
                        ? new Group(getConnection(), (com.zenfolio.www.api._1_1.Group) subGroup)
                        : new Gallery(getConnection(), (PhotoSet) element.getPhotoSet());

                subFolders.add(subDirectory);
            }
        }
    }


    @Override
    public List<Folder<Zenfolio, ZenfolioPhoto>> getFolders() throws PhotoException {
        ensureIsPopulated();

        // @todo sort and immute?
        return subFolders;
    }


    @Override
    public Folder<Zenfolio, ZenfolioPhoto> getFolder(final String name) throws PhotoException {
        Folder<Zenfolio, ZenfolioPhoto> result = null;

        for (final Folder<Zenfolio, ZenfolioPhoto> subDirectory : getFolders()) {
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
    public Folder<Zenfolio, ZenfolioPhoto> doCreateFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        final Folder<Zenfolio, ZenfolioPhoto> result;

        try {
            if (folderType.canHaveFolders()) {
                final GroupUpdater updater = new GroupUpdater();
                updater.setTitle(name);
                result = new Group(getConnection(), getConnection().getConnection().createGroup(group.getId(), updater));
            } else {
                final PhotoSetUpdater updater = new PhotoSetUpdater();
                updater.setTitle(name);
                return new Gallery(getConnection(), getConnection().getConnection().createPhotoSet(group.getId(), PhotoSetType.Gallery, updater));
            }
        } catch (final RemoteException e) {
            throw new PhotoException(e);
        }

        return result;
    }


    @Override
    public Folder<Zenfolio, ZenfolioPhoto> doCreateFakeFolder(
        final String name,
        final FolderType folderType)
    {
        final Folder<Zenfolio, ZenfolioPhoto> result;

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


    private com.zenfolio.www.api._1_1.Group group;


    private final List<Folder<Zenfolio, ZenfolioPhoto>> subFolders = new LinkedList<Folder<Zenfolio, ZenfolioPhoto>>();
}
