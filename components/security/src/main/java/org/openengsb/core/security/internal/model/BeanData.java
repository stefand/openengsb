/**
 * Licensed to the Austrian Association for Software Tool Integration (AASTI)
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. The AASTI licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openengsb.core.security.internal.model;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import org.openengsb.core.common.AbstractDataRow;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * This entity-class is used to save Java-beans to the Database. Because the Object is not serialized and saved as a
 * BLOB, there are some restrictions on the bean-properties.
 * 
 * Only types are allowed which have a String-only-constructor, that can be used to instantiate the original instance
 * with a previous result of the type's toString-method.
 * 
 * Arrays and Collections of such objects are also supported.
 */
@SuppressWarnings("serial")
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BeanData extends AbstractDataRow {

    @Column(name = "type", nullable = false)
    private String type;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @MapKey(name = "key")
    private Map<String, EntryValue> attributes = Maps.newHashMap();

    public BeanData(String type, Map<String, EntryValue> attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    public BeanData() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, EntryValue> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, EntryValue> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return String.format("BeanData (%s): %s", type, attributes.values());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, attributes);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BeanData)) {
            return false;
        }
        final BeanData other = (BeanData) obj;
        return Objects.equal(type, other.type) && Objects.equal(attributes, other.attributes);
    }

}
