package org.podval.zenfolio;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;


/* package */ final class Bytes {

    private Bytes() {
    }


    public static byte[] hash(final byte[] what) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        return digest.digest(what);
    }


    public static byte[] concatenate(final byte[] first, final byte[] second) {
        final byte[] result = new byte[first.length + second.length];

        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);

        return result;
    }


    public static byte[] readBytes(final DataHandler dataHandler) throws IOException {
        final ByteArrayInputStream is = (ByteArrayInputStream) dataHandler.getContent();
        final int length = is.available();
        final byte[] result = new byte[length];
        is.read(result);
        return result;
    }


    public static DataHandler wrapBytes(final byte[] bytes) {
        return new DataHandler(new DataSource() {

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(bytes);
            }


            @Override
            public OutputStream getOutputStream() throws IOException {
                throw new UnsupportedOperationException("Not supported yet.");
            }


            @Override
            public String getContentType() {
                return "application/octet-stream";
            }


            @Override
            public String getName() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
}
