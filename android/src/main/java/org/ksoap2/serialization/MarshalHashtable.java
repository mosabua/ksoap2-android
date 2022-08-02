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
 * IN THE SOFTWARE. 
 *
 * Contributor(s): Sean McDaniel
 *
 * */

package org.ksoap2.serialization;

import java.io.*;
import java.util.*;

import org.xmlpull.v1.*;

/**
 * Serializes instances of hashtable to and from xml. This implementation is
 * based on the xml schema from apache-soap, namely the type 'map' in the
 * namespace 'http://xml.apache.org/xml-soap'. Other soap implementations
 * including apache (obviously) and glue are also interoperable with the
 * schema.
 */
public class MarshalHashtable implements Marshal {

    /** use then during registration */
    public static final String NAMESPACE = "http://xml.apache.org/xml-soap";
    /** use then during registration */
    public static final String NAME = "Map";
    /** CLDC does not support .class, so this helper is needed. */
    public static final Class HASHTABLE_CLASS = new Hashtable().getClass();
    SoapSerializationEnvelope envelope;

    public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo expected)
            throws IOException, XmlPullParserException {
        Hashtable instance = new Hashtable();
        String elementName = parser.getName();
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            SoapObject item = new ItemSoapObject(instance);
            parser.require(XmlPullParser.START_TAG, null, "item");
            parser.nextTag();
            Object key = envelope.read(parser, item, 0, null, null, PropertyInfo.OBJECT_TYPE);
            parser.nextTag();
            if (key != null) {
                item.setProperty(0, key);
            }
            Object value = envelope.read(parser, item, 1, null, null, PropertyInfo.OBJECT_TYPE);
            parser.nextTag();
            if (value != null) {
                item.setProperty(1, value);
            }
            parser.require(XmlPullParser.END_TAG, null, "item");
        }
        parser.require(XmlPullParser.END_TAG, null, elementName);
        return instance;
    }

    public void writeInstance(XmlSerializer writer, Object instance) throws IOException {
        Hashtable h = (Hashtable) instance;
        SoapObject item = new SoapObject(null, null);
        item.addProperty("key", null);
        item.addProperty("value", null);
        for (Enumeration keys = h.keys(); keys.hasMoreElements();) {
            writer.startTag("", "item");
            Object key = keys.nextElement();
            item.setProperty(0, key);
            item.setProperty(1, h.get(key));
            envelope.writeObjectBodyWithAttributes(writer, item);
            writer.endTag("", "item");
        }
    }

    class ItemSoapObject extends SoapObject {
        Hashtable h;
        int resolvedIndex = -1;
        ItemSoapObject(Hashtable h) {
            super(null, null);
            this.h = h;
            addProperty("key", null);
            addProperty("value", null);
        }

        // 0 & 1 only valid
        public void setProperty(int index, Object value) {
            if (resolvedIndex == -1) {
                super.setProperty(index, value);
                resolvedIndex = index;
            } else {
                // already have a key or value
                Object resolved = resolvedIndex == 0 ? getProperty(0) : getProperty(1);
                if (index == 0) {
                    h.put(value, resolved);
                } else  {
                    h.put(resolved, value);
                }
            }
        }
    }

    public void register(SoapSerializationEnvelope cm) {
        envelope = cm;
        cm.addMapping(MarshalHashtable.NAMESPACE, MarshalHashtable.NAME, HASHTABLE_CLASS, this);
    }
}
