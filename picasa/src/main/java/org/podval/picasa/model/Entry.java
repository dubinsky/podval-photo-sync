/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.podval.picasa.model;

import com.google.api.client.util.Key;
import com.google.api.client.util.Data;

import java.util.List;


/**
 * @author Yaniv Inbar
 */
public class Entry implements Cloneable {

    @Key("@gd:etag")
    public String etag;


    @Key("link")
    public List<Link> links;


    @Key
    public String summary;


    @Key
    public String title;


    @Key
    public String updated;


    public final String getFeedLink() {
        return Link.find(links, "http://schemas.google.com/g/2005#feed");
    }


    public final String getSelfLink() {
        return Link.find(links, "self");
    }


    @Override
    protected Entry clone() {
        try {
            @SuppressWarnings("unchecked")
            Entry result = (Entry) super.clone();
            Data.deepCopy(this, result);
            return result;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }


    /* package */ String getEditLink() {
        return Link.find(links, "edit");
    }
}
