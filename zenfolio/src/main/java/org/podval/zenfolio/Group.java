package org.podval.zenfolio;

import org.podval.things.Folder;
import org.podval.things.FolderType;
import org.podval.things.PhotoException;

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


/* package */ final class Group extends Folder<ZenfolioPhoto> {

    public Group(final Zenfolio zenfolio, final com.zenfolio.www.api._1_1.Group group) {
        this.zenfolio = zenfolio;
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

                final Folder<ZenfolioPhoto> subDirectory =
                        (subGroup != null)
                        ? new Group(zenfolio, (com.zenfolio.www.api._1_1.Group) subGroup)
                        : new Gallery(zenfolio, (PhotoSet) element.getPhotoSet());

                subFolders.add(subDirectory);
            }
        }
    }


    @Override
    public List<Folder<ZenfolioPhoto>> getFolders() throws PhotoException {
        ensureIsPopulated();

        // @todo sort and immute?
        return subFolders;
    }


    @Override
    public Folder<ZenfolioPhoto> getFolder(final String name) throws PhotoException {
        Folder<ZenfolioPhoto> result = null;

        for (final Folder<ZenfolioPhoto> subDirectory : getFolders()) {
            if (subDirectory.getName().equals(name)) {
                result = subDirectory;
                break;
            }
        }

        return result;
    }


    @Override
    public List<ZenfolioPhoto> getThings() {
        final List<ZenfolioPhoto> result = new LinkedList<ZenfolioPhoto>();

        return result;
    }


    @Override
    public ZenfolioPhoto getThing(final String name) {
        return null;
    }


    @Override
    public Folder<ZenfolioPhoto> doCreateFolder(
        final String name,
        final FolderType folderType) throws PhotoException
    {
        final Folder<ZenfolioPhoto> result;

        try {
            if (folderType.canHaveFolders()) {
                final GroupUpdater updater = new GroupUpdater();
                updater.setTitle(name);
                result = new Group(zenfolio, zenfolio.getConnection().createGroup(group.getId(), updater));
            } else {
                final PhotoSetUpdater updater = new PhotoSetUpdater();
                updater.setTitle(name);
                return new Gallery(zenfolio, zenfolio.getConnection().createPhotoSet(group.getId(), PhotoSetType.Gallery, updater));
            }
        } catch (final RemoteException e) {
            throw new PhotoException(e);
        }

        return result;
    }


    @Override
    public Folder<ZenfolioPhoto> doCreateFakeFolder(
        final String name,
        final FolderType folderType)
    {
        final Folder<ZenfolioPhoto> result;

        if (folderType.canHaveFolders()) {
            final com.zenfolio.www.api._1_1.Group newGroup = new com.zenfolio.www.api._1_1.Group();
            newGroup.setTitle(name);
            result = new Group(zenfolio, newGroup);
        } else {
            final PhotoSet gallery = new PhotoSet();
            gallery.setTitle(name);
            gallery.setType(PhotoSetType.Gallery);
            return new Gallery(zenfolio, gallery);
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


    private final Zenfolio zenfolio;


    private com.zenfolio.www.api._1_1.Group group;


    private final List<Folder<ZenfolioPhoto>> subFolders = new LinkedList<Folder<ZenfolioPhoto>>();
}
