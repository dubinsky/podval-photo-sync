package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.ArrayOfChoice1;
import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;
import com.zenfolio.www.api._1_1.Group;
import com.zenfolio.www.api._1_1.GroupElement;
import com.zenfolio.www.api._1_1.GroupUpdater;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.PhotoSetType;
import com.zenfolio.www.api._1_1.PhotoSetUpdater;

import java.rmi.RemoteException;



public final class GroupNg extends ZenfolioDirectory {

    public GroupNg(final Zenfolio zenfolio, final Group group) {
        this.zenfolio = zenfolio;
        this.group = group;
    }


    public String getName() {
        return group.getTitle();
    }


    public ZenfolioDirectory find(final String name) {
        GroupElement result = null;

        for (final ArrayOfChoice1Choice element : getElements()) {
            GroupElement subElement = element.getGroup();

            if (subElement == null) {
                subElement = element.getPhotoSet();
            }

            if (subElement.getTitle().equals(name)) {
                result = subElement;
                break;
            }
        }

        return (result instanceof Group) ?
            new GroupNg(zenfolio, (Group) result) :
            new Gallery(zenfolio, (PhotoSet) result);
    }


    public ArrayOfChoice1Choice[] getElements() {
        final ArrayOfChoice1 array = group.getElements();

        return (array == null) ? new ArrayOfChoice1Choice[0] : array.getArrayOfChoice1Choice();
    }


    public GroupNg createGroup(final String name) throws RemoteException {
        final GroupUpdater updater = new GroupUpdater();
        updater.setTitle(name);
        return new GroupNg(zenfolio, zenfolio.getConnection().createGroup(group.getId(), updater));
    }


    public GroupNg createFakeGroup(final String name) throws RemoteException {
        final Group result = new Group();
        result.setTitle(name);
        return new GroupNg(zenfolio, result);
    }


    public Gallery createGallery(final String name) throws RemoteException {
        final PhotoSetUpdater updater = new PhotoSetUpdater();
        updater.setTitle(name);
        return new Gallery(zenfolio, zenfolio.getConnection().createPhotoSet(group.getId(), PhotoSetType.Gallery, updater));
    }


    public Gallery createFakeGallery(final String name) throws RemoteException {
        final PhotoSet result = new PhotoSet();
        result.setTitle(name);
        result.setType(PhotoSetType.Gallery);
        return new Gallery(zenfolio, result);
    }


    private final Zenfolio zenfolio;


    private Group group;
}
