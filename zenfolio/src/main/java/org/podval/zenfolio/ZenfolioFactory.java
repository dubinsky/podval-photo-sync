package org.podval.zenfolio;

import org.podval.photo.Connection;
import org.podval.photo.ConnectionFactory;
import org.podval.photo.ConnectionDescriptor;
import org.podval.photo.PhotoException;


public final class ZenfolioFactory extends ConnectionFactory<ZenfolioPhoto> {

    public static final String SCHEME = "zenfolio";


    @Override
    public Connection<ZenfolioPhoto> createConnection(final ConnectionDescriptor ticket) throws PhotoException {
        return new Zenfolio(ticket.getLogin(), ticket.getPassword(), ticket.getPath());
    }


    @Override
    public String getScheme() {
        return SCHEME;
    }
}
