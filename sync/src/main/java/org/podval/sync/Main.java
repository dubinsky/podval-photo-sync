package org.podval.sync;

import org.podval.things.Synchronizer;
import org.podval.things.Lister;
import org.podval.things.Crate;
import org.podval.things.CrateFactory;
import org.podval.things.CrateTicket;
import org.podval.things.ThingsException;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;


public final class Main {

    public static void main(final String[] args) {
        final Main main = new Main();
        main.run(args);
    }


    private Main() {
        this.options = new Options();

        options.addOption(OptionBuilder.
            withLongOpt("help").
            withDescription("print this help message").
            create('h'));

        options.addOption(OptionBuilder.
            withLongOpt("suffix").
            hasArg().withArgName("suffix").
            withDescription("suffix that selects a subtree").
            create('s'));

        options.addOption(OptionBuilder.
            withLongOpt("dry-run").
            withDescription("dry run").
            create('d'));
    }


    private void run(final String[] args) {
        try {
            parse(args);
            run();
        } catch (final ParseException e) {
            System.err.println("Error: " + e.getMessage());
            printUsage();
            // @todo available schemes...
            // @todo help
        } catch (final ThingsException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    private void printUsage() {
        new HelpFormatter().printHelp("photo-sync [ options ] <from-uri> <to-uri>", options);

        System.out.println(
            "\n" +
            "uri: scheme://[user:password@][host]/path\n"
        );

        printSchemes();
    }


    private void printSchemes() {
        System.out.println("  Available schemes:");
        for (final CrateFactory crateFactory : CrateFactory.getAll()) {
            System.out.println("    " + crateFactory.getScheme());
        }
    }


    private void parse(final String[] args) throws ParseException {
        final CommandLine commandLine = new GnuParser().parse(options, args);

        final String suffix = commandLine.getOptionValue("s");

        final String[] realArgs = commandLine.getArgs();

        if (commandLine.hasOption('h')) {
            printUsage();
        } else {
            if (realArgs.length < 1) {
                throw new ParseException("Too few arguments");
            }

            if (realArgs.length > 2) {
                throw new ParseException("Too many arguments");
            }

            firstTicket = UriParser.fromUri(realArgs[0], suffix);

            if (realArgs.length > 1) {
                secondTicket = UriParser.fromUri(realArgs[1], suffix);
            }

            isDryRun = commandLine.hasOption("d");
        }
    }


    private void run() throws ThingsException {
/////        org.podval.picasa.model.Util.enableLogging();

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
