package org.podval.photo;

import java.util.ServiceLoader;


public abstract class ConnectionFactory<T extends Photo> {

    public static Connection getConnection(final ConnectionDescriptor ticket) throws PhotoException {
        final String scheme = ticket.getScheme();

        final ConnectionFactory connectionFactory = get(scheme);

        if (connectionFactory == null) {
            throw new PhotoException("Unknown scheme: " + scheme);
        }

        return connectionFactory.createConnection(ticket);
    }


    public static ConnectionFactory get(final String scheme) {
        ConnectionFactory result = null;

        for (final ConnectionFactory factory : getAll()) {
            if (factory.getScheme().equals(scheme)) {
                result = factory;
                break;
            }
        }

        return result;
    }


    public static synchronized Iterable<ConnectionFactory> getAll() {
        if (loader == null) {
            loader = ServiceLoader.load(ConnectionFactory.class);
        }

        return loader;
    }


    private static ServiceLoader<ConnectionFactory> loader;


    // TODO do createConnection() and getScheme() through reflection?

    public abstract Connection<T> createConnection(final ConnectionDescriptor descriptor) throws PhotoException;


    public abstract String getScheme();
}
