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

import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xmlpull.v1.*;


/** 
 * Marshal class for Dates. 
 */
public class MarshalDate implements Marshal {
    public static Class DATE_CLASS = new Date().getClass();
    private final SimpleDateFormat dateFormat;

    public MarshalDate() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'",
            Locale.US);
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo expected)
            throws IOException, XmlPullParserException {
        try {
            return dateFormat.parse(parser.nextText());
            
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    public void writeInstance(XmlSerializer writer, Object obj) throws IOException {
        writer.text(dateFormat.format((Date) obj));
    }

    public void register(SoapSerializationEnvelope cm) {
        cm.addMapping(cm.xsd, "dateTime", MarshalDate.DATE_CLASS, this);
    }

}
