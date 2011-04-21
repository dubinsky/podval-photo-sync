package org.podval.zenfolio;

import java.security.{MessageDigest, NoSuchAlgorithmException}

import javax.activation.{DataHandler, DataSource}

import java.io.{InputStream, OutputStream, ByteArrayInputStream, IOException}


object Bytes {

    def hash(what: Array[Byte]): Array[Byte] = { //throws NoSuchAlgorithmException
        val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
        digest.reset()
        digest.digest(what)
    }


    def concatenate(first: Array[Byte], second: Array[Byte]): Array[Byte] = {
        val result: Array[Byte] = new Array[Byte](first.length + second.length)

        System.arraycopy(first, 0, result, 0, first.length)
        System.arraycopy(second, 0, result, first.length, second.length)

        result
    }


    def readBytes(dataHandler: DataHandler): Array[Byte] = { // throws IOException
        val is: ByteArrayInputStream = dataHandler.getContent().asInstanceOf[ByteArrayInputStream]
        val length: Int = is.available()
        val result: Array[Byte] = new Array[Byte](length)
        is.read(result)
        result;
    }


    private final class BytesDataSource(bytes: Array[Bytes]) extends DataSource {

        override def getInputStream(): InputStream = new ByteArrayInputStream(bytes) // throws IOException {
            

        override def getOutputStream(): OutputStream = throw new UnsupportedOperationException("Not supported yet."); // throws IOException {


        override def getContentType(): String = "application/octet-stream"


        override def getName(): String = throw new UnsupportedOperationException("Not supported yet.")
    }


    def wrapBytes(bytes: Array[Byte]): DataHandler =
        new DataHandler(new BytesDataSource(bytes))
}
