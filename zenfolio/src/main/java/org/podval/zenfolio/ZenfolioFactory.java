package org.podval.zenfolio;

import org.podval.photo.Connection;
import org.podval.photo.ConnectionFactory;
import org.podval.photo.ConnectionDescriptor;
import org.podval.photo.PhotoException;


public final class ZenfolioFactory extends ConnectionFactory<ZenfolioPhoto> {

    @Override
    public Connection<ZenfolioPhoto> createConnection(final ConnectionDescriptor descriptor) throws PhotoException {
        return new Zenfolio(descriptor);
    }


    @Override
    public String getScheme() {
        return Zenfolio.SCHEME;
    }
}
