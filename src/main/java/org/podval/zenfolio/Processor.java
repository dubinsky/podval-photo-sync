package org.podval.zenfolio;

import org.podval.things.Folder;
import org.podval.things.ThingsException;

import java.security.NoSuchAlgorithmException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;


public abstract class Processor {

    protected Processor(
        final String login,
        final String password,
        final String groupPath) throws ThingsException
    {
        try {
            this.zenfolio = new Zenfolio(login, password);
        } catch (final RemoteException e) {
            throw new ThingsException(e);
        }

        this.groupPath = groupPath;
    }


    public final void run() throws ThingsException, UnsupportedEncodingException,
        NoSuchAlgorithmException, IOException
    {
        zenfolio.connect();

        run(findGroupByPath(groupPath));
    }


    public Folder<Photo> findGroupByPath(final String path) throws ThingsException {
        Folder<Photo> result;

        try {
            result = zenfolio.loadGroupHierarchy();
        } catch (final RemoteException e) {
            throw new ThingsException(e);
        }

        if (path != null) {
            for (final String name : path.split("/")) {
                if (!name.isEmpty()) {
                    result = result.getFolder(name);
                    checkCanHaveSubDirectories(result);
                }
            }
        }

        return result;
    }


    private void checkCanHaveSubDirectories(final Folder<Photo> element) {
        if (!element.canHaveFolders()) {
            throw new IllegalArgumentException("Not a group: " + element);
        }
    }


    protected abstract void run(final Folder<Photo> rootDirectory) throws ThingsException, IOException;


    protected final void message(final int level, final String line) {
        println(level, "*** " + line);
    }


    protected final void println(final int level, final String line) {
        indent(level);
        System.out.println(line);
    }


    private void indent(final int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
    }


    private Zenfolio zenfolio;


    private final String groupPath;
}
