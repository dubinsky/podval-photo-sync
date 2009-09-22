package org.podval.sync;

import org.podval.things.Crate;
import org.podval.things.Folder;
import org.podval.things.Thing;
import org.podval.things.ThingsException;


public final class Lister<T extends Thing> extends Processor<T> {

    public Lister(final Crate crate, final String groupPath)
        throws ThingsException
    {
        super(crate, groupPath);
    }


    @Override
    protected void run(final Folder<T> rootFolder) throws ThingsException {
        list(rootFolder, 0);
    }


    private void list(final Folder<T> folder, int level) throws ThingsException {
        println(level, folder.getName());

        level++;

        for (final Folder<T> subFolder : folder.getFolders()) {
            list(subFolder, level);
        }

        for (final T thing : folder.getThings()) {
        }
    }
}
