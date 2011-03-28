package org.podval.things;


public final class PhotoException extends Exception {

    public PhotoException(final Throwable cause) {
        super(cause);
    }


    public PhotoException(final String message) {
        super(message);
    }
}
