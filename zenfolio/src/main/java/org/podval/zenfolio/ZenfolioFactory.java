package org.podval.zenfolio;

import org.podval.things.Connection;
import org.podval.things.ConnectionFactory;
import org.podval.things.ConnectionDescriptor;
import org.podval.things.ThingsException;


public final class ZenfolioFactory extends ConnectionFactory<ZenfolioThing> {

    public static final String SCHEME = "zenfolio";


    @Override
    public Connection<ZenfolioThing> createConnection(final ConnectionDescriptor ticket) throws ThingsException {
        return new Zenfolio(ticket.getLogin(), ticket.getPassword(), ticket.getPath());
    }


    @Override
    public String getScheme() {
        return SCHEME;
    }
}
