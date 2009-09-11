package org.podval.zenfolio;

import java.rmi.RemoteException;

import java.security.NoSuchAlgorithmException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public abstract class Processor {

    protected Processor(
        final String login,
        final String password,
        final String groupPath) throws RemoteException
    {
        this.zenfolio = new Zenfolio(login, password);

        this.groupPath = groupPath;
    }


    protected final Zenfolio getZenfolio() {
        return zenfolio;
    }


    public final void run()  throws RemoteException, UnsupportedEncodingException,
        NoSuchAlgorithmException, IOException
    {
        zenfolio.connect();

        run(findGroupByPath(groupPath));
    }


    public GroupNg findGroupByPath(final String path) throws RemoteException {
        GroupNg result = zenfolio.loadGroupHierarchy();

        if (path != null) {
            for (final String name : path.split("/")) {
                if (!name.isEmpty()) {
                    result = asGroup(result.find(name));
                }
            }
        }

        return result;
    }


    private GroupNg asGroup(final ZenfolioDirectory element) {
        if (!(element instanceof GroupNg)) {
            throw new IllegalArgumentException("Not a group: " + element);
        }

        return (GroupNg) element;
    }


    protected abstract void run(final GroupNg rootGroup) throws RemoteException, IOException;


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
