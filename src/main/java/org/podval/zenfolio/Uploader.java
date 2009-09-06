package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.Group;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;
import com.zenfolio.www.api._1_1.GroupElement;
import com.zenfolio.www.api._1_1.Photo;
import com.zenfolio.www.api._1_1.PhotoSetType;

import java.rmi.RemoteException;

import java.security.NoSuchAlgorithmException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public final class Uploader {

    public Uploader(
        final String login,
        final String password,
        final String groupPath,
        final String directoryPath,
        final boolean doIt) throws RemoteException
    {
        this.zenfolio = new Zenfolio(login, password);

        this.groupPath = groupPath;
        this.directoryPath = directoryPath;
        this.doIt = doIt;
    }


    public void doIt()  throws RemoteException, UnsupportedEncodingException,
        NoSuchAlgorithmException, IOException
    {
        zenfolio.connect();

        final Group root = zenfolio.findGroup(groupPath);
        final File rootDirectory = Files.getDirectory(directoryPath);

        if (rootDirectory != null) {
            syncGroup(root, rootDirectory, 0);
        } else {
            list(root, 0);
        }
    }


    private void syncGroup(final Group group, final File directory, int level) throws RemoteException {
        println(level, group.getTitle());

        level++;

        syncGroupFromDirectory(group, directory, level);
        syncGroupToDirectory(group, directory, level);
    }


    private void syncGroupFromDirectory(final Group group, final File directory, int level)
        throws RemoteException
    {
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                sync(group, file, level);

            } else {
                if (isPhoto(file)) {
                    println(level, "Skipping photo " + file + " on the group level");
                }
            }
        }
    }


    private void sync(final Group group, final File directory, final int level)
        throws RemoteException
    {
        final String name = directory.getName();
        final boolean shouldBeGroup = Files.hasSubDirectories(directory);

        GroupElement element = zenfolio.find(group, name);

        if (element == null) {
            final String message =
                ((doIt) ? "creating" : "'creating'") + " " +
                ((shouldBeGroup) ? "group" : "gallery") + " " + name;

            println(level, message);

            element = (shouldBeGroup) ?
                zenfolio.createGroup(group, name, doIt) :
                zenfolio.createGallery(group, name, doIt);
        }

        if (element instanceof Group) {
            if (shouldBeGroup) {
                syncGroup((Group) element, directory, level);
            } else {
                println(level, "Is not a group, but should be: " + name);
            }
        } else if (element instanceof PhotoSet) {
            if (!shouldBeGroup) {
                syncGallery((PhotoSet) element, directory, level);
            } else {
                println(level, "Is a group, but should not be: " + name);
            }
        }
    }


    private void syncGroupToDirectory(final Group group, final File directory, int level) {
        final ArrayOfChoice1Choice[] elements = zenfolio.getElements(group);

        if (elements != null) {
            for (final ArrayOfChoice1Choice choice : elements) {
                final GroupElement element = (choice.getGroup() != null) ? choice.getGroup() : choice.getPhotoSet();
                final String name = element.getTitle();
                final File file = new File(directory, name);
                if (!file.exists()) {
                    println(level, "No file for the element: " + name);
                }
            }
        }
    }


    private void syncGallery(final PhotoSet gallery, final File directory, final int level)
        throws RemoteException
    {
        for (final File file : directory.listFiles()) {
            if (isPhoto(file)) {
                final String name = getName(file);
                final Photo photo = zenfolio.find(gallery, name);
                if (photo == null) {
                    final String message = ((doIt) ? "adding" : "'adding'") + " photo";
//                    println(level, message);

                    // @todo
                }
            } else {
//                println(level, "Skipping non-photo " + file + " on the gallery level");
            }
        }
    }


    private boolean isPhoto(final File file) {
        return file.getName().endsWith(".jpg");
    }


    private String getName(final File file) {
        final String filename = file.getName();
        final int dot = filename.lastIndexOf(".");
        return (dot == -1) ? filename : filename.substring(0, dot);
    }


    private void list(final Group group, int level) {
        println(level, group.getTitle());

        level++;

        final ArrayOfChoice1Choice[] elements = zenfolio.getElements(group);
        if (elements != null) {
            for (final ArrayOfChoice1Choice element : elements) {
                final Group subGroup = element.getGroup();
                indent(level);
                if (subGroup != null) {
                    list(subGroup, level);
                } else {
                    list(element.getPhotoSet(), level);
                }
            }
        }
    }


    private void list(final PhotoSet set, final int level) {
        println(level, set.getTitle());
    }


    private void println(final int level, final String line) {
        indent(level);
        System.out.println(line);
    }


    private void indent(final int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
    }


    public static void main(final String[] args) throws Exception {
        final String login = args[0];
        final String password = getArg(args, 1);
        final String groupPath =  getArg(args, 2);
        final String path =  getArg(args, 3);

        final Uploader uploader = new Uploader(login, password, groupPath, path, false);
        uploader.doIt();
    }


    private static String getArg(final String[] args, final int n) {
        return (args.length > n) ? args[n] : null;
    }


    private Zenfolio zenfolio;


    private final String groupPath;


    private final String directoryPath;


    private final boolean doIt;
}
