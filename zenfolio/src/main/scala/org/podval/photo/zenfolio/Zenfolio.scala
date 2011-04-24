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

import org.podval.photo.{ConnectionFactory, Connection, ConnectionDescriptor, PhotoException}

import com.zenfolio.www.api._1_1.{ZfApi, ZfApiStub, AuthChallenge}

import java.rmi.RemoteException

import java.security.NoSuchAlgorithmException

import org.apache.axis2.client.{Stub, Options}
import org.apache.axis2.transport.http.HTTPConstants

import org.apache.commons.httpclient.Header

import java.io.IOException


final class Zenfolio(descriptor: ConnectionDescriptor) extends Connection(descriptor) {

    type F = ZenfolioFolder[_]


    override def scheme = Zenfolio.SCHEME


    if (descriptor.login.isEmpty) {
        throw new PhotoException("Zenfolio requires a login to be specified!")
    }

    override def enableLowLevelLogging() {
    }


    val connection: ZfApi = createConnection()


    private def createConnection() = {
        try {
            new ZfApiStub()
        } catch {
            case e: RemoteException => throw new PhotoException(e)
        }
    }
    

    override val rootFolder: F = getSubFolderByPath(getRealRootFolder(), descriptor.path)


    private def getRealRootFolder(): F = {
        try {
            new RootGroup(this, connection.loadGroupHierarchy(descriptor.login.get))
        } catch {
            case e: RemoteException => throw new PhotoException(e)
        }
    }


    protected override def login() {
        try {
            val authChallenge: AuthChallenge = connection.getChallenge(descriptor.login.get)

            val challenge: Array[Byte] = Bytes.readBytes(authChallenge.getChallenge())
            val passwordSalt: Array[Byte] = Bytes.readBytes(authChallenge.getPasswordSalt())
            val passwordUtf8: Array[Byte] = descriptor.password.get.getBytes("UTF-8")
            val passwordHash: Array[Byte] = Bytes.hash(Bytes.concatenate(passwordSalt, passwordUtf8))
            val proof: Array[Byte] = Bytes.hash(Bytes.concatenate(challenge, passwordHash))

            authToken = connection.authenticate(
                    Bytes.wrapBytes(challenge),
                    Bytes.wrapBytes(proof))

        } catch {
            case e: RemoteException => throw new PhotoException(e)
            case e: IOException => throw new PhotoException(e)
            case e: NoSuchAlgorithmException => throw new PhotoException(e)
        }

        val options: Options = connection.asInstanceOf[Stub]._getServiceClient().getOptions()

//      options.setProperty(HTTPConstants.HEADER_COOKIE, "zf_token=" + authToken)

        val headers = Seq(getAuthTokenHeader())
        options.setProperty(HTTPConstants.HTTP_HEADERS, headers)
    }


    /* package */ def getAuthTokenHeader(): Header = new Header("X-Zenfolio-Token", authToken)


    private var authToken: String = null
}



object Zenfolio {

    val SCHEME = "zenfolio"
}



final class ZenfolioFactrory extends ConnectionFactory(Zenfolio.SCHEME) {

    def createConnection(descriptor: ConnectionDescriptor) = new Zenfolio(descriptor)
}
