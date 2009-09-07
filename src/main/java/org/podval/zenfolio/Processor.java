package org.podval.zenfolio;

import com.zenfolio.www.api._1_1.Group;

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

        run(zenfolio.findGroup(groupPath));
    }


    protected abstract void run(final Group rootGroup) throws RemoteException, IOException;


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
