package org.podval.sync;

import org.podval.things.Synchronizer;
import org.podval.things.Lister;
import org.podval.things.Crate;
import org.podval.things.CrateFactory;
import org.podval.things.CrateTicket;
import org.podval.things.ThingsException;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;


public final class Main {

    public static void main(final String[] args) {
        final Main main = new Main();
        main.run(args);
    }


    private Main() {
        this.options = new Options();

        options.addOption("s", "suffix", true, "suffix that selects a subtree");
        options.addOption("d", "dry-run", false, "dry run");
    }


    private void run(final String[] args) {
        try {
            parse(args);
            run();
        } catch (final ParseException e) {
            System.err.println("Error: " + e.getMessage());
            printUsage();
            // @todo available schemes...
        } catch (final ThingsException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    private void printUsage() {
        // @todo ?  and non-option parameters...
//        new HelpFormatter().printUsage(new java.io.PrintWriter(System.out), 80, "Podval Photo Sync", options);
    }


    private void parse(final String[] args) throws ParseException {
        final CommandLine commandLine = new PosixParser().parse(options, args);

        final String suffix = commandLine.getOptionValue("s");

        final String[] realArgs = commandLine.getArgs();

        if (realArgs.length > 2) {
            throw new ParseException("Too many arguments");
        }

        firstTicket = UriParser.fromUri(realArgs[0], suffix);

        if (realArgs.length > 1) {
            secondTicket = UriParser.fromUri(realArgs[1], suffix);
        }

        isDryRun = commandLine.hasOption("d");
    }


    private void run() throws ThingsException {
        final Crate firstCrate = CrateFactory.getCrate(firstTicket);

        if (secondTicket == null) {
            new Lister(firstCrate).run();
        } else {
            final Crate secondCrate = CrateFactory.getCrate(secondTicket);
            new Synchronizer(firstCrate, secondCrate, !isDryRun).run();
        }
    }


    private final Options options;


    private CrateTicket firstTicket;


    private CrateTicket secondTicket;


    private boolean isDryRun;
}
