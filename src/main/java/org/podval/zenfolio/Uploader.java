package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.ZfApi;
import com.zenfolio.www.api._1_1.ZfApiStub;
import com.zenfolio.www.api._1_1.Group;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;
import com.zenfolio.www.api._1_1.GroupElement;
import com.zenfolio.www.api._1_1.GroupUpdater;

import java.rmi.RemoteException;

import java.security.NoSuchAlgorithmException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public final class Uploader {

    public Uploader(final String login, final String password) {
        this.login = login;
        this.password = password;
    }


    protected final void connect() throws RemoteException, UnsupportedEncodingException,
        NoSuchAlgorithmException, IOException
    {
        zenfolio = new ZfApiStub();
        Login.login(zenfolio, login, password);
    }


    public void scan(final String groupPath, final String filePath) throws RemoteException {
        scan(find(groupPath), new File(filePath), 0);
    }


    private void scan(final Group group, final File directory, int level) throws RemoteException {
        indent(level);
        System.out.println(directory.getName());

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + directory);
        }

        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                scan(findOrCreateGroup(group, file.getName()), file, level+1);
            } else {
                if (isPhoto(file)) {
                    System.out.println("Skipping photo " + file + " on the group level");
                }
            }
        }
    }


    private boolean isPhoto(final File file) {
        return file.getName().endsWith(".jpg");
    }


    public void list(final String path) throws RemoteException {
        list(find(path), 0);
    }


    private void list(final Group group, final int level) {
        System.out.println(group.getTitle());

        final ArrayOfChoice1Choice[] elements = group.getElements().getArrayOfChoice1Choice();
        if (elements != null) {
            for (int i = 0; i < elements.length; i++) {
                final ArrayOfChoice1Choice element = elements[i];
                final Group subGroup = element.getGroup();
                indent(level);
                if (subGroup != null) {
                    list(subGroup, level + 1);
                } else {
                    list(element.getPhotoSet(), level + 1);
                }
            }
        }
    }


    private void list(final PhotoSet set, final int level) {
        System.out.println(set.getTitle());
    }


    private void indent(final int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
    }


    private Group findOrCreateGroup(final Group group, final String name) throws RemoteException {
        final Group result;

        final GroupElement subGroup = find(group, name);

        if (subGroup != null) {
            result = asGroup(subGroup);
        } else {
            final GroupUpdater updater = new GroupUpdater();
            updater.setTitle(name);
            result = zenfolio.createGroup(group.getId(), updater);
        }

        return result;
    }


    private Group find(final String path) throws RemoteException {
        Group result = zenfolio.loadGroupHierarchy(login);

        for (final String name : path.split("/")) {
            if (!name.isEmpty()) {
                final GroupElement element = find(result, name);
                if (!(element instanceof Group)) {
                    throw new IllegalArgumentException("Not a group: " + element);
                }
                result = (Group) element;
            }
        }

        return result;
    }


    private Group asGroup(final GroupElement element) {
        if (!(element instanceof Group)) {
            throw new IllegalArgumentException("Not a group: " + element);
        }

        return (Group) element;
    }


    private GroupElement find(final Group group, final String name) {
        Group result = null;

        final ArrayOfChoice1Choice[] elements = group.getElements().getArrayOfChoice1Choice();
        if (elements != null) {
            for (int i = 0; i < elements.length; i++) {
                final ArrayOfChoice1Choice element = elements[i];
                final Group subGroup = element.getGroup();
                final String subGroupName = subGroup.getTitle();
                if ((subGroup != null) && (subGroupName.equals(name))) {
                    result = subGroup;
                    break;
                }
            }
        }

        return result;
    }


    public static void main(final String[] args) throws Exception {
        final String login = args[0];
        final String password = args[1];
        final String groupPath = args[2];
        final String path = args[3];

        final Uploader uploader = new Uploader(login, password);
        uploader.connect();
        uploader.scan(groupPath, path);
    }


    private final String login;


    private final String password;


    private ZfApi zenfolio;
}
