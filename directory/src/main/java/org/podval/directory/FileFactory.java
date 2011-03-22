package org.podval.directory;

import org.podval.things.Connection;
import org.podval.things.ConnectionFactory;
import org.podval.things.ConnectionDescriptor;


public final class FileFactory extends ConnectionFactory<FileThing> {

    public static final String SCHEME = "file";


    @Override
    public Connection<FileThing> createConnection(final ConnectionDescriptor ticket) {
        return new FileConnection(ticket.getPath());
    }


    @Override
    public String getScheme() {
        return SCHEME;
    }
}
