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

import org.podval.photo.{Connector, Connection, Folder, Photo, PhotoId, PhotoException}

import java.util.logging.{Logger, Level, Handler}

import org.kohsuke.args4j.{Option => COption, Argument, CmdLineParser, CmdLineException}

import scala.xml.Elem


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


    @COption(name="-l", aliases=Array("--log-level"), usage="logging level")
//    private var logLevel: LogLevel = LogLevel.INFO
    private var logLevel: String = "info"


    @COption(name="-h", aliases=Array("--help"), usage="display help message")
    private var help: Boolean = false


    @COption(name="-d", aliases=Array("--dry-run"), usage="dry run")
    private var isDryRun: Boolean = false


    @COption(name="-s", aliases=Array("--suffux"), usage="suffix that selects a subtree")
    private var suffix: String = null


    @Argument(index=0, required=true)
    private def setFirstUri(uri: String) {
        firstUri = Some(uri)
    }


    @Argument(index=1, required=false)
    private def setSecondUri(uri: String) {
        secondUri = Some(uri)
    }


    private def printSchemes() {
        System.out.println("  Available schemes:")
        Connector.all.foreach(connector => System.out.println("    " + connector.scheme))
    }


    private def parse(args: Array[String]) {
        val parser = new CmdLineParser(this)
        try {
            parser.parseArgument(args: _*)
            // TODO: ?
//            if (realArgs.length > 2) {
//                throw new ParseException("Too many arguments")
//            }
        } catch {
            case e: CmdLineException =>
            System.err.println("Error: " + e.getMessage())
            printUsage(parser)
            System.exit(1)
        }
        if (help) {
            printUsage(parser)
            System.exit(0)
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
        )

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

        val log = Logger.getLogger(Connection.LOG)

        log.setLevel(level)
    }


    private def run[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]]() {
        if (secondUri.isEmpty) {
            val folder = getFolder[C,F,P](firstUri.get)
            FolderList.list[C,F,P](folder)
//            XmlList.list(folder)
        } else {
//            val secondConnection = ConnectionFactories.getConnection(secondTicket)
//            synchronize(firstConnection, secondConnection)
        }
    }


    private def getFolder[C <: Connection[C,F,P], F <: Folder[C,F,P], P <: Photo[C,F,P]](uri: String) =
        UriParser.uri2folder[C,F,P](uri, enableLowLevelLogging, suffix)


//    private <F extends Photo, T extends Photo> void synchronize(
//        final Connection<F> fromConnection,
//        final Connection<T> toConnection) throws PhotoException
//    {
//        open(fromConnection)
//        open(toConnection)
//
//        fromConnection.getRootFolder().syncFolderTo(toConnection.getRootFolder())
//    }


    private var firstUri: Option[String] = None


    private var secondUri: Option[String] = None


    private var enableLowLevelLogging = false
}
