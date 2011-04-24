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

import java.util.Date


trait PhotoId {

    def name: String


    def timestamp: Date


    def size: Int


    final def isIdentifiedBy(id: PhotoId): Boolean = {
        val namesEqual = name == id.name
        val timestampsEqual = timestamp == id.timestamp
        val sizesEqual = size == id.size

        timestampsEqual || (namesEqual && sizesEqual)
    }
}
