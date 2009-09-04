package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.ZfApi;
import com.zenfolio.www.api._1_1.ZfApiStub;
import com.zenfolio.www.api._1_1.Group;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;
import com.zenfolio.www.api._1_1.GroupElement;

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

        if (password != null) {
            Login.login(zenfolio, login, password);
        }
    }


    public void sync(final String groupPath, final String filePath) throws RemoteException {
        final Group root = Util.asGroup(find(groupPath));
        if (filePath != null) {
            sync(root, new File(filePath), 0);
        } else {
            list(root, 0);
        }
    }


    private void sync(final Group group, final File directory, int level) throws RemoteException {
        indent(level);

        System.out.println(group.getTitle());

        if (!directory.isDirectory()) {
            // @todo do not crash!!!
            throw new IllegalArgumentException("Not a directory: " + directory);
        }

        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                final boolean shouldBeGroup = Util.hasSubDirectories(file);

                sync(group, file, shouldBeGroup, level);

            } else {
                if (isPhoto(file)) {
                    System.out.println("Skipping photo " + file + " on the group level");
                }
            }
        }

        // @todo enumerate elements that are not in the directory!!!
    }


    private void sync(final Group group, final File directory, final boolean shouldBeGroup, final int level)
        throws RemoteException
    {
        final String name = directory.getName();

        GroupElement element = Util.find(group, name);

        if (element == null) {
            element = (shouldBeGroup) ?
                Util.createGroup(zenfolio, group, name) :
                Util.createGallery(zenfolio, group, name);
        }

        final int nextLevel = level + 1;

        if (element instanceof Group) {
            if (!shouldBeGroup) {
                // @todo do not crash!!!
                throw new IllegalArgumentException("Is not a group, but should be: " + name);
            }

            sync((Group) element, directory, nextLevel);
        } else if (element instanceof PhotoSet) {
            if (shouldBeGroup) {
                // @todo do not crash!!!
                throw new IllegalArgumentException("Is a group, but should not be: " + name);
            }

            sync((PhotoSet) element, directory, nextLevel);
        }
    }


    private void sync(final PhotoSet gallery, final File directory, final int level)
        throws RemoteException
    {
        // @todo sync galleries!!!
    }


    private boolean isPhoto(final File file) {
        return file.getName().endsWith(".jpg");
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


    private GroupElement find(final String path) throws RemoteException {
        GroupElement result = zenfolio.loadGroupHierarchy(login);

        if (path != null) {
            for (final String name : path.split("/")) {
                if (!name.isEmpty()) {
                    result = Util.find(Util.asGroup(result), name);
                }
            }
        }

        return result;
    }


    public static void main(final String[] args) throws Exception {
        final String login = args[0];
        final String password = getArg(args, 1);
        final String groupPath =  getArg(args, 2);
        final String path =  getArg(args, 3);

        final Uploader uploader = new Uploader(login, password);
        uploader.connect();
        uploader.sync(groupPath, path);
    }


    private static String getArg(final String[] args, final int n) {
        return (args.length > n) ? args[n] : null;
    }


    private final String login;


    private final String password;


    private ZfApi zenfolio;
}
