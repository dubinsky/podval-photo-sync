package org.podval.zenfolio;

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


public final class Group extends ZenfolioDirectory {

    public Group(final Zenfolio zenfolio, final com.zenfolio.www.api._1_1.Group group) {
        this.zenfolio = zenfolio;
        this.group = group;
    }


    @Override
    public String getName() {
        return group.getTitle();
    }


    private void ensureIsPopulated() {
        if (!isPopulated) {
            populate();
            isPopulated = true;
        }
    }


    @Override
    protected void populate() {
        for (final ArrayOfChoice1Choice element : getElements()) {
            GroupElement subGroup = element.getGroup();

            final ZenfolioDirectory subDirectory =
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
    public boolean hasSubDirectories() {
        return !getSubDirectories().isEmpty();
    }


    @Override
    public List<ZenfolioDirectory> getSubDirectories() {
        ensureIsPopulated();

        // @todo sort and immute?
        return subFolders;
    }


    @Override
    public ZenfolioDirectory getSubDirectory(final String name) {
        ZenfolioDirectory result = null;

        for (final ZenfolioDirectory subDirectory : getSubDirectories()) {
            if (subDirectory.getName().equals(name)) {
                result = subDirectory;
                break;
            }
        }

        return result;
    }


    @Override
    public List<Photo> getItems() {
        final List<Photo> result = new LinkedList<Photo>();

        return result;
    }


    @Override
    public Photo getItem(final String name) {
        return null;
    }


    @Override
    public ZenfolioDirectory doCreateSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws RemoteException
    {
        final ZenfolioDirectory result;

        if (canHaveDirectories) {
            final GroupUpdater updater = new GroupUpdater();
            updater.setTitle(name);
            result = new Group(zenfolio, zenfolio.getConnection().createGroup(group.getId(), updater));
        } else {
            final PhotoSetUpdater updater = new PhotoSetUpdater();
            updater.setTitle(name);
            return new Gallery(zenfolio, zenfolio.getConnection().createPhotoSet(group.getId(), PhotoSetType.Gallery, updater));
        }

        return result;
    }


    @Override
    public ZenfolioDirectory doCreateFakeSubDirectory(
        final String name,
        final boolean canHaveDirectories,
        final boolean canHaveItems) throws RemoteException
    {
        final ZenfolioDirectory result;

        if (canHaveDirectories) {
            final com.zenfolio.www.api._1_1.Group group = new com.zenfolio.www.api._1_1.Group();
            group.setTitle(name);
            result = new Group(zenfolio, group);
        } else {
            final PhotoSet gallery = new PhotoSet();
            gallery.setTitle(name);
            gallery.setType(PhotoSetType.Gallery);
            return new Gallery(zenfolio, gallery);
        }

        return result;
    }


    @Override
    public boolean canHaveSubDirectories() {
        return true;
    }


    @Override
    protected void checkSubDirectoryType(final boolean canHaveDirectories, final boolean canHaveItems) {
        if (canHaveDirectories && canHaveItems) {
            throw new IllegalArgumentException("Mixed directories not supported!");
        }

        if (!canHaveDirectories && !canHaveItems) {
            throw new IllegalArgumentException("No elements allowed for the directory!");
        }
    }


    private final Zenfolio zenfolio;


    private com.zenfolio.www.api._1_1.Group group;


    private boolean isPopulated;


    private final List<ZenfolioDirectory> subFolders = new LinkedList<ZenfolioDirectory>();
}
