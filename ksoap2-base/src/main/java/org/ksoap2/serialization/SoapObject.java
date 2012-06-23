/*
 * Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * Contributor(s): John D. Beatty, Dave Dash, Andre Gerard, F. Hunter, Renaud
 * Tognelli
 */

package org.ksoap2.serialization;

import java.util.*;

/**
 * A simple dynamic object that can be used to build soap calls without
 * implementing KvmSerializable
 * <p/>
 * Essentially, this is what goes inside the body of a soap envelope - it is the
 * direct subelement of the body and all further subelements
 * <p/>
 * Instead of this this class, custom classes can be used if they implement the
 * KvmSerializable interface.
 */

public class SoapObject extends AttributeContainer implements KvmSerializable {

    private static final String EMPTY_STRING = "";
    /**
     * The namespace of this soap object.
     */
    protected String namespace;
    /**
     * The name of this soap object.
     */
    protected String name;
    /**
     * The Vector of properties (can contain PropertyInfo and SoapObject)
     */
    protected Vector properties = new Vector();

    // TODO: accessing properties and attributes would work much better if we
    // kept a list of known properties instead of iterating through the list
    // each time

    /**
     * Creates a new <code>SoapObject</code> instance.
     */

    public SoapObject() {
        this("","");
    }
    /**
     * Creates a new <code>SoapObject</code> instance.
     *
     * @param namespace
     *            the namespace for the soap object
     * @param name
     *            the name of the soap object
     */

