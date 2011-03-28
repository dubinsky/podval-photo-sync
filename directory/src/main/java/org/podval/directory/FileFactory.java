package org.podval.directory;

import org.podval.photo.Connection;
import org.podval.photo.ConnectionFactory;
import org.podval.photo.ConnectionDescriptor;


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
