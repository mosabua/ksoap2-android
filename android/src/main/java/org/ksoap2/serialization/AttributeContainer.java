package org.ksoap2.serialization;

import java.util.Vector;

public class AttributeContainer implements HasAttributes{
    protected Vector attributes = new Vector();

    /**
     * Places AttributeInfo of desired attribute into a designated AttributeInfo object
     *
     * @param index         index of desired attribute
     * @param attributeInfo designated retainer of desired attribute
     */
    public void getAttributeInfo(int index, AttributeInfo attributeInfo) {
        AttributeInfo p = (AttributeInfo) attributes.elementAt(index);
        attributeInfo.name = p.name;
        attributeInfo.namespace = p.namespace;
        attributeInfo.flags = p.flags;
        attributeInfo.type = p.type;
        attributeInfo.elementType = p.elementType;
        attributeInfo.value = p.getValue();
    }

    /**
     * Get the attribute at the given index
     */
    public Object getAttribute(int index) {
        return ((AttributeInfo) attributes.elementAt(index)).getValue();
    }

     /**
     * Get the attribute's toString value.
     */
    public String getAttributeAsString(int index) {
        AttributeInfo attributeInfo = (AttributeInfo) attributes.elementAt(index);
        return attributeInfo.getValue().toString();
    }

    /**
     * Get the attribute with the given name
     *
     * @throws RuntimeException if the attribute does not exist
     */
    public Object getAttribute(String name) {
        Integer i = attributeIndex(name);
        if (i != null) {
            return getAttribute(i.intValue());
        } else {
            throw new RuntimeException("illegal property: " + name);
        }
    }

    /**
     * Get the attribute with the given name
     *
     * @throws RuntimeException if the attribute does not exist
     */
    public Object getAttribute(String namespace,String name) {
        Integer i = attributeIndex(namespace,name);
        if (i != null) {
            return getAttribute(i.intValue());
        } else {
            throw new RuntimeException("illegal property: " + name);
        }
    }

    /**
     * Get the toString value of the attribute with the given name.
     *
     * @throws RuntimeException if the attribute does not exist
     */
    public String getAttributeAsString(String name) {
        Integer i = attributeIndex(name);
        if (i != null) {
            return getAttribute(i.intValue()).toString();
        } else {
            throw new RuntimeException("illegal property: " + name);
        }
    }

