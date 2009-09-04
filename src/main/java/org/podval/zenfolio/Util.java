package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.ZfApi;
import com.zenfolio.www.api._1_1.GroupElement;
import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;
import com.zenfolio.www.api._1_1.Group;
import com.zenfolio.www.api._1_1.GroupUpdater;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.PhotoSetType;
import com.zenfolio.www.api._1_1.PhotoSetUpdater;

import java.rmi.RemoteException;

import java.io.File;


public class Util {

    public static Group asGroup(final GroupElement element) {
        if (!(element instanceof Group)) {
            throw new IllegalArgumentException("Not a group: " + element);
        }

        return (Group) element;
    }


    public static GroupElement find(final Group group, final String name) {
        GroupElement result = null;

        final ArrayOfChoice1Choice[] elements = group.getElements().getArrayOfChoice1Choice();

        if (elements != null) {
            for (int i = 0; i < elements.length; i++) {
                final ArrayOfChoice1Choice element = elements[i];
                GroupElement subElement = element.getGroup();
                if (subElement == null) {
                    subElement = element.getPhotoSet();
                }

                final String subElementName = subElement.getTitle();
                if (subElementName.equals(name)) {
                    result = subElement;
                    break;
                }
            }
        }

        return result;
    }


    public static Group createGroup(final ZfApi zenfolio, final Group group, final String name) throws RemoteException {
        final GroupUpdater updater = new GroupUpdater();
        updater.setTitle(name);
        return zenfolio.createGroup(group.getId(), updater);
    }


    public static PhotoSet createGallery(final ZfApi zenfolio, final Group group, final String name) throws RemoteException {
        final PhotoSetUpdater updater = new PhotoSetUpdater();
        updater.setTitle(name);
        return zenfolio.createPhotoSet(group.getId(), PhotoSetType.Gallery, updater);
    }


    public static boolean hasSubDirectories(final File directory) {
        boolean result = false;

        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                result = true;
                break;
            }
        }

        return result;
    }
}
