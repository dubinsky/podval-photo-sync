package org.podval.zenfolio;

import org.podval.things.Crate;
import org.podval.things.CrateFactory;
import org.podval.things.CrateTicket;
import org.podval.things.ThingsException;


public final class ZenfolioFactory extends CrateFactory<Photo> {

    @Override
    public Crate<Photo> createCrate(final CrateTicket ticket) throws ThingsException {
        return new Zenfolio(ticket.login, ticket.password);
    }


    @Override
    public String getScheme() {
        return "zenfolio";
    }
}
