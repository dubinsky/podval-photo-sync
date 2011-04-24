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

import org.podval.photo.Folder

import com.zenfolio.www.api._1_1.{GroupElement, AccessType}


abstract class ZenfolioFolder[R <: GroupElement](var element: R) extends Folder {

    type C = Zenfolio


    type F = ZenfolioFolder[_]


    type P = ZenfolioPhoto


    override final def name: String = element.getTitle()


    override final def public: Boolean = element.getAccessDescriptor().getAccessType() == AccessType.Public


    override final def public_=(value: Boolean) {
        element.getAccessDescriptor().setAccessType(if (value) AccessType.Public else AccessType.Private)
    }
}
