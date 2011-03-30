package org.podval.sync;

import org.podval.photo.Connection;
import org.podval.photo.ConnectionFactory;
import org.podval.photo.ConnectionDescriptor;
import org.podval.photo.Photo;
import org.podval.photo.Indenter;
import org.podval.photo.PhotoException;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.CmdLineException;


public final class Main {

    public static void main(final String[] args) {
        final Main main = new Main();
        main.run(args);
    }


    private static enum LogLevel { INFO, DEBUG, TRACE }


    @Option(name="-h", aliases="--help", usage="display help message")
    private boolean help = false;


    @Option(name="-l", aliases="--log-level", usage="logging level")
    private LogLevel logLevel = LogLevel.INFO;


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

        configureLogging();

        try {
            run();
        } catch (final PhotoException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    private void printSchemes() {
        System.out.println("  Available schemes:");
        for (final ConnectionFactory connectionFactory : ConnectionFactory.getAll()) {
            System.out.println("    " + connectionFactory.getScheme());
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


    private void configureLogging() {
        for (final Handler handler: Logger.getLogger("").getHandlers()) {
            handler.setLevel(Level.ALL);
        }

        final Level level;

        if (logLevel == LogLevel.TRACE) {
            level = Level.ALL;
        } else if (logLevel == LogLevel.DEBUG) {
            level = Level.FINE;
        } else {
            level = Level.INFO;
        }

        final Logger log = Logger.getLogger(Connection.LOG);
        log.setLevel(level);
    }


    private void run() throws PhotoException {
        final Connection firstConnection = ConnectionFactory.getConnection(firstTicket);

        if (secondTicket == null) {
            list(firstConnection);
        } else {
            final Connection secondConnection = ConnectionFactory.getConnection(secondTicket);
            synchronize(firstConnection, secondConnection, !isDryRun);
        }
    }


    private void list(final Connection<?> connection) throws PhotoException {
        open(connection);
        connection.getRootFolder().list(new Indenter(System.out));
    }



    private <F extends Photo, T extends Photo> void synchronize(
        final Connection<F> fromConnection,
        final Connection<T> toConnection,
        final boolean doIt) throws PhotoException
    {
        open(fromConnection);
        open(toConnection);

        fromConnection.getRootFolder().syncFolderTo(
            toConnection.getRootFolder(),
            doIt);
    }


    private void open(final Connection connection) throws PhotoException {
        if (logLevel == LogLevel.TRACE) {
            connection.enableLowLevelLogging();
        }

        connection.open();
    }


    private ConnectionDescriptor firstTicket;


    private ConnectionDescriptor secondTicket;
}
