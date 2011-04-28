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

import org.podval.photo.{Connector, Connection, PhotoException}

import com.zenfolio.www.api._1_1.{ZfApi, ZfApiStub, AuthChallenge}

import java.rmi.RemoteException

import java.security.NoSuchAlgorithmException

import org.apache.axis2.client.{Stub, Options}
import org.apache.axis2.transport.http.HTTPConstants

import org.apache.commons.httpclient.Header

import java.io.IOException


final class Zenfolio(connector: ZenfolioConnector) extends Connection[ZfApiStub](connector) {

    type F = ZenfolioFolder[_]


    override def enableLowLevelLogging() {
    }


    protected override def createTransport(): ZfApiStub = {
        try {
            new ZfApiStub()
        } catch {
            case e: RemoteException => throw new PhotoException(e)
        }
    }


    override def isLoginRequired: Boolean = true


    protected override def login(login: String, password: String) {
        try {
            val authChallenge: AuthChallenge = transport.getChallenge(login)

            val challenge: Array[Byte] = Bytes.readBytes(authChallenge.getChallenge())
            val passwordSalt: Array[Byte] = Bytes.readBytes(authChallenge.getPasswordSalt())
            val passwordUtf8: Array[Byte] = password.getBytes("UTF-8")
            val passwordHash: Array[Byte] = Bytes.hash(Bytes.concatenate(passwordSalt, passwordUtf8))
            val proof: Array[Byte] = Bytes.hash(Bytes.concatenate(challenge, passwordHash))

            authToken = transport.authenticate(
                    Bytes.wrapBytes(challenge),
                    Bytes.wrapBytes(proof))

        } catch {
            case e: RemoteException => throw new PhotoException(e)
            case e: IOException => throw new PhotoException(e)
            case e: NoSuchAlgorithmException => throw new PhotoException(e)
        }

        val options: Options = transport.asInstanceOf[Stub]._getServiceClient().getOptions()

//      options.setProperty(HTTPConstants.HEADER_COOKIE, "zf_token=" + authToken)

        val headers = Seq(getAuthTokenHeader())
        options.setProperty(HTTPConstants.HTTP_HEADERS, headers)
    }


    /* package */ def getAuthTokenHeader(): Header = new Header("X-Zenfolio-Token", authToken)


    private var authToken: String = null
    

    protected override def createRootFolder(): R = {
        try {
            new RootGroup(this, transport.loadGroupHierarchy(login))
        } catch {
            case e: RemoteException => throw new PhotoException(e)
        }
    }
}



final class ZenfolioConnector extends Connector("zenfolio") {

    def connect() = new Zenfolio(this)
}
