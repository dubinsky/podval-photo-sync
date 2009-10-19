package org.podval.sync;

import org.podval.things.Crate;

import org.podval.directory.Root;

import org.podval.zenfolio.Zenfolio;

import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.Option;


public final class Main {

    public static void main(final String[] args) throws Exception {
        final String login = args[0];
        final String password = getArg(args, 1);
        String groupPath =  getArg(args, 2);
        String path =  getArg(args, 3);
        final String suffix = getArg(args, 4);

        if (suffix != null) {
            groupPath += suffix;
            path += suffix;
        }

        final Crate crate = new Zenfolio(login, password);

        if (path == null) {
            new Lister(crate, groupPath).run();
        } else {
            new Synchronizer(crate, new Root(path), groupPath, false).run();
        }
    }


    private static String getArg(final String[] args, final int n) {
        return (args.length > n) ? args[n] : null;
    }
}