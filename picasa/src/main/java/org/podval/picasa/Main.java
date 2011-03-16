/*
 *  Copyright 2011 dub.
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

package org.podval.picasa;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.CmdLineException;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;

import java.io.IOException;


/**
 *
 * @author dub
 */
public class Main {

    @Option(name="-h", aliases="--help", usage="display help message")
    private boolean help = false;


    @Option(name="-u", aliases="--user-name", metaVar="USERNAME", usage="user name", required=true)
    private String userName;


    @Option(name="-p", aliases="--password", metaVar="PASSWORD", usage="password", required=true)
    private String password;


    public static void main(final String[] args) throws IOException {
//        Util.enableLogging();

        final Main main = new Main();

        final CmdLineParser parser = new CmdLineParser(main);
        parser.setUsageWidth(80);

        try {
            parser.parseArgument(args);
        } catch (final CmdLineException e) {
            System.err.println(e.getMessage());

            printUsage(parser);
            System.exit(1);
        }

        if (main.help) {
            printUsage(parser);
            System.exit(0);
        }

        try {
            final HttpTransport transport = Transport.create();
            Transport.authenticate(main.userName, main.password, transport);

//            AlbumEntry album = postAlbum(transport, feed);
//            postPhoto(transport, album);
//            // postVideo(transport, album);
//            album = getUpdatedAlbum(transport, album);
//            album = updateTitle(transport, album);
//            deleteAlbum(transport, album);
        } catch (final HttpResponseException e) {
            System.err.println(e.response.parseAsString());
            throw e;
        }
    }


    private static void printUsage(final CmdLineParser parser) {
        parser.printUsage(System.err);
        System.err.println();
    }
}
