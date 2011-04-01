package org.podval.directory;

import org.podval.photo.Connection;
import org.podval.photo.ConnectionFactory;
import org.podval.photo.ConnectionDescriptor;


public final class FileFactory extends ConnectionFactory<FilePhoto> {

    @Override
    public Connection<FilePhoto> createConnection(final ConnectionDescriptor descriptor) {
        return new FileConnection(descriptor);
    }


    @Override
    public String getScheme() {
        return FileConnection.SCHEME;
    }
}
