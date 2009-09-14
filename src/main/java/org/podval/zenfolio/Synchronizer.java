package org.podval.zenfolio;

import org.podval.things.Folder;

import org.podval.directory.Directory;
import org.podval.directory.Item;

import java.rmi.RemoteException;

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
    protected void run(final ZenfolioDirectory rootDirectory) throws RemoteException, IOException {
        syncGroup(rootDirectory, new Directory(rootDirectoryPath), 0);
    }


    private void syncGroup(final ZenfolioDirectory zenfolioDirectory, final Directory directory, int level)
        throws RemoteException, IOException
    {
        println(level, zenfolioDirectory.getName());

        level++;

        syncToZenfolio(zenfolioDirectory, directory, level);
        syncFromZenfolio(zenfolioDirectory, directory, level);
    }


    private void syncToZenfolio(final ZenfolioDirectory zenfolioDirectory, final Directory directory, int level)
        throws RemoteException, IOException
    {
        for (final Item item : directory.getItems()) {
            if (directory.hasSubDirectories()) {
                message(level, "Skipping " + item + " on the group level");
            } else {
                if (!isPhoto(item)) {
                    message(level, "Skipping non-photo " + item + " on the gallery level");

                } else {
                    final Photo photo = zenfolioDirectory.getItem(item.getName() + ".jpg");
                    if (photo == null) {
                        addPhoto(zenfolioDirectory, item, level);
                    }
                }
            }
        }

        // @todo skip the collections!

        for (final Directory subDirectory : directory.getSubDirectories()) {
            final ZenfolioDirectory element = getElementForSubDirectory(zenfolioDirectory, subDirectory, level);

            if (element != null) {
                syncGroup(element, subDirectory, level);
            }
        }
    }


    private ZenfolioDirectory getElementForSubDirectory(
        final ZenfolioDirectory zenfolioDirectory,
        final Directory directory,
        final int level) throws RemoteException, IOException
    {
        ZenfolioDirectory result = null;

        final String name = directory.getName();
        final boolean shouldBeGroup = directory.hasSubDirectories();

        ZenfolioDirectory element = zenfolioDirectory.getSubDirectory(name);

        if (element == null) {
            final String message =
                ((doIt) ? "creating" : "'creating'") + " " +
                ((shouldBeGroup) ? "group" : "gallery") + " " + name;

            message(level, message);

            element = create(zenfolioDirectory, name, shouldBeGroup, doIt);
        }

        if (element.canHaveSubDirectories() && !shouldBeGroup) {
            message(level, "Is a group, but should not be: " + name);
        } if (!element.canHaveSubDirectories() && shouldBeGroup) {
            message(level, "Is not a group, but should be: " + name);
        } else {
            result = element;
        }

        return result;
    }


    private ZenfolioDirectory create(
        final ZenfolioDirectory zenfolioDirectory,
        final String name,
        final boolean shouldBeGroup,
        final boolean doIt) throws RemoteException
    {
        final ZenfolioDirectory result;

        result = (doIt) ?
            zenfolioDirectory.createSubDirectory(name, shouldBeGroup, !shouldBeGroup) :
            zenfolioDirectory.createFakeSubDirectory(name, shouldBeGroup, !shouldBeGroup);

        return result;
    }


    private void syncFromZenfolio(final ZenfolioDirectory zenfolioDirectory, final Directory directory, int level) {
        for (final Folder zenfolioSubDirectory : zenfolioDirectory.getSubDirectories()) {
            final String name = zenfolioSubDirectory.getName();
            final Directory subDirectory = directory.getSubDirectory(name);
            if (subDirectory == null) {
                message(level, "No file for the element: " + name);
            }
        }
    }


    private void addPhoto(final ZenfolioDirectory zenfolioDirectory, final Item item, final int level)
        throws IOException
    {
        final String name = item.getName();

        if (item.exists("jpg")) {
            final String message = ((doIt) ? "adding" : "'adding'") + " photo" + " " + name;
            message(level, message);

            if (doIt) {
                // @todo factor OUT!!!
                final Gallery gallery = (Gallery) zenfolioDirectory;
                final String status = gallery.postFile(item.get("jpg"));
                if (status != null) {
                    message(level, status);
                }
            }

        } else {
            message(level, "Raw conversions are not yet implemented. Can not add " + name);
        }
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