    public SoapObject(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SoapObject)) {
            return false;
        }

        SoapObject otherSoapObject = (SoapObject) obj;

        if (!name.equals(otherSoapObject.name)
                || !namespace.equals(otherSoapObject.namespace)) {
            return false;
        }

        int numProperties = properties.size();
        if (numProperties != otherSoapObject.properties.size()) {
            return false;
        }

        // SoapObjects are only considered the same if properties equals and in the same order
        for (int propIndex = 0; propIndex < numProperties; propIndex++) {
            Object thisProp = this.properties.elementAt(propIndex);
            if(!otherSoapObject.isPropertyEqual(thisProp, propIndex)) {
                return false;
            }
        }

        return attributesAreEqual(otherSoapObject);
    }

    /**
     * Helper function for SoapObject.equals
     * Checks if a given property and index are the same as in this
     *
     *  @param otherProp, index
     *  @return
     */
    public boolean isPropertyEqual(Object otherProp, int index) {
        if(index >= getPropertyCount()) {
            return false;
        }
        Object thisProp = this.properties.elementAt(index);
        if(otherProp instanceof PropertyInfo &&
                thisProp instanceof PropertyInfo) {
            // Get both PropertInfos and compare values
            PropertyInfo otherPropInfo = (PropertyInfo)otherProp;
            PropertyInfo thisPropInfo = (PropertyInfo)thisProp;
            return otherPropInfo.getName().equals(thisPropInfo.getName()) &&
                    otherPropInfo.getValue().equals(thisPropInfo.getValue());
        } else if (otherProp instanceof SoapObject && thisProp instanceof SoapObject) {
            SoapObject otherPropSoap = (SoapObject)otherProp;
            SoapObject thisPropSoap = (SoapObject)thisProp;
            return otherPropSoap.equals(thisPropSoap);
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    /**
     * @inheritDoc
     */
    public Object getProperty(int index) {
        Object prop = properties.elementAt(index);
        if(prop instanceof PropertyInfo) {
            return ((PropertyInfo)prop).getValue();
        } else {
            return ((SoapObject)prop);
        }
    }

    /**
     * Get the toString value of the property.
     *
     * @param index
     * @return
     */
    public String getPropertyAsString(int index) {
        PropertyInfo propertyInfo = (PropertyInfo) properties.elementAt(index);
        return propertyInfo.getValue().toString();
    }

    /**
     * Get the property with the given name
     *
     * @throws java.lang.RuntimeException
     *             if the property does not exist
     */
    public Object getProperty(String name) {
        Integer index = propertyIndex(name);
        if (index != null) {
            return getProperty(index.intValue());
        } else {
            throw new RuntimeException("illegal property: " + name);
        }
    }

    /**
     * Get the toString value of the property.
     *
     * @param name
     * @return
     */

    public String getPropertyAsString(String name) {
        Integer index = propertyIndex(name);
        if (index != null) {
            return getProperty(index.intValue()).toString();
        } else {
            throw new RuntimeException("illegal property: " + name);
        }
    }

    /**
     * Knows whether the given property exists
     */
    public boolean hasProperty(final String name) {
        if (propertyIndex(name) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get a property without chance of throwing an exception
     *
     * @return the property if it exists; if not, {@link NullSoapObject} is
     *         returned
     */
    public Object getPropertySafely(final String name) {
        Integer i = propertyIndex(name);
        if (i != null) {
            return getProperty(i.intValue());
        } else {
            return new NullSoapObject();
        }
    }

    /**
     * Get the toString value of a property without chance of throwing an
     * exception
     *
     * @return the string value of the property if it exists; if not, #EMPTY_STRING is
     *         returned
     */
    public String getPropertySafelyAsString(final String name) {
        Integer i = propertyIndex(name);
        if (i != null) {
            Object foo = getProperty(i.intValue());
            if (foo == null) {
                return EMPTY_STRING;
            } else {
                return foo.toString();
            }
        } else {
            return EMPTY_STRING;
        }
    }

    /**
     * Get a property without chance of throwing an exception. An object can be
     * provided to this method; if the property is not found, this object will
     * be returned.
     *
     * @param defaultThing
     *            the object to return if the property is not found
     * @return the property if it exists; defaultThing if the property does not
     *         exist
     */
    public Object getPropertySafely(final String name, final Object defaultThing) {
        Integer i = propertyIndex(name);
        if (i != null) {
            return getProperty(i.intValue());
        } else {
            return defaultThing;
        }
    }

    /**
     * Get the toString value of a property without chance of throwing an
     * exception. An object can be provided to this method; if the property is
     * not found, this object's string representation will be returned.
     *
     * @param defaultThing
     *            toString of the object to return if the property is not found
     * @return the property toString if it exists; defaultThing toString if the
     *         property does not exist, if the defaultThing is null #EMPTY_STRING
     *         is returned
     */
    public String getPropertySafelyAsString(final String name,
            final Object defaultThing) {
        Integer i = propertyIndex(name);
        if (i != null) {
            Object property = getProperty(i.intValue());
            if (property != null) {
                return property.toString();
            } else {
                return EMPTY_STRING;
            }
        } else {
            if (defaultThing != null) {
                return defaultThing.toString();
            } else {
                return EMPTY_STRING;
            }
        }
    }

    /**
     * Get the primitive property with the given name.
     *
     * @param name
     * @return PropertyInfo containing an empty string if property either complex or empty
     */
    public Object getPrimitiveProperty(final String name){
        Integer index = propertyIndex(name);
        if (index != null){
            PropertyInfo propertyInfo = (PropertyInfo) properties.elementAt(index.intValue());
            if (propertyInfo.getType()!=SoapObject.class){
                return propertyInfo.getValue();
            } else {
                propertyInfo = new PropertyInfo();
                propertyInfo.setType(String.class);
                propertyInfo.setValue(EMPTY_STRING);
                propertyInfo.setName(name);
                return (Object) propertyInfo.getValue();
            }
        } else {
            throw new RuntimeException("illegal property: " + name);
        }
    }

    /**
     * Get the toString value of the primitive property with the given name.
     * Returns empty string if property either complex or empty
     *
     * @param name
     * @return the string value of the property
     */
    public String getPrimitivePropertyAsString(final String name){
        Integer index = propertyIndex(name);
        if (index != null){
            PropertyInfo propertyInfo = (PropertyInfo) properties.elementAt(index.intValue());
            if (propertyInfo.getType()!=SoapObject.class){
                return propertyInfo.getValue().toString();
            } else {
                return EMPTY_STRING;
            }
        } else {
            throw new RuntimeException("illegal property: " + name);
        }
    }

    /**
     * Get the toString value of a primitive property without chance of throwing an
     * exception
     *
     * @param name
     * @return the string value of the property if it exists and is primitive; if not, #EMPTY_STRING is
     *         returned
     */
    public Object getPrimitivePropertySafely(final String name) {
        Integer index = propertyIndex(name);
        if (index != null){
            PropertyInfo propertyInfo = (PropertyInfo) properties.elementAt(index.intValue());
            if (propertyInfo.getType()!=SoapObject.class){
                return propertyInfo.getValue().toString();
            } else {
                propertyInfo = new PropertyInfo();
                propertyInfo.setType(String.class);
                propertyInfo.setValue(EMPTY_STRING);
                propertyInfo.setName(name);
                return (Object) propertyInfo.getValue();
            }
        } else {
            return new NullSoapObject();
        }
    }

    /**
     * Get the toString value of a primitive property without chance of throwing an
     * exception
     *
     * @param name
     * @return the string value of the property if it exists and is primitive; if not, #EMPTY_STRING is
     *         returned
     */
    public String getPrimitivePropertySafelyAsString(final String name) {
        Integer index = propertyIndex(name);
        if (index != null){
            PropertyInfo propertyInfo = (PropertyInfo) properties.elementAt(index.intValue());
            if (propertyInfo.getType()!=SoapObject.class){
                return propertyInfo.getValue().toString();
            } else {
                return EMPTY_STRING;
            }
        } else {
            return EMPTY_STRING;
        }
    }



    private Integer propertyIndex(String name) {
        if (name != null) {
            for (int i = 0; i < properties.size(); i++) {
                if (name.equals(((PropertyInfo) properties.elementAt(i)).getName())) {
                    return new Integer(i);
                }
            }
        }
        return null;
    }


    /**
     * Returns the number of properties
     *
     * @return the number of properties
     */
    public int getPropertyCount() {
        return properties.size();
    }

    /**
     * Places PropertyInfo of desired property into a designated PropertyInfo
     * object. Just calls #getPropertyInfo and discards any provided properties.
     *
     * @param index
     *            index of desired property
     * @param properties
     *            this parameter is ignored
     * @param propertyInfo
     *            designated retainer of desired property
     */
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo propertyInfo) {
        getPropertyInfo(index, propertyInfo);
    }

    /**
     * Places PropertyInfo of desired property into a designated PropertyInfo
     * object
     *
     * @param index
     *            index of desired property
     * @param propertyInfo
     *            designated retainer of desired property
     */
    public void getPropertyInfo(int index, PropertyInfo propertyInfo) {
        Object element = properties.elementAt(index);
        if (element instanceof PropertyInfo) {
            PropertyInfo p = (PropertyInfo) element;
            propertyInfo.name = p.name;
            propertyInfo.namespace = p.namespace;
            propertyInfo.flags = p.flags;
            propertyInfo.type = p.type;
            propertyInfo.elementType = p.elementType;
            propertyInfo.value = p.value;
            propertyInfo.multiRef = p.multiRef;
        } else {
            // SoapObject
            propertyInfo.name = null;
            propertyInfo.namespace = null;
            propertyInfo.flags = 0;
            propertyInfo.type = null;
            propertyInfo.elementType = null;
            propertyInfo.value = element;
            propertyInfo.multiRef = false;
        }
    }

    /**
     * Creates a new SoapObject based on this, allows usage of SoapObjects as
     * templates. One application is to set the expected return type of a soap
     * call if the server does not send explicit type information.
     *
     * @return a copy of this.
     */
    public SoapObject newInstance() {
        SoapObject o = new SoapObject(namespace, name);
        for (int propIndex = 0; propIndex < properties.size(); propIndex++) {
            Object prop = properties.elementAt(propIndex);
            if(prop instanceof PropertyInfo) {
                PropertyInfo propertyInfo = (PropertyInfo) properties.elementAt(propIndex);
                PropertyInfo propertyInfoClonned = (PropertyInfo)propertyInfo.clone();
                o.addProperty( propertyInfoClonned );
            } else if(prop instanceof SoapObject) {
                o.addSoapObject(((SoapObject)prop).newInstance());
            }
        }
        for (int attribIndex = 0; attribIndex < getAttributeCount(); attribIndex++) {
            AttributeInfo newAI = new AttributeInfo();
            getAttributeInfo(attribIndex, newAI);
            AttributeInfo attributeInfo = newAI; // (AttributeInfo)
                                                    // attributes.elementAt(attribIndex);
            o.addAttribute(attributeInfo);
        }
        return o;
    }

    /**
     * Sets a specified property to a certain value.
     *
     * @param index
     *            the index of the specified property
     * @param value
     *            the new value of the property
     */
    public void setProperty(int index, Object value) {
        Object prop = properties.elementAt(index);
        if(prop instanceof PropertyInfo) {
            ((PropertyInfo) prop).setValue(value);
        }
        // TODO: not sure how you want to handle an exception here if the index points to a SoapObject
    }

    /**
     * Adds a property (parameter) to the object. This is essentially a sub
     * element.
     *
     * @param name
     *            The name of the property
     * @param value
     *            the value of the property
     */
    public SoapObject addProperty(String name, Object value) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.name = name;
        propertyInfo.type = value == null ? PropertyInfo.OBJECT_CLASS : value
                .getClass();
        propertyInfo.value = value;
        return addProperty(propertyInfo);
    }

    /**
     * Add a property only if the value is not null.
     *
     * @param name
     * @param value
     * @return
     */
    public SoapObject addPropertyIfValue(String name, Object value) {
        if (value != null) {
            return addProperty(name, value);
        } else {
            return this;
        }
    }

    /**
     * Add a property only if the value is not null.
     *
     * @param propertyInfo
     * @param value
     * @return
     */
    public SoapObject addPropertyIfValue(PropertyInfo propertyInfo, Object value) {
        if (value != null) {
            propertyInfo.setValue(value);
            return addProperty(propertyInfo);
        } else {
            return this;
        }
    }

    /**
     * Adds a property (parameter) to the object. This is essentially a sub
     * element.
     *
     * @param propertyInfo
     *            designated retainer of desired property
     */
    public SoapObject addProperty(PropertyInfo propertyInfo) {
        properties.addElement(propertyInfo);
        return this;
    }

    /**
     * Ad the propertyInfo only if the value of it is not null.
     *
     * @param propertyInfo
     * @return
     */
    public SoapObject addPropertyIfValue(PropertyInfo propertyInfo) {
        if (propertyInfo.value != null) {
            properties.addElement(propertyInfo);
            return this;
        } else {
            return this;
        }
    }

    /**
     * Adds a SoapObject the properties array. This is a sub element to
     * allow nested SoapObjects
     *
     * @param soapObject
     *            to be added as a property of the current object
     */
    public SoapObject addSoapObject(SoapObject soapObject) {
        properties.addElement(soapObject);
        return this;
    }

    /**
     * Generate a {@code String} describing this object.
     *
     * @return
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(EMPTY_STRING + name + "{");
        for (int i = 0; i < getPropertyCount(); i++) {
            Object prop = properties.elementAt(i);
            if(prop instanceof PropertyInfo) {
                buf.append(EMPTY_STRING)
                    .append(((PropertyInfo) prop).getName())
                    .append("=")
                    .append(getProperty(i))
                    .append("; ");
            } else {
                buf.append(((SoapObject) prop).toString());
            }
        }
        buf.append("}");
        return buf.toString();
    }
}
