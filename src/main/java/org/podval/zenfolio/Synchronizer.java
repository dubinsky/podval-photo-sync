package org.podval.zenfolio;

import org.podval.things.Folder;
import org.podval.things.ThingsException;

import org.podval.directory.Directory;
import org.podval.directory.Item;

import java.io.IOException;


public final class Synchronizer extends Processor {

    public Synchronizer(
        final String login,
        final String password,
        final String groupPath,
        final String rootDirectoryPath,
        final boolean doIt) throws ThingsException
    {
        super(login, password, groupPath);

        this.rootDirectoryPath = rootDirectoryPath;
        this.doIt = doIt;
    }


    @Override
    protected void run(final Folder<Photo> rootDirectory) throws ThingsException, IOException {
        syncGroup(rootDirectory, new Directory(rootDirectoryPath), 0);
    }


    private void syncGroup(final Folder<Photo> zenfolioDirectory, final Directory directory, int level)
        throws ThingsException, IOException
    {
        println(level, zenfolioDirectory.getName());

        level++;

        syncToZenfolio(zenfolioDirectory, directory, level);
        syncFromZenfolio(zenfolioDirectory, directory, level);
    }


    private void syncToZenfolio(final Folder<Photo> zenfolioDirectory, final Directory directory, int level)
        throws ThingsException, IOException
    {
        for (final Item item : directory.getThings()) {
            if (directory.hasFolders()) {
                message(level, "Skipping " + item + " on the group level");
            } else {
                if (!isPhoto(item)) {
                    message(level, "Skipping non-photo " + item + " on the gallery level");

                } else {
                    final Photo photo = zenfolioDirectory.getThing(item.getName() + ".jpg");
                    if (photo == null) {
                        addPhoto(zenfolioDirectory, item, level);
                    }
                }
            }
        }

        // @todo skip the collections!

        for (final Directory subDirectory : directory.getFolders()) {
            final Folder<Photo> element = getElementForSubDirectory(zenfolioDirectory, subDirectory, level);

            if (element != null) {
                syncGroup(element, subDirectory, level);
            }
        }
    }


    private Folder<Photo> getElementForSubDirectory(
        final Folder<Photo> zenfolioDirectory,
        final Directory directory,
        final int level) throws ThingsException, IOException
    {
        Folder<Photo> result = null;

        final String name = directory.getName();
        final boolean shouldBeGroup = directory.hasFolders();

        Folder<Photo> element = zenfolioDirectory.getFolder(name);

        if (element == null) {
            final String message =
                ((doIt) ? "creating" : "'creating'") + " " +
                ((shouldBeGroup) ? "group" : "gallery") + " " + name;

            message(level, message);

            element = create(zenfolioDirectory, name, shouldBeGroup, doIt);
        }

        if (element.canHaveFolders() && !shouldBeGroup) {
            message(level, "Is a group, but should not be: " + name);
        } if (!element.canHaveFolders() && shouldBeGroup) {
            message(level, "Is not a group, but should be: " + name);
        } else {
            result = element;
        }

        return result;
    }


    private Folder<Photo> create(
        final Folder<Photo> zenfolioDirectory,
        final String name,
        final boolean shouldBeGroup,
        final boolean doIt) throws ThingsException
    {
        final Folder<Photo> result;

        result = (doIt) ?
            zenfolioDirectory.createFolder(name, shouldBeGroup, !shouldBeGroup) :
            zenfolioDirectory.createFakeFolder(name, shouldBeGroup, !shouldBeGroup);

        return result;
    }


    private void syncFromZenfolio(
        final Folder<Photo> zenfolioDirectory,
        final Directory directory,
        int level) throws ThingsException
    {
        for (final Folder zenfolioSubDirectory : zenfolioDirectory.getFolders()) {
            final String name = zenfolioSubDirectory.getName();
            final Directory subDirectory = directory.getFolder(name);
            if (subDirectory == null) {
                message(level, "No file for the element: " + name);
            }
        }
    }


    private void addPhoto(final Folder<Photo> zenfolioDirectory, final Item item, final int level)
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
