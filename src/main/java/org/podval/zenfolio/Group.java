package org.podval.zenfolio;

import org.podval.things.Folder;
import org.podval.things.ThingsException;

import com.zenfolio.www.api._1_1.ArrayOfChoice1;
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


public final class Group extends Folder<Photo> {

    public Group(final Zenfolio zenfolio, final com.zenfolio.www.api._1_1.Group group) {
        this.zenfolio = zenfolio;
        this.group = group;
    }


    @Override
    public String getName() {
        return group.getTitle();
    }


    @Override
    protected void populate() {
        for (final ArrayOfChoice1Choice element : getElements()) {
            GroupElement subGroup = element.getGroup();

            final Folder<Photo> subDirectory =
                (subGroup != null) ?
                new Group(zenfolio, (com.zenfolio.www.api._1_1.Group) subGroup) :
                new Gallery(zenfolio, (PhotoSet) element.getPhotoSet());

            subFolders.add(subDirectory);
        }
    }


    private ArrayOfChoice1Choice[] getElements() {
        final ArrayOfChoice1 array = group.getElements();

        return (array == null) ? new ArrayOfChoice1Choice[0] : array.getArrayOfChoice1Choice();
    }


    @Override
    public List<Folder<Photo>> getFolders() throws ThingsException {
        ensureIsPopulated();

        // @todo sort and immute?
        return subFolders;
    }


    @Override
    public Folder<Photo> getFolder(final String name) throws ThingsException {
        Folder<Photo> result = null;

        for (final Folder<Photo> subDirectory : getFolders()) {
            if (subDirectory.getName().equals(name)) {
                result = subDirectory;
                break;
            }
        }

        return result;
    }


    @Override
    public List<Photo> getThings() {
        final List<Photo> result = new LinkedList<Photo>();

        return result;
    }


    @Override
    public Photo getThing(final String name) {
        return null;
    }


    @Override
    public Folder<Photo> doCreateFolder(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws ThingsException
    {
        final Folder<Photo> result;

        try {
            if (canHaveDirectories) {
                final GroupUpdater updater = new GroupUpdater();
                updater.setTitle(name);
                result = new Group(zenfolio, zenfolio.getConnection().createGroup(group.getId(), updater));
            } else {
                final PhotoSetUpdater updater = new PhotoSetUpdater();
                updater.setTitle(name);
                return new Gallery(zenfolio, zenfolio.getConnection().createPhotoSet(group.getId(), PhotoSetType.Gallery, updater));
            }
        } catch (final RemoteException e) {
            throw new ThingsException(e);
        }

        return result;
    }


    @Override
    public Folder<Photo> doCreateFakeFolder(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems)
    {
        final Folder<Photo> result;

        if (canHaveDirectories) {
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
        throw new UnsupportedOperationException("нот имплементед ыет!!!");
    }


    @Override
    public boolean canHaveFolders() {
        return true;
    }


    @Override
    public boolean canHaveThings() {
        return true;
    }


    @Override
    protected void checkFolderType(final boolean canHaveDirectories, final boolean canHaveItems) {
        if (canHaveDirectories && canHaveItems) {
            throw new IllegalArgumentException("Mixed directories not supported!");
        }

        if (!canHaveDirectories && !canHaveItems) {
            throw new IllegalArgumentException("No elements allowed for the directory!");
        }
    }


    private final Zenfolio zenfolio;


    private com.zenfolio.www.api._1_1.Group group;


    private final List<Folder<Photo>> subFolders = new LinkedList<Folder<Photo>>();
}
