/*
 *  Copyright 2011 Leonid Dubinsky <dub@podval.org>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.podval.photo.zenfolio

import java.security.{MessageDigest, NoSuchAlgorithmException}

import javax.activation.{DataHandler, DataSource}

import java.io.{InputStream, OutputStream, ByteArrayInputStream, IOException}


object Bytes {

    @throws(classOf[NoSuchAlgorithmException])
    def hash(what: Array[Byte]): Array[Byte] = {
        val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
        digest.reset()
        digest.digest(what)
    }


    @throws(classOf[IOException])
    def readBytes(dataHandler: DataHandler): Array[Byte] = {
        val is: ByteArrayInputStream = dataHandler.getContent().asInstanceOf[ByteArrayInputStream]
        val length: Int = is.available()
        val result: Array[Byte] = new Array[Byte](length)
        is.read(result)
        result
    }


    def wrapBytes(bytes: Array[Byte]): DataHandler =  new DataHandler(new BytesDataSource(bytes))
}



private final class BytesDataSource(bytes: Array[Byte]) extends DataSource {

    @throws(classOf[IOException])
    override def getInputStream(): InputStream = new ByteArrayInputStream(bytes)
            

    @throws(classOf[IOException])
    override def getOutputStream(): OutputStream = throw new UnsupportedOperationException("Not supported yet.")


    override def getContentType(): String = "application/octet-stream"


    override def getName(): String = throw new UnsupportedOperationException("Not supported yet.")
}
