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

import java.io.*;
import org.xmlpull.v1.*;

/**
 * Interface for custom (de)serialization.
 */

public interface Marshal {

    /**
     * This methods reads an instance from the given parser. For implementation,
     * please note that the start and and tag must be consumed. This is not
     * symmetric to writeInstance, but otherwise it would not be possible to
     * access the attributes of the start tag here.
     * 
     * @param parser
     *            the xml parser
     * @param namespace
     *            the namespace.
     * @return the object read from the xml stream.
     */
    public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo expected)
            throws IOException, XmlPullParserException;

    /**
     * Write the instance to the given XmlSerializer. In contrast to
     * readInstance, it is not neccessary to care about the surrounding start
     * and end tags. Additional attributes must be writen before anything else
     * is written.
     * 
     * @param writer
     *            the xml serializer.
     * @param instance
     *            the instance to write to the writer.
     */
    public void writeInstance(XmlSerializer writer, Object instance) throws IOException;

    /**
     * Register this Marshal with Envelope
     * 
     * @param envelope
     *            the soap serialization envelope.
     */
    public void register(SoapSerializationEnvelope envelope);
}
