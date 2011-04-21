/*
 *  Copyright 2011 Leonid Dubinsky <dub@podval.org>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.podval.photo.cli

import org.podval.photo.ConnectionNG;
import org.podval.photo.ConnectionFactories
import org.podval.photo.ConnectionDescriptorNg;
//import org.podval.photo.Photo;
//import org.podval.photo.Indenter;
import org.podval.photo.PhotoException;
//
import java.util.logging.Logger;
import java.util.logging.Level;
//import java.util.logging.Handler;
//
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.CmdLineException;


object Main {

    /**
     * @param args the command line arguments
     */
    def main(args: Array[String]): Unit = {
        parse(args)

        configureLogging()

        try {
            run()
        } catch {
            case e: PhotoException => System.err.println("Error: " + e.getMessage())
        }
    }


    // I do not have the time to figure out how to use Scala enums with args4j.
    // Switching to strings.
//    private static enum LogLevel { INFO, DEBUG, TRACE }


    @Option(name="-l", aliases=Array("--log-level"), usage="logging level")
//    private var logLevel: LogLevel = LogLevel.INFO
    private var logLevel: String = "info"


    @Option(name="-h", aliases=Array("--help"), usage="display help message")
    private var help: Boolean = false;


    @Option(name="-d", aliases=Array("--dry-run"), usage="dry run")
    private var isDryRun: Boolean = false


    @Option(name="-s", aliases=Array("--suffux"), usage="suffix that selects a subtree")
    private var suffix: String = null


    @Argument(index=0, required=true)
    private def setFirstUri(value: String) {
        firstTicket = UriParser.fromUri(value, suffix);
    }


    @Argument(index=1, required=false)
    private def setSecondUri(value: String) {
        secondTicket = UriParser.fromUri(value, suffix);
    }


    private def printSchemes() {
        System.out.println("  Available schemes:");
        for (connectionFactory <- ConnectionFactories.getAll()) {
            System.out.println("    " + connectionFactory.getScheme())
        }
    }


    private def parse(args: Array[String]) {
        val parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            // TODO: ?
//            if (realArgs.length > 2) {
//                throw new ParseException("Too many arguments");
//            }
        } catch {
            case e: CmdLineException =>
            System.err.println("Error: " + e.getMessage())
            printUsage(parser)
            System.exit(1);
        }
        if (help) {
            printUsage(parser)
            System.exit(0);
        }
    }


    private def printUsage(parser: CmdLineParser) {
        System.err.println("photo-sync [ options ] <from-uri> <to-uri>")

        parser.setUsageWidth(80)
        parser.printUsage(System.err)
        System.err.println()

        System.err.println(
            "\n" +
            "uri: scheme://[user:password@][host]/path\n"
        );

        printSchemes()
    }


    private def configureLogging() {
        Logger.getLogger("").getHandlers().foreach(_.setLevel(Level.ALL))

        val level =
            if (logLevel == "trace") {
                enableLowLevelLogging = true
                Level.ALL

            } else if (logLevel == "debug") {
                Level.FINE

            } else {
                Level.INFO
            }

        val log = Logger.getLogger(ConnectionNG.LOG);

        log.setLevel(level);
    }


    private def run() {
        val firstConnection: ConnectionNG = ConnectionFactories.getConnection(firstTicket)

        if (secondTicket == null) {
            list(firstConnection);
        } else {
//            val secondConnection = ConnectionFactories.getConnection(secondTicket)
//            synchronize(firstConnection, secondConnection);
        }
    }


    private def list(connection: ConnectionNG) {
        open(connection);
        val xml = connection.getRootFolder().list();
        // TODO prettyprint!
    }



//    private <F extends Photo, T extends Photo> void synchronize(
//        final Connection<F> fromConnection,
//        final Connection<T> toConnection) throws PhotoException
//    {
//        open(fromConnection);
//        open(toConnection);
//
//        fromConnection.getRootFolder().syncFolderTo(toConnection.getRootFolder());
//    }


    private def open(connection: ConnectionNG) {
        if (enableLowLevelLogging) {
            connection.enableLowLevelLogging()
        }

        connection.isReadOnly = isDryRun

        connection.open()
    }


    private var firstTicket: ConnectionDescriptorNg = null


    private var secondTicket: ConnectionDescriptorNg = null


    private var enableLowLevelLogging = false
}
