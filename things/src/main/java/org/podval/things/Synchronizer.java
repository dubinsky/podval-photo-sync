package org.podval.things;

import java.io.File;
import java.io.IOException;


public final class Synchronizer<F extends Thing, T extends Thing> {

    public Synchronizer(
        final Crate<F> fromCrate,
        final Crate<T> toCrate,
        final boolean doIt)
    {
        this.fromCrate = fromCrate;
        this.toCrate = toCrate;
        this.doIt = doIt;

        this.out = new Indenter(System.out);
    }


    public void run() throws ThingsException {
        converter = ThingsConverter.get(fromCrate.getScheme(), toCrate.getScheme());

        fromCrate.open();
        toCrate.open();

        syncFolder(fromCrate.getRootFolder(), toCrate.getRootFolder(), 0);
    }


    private void syncFolder(final Folder<F> from, final Folder<T> to, int level)
        throws ThingsException
    {
        out.println(level, from.getName());

        int nextLevel = level + 1;

        syncRightToLeft(from, to, nextLevel);
        syncLeftToRight(from, to, nextLevel);
    }


    private void syncRightToLeft(final Folder<F> fromFolder, final Folder<T> toFolder, int level)
        throws ThingsException
    {
        for (final F fromThing : fromFolder.getThings()) {
            if (fromFolder.hasFolders()) {
                out.message(level, "Skipping " + fromThing + " on the folder level");
            } else {
                if (!converter.isConvertible(fromThing)) {
                    out.message(level, "Skipping non-convertible " + fromThing + " on the folder level");

                } else {
                    if (toFolder.getThing(converter.getName(fromThing)) == null) {
                        try {
                            addPhoto(fromThing, toFolder, level);
                        } catch (final IOException e) {
                            throw new ThingsException(e);
                        }
                    }
                }
            }
        }

        // @todo skip the collections!

        for (final Folder<F> fromSubFolder : fromFolder.getFolders()) {
            final Folder<T> toSubFolder = getElementForSubDirectory(fromSubFolder, toFolder, level);

            if (toSubFolder != null) {
                syncFolder(fromSubFolder, toSubFolder, level);
            }
        }
    }


    private Folder<T> getElementForSubDirectory(
        final Folder<F> fromFolder,
        final Folder<T> toFolder,
        final int level) throws ThingsException
    {
        Folder<T> result = null;

        final String name = fromFolder.getName();
        final boolean shouldHaveFolders = fromFolder.hasFolders();

        Folder<T> toSubFolder = toFolder.getFolder(name);

        if (toSubFolder == null) {
            final String message =
                ((doIt) ? "creating" : "'creating'") + " " +
                ((shouldHaveFolders) ? "group" : "gallery") + " " + name;

            out.message(level, message);

            toSubFolder = toFolder.create(name, shouldHaveFolders, doIt);
        }

        if (toSubFolder.canHaveFolders() && !shouldHaveFolders) {
            out.message(level, "Can have sub-folders, but should't: " + name);
        } if (!toSubFolder.canHaveFolders() && shouldHaveFolders) {
            out.message(level, "Can't have sub-folders, but should: " + name);
        } else {
            result = toSubFolder;
        }

        return result;
    }


    private void syncLeftToRight(
        final Folder<F> fromFolder,
        final Folder<T> toFolder,
        int level) throws ThingsException
    {
        for (final Folder<T> toSubFolder : toFolder.getFolders()) {
            final String name = toSubFolder.getName();
            final Folder<F> fromSubFolder = fromFolder.getFolder(name);
            if (fromSubFolder == null) {
                out.message(level, "No file for the element: " + name);
            }
        }
    }


    private void addPhoto(final F fromThing, final Folder<T> toFolder, final int level)
        throws IOException
    {
        final String name = fromThing.getName();

        // @todo distinguish between "exist" and "available as local file"...
        final File file = converter.toFile(fromThing);
        if (file != null) {
            final String message = ((doIt) ? "adding" : "'adding'") + " thing" + " " + name;
            out.message(level, message);

            if (doIt) {
                try {
                    toFolder.addFile(file.getName(), file);
                } catch (final ThingsException e) {
                    out.message(level, e.getMessage());
                }
            }

        } else {
            out.message(level, "Raw conversions are not yet implemented. Can not add " + name);
        }
    }


    private final Crate<F> fromCrate;


    private final Crate<T> toCrate;


    private ThingsConverter<F, T> converter;


    private final boolean doIt;


    private final Indenter out;
}
