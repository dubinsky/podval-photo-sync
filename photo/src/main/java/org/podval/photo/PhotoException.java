package org.podval.photo;


public final class PhotoException extends Exception {

    public PhotoException(final Throwable cause) {
        super(cause);
    }


    public PhotoException(final String message) {
        super(message);
    }


    public PhotoException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
