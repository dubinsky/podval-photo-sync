package org.podval.things;

import java.io.File;
import java.io.IOException;


public final class Synchronizer<L extends Thing, R extends Thing> {

    public Synchronizer(
        final Crate<L> left,
        final Crate<R> right,
        final boolean doIt)
    {
        this.leftCrate = left;
        this.rightCrate = right;
        this.doIt = doIt;

        this.out = new Indenter(System.out);
    }


    public void run() throws ThingsException {
        converter = ThingsConverter.get(rightCrate.getScheme(), leftCrate.getScheme());

        leftCrate.open();
        rightCrate.open();
        syncFolder(leftCrate.getRootFolder(), rightCrate.getRootFolder(), 0);
    }


    private void syncFolder(final Folder<L> left, final Folder<R> right, int level)
        throws ThingsException
    {
        out.println(level, left.getName());

        int nextLevel = level + 1;

        syncRightToLeft(left, right, nextLevel);
        syncLeftToRight(left, right, nextLevel);
    }


    private void syncRightToLeft(final Folder<L> leftFolder, final Folder<R> rightFolder, int level)
        throws ThingsException
    {
        for (final R rightThing : rightFolder.getThings()) {
            if (rightFolder.hasFolders()) {
                out.message(level, "Skipping " + rightThing + " on the folder level");
            } else {
                if (!converter.isConvertible(rightThing)) {
                    out.message(level, "Skipping non-convertible " + rightThing + " on the folder level");

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
        final File file = converter.toFile(right);
        if (file != null) {
            final String message = ((doIt) ? "adding" : "'adding'") + " thing" + " " + name;
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


    private final Crate<L> leftCrate;


    private final Crate<R> rightCrate;


    private ThingsConverter<R, L> converter;


    private final boolean doIt;


    private final Indenter out;
}
