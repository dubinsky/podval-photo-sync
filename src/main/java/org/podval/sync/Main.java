package org.podval.sync;

import org.podval.things.Lister;
import org.podval.things.Crate;

import org.podval.directory.Root;

import org.podval.zenfolio.Zenfolio;

//import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;


public final class Main {

    public static void main(final String[] args) throws Exception {
        final Options options = new Options();

        options.addOption("s", "suffix", true, "suffix that selects a subtree");
        options.addOption("u", "user", true, "Zenfolio user name");
        options.addOption("p", "password", true, "Zenfolio password");
        options.addOption("z", "zenfolio-path", true, "Zenfolio path");
        options.addOption("f", "file-path", true, "file path");

        final CommandLine commandLine = new PosixParser().parse(options, args);

//        commandLine.getArgs();

        final String login = commandLine.getOptionValue("u");
        final String password = commandLine.getOptionValue("p");
        final String suffix = commandLine.getOptionValue("s");
        final String groupPath = addSuffix(commandLine.getOptionValue("z"), suffix);
        final String path = addSuffix(commandLine.getOptionValue("f"), suffix);


        final Crate crate = new Zenfolio(login, password);

        if (path == null) {
            new Lister(crate, groupPath).run();
        } else {
            final Crate directory = new Root(path, "jpg");
            new Synchronizer(crate, directory, groupPath, false).run();
        }
    }


    private static String addSuffix(final String what, final String suffix) {
        return ((what == null) || (suffix == null)) ? what : what + suffix;
    }
}
