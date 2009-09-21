package org.podval.zenfolio;

import org.podval.things.Folder;
import org.podval.things.ThingsException;


public final class Lister extends Processor {

    public Lister(
        final String login,
        final String password,
        final String groupPath) throws ThingsException
    {
        super(login, password, groupPath);
    }


    @Override
    protected void run(final Folder<Photo> rootDirectory) throws ThingsException {
        list(rootDirectory, 0);
    }


    private void list(final Folder<Photo> directory, int level) throws ThingsException {
        println(level, directory.getName());

        level++;

        for (final Folder<Photo> subDirectory : directory.getFolders()) {
            list(subDirectory, level);
        }

        for (final Photo photo : directory.getThings()) {
        }
    }
}
