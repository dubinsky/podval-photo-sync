package org.podval.directory;

import org.podval.things.Connection;
import org.podval.things.ConnectionFactory;
import org.podval.things.ConnectionDescriptor;


public final class FileFactory extends ConnectionFactory<FilePhoto> {

    public static final String SCHEME = "file";


    @Override
    public Connection<FilePhoto> createConnection(final ConnectionDescriptor ticket) {
        return new FileConnection(ticket.getPath());
    }


    @Override
    public String getScheme() {
        return SCHEME;
    }
}
