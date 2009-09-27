package org.podval.sync;

import org.podval.things.Crate;
import org.podval.things.Folder;
import org.podval.things.Thing;
import org.podval.things.ThingsException;


public final class Lister<T extends Thing> {

    public Lister(final Crate crate, final String groupPath) {
        this.crate = crate;
        this.groupPath = groupPath;
        this.out = new Indenter(System.out);
    }


    protected void run() throws ThingsException {
        crate.open();
        final Folder<T> rootFolder = crate.getFolderByPath(groupPath);
        list(rootFolder, 0);
    }


    private void list(final Folder<T> folder, int level) throws ThingsException {
        out.println(level, folder.getName());

        level++;

        for (final Folder<T> subFolder : folder.getFolders()) {
            list(subFolder, level);
        }

        for (final T thing : folder.getThings()) {
        }
    }


    private final Crate crate;


    private final String groupPath;


    private final Indenter out;
}
