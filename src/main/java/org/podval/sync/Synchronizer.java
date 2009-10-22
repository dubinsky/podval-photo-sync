package org.podval.sync;

import org.podval.things.Indenter;
import org.podval.things.Crate;
import org.podval.things.Folder;
import org.podval.things.Thing;
import org.podval.things.ThingsException;

import java.io.File;
import java.io.IOException;

// @todo get rid of this and move the class to the "things" package
import org.podval.directory.Item;


public final class Synchronizer<L extends Thing, R extends Thing> {

    public Synchronizer(
        final Crate<L> left,
        final Crate<R> right,
        final String leftPath,
        final boolean doIt)
    {
        this.leftCrate = left;
        this.rightCrate = right;
        this.leftPath = leftPath;
        this.doIt = doIt;
        this.out = new Indenter(System.out);
    }


    public void run() throws ThingsException {
        leftCrate.open();
        rightCrate.open();
        syncFolder(leftCrate.getFolderByPath(leftPath), rightCrate.getRootFolder(), 0);
    }


    private void syncFolder(final Folder<L> left, final Folder<R> right, int level)
        throws ThingsException
    {
        out.println(level, left.getName());

        level++;

        syncRightToLeft(left, right, level);
        syncLeftToRight(left, right, level);
    }


    private void syncRightToLeft(final Folder<L> leftFolder, final Folder<R> rightFolder, int level)
        throws ThingsException
    {
        for (final R rightThing : rightFolder.getThings()) {
            if (rightFolder.hasFolders()) {
                out.message(level, "Skipping " + rightThing + " on the folder level");
            } else {
                if (!isPhoto(rightThing)) {
                    out.message(level, "Skipping non-photo " + rightThing + " on the gallery level");

                } else {
                    final L leftThing = leftFolder.getThing(rightThing.getName() + ".jpg");
                    if (leftThing == null) {
                        try {
                            addPhoto(leftFolder, rightThing, level);
                        } catch (final IOException e) {
                            throw new ThingsException(e);
                        }
                    }
                }
            }
        }

        // @todo skip the collections!

        for (final Folder<R> rightSubFolder : rightFolder.getFolders()) {
            final Folder<L> element = getElementForSubDirectory(leftFolder, rightSubFolder, level);

            if (element != null) {
                syncFolder(element, rightSubFolder, level);
            }
        }
    }


    private Folder<L> getElementForSubDirectory(
        final Folder<L> leftFolder,
        final Folder<R> rightFolder,
        final int level) throws ThingsException
    {
        Folder<L> result = null;

        final String name = rightFolder.getName();
        final boolean shouldHaveFolders = rightFolder.hasFolders();

        Folder<L> leftSubFolder = leftFolder.getFolder(name);

        if (leftSubFolder == null) {
            final String message =
                ((doIt) ? "creating" : "'creating'") + " " +
                ((shouldHaveFolders) ? "group" : "gallery") + " " + name;

            out.message(level, message);

            leftSubFolder = leftFolder.create(name, shouldHaveFolders, doIt);
        }

        if (leftSubFolder.canHaveFolders() && !shouldHaveFolders) {
            out.message(level, "Can have sub-folders, but should't: " + name);
        } if (!leftSubFolder.canHaveFolders() && shouldHaveFolders) {
            out.message(level, "Can't have sub-folders, but should: " + name);
        } else {
            result = leftSubFolder;
        }

        return result;
    }


    private void syncLeftToRight(
        final Folder<L> leftFolder,
        final Folder<R> rightFolder,
        int level) throws ThingsException
    {
        for (final Folder<L> leftSubFolder : leftFolder.getFolders()) {
            final String name = leftSubFolder.getName();
            final Folder<R> rightSubFolder = rightFolder.getFolder(name);
            if (rightSubFolder == null) {
                out.message(level, "No file for the element: " + name);
            }
        }
    }


    private void addPhoto(final Folder<L> leftFolder, final R right, final int level)
        throws IOException
    {
        final String name = right.getName();

        // @todo distinguish between "exist" and "available as local file"...
        final File file = rightCrate.toFile(right);
        if (file != null) {
            final String message = ((doIt) ? "adding" : "'adding'") + " photo" + " " + name;
            out.message(level, message);

            if (doIt) {
                try {
                    leftFolder.addFile(file.getName(), file);
                } catch (final ThingsException e) {
                    out.message(level, e.getMessage());
                }
            }

        } else {
            out.message(level, "Raw conversions are not yet implemented. Can not add " + name);
        }
    }


    private boolean isPhoto(final R rightThing) {
        final Item item = (Item) rightThing;

        return
            item.exists("jpg") ||
            item.exists("crw") ||
            item.exists("cr2") ||
            item.exists("thm");
    }


    private final Crate<L> leftCrate;


    private final Crate<R> rightCrate;


    private final String leftPath;


    private final boolean doIt;


    private final Indenter out;
}
