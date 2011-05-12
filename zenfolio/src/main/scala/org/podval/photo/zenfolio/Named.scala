/*
 *  Copyright 2011 Leonid Dubinsky <dub@podval.org>.
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

package org.podval.photo.zenfolio

import com.zenfolio.www.api._1_1.GroupElement


trait Named[R <: GroupElement] extends ZenfolioFolder[R] {

    final override def name: String = element.getTitle()


    final override def name_=(value: String) {
        throw new UnsupportedOperationException("Did not boither implementing :)")
    }
}