    /**
     * Get the toString value of the attribute with the given name.
     *
     * @throws RuntimeException if the attribute does not exist
     */
    public String getAttributeAsString(String namespace,String name) {
        Integer i = attributeIndex(namespace,name);
        if (i != null) {
            return getAttribute(i.intValue()).toString();
        } else {
            throw new RuntimeException("illegal property: " + name);
        }
    }
    /**
     * Knows whether the given attribute exists
     */
    public boolean hasAttribute(final String name) {
        if (attributeIndex(name) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Knows whether the given attribute exists
     */
    public boolean hasAttribute(final String namespace,final String name) {
        if (attributeIndex(namespace,name) != null) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Get an attribute without chance of throwing an exception
     *
     * @param name the name of the attribute to retrieve
     * @return the value of the attribute if it exists; {@code null} if it does not exist
     */
    public Object getAttributeSafely(String name) {
        Integer i = attributeIndex(name);
        if (i != null) {
            return getAttribute(i.intValue());
        } else {
            return null;
        }
    }

    /**
     * Get an attribute without chance of throwing an exception
     *
     * @param name the name of the attribute to retrieve
     * @return the value of the attribute if it exists; {@code null} if it does not exist
     */
    public Object getAttributeSafely(String namespace,String name) {
        Integer i = attributeIndex(namespace,name);
        if (i != null) {
            return getAttribute(i.intValue());
        } else {
            return null;
        }
    }

    /**
     * Get an attributes' toString value without chance of throwing an
     * exception.

     * @param name
     * @return the value of the attribute,s toString method if it exists; ""
     * if it does not exist
     */
    public Object getAttributeSafelyAsString(String name) {
        Integer i = attributeIndex(name);
        if (i != null) {
            return getAttribute(i.intValue()).toString();
        } else {
            return "";
        }
    }

    /**
     * Get an attributes' toString value without chance of throwing an
     * exception.

     * @param name
     * @return the value of the attribute,s toString method if it exists; ""
     * if it does not exist
     */
    public Object getAttributeSafelyAsString(String namespace,String name) {
        Integer i = attributeIndex(namespace,name);
        if (i != null) {
            return getAttribute(i.intValue()).toString();
        } else {
            return "";
        }
    }

    private Integer attributeIndex(String name) {
        for (int i = 0; i < attributes.size(); i++) {
            if (name.equals(((AttributeInfo) attributes.elementAt(i)).getName())) {
                return new Integer(i);
            }
        }
        return null;
    }

    private Integer attributeIndex(String namespace,String name) {
        for (int i = 0; i < attributes.size(); i++) {
            AttributeInfo attrInfo=(AttributeInfo) attributes.elementAt(i);
            if (name.equals(attrInfo.getName()) && namespace.equals(attrInfo.getNamespace())) {
                return new Integer(i);
            }
        }
        return null;
    }

    /**
     * Returns the number of attributes
     *
     * @return the number of attributes
     */
    public int getAttributeCount() {
        return attributes.size();
    }

    /**
     * Checks that the two objects have identical sets of attributes.
     *
     * @param other
     * @return {@code true} of the attrubte sets are equal, {@code false} otherwise.
     */
    protected boolean attributesAreEqual(AttributeContainer other) {
        int numAttributes = getAttributeCount();
        if (numAttributes != other.getAttributeCount()) {
            return false;
        }

        for (int attribIndex = 0; attribIndex < numAttributes; attribIndex++) {
            AttributeInfo thisAttrib = (AttributeInfo) this.attributes.elementAt(attribIndex);
            Object thisAttribValue = thisAttrib.getValue();
            if (!other.hasAttribute(thisAttrib.getName())) {
                return false;
            }
            Object otherAttribValue = other.getAttributeSafely(thisAttrib.getName());
            if (!thisAttribValue.equals(otherAttribValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds a attribute (parameter) to the object.
     *
     * @param name  The name of the attribute
     * @param value the value of the attribute
     * @return {@code this} object.
     */
    public void addAttribute(String name, Object value) {
        addAttribute(null,name,value);
    }

    /**
     * Adds a attribute (parameter) to the object.
     *
     * @param namespace  The namespace of the attribute
     * @param name  The name of the attribute
     * @param value the value of the attribute
     * @return {@code this} object.
     */
    public void addAttribute(String namespace,String name, Object value) {
        AttributeInfo attributeInfo = new AttributeInfo();
        attributeInfo.name = name;
        attributeInfo.namespace = namespace;
        attributeInfo.type = value == null ? PropertyInfo.OBJECT_CLASS : value.getClass();
        attributeInfo.value = value;
        addAttribute(attributeInfo);
    }
    /**
     * Add an attribute if the value is not null.
     * @param name
     * @param value
     */
    public void addAttributeIfValue(String name, Object value) {
        if (value != null) {
            addAttribute(name, value);
        }
    }

    /**
     * Add an attribute if the value is not null.
     * @param namespace  The namespace of the attribute
     * @param name
     * @param value
     */
    public void addAttributeIfValue(String namespace,String name, Object value) {
        if (value != null) {
            addAttribute(namespace,name, value);
        }
    }

    /**
     * Add a new attribute by providing an {@link AttributeInfo} object.  {@code AttributeInfo}
     * contains all data about the attribute, including name and value.}
     *
     * @param attributeInfo the {@code AttributeInfo} object to add.
     * @return {@code this} object.
     */
    public void addAttribute(AttributeInfo attributeInfo) {
        attributes.addElement(attributeInfo);
    }

    /**
     * Add an attributeInfo if its value is not null.
     * @param attributeInfo
     */
    public void addAttributeIfValue(AttributeInfo attributeInfo) {
        if (attributeInfo.value != null) {
            attributes.addElement(attributeInfo);
        }
    }


    public void setAttribute(AttributeInfo info) {


    }


    public void getAttribute(int index, AttributeInfo info) {


    }

}
