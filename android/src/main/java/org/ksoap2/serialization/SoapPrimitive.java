/* Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */

package org.ksoap2.serialization;

/**
 * A class that is used to encapsulate primitive types (represented by a string
 * in XML serialization).
 *
 * Basically, the SoapPrimitive class encapsulates "unknown" primitive types
 * (similar to SoapObject encapsulating unknown complex types). For example, new
 * SoapPrimitive (classMap.xsd, "float", "12.3") allows you to send a float from
 * a MIDP device to a server although MIDP does not support floats. In the other
 * direction, kSOAP will deserialize any primitive type (=no subelements) that
 * are not recognized by the ClassMap to SoapPrimitive, preserving the
 * namespace, name and string value (this is how the stockquote example works).
 */

public class SoapPrimitive extends AttributeContainer {
    protected String namespace;
    protected String name;
    protected Object value;

    public  static final Object NullSkip = new Object();
    public  static final Object NullNilElement = new Object();

    public SoapPrimitive(String namespace, String name, Object value) {
        this.namespace = namespace;
        this.name = name;
        this.value = value;
    }

    public boolean equals(Object o) {
        if (!(o instanceof SoapPrimitive)) {
            return false;
        }
        SoapPrimitive p = (SoapPrimitive) o;
        boolean varsEqual = name.equals(p.name)
                && (namespace == null ? p.namespace == null:namespace.equals(p.namespace))
                && (value == null ? (p.value == null) : value.equals(p.value));
        return varsEqual && attributesAreEqual(p);
    }

    public int hashCode() {
        return name.hashCode() ^ (namespace == null ? 0 : namespace.hashCode());
    }

    public String toString() {
        return value != null ? value.toString() : null;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

}
