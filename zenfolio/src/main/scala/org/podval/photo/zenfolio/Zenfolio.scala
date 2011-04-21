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

package org.podval.zenfolio

import org.podval.photo.{ConnectionFactoryNg, ConnectionNG, ConnectionDescriptorNg, PhotoException}

import com.zenfolio.www.api._1_1.{ZfApi, ZfApiStub, AuthChallenge}

import java.rmi.RemoteException;

import java.security.NoSuchAlgorithmException;

import org.apache.axis2.client.Stub;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;

import org.apache.commons.httpclient.Header;

import org.podval.photo.ConnectionDescriptor;
import org.podval.photo.Connection;
import org.podval.photo.Folder;
import org.podval.photo.PhotoException;

import java.io.IOException;

import java.util.List;
import java.util.ArrayList;


final class Zenfolio(descriptor: ConnectionDescriptorNg) extends ConnectionNG(descriptor) {

    type F = ZenfolioFolder


    override def getScheme() = Zenfolio.SCHEME


    override def enableLowLevelLogging() {
    }


    private val connection: ZfApi = createConnection()


    private def createConnection() = {
        try {
            new ZfApiStub()
        } catch {
            case e: RemoteException => throw new PhotoException(e)
        }
    }
    

    override def getRootFolder(): F = rootFolder


    private val rootFolder = getSubFolderByPath(getRealRootFolder(), descriptor.path)


    private def getRealRootFolder(): F = {
        try {
            new Group(this, connection.loadGroupHierarchy(descriptor.login))
        } catch {
            case e: RemoteException => throw new PhotoException(e)
        }
    }


    protected override def login() {
        try {
            val authChallenge = connection.getChallenge(descriptor.login)

            val challenge: Array[Byte] = Bytes.readBytes(authChallenge.getChallenge())
            val passwordSalt: Array[Byte] = Bytes.readBytes(authChallenge.getPasswordSalt())
            val passwordUtf8: Array[Byte] = descriptor.password.getBytes("UTF-8")
            val passwordHash: Array[Byte] = Bytes.hash(Bytes.concatenate(passwordSalt, passwordUtf8))
            val proof: Array[Byte] = Bytes.hash(Bytes.concatenate(challenge, passwordHash))

            authToken = connection.authenticate(
                    Bytes.wrapBytes(challenge),
                    Bytes.wrapBytes(proof));

        } catch {
            case e: RemoteException => throw new PhotoException(e)
            case e: IOException => throw new PhotoException(e)
            case e: NoSuchAlgorithmException => throw new PhotoException(e)
        }

        val options: Options = connection.asInstanceOf[Stub]._getServiceClient().getOptions()

//      options.setProperty(HTTPConstants.HEADER_COOKIE, "zf_token=" + authToken);

        val headers = new ArrayList()
        headers.add(getAuthTokenHeader())
        options.setProperty(HTTPConstants.HTTP_HEADERS, headers)
    }


    /* package */ def getAuthTokenHeader(): Header = new Header("X-Zenfolio-Token", authToken)


    def getConnection(): ZfApi = connection


    private var authToken: String = null
}



object Zenfolio {

    val SCHEME = "zenfolio"
}



final class ZenfolioFactrory extends ConnectionFactoryNg {

    def createConnection(descriptor: ConnectionDescriptorNg) = new Zenfolio(descriptor)


    def getScheme() = Zenfolio.SCHEME
}
