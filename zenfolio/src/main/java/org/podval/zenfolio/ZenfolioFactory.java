package org.podval.zenfolio;

import org.podval.things.Connection;
import org.podval.things.ConnectionFactory;
import org.podval.things.ConnectionDescriptor;
import org.podval.things.PhotoException;


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
