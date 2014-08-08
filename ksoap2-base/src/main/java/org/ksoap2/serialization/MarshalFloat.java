/* Copyright (c) 2003,2004 Stefan Haustein, Oberhausen, Rhld., Germany
 * Copyright (c) 2006, James Seigel, Calgary, AB., Canada
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class MarshalFloat implements Marshal {

    public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo propertyInfo)
            throws IOException, XmlPullParserException {
        String stringValue = parser.nextText();
        Object result;
        if (name.equals("float")) {
            result = new Float(stringValue);
        } else if (name.equals("double")) {
            result = new Double(stringValue);
        } else if (name.equals("decimal")) {
            result = new java.math.BigDecimal(stringValue);
        } else {
            throw new RuntimeException("float, double, or decimal expected");
        }
        return result;
    }

    public void writeInstance(XmlSerializer writer, Object instance, PropertyInfo expected) throws IOException {
        writer.text(instance.toString());
    }

    public void register(SoapSerializationEnvelope cm) {
        cm.addMapping(cm.xsd, "float", Float.class, this);
        cm.addMapping(cm.xsd, "double", Double.class, this);
        cm.addMapping(cm.xsd, "decimal", java.math.BigDecimal.class, this);
    }
}
