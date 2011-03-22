package org.podval.sync;

import org.podval.things.Synchronizer;
import org.podval.things.Lister;
import org.podval.things.Crate;
import org.podval.things.CrateFactory;
import org.podval.things.CrateTicket;
import org.podval.things.ThingsException;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.CmdLineException;


public final class Main {

    public static void main(final String[] args) {
        final Main main = new Main();
        main.run(args);
    }


    @Option(name="-h", aliases="--help", usage="display help message")
    private boolean help = false;


    @Option(name="-d", aliases="--dry-run", usage="dry run")
    private boolean isDryRun = false;


    @Option(name="-s", aliases="--suffux", usage="suffix that selects a subtree")
    private String suffix;


    @Argument(index=0, required=true)
    private void setFirstUri(final String value) throws CmdLineException {
        firstTicket = UriParser.fromUri(value, suffix);
    }


    @Argument(index=1, required=false)
    private void setSecondUri(final String value) throws CmdLineException {
        secondTicket = UriParser.fromUri(value, suffix);
    }


    private void run(final String[] args) {
        parse(args);

        try {
            run();
        } catch (final ThingsException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    private void printSchemes() {
        System.out.println("  Available schemes:");
        for (final CrateFactory crateFactory : CrateFactory.getAll()) {
            System.out.println("    " + crateFactory.getScheme());
        }
    }


    private void parse(final String[] args) {
        final CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            // TODO: ?
//            if (realArgs.length > 2) {
//                throw new ParseException("Too many arguments");
//            }
        } catch (final CmdLineException e) {
            System.err.println("Error: " + e.getMessage());
            printUsage(parser);
            System.exit(1);
        }
        if (help) {
            printUsage(parser);
            System.exit(0);
        }
    }


    private void printUsage(final CmdLineParser parser) {
        System.err.println("photo-sync [ options ] <from-uri> <to-uri>");

        parser.setUsageWidth(80);
        parser.printUsage(System.err);
        System.err.println();

        System.err.println(
            "\n" +
            "uri: scheme://[user:password@][host]/path\n"
        );

        printSchemes();
    }


    private void run() throws ThingsException {
        // TODO: Provide universal way to enable logging...
/////        org.podval.picasa.model.Util.enableLogging();

        final Crate firstCrate = CrateFactory.getCrate(firstTicket);

        if (secondTicket == null) {
            new Lister(firstCrate).run();
        } else {
            final Crate secondCrate = CrateFactory.getCrate(secondTicket);
            new Synchronizer(firstCrate, secondCrate, !isDryRun).run();
        }
    }


    private CrateTicket firstTicket;


    private CrateTicket secondTicket;
}
