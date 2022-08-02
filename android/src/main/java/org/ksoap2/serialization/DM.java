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

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/**
 * This class is not public, so save a few bytes by using a short class name (DM
 * stands for DefaultMarshal)...
 */
class DM implements Marshal {

    public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo excepted)
            throws IOException, XmlPullParserException {
        String text = parser.nextText();
        switch (name.charAt(0)) {
            case 's':
                return text;
            case 'i':
                return new Integer(Integer.parseInt(text));
            case 'l':
                return new Long(Long.parseLong(text));
            case 'b':
                return new Boolean(SoapEnvelope.stringToBoolean(text));
            default:
                throw new RuntimeException();
        }
    }

    /**
     * Write the instance out. In case it is an AttributeContainer write those our first though. 
     * If it HasAttributes then write the attributes and values.
     *
     * @param writer   the xml serializer.
     * @param instance
     * @throws IOException
     */
    public void writeInstance(XmlSerializer writer, Object instance) throws IOException {
        if (instance instanceof AttributeContainer) {
            AttributeContainer attributeContainer = (AttributeContainer) instance;
            int cnt = attributeContainer.getAttributeCount();
            for (int counter = 0; counter < cnt; counter++) {
                AttributeInfo attributeInfo = new AttributeInfo();
                attributeContainer.getAttributeInfo(counter, attributeInfo);
                try {
                    attributeContainer.getAttribute(counter, attributeInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (attributeInfo.getValue() != null) {
                    writer.attribute(attributeInfo.getNamespace(), attributeInfo.getName(),
                            (attributeInfo.getValue() != null) ? attributeInfo.getValue().toString() : "");
                }
            }
        } else if (instance instanceof HasAttributes) {
            HasAttributes soapObject = (HasAttributes) instance;
            int cnt = soapObject.getAttributeCount();
            for (int counter = 0; counter < cnt; counter++) {
                AttributeInfo attributeInfo = new AttributeInfo();
                soapObject.getAttributeInfo(counter, attributeInfo);
                try {
                    soapObject.getAttribute(counter, attributeInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (attributeInfo.getValue() != null) {
                    writer.attribute(attributeInfo.getNamespace(), attributeInfo.getName(),
                            attributeInfo.getValue() != null ? attributeInfo.getValue().toString() : "");
                }
            }
        }

        if(instance  instanceof ValueWriter)
        {
            ((ValueWriter)instance).write(writer);
        }
        else
        {
            writer.text(instance.toString());
        }

    }

    public void register(SoapSerializationEnvelope cm) {
        cm.addMapping(cm.xsd, "int", PropertyInfo.INTEGER_CLASS, this);
        cm.addMapping(cm.xsd, "long", PropertyInfo.LONG_CLASS, this);
        cm.addMapping(cm.xsd, "string", PropertyInfo.STRING_CLASS, this);
        cm.addMapping(cm.xsd, "boolean", PropertyInfo.BOOLEAN_CLASS, this);
    }
}
