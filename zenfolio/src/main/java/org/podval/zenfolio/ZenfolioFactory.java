package org.podval.zenfolio;

import org.podval.things.Crate;
import org.podval.things.CrateFactory;
import org.podval.things.CrateTicket;
import org.podval.things.ThingsException;


public final class ZenfolioFactory extends CrateFactory<ZenfolioThing> {

    public static final String SCHEME = "zenfolio";


    @Override
    public Crate<ZenfolioThing> createCrate(final CrateTicket ticket) throws ThingsException {
        return new Zenfolio(ticket.getLogin(), ticket.getPassword(), ticket.getPath());
    }


    @Override
    public String getScheme() {
        return SCHEME;
    }
}
