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

import scala.collection.JavaConversions.iterableAsScalaIterable


abstract class Connector(val scheme: String) {

    // TODO do connect() through reflection?

    def connect(): Connection
}



object Connector {

    private val loader: ServiceLoader[Connector] = ServiceLoader.load(classOf[Connector])


    val all: Seq[Connector] = Seq[Connector]() ++ iterableAsScalaIterable(loader)


    def get(scheme: String): Option[Connector] = {
        all.find(_.scheme.equals(scheme))
    }
}
