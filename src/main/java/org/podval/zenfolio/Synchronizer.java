package org.podval.zenfolio;

import org.podval.directory.Directory;
import org.podval.directory.Item;

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
    protected void run(final GroupNg rootGroup) throws RemoteException, IOException {
        syncGroup(rootGroup, new Directory(rootDirectoryPath), 0);
    }


    private void syncGroup(final GroupNg group, final Directory directory, int level)
        throws RemoteException, IOException
    {
        println(level, group.getName());

        level++;

        syncGroupFromDirectory(group, directory, level);
        syncGroupToDirectory(group, directory, level);
    }


    private void syncGroupFromDirectory(final GroupNg group, final Directory directory, int level)
        throws RemoteException, IOException
    {
        for (final Item item : directory.getItems()) {
            message(level, "Skipping " + item + " on the group level");
        }

        for (final File subDirectory : directory.getSubDirectories()) {
            sync(group, new Directory(subDirectory), level);
        }
    }


    private void sync(final GroupNg group, final Directory directory, final int level)
        throws RemoteException, IOException
    {
        final String name = directory.getName();
        final boolean shouldBeGroup = directory.hasSubDirectories();

        ZenfolioDirectory element = group.find(name);

        if (element == null) {
            final String message =
                ((doIt) ? "creating" : "'creating'") + " " +
                ((shouldBeGroup) ? "group" : "gallery") + " " + name;

            message(level, message);

            element = create(group, name, shouldBeGroup, doIt);
        }

        if (element instanceof GroupNg) {
            if (!shouldBeGroup) {
                message(level, "Is a group, but should not be: " + name);
            } else {
                syncGroup((GroupNg) element, directory, level);
            }

        } else if (element instanceof Gallery) {
            if (shouldBeGroup) {
                message(level, "Is not a group, but should be: " + name);
            } else {
                final Gallery gallery = (Gallery) element;
                gallery.populate();
                syncGallery(gallery, directory, level);
            }
        }
    }


    private ZenfolioDirectory create(
        final GroupNg group,
        final String name,
        final boolean shouldBeGroup,
        final boolean doIt) throws RemoteException
    {
        final ZenfolioDirectory result;

        if (shouldBeGroup) {
            result = (doIt) ? group.createGroup(name) : group.createFakeGroup(name);
        } else {
            result = (doIt) ? group.createGallery(name) : group.createFakeGallery(name);
        }

        return result;
    }



    private void syncGroupToDirectory(final GroupNg group, final Directory directory, int level) {
        for (final ArrayOfChoice1Choice choice : group.getElements()) {
            final GroupElement element = (choice.getGroup() != null) ? choice.getGroup() : choice.getPhotoSet();
            final String name = element.getTitle();
            final File subDirectory = directory.getSubDirectory(name);
            if (subDirectory == null) {
                message(level, "No file for the element: " + name);
            }
        }
    }


    private void syncGallery(final Gallery gallery, final Directory directory, final int level)
        throws RemoteException, IOException
    {
        println(level, gallery.getName());

        // @todo skip the collections!

        syncGalleryFromDirectory(gallery, directory, level+1);
        syncGalleryToDirectory(gallery, directory, level+1);
    }


    private void syncGalleryFromDirectory(final Gallery gallery, final Directory directory, final int level)
        throws IOException
    {
        for (final Item item : directory.getItems()) {
            if (!isPhoto(item)) {
                message(level, "Skipping non-photo " + item + " on the gallery level");

            } else {
                final Photo photo = gallery.findPhotoByFileName(item.getName() + ".jpg");
                if (photo == null) {
                    addPhoto(gallery, directory, item, level);
                }
            }
        }
    }


    private void addPhoto(final Gallery gallery, final Directory directory, final Item item, final int level)
        throws IOException
    {
        final String name = item.getName();

        if (item.exists("jpg")) {
            final String message = ((doIt) ? "adding" : "'adding'") + " photo" + " " + name;
            message(level, message);

            if (doIt) {
                final String status = gallery.postFile(item.get("jpg"));
                if (status != null) {
                    message(level, status);
                }
            }

        } else {
            message(level, "Raw conversions are not yet implemented. Can not add " + name);
        }
    }


    private void syncGalleryToDirectory(final Gallery gallery, final Directory directory, final int level) {
        // @todo
    }


    private boolean isPhoto(final Item item) {
        return
            item.exists("jpg") ||
            item.exists("crw") ||
            item.exists("cr2") ||
            item.exists("thm");
    }


    private final String rootDirectoryPath;


    private final boolean doIt;
}
