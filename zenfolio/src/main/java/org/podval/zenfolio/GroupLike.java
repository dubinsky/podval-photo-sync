/*
 *  Copyright 2011 dub.
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


package org.podval.zenfolio;

import org.podval.photo.Folder;

import com.zenfolio.www.api._1_1.GroupElement;
import com.zenfolio.www.api._1_1.AccessType;


/**
 *
 * @author dub
 */
/* package */ abstract class GroupLike<R extends GroupElement> extends Folder<Zenfolio, ZenfolioPhoto> {


    protected GroupLike(final Zenfolio connection, final R element) {
        super(connection);

        this.element = element;
    }


    protected final R getElement() {
        return element;
    }


    protected final void setElement(final R value) {
        element = value;
    }


    @Override
    public final String getName() {
        return element.getTitle();
    }


    @Override
    public final boolean isPublic() {
        return element.getAccessDescriptor().getAccessType() == AccessType.Public;
    }


    @Override
    public final void setPublic(final boolean value) {
        element.getAccessDescriptor().setAccessType((value) ? AccessType.Public : AccessType.Private );
    }


    private R element;
}
