package org.podval.things;

import java.util.ServiceLoader;


public abstract class ConnectionFactory<T extends Thing> {

    public static Connection getConnection(final ConnectionDescriptor ticket) throws ThingsException {
        final String scheme = ticket.getScheme();

        final ConnectionFactory connectionFactory = get(scheme);

        if (connectionFactory == null) {
            throw new ThingsException("Unknown scheme: " + scheme);
        }

        return connectionFactory.createConnection(ticket);
    }


    public static ConnectionFactory get(final String scheme) {
        ConnectionFactory result = null;

        for (final ConnectionFactory factory : getLoader()) {
            if (factory.getScheme().equals(scheme)) {
                result = factory;
                break;
            }
        }

        return result;
    }


    public static Iterable<ConnectionFactory> getAll() {
        return getLoader();
    }


    private static synchronized ServiceLoader<ConnectionFactory> getLoader() {
        if (loader == null) {
            loader = ServiceLoader.load(ConnectionFactory.class);
        }

        return loader;
    }


    private static ServiceLoader<ConnectionFactory> loader;


    public abstract Connection<T> createConnection(final ConnectionDescriptor ticket) throws ThingsException;


    public abstract String getScheme();
}