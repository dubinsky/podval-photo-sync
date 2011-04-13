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

package org.podval.photo

import java.util.ServiceLoader


object ConnectionFactories {

    def getAll(): Seq[ConnectionFactoryNg] = {
        if (loader == null) {
            loader = ServiceLoader.load(classOf[ConnectionFactoryNg])
        }

        loader.asInstanceOf[Seq[ConnectionFactoryNg]]
    }


    def get(scheme: String): ConnectionFactoryNg = {
        val result = getAll().find(factory => factory.getScheme().equals(scheme))

        if (result.isEmpty) {
            throw new PhotoException("Unknown scheme: " + scheme)
        }

        result.get
    }


    def getConnection(ticket: ConnectionDescriptorNg): ConnectionNG = get(ticket.scheme).createConnection(ticket)


    private var loader: ServiceLoader[ConnectionFactoryNg] = null
}
