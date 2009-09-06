package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.ZfApi;
import com.zenfolio.www.api._1_1.ZfApiStub;
import com.zenfolio.www.api._1_1.Group;
import com.zenfolio.www.api._1_1.GroupElement;
import com.zenfolio.www.api._1_1.GroupUpdater;
import com.zenfolio.www.api._1_1.Photo;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.PhotoSetType;
import com.zenfolio.www.api._1_1.PhotoSetUpdater;
import com.zenfolio.www.api._1_1.ArrayOfChoice1;
import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;
import com.zenfolio.www.api._1_1.ArrayOfPhoto;

import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class Zenfolio {

    public Zenfolio(final String login, final String password) throws RemoteException {
        this.login = login;
        this.password = password;
        this.connection = new ZfApiStub();
    }


    public void connect() throws RemoteException, UnsupportedEncodingException, NoSuchAlgorithmException, IOException {
        if (password != null) {
            Login.login(connection, login, password);
        }
    }


    public Group findGroup(final String path) throws RemoteException {
        Group result = loadGroupHierarchy();

        if (path != null) {
            for (final String name : path.split("/")) {
                if (!name.isEmpty()) {
                    result = asGroup(find(result, name));
                }
            }
        }

        return result;
    }


    public Group loadGroupHierarchy() throws RemoteException {
        return connection.loadGroupHierarchy(login);
    }


    public Group asGroup(final GroupElement element) {
        if (!(element instanceof Group)) {
            throw new IllegalArgumentException("Not a group: " + element);
        }

        return (Group) element;
    }


    public GroupElement find(final Group group, final String name) {
        GroupElement result = null;

        final ArrayOfChoice1Choice[] elements = getElements(group);

        if (elements != null) {
            for (final ArrayOfChoice1Choice element : elements) {
                GroupElement subElement = element.getGroup();
                if (subElement == null) {
                    subElement = element.getPhotoSet();
                }

                if (subElement.getTitle().equals(name)) {
                    result = subElement;
                    break;
                }
            }
        }

        return result;
    }


    public ArrayOfChoice1Choice[] getElements(final Group group) {
        final ArrayOfChoice1 array = group.getElements();

        return (array == null) ? null : array.getArrayOfChoice1Choice();
    }


    public Photo find(final PhotoSet photoSet, final String name) {
        Photo result = null;

        final Photo[] photos = getPhotos(photoSet);
        if (photos != null) {
            for (final Photo photo : photos) {
                if (photo.getTitle().equals(name)) {
                    result = photo;
                    break;
                }
            }
        }

        return result;
    }


    public Photo[] getPhotos(final PhotoSet photoSet) {
        final ArrayOfPhoto array = photoSet.getPhotos();

        return (array == null) ? null : array.getPhoto();
    }


    public Group createGroup(final Group group, final String name, final boolean doIt) throws RemoteException {
        final Group result;

        if (doIt) {
            final GroupUpdater updater = new GroupUpdater();
            updater.setTitle(name);
            result = connection.createGroup(group.getId(), updater);
        } else {
            result = new Group();
            result.setTitle(name);
        }

        return result;
    }


    public PhotoSet createGallery(final Group group, final String name, final boolean doIt) throws RemoteException {
        final PhotoSet result;

        if (doIt) {
            final PhotoSetUpdater updater = new PhotoSetUpdater();
            updater.setTitle(name);
            result = connection.createPhotoSet(group.getId(), PhotoSetType.Gallery, updater);
        } else {
            result = new PhotoSet();
            result.setTitle(name);
            result.setType(PhotoSetType.Gallery);
        }

        return result;
    }


    private final String  login;


    private final String password;


    private final ZfApi connection;
}
