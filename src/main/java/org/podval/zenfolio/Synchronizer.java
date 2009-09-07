package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.Group;
import com.zenfolio.www.api._1_1.GroupElement;
import com.zenfolio.www.api._1_1.PhotoSet;
import com.zenfolio.www.api._1_1.Photo;
import com.zenfolio.www.api._1_1.ArrayOfChoice1Choice;

import java.rmi.RemoteException;

import java.io.File;
import java.io.IOException;


public final class Synchronizer extends Processor {

    public Synchronizer(
        final String login,
        final String password,
        final String groupPath,
        final String rootDirectoryPath,
        final boolean doIt) throws RemoteException
    {
        super(login, password, groupPath);

        this.rootDirectoryPath = rootDirectoryPath;
        this.doIt = doIt;
    }


    @Override
    protected void run(final Group rootGroup) throws RemoteException, IOException {
        final File rootDirectory = new File(rootDirectoryPath);

        if (!rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + rootDirectory);
        }

        syncGroup(rootGroup, rootDirectory, 0);
    }


    private void syncGroup(final Group group, final File directory, int level)
        throws RemoteException, IOException
    {
        println(level, group.getTitle());

        level++;

        syncGroupFromDirectory(group, directory, level);
        syncGroupToDirectory(group, directory, level);
    }


    private void syncGroupFromDirectory(final Group group, final File directory, int level)
        throws RemoteException, IOException
    {
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                sync(group, file, level);

            } else {
                if (isPhoto(file)) {
                    message(level, "Skipping photo " + file + " on the group level");
                }
            }
        }
    }


    private void sync(final Group group, final File directory, final int level)
        throws RemoteException, IOException
    {
        final String name = directory.getName();
        final boolean shouldBeGroup = Files.hasSubDirectories(directory);

        GroupElement element = getZenfolio().find(group, name);

        if (element == null) {
            final String message =
                ((doIt) ? "creating" : "'creating'") + " " +
                ((shouldBeGroup) ? "group" : "gallery") + " " + name;

            message(level, message);

            element = getZenfolio().create(group, name, shouldBeGroup, doIt);
        }

        if (element instanceof Group) {
            if (!shouldBeGroup) {
                message(level, "Is a group, but should not be: " + name);
            } else {
                syncGroup((Group) element, directory, level);
            }

        } else if (element instanceof PhotoSet) {
            if (shouldBeGroup) {
                message(level, "Is not a group, but should be: " + name);
            } else {
                syncGallery(getZenfolio().populate((PhotoSet) element), directory, level);
            }
        }
    }


    private void syncGroupToDirectory(final Group group, final File directory, int level) {
        final ArrayOfChoice1Choice[] elements = getZenfolio().getElements(group);

        if (elements != null) {
            for (final ArrayOfChoice1Choice choice : elements) {
                final GroupElement element = (choice.getGroup() != null) ? choice.getGroup() : choice.getPhotoSet();
                final String name = element.getTitle();
                final File file = new File(directory, name);
                if (!file.exists()) {
                    message(level, "No file for the element: " + name);
                }
            }
        }
    }


    private void syncGallery(final PhotoSet gallery, final File directory, final int level)
        throws RemoteException, IOException
    {
        println(level, gallery.getTitle());

        // @todo skip the collections!

        syncGalleryFromDirectory(gallery, directory, level+1);
        syncGalleryToDirectory(gallery, directory, level+1);
    }


    private void syncGalleryFromDirectory(final PhotoSet gallery, final File directory, final int level)
        throws IOException
    {
        for (final File file : directory.listFiles()) {
            if (!isPhoto(file)) {
                message(level, "Skipping non-photo " + file + " on the gallery level");

            } else {
                final String name = Files.getName(file);
                final String extension = Files.getExtension(file);

                final Photo photo = getZenfolio().findPhotoByFileName(gallery, name + ".jpg");
                if ((photo == null) && "jpg".equals(extension)) {
                    addPhoto(gallery, directory, name, level);
                }
            }
        }
    }


    private void addPhoto(final PhotoSet gallery, final File directory, final String name, final int level)
        throws IOException
    {
        // After a photo is added, nothing gets refreshed on the client side, so I need to make
        // sure that all photos are added exactly once!

        final File jpgFile = new File(directory, name + ".jpg");

        if (jpgFile.exists()) {
            final String message = ((doIt) ? "adding" : "'adding'") + " photo" + " " + name;
            message(level, message);

            if (doIt) {
                final String status = getZenfolio().postFile(gallery, jpgFile);
                if (status != null) {
                    message(level, status);
                }
            }

        } else {
            message(level, "Raw conversions are not yet implemented. Can not add " + name);
        }
    }


    private void syncGalleryToDirectory(final PhotoSet gallery, final File directory, final int level) {
        // @todo
    }


    private boolean isPhoto(final File file) {
        final String extension = Files.getExtension(file);
        return
            "jpg".equals(extension) ||
            "crw".equals(extension) ||
            "cr2".equals(extension) ||
            "thm".equals(extension);
    }


    private final String rootDirectoryPath;


    private final boolean doIt;
}
