package org.podval.things;


public final class ThingsException extends Exception {

    public ThingsException(final Throwable cause) {
        super(cause);
    }


    public ThingsException(final String message) {
        super(message);
    }
}
