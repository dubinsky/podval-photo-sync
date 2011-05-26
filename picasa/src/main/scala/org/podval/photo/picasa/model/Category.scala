/*
 * Copyright 2011 Podval Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.podval.photo.picasa.model

import com.google.api.client.util.Key


final class Category {

    @Key("@scheme")
    var scheme: String = null


    @Key("@term")
    var term: String = null
}



object Category {
    
    def apply(kind: String): Category = {
        val category = new Category()
        category.scheme = "http://schemas.google.com/g/2005#kind"
        category.term = "http://schemas.google.com/photos/2007#" + kind
        category
    }
}
