package org.podval.sync;

import org.podval.things.Synchronizer;
import org.podval.things.Lister;
import org.podval.things.Crate;
import org.podval.things.CrateFactory;
import org.podval.things.CrateTicket;
import org.podval.things.ThingsException;

import java.net.URISyntaxException;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.ParseException;


public final class Main {

    public static void main(final String[] args) throws Exception {
        final Main main = new Main();
        main.parse(args);
        main.run();
    }


    private Main() {
        this.options = new Options();

        options.addOption("s", "suffix", true, "suffix that selects a subtree");
    }


    private void parse(final String[] args) throws ParseException {
        final CommandLine commandLine = new PosixParser().parse(options, args);

        suffix = commandLine.getOptionValue("s");

        final String[] realArgs = commandLine.getArgs();

        if (realArgs.length > 2) {
            throw new ParseException("Too many arguments");
        }

        firstTicket = parseTicket(realArgs[0]);

        if (realArgs.length > 1) {
            secondTicket = parseTicket(realArgs[0]);
        }
    }


    private CrateTicket parseTicket(final String uri) throws ParseException {
        try {
            return UriParser.fromUri(uri);
        } catch (final URISyntaxException e) {
            throw new ParseException(e.getMessage());
        }
    }


    private void run() throws ParseException, ThingsException {
        final Crate firstCrate = getCrate(firstTicket);
        final Crate secondCrate = getCrate(secondTicket);

        final String path = firstTicket.path; // @todo !!!

        if (secondCrate == null) {
            new Lister(firstCrate, path).run();
        } else {
            new Synchronizer(firstCrate, secondCrate, new PhotoConverter(), path, false).run();
        }
    }


    private static Crate getCrate(final CrateTicket ticket) throws ParseException, ThingsException {
        final Crate result;

        if (ticket == null) {
            result = null;
        } else {
            final CrateFactory crateFactory = CrateFactory.get(ticket.scheme);
            if (crateFactory == null) {
                throw new ParseException("Unknown scheme: " + ticket.scheme);
            }
            result = crateFactory.createCrate(ticket);
        }

        return result;
    }


    private static String addSuffix(final String what, final String suffix) {
        return ((what == null) || (suffix == null)) ? what : what + suffix;
    }


    private final Options options;


    private String suffix;


    private CrateTicket firstTicket;


    private CrateTicket secondTicket;
}
