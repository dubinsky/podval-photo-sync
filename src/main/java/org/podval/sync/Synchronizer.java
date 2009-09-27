package org.podval.sync;

import org.podval.things.Crate;
import org.podval.things.Folder;
import org.podval.things.Thing;
import org.podval.things.ThingsException;

import org.podval.directory.Item;

import org.podval.zenfolio.Gallery;

import java.io.IOException;


public final class Synchronizer<L extends Thing, R extends Thing> {

    public Synchronizer(
        final Crate<L> left,
        final Crate<R> right,
        final String groupPath,
        final boolean doIt)
    {
        this.leftCrate = left;
        this.rightCrate = right;
        this.groupPath = groupPath;
        this.doIt = doIt;
        this.out = new Indenter(System.out);
    }


    protected void run() throws ThingsException {
        leftCrate.open();
        rightCrate.open();
        syncGroup(leftCrate.getFolderByPath(groupPath), rightCrate.getRootFolder(), 0);
    }


    private void syncGroup(final Folder<L> left, final Folder<R> right, int level)
        throws ThingsException
    {
        out.println(level, left.getName());

        level++;

        syncToZenfolio(left, right, level);
        syncFromZenfolio(left, right, level);
    }


    private void syncToZenfolio(final Folder<L> left, final Folder<R> right, int level)
        throws ThingsException
    {
        for (final R item : right.getThings()) {
            if (right.hasFolders()) {
                out.message(level, "Skipping " + item + " on the group level");
            } else {
                if (!isPhoto(item)) {
                    out.message(level, "Skipping non-photo " + item + " on the gallery level");

                } else {
                    final L thing = left.getThing(item.getName() + ".jpg");
                    if (thing == null) {
                        try {
                            addPhoto(left, item, level);
                        } catch (final IOException e) {
                            throw new ThingsException(e);
                        }
                    }
                }
            }
        }

        // @todo skip the collections!

        for (final Folder<R> subDirectory : right.getFolders()) {
            final Folder<L> element = getElementForSubDirectory(left, subDirectory, level);

            if (element != null) {
                syncGroup(element, subDirectory, level);
            }
        }
    }


    private Folder<L> getElementForSubDirectory(
        final Folder<L> zenfolioDirectory,
        final Folder<R> directory,
        final int level) throws ThingsException
    {
        Folder<L> result = null;

        final String name = directory.getName();
        final boolean shouldBeGroup = directory.hasFolders();

        Folder<L> element = zenfolioDirectory.getFolder(name);

        if (element == null) {
            final String message =
                ((doIt) ? "creating" : "'creating'") + " " +
                ((shouldBeGroup) ? "group" : "gallery") + " " + name;

            out.message(level, message);

            element = create(zenfolioDirectory, name, shouldBeGroup, doIt);
        }

        if (element.canHaveFolders() && !shouldBeGroup) {
            out.message(level, "Is a group, but should not be: " + name);
        } if (!element.canHaveFolders() && shouldBeGroup) {
            out.message(level, "Is not a group, but should be: " + name);
        } else {
            result = element;
        }

        return result;
    }


    private Folder<L> create(
        final Folder<L> zenfolioDirectory,
        final String name,
        final boolean shouldBeGroup,
        final boolean doIt) throws ThingsException
    {
        final Folder<L> result;

        result = (doIt) ?
            zenfolioDirectory.createFolder(name, shouldBeGroup, !shouldBeGroup) :
            zenfolioDirectory.createFakeFolder(name, shouldBeGroup, !shouldBeGroup);

        return result;
    }


    private void syncFromZenfolio(
        final Folder<L> zenfolioDirectory,
        final Folder<R> directory,
        int level) throws ThingsException
    {
        for (final Folder zenfolioSubDirectory : zenfolioDirectory.getFolders()) {
            final String name = zenfolioSubDirectory.getName();
            final Folder<R> subDirectory = directory.getFolder(name);
            if (subDirectory == null) {
                out.message(level, "No file for the element: " + name);
            }
        }
    }


    private void addPhoto(final Folder<L> zenfolioDirectory, final R right, final int level)
        throws IOException
    {
        final String name = right.getName();

        final Item item = (Item) right;
        if (item.exists("jpg")) {
            final String message = ((doIt) ? "adding" : "'adding'") + " photo" + " " + name;
            out.message(level, message);

            if (doIt) {
                // @todo factor OUT!!!
                final Gallery gallery = (Gallery) zenfolioDirectory;
                final String status = gallery.postFile(item.get("jpg"));
                if (status != null) {
                    out.message(level, status);
                }
            }

        } else {
            out.message(level, "Raw conversions are not yet implemented. Can not add " + name);
        }
    }


    private boolean isPhoto(final R right) {
        final Item item = (Item) right;

        return
            item.exists("jpg") ||
            item.exists("crw") ||
            item.exists("cr2") ||
            item.exists("thm");
    }


    private final Crate<L> leftCrate;


    private final Crate<R> rightCrate;


    private final String groupPath;


    private final boolean doIt;


    private final Indenter out;
}
