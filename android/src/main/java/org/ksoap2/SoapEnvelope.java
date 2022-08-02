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

package org.ksoap2;

import java.io.*;
import org.kxml2.kdom.*;
import org.xmlpull.v1.*;

/**
 * A SOAP envelope, holding head and body objects. While this basic envelope
 * supports literal encoding as content format via KDom, The
 * SoapSerializationEnvelope provides support for the SOAP Serialization format
 * specification and simple object serialization.
 */

public class SoapEnvelope {

    /** SOAP Version 1.0 constant */
    public static final int VER10 = 100;
    /** SOAP Version 1.1 constant */
    public static final int VER11 = 110;
    /** SOAP Version 1.2 constant */
    public static final int VER12 = 120;
    public static final String ENV2003 = "http://www.w3.org/2003/05/soap-envelope";
    public static final String ENC2003 = "http://www.w3.org/2003/05/soap-encoding";
    /** Namespace constant: http://schemas.xmlsoap.org/soap/envelope/ */
    public static final String ENV = "http://schemas.xmlsoap.org/soap/envelope/";
    /** Namespace constant: http://schemas.xmlsoap.org/soap/encoding/ */
    public static final String ENC = "http://schemas.xmlsoap.org/soap/encoding/";
    /** Namespace constant: http://www.w3.org/2001/XMLSchema */
    public static final String XSD = "http://www.w3.org/2001/XMLSchema";
    /** Namespace constant: http://www.w3.org/2001/XMLSchema */
    public static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";
    /** Namespace constant: http://www.w3.org/1999/XMLSchema */
    public static final String XSD1999 = "http://www.w3.org/1999/XMLSchema";
    /** Namespace constant: http://www.w3.org/1999/XMLSchema */
    public static final String XSI1999 = "http://www.w3.org/1999/XMLSchema-instance";

    /**
     * Returns true for the string values "1" and "true", ignoring upper/lower
     * case and whitespace, false otherwise.
     */
    public static boolean stringToBoolean(String booleanAsString) {
        if (booleanAsString == null) {
            return false;
        }
        booleanAsString = booleanAsString.trim().toLowerCase();
        return (booleanAsString.equals("1") || booleanAsString.equals("true"));
    }

    /**
     * The body object received with this envelope. Will be an KDom Node for
     * literal encoding. For SOAP Serialization, please refer to
     * SoapSerializationEnvelope.
     */
    public Object bodyIn;
    /**
     * The body object to be sent with this envelope. Must be a KDom Node
     * modelling the remote call including all parameters for literal encoding.
     * For SOAP Serialization, please refer to SoapSerializationEnvelope
     */
    public Object bodyOut;
    /**
     * Incoming header elements
     */
    public Element[] headerIn;
    /**
     * Outgoing header elements
     */
    public Element[] headerOut;
    public String encodingStyle;
    /**
     * The SOAP version, set by the constructor
     */
    public int version;
    /** Envelope namespace, set by the constructor */
    public String env;
    /** Encoding namespace, set by the constructor */
    public String enc;
    /** Xml Schema instance namespace, set by the constructor */
    public String xsi;
    /** Xml Schema data namespace, set by the constructor */
    public String xsd;

    /**
     * Initializes a SOAP Envelope. The version parameter must be set to one of
     * VER10, VER11 or VER12
     */
    public SoapEnvelope(int version) {
        this.version = version;
        if (version == SoapEnvelope.VER10) {
            xsi = SoapEnvelope.XSI1999;
            xsd = SoapEnvelope.XSD1999;
        } else {
            xsi = SoapEnvelope.XSI;
            xsd = SoapEnvelope.XSD;
        }
        if (version < SoapEnvelope.VER12) {
            enc = SoapEnvelope.ENC;
            env = SoapEnvelope.ENV;
        } else {
            enc = SoapEnvelope.ENC2003;
            env = SoapEnvelope.ENV2003;
        }
    }

    /** Parses the SOAP envelope from the given parser */
    public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, env, "Envelope");
        encodingStyle = parser.getAttributeValue(env, "encodingStyle");
        parser.nextTag();
        if (parser.getEventType() == XmlPullParser.START_TAG
                && parser.getNamespace().equals(env)
                && parser.getName().equals("Header")) {
            parseHeader(parser);
            parser.require(XmlPullParser.END_TAG, env, "Header");
            parser.nextTag();
        }
        parser.require(XmlPullParser.START_TAG, env, "Body");
        encodingStyle = parser.getAttributeValue(env, "encodingStyle");
        parseBody(parser);
        parser.require(XmlPullParser.END_TAG, env, "Body");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, env, "Envelope");
    }

    public void parseHeader(XmlPullParser parser) throws IOException, XmlPullParserException {
        // consume start header
        parser.nextTag();
        // look at all header entries
        Node headers = new Node();
        headers.parse(parser);
        int count = 0;
        for (int i = 0; i < headers.getChildCount(); i++) {
            Element child = headers.getElement(i);
            if (child != null) {
                count++;
            }
        }
        headerIn = new Element[count];
        count = 0;
        for (int i = 0; i < headers.getChildCount(); i++) {
            Element child = headers.getElement(i);
            if (child != null) {
                headerIn[count++] = child;
            }
        }
    }

    public void parseBody(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.nextTag();
        // insert fault generation code here
        if (parser.getEventType() == XmlPullParser.START_TAG
                && parser.getNamespace().equals(env)
                && parser.getName().equals("Fault")) {

            SoapFault fault;
            if (this.version < SoapEnvelope.VER12) {
                fault = new SoapFault(this.version);
            } else {
                fault = new SoapFault12(this.version);
            }
            fault.parse(parser);
            bodyIn = fault;
        } else {
            Node node = (bodyIn instanceof Node) ? (Node) bodyIn : new Node();
            node.parse(parser);
            bodyIn = node;
        }
    }

    /**
     * Writes the complete envelope including header and body elements to the
     * given XML writer.
     */
    public void write(XmlSerializer writer) throws IOException {
        writer.setPrefix("i", xsi);
        writer.setPrefix("d", xsd);
        writer.setPrefix("c", enc);
        writer.setPrefix("v", env);
        writer.startTag(env, "Envelope");
        writer.startTag(env, "Header");
        writeHeader(writer);
        writer.endTag(env, "Header");
        writer.startTag(env, "Body");
        writeBody(writer);
        writer.endTag(env, "Body");
        writer.endTag(env, "Envelope");
    }

    /**
     * Writes the header elements contained in headerOut
     */
    public void writeHeader(XmlSerializer writer) throws IOException {
        if (headerOut != null) {
            for (int i = 0; i < headerOut.length; i++) {
                headerOut[i].write(writer);
            }
        }
    }

    /**
     * Writes the SOAP body stored in the object variable bodyIn, Overwrite this
     * method for customized writing of the soap message body.
     */
    public void writeBody(XmlSerializer writer) throws IOException {
        if (encodingStyle != null) {
            writer.attribute(env, "encodingStyle", encodingStyle);
        }
        ((Node) bodyOut).write(writer);
    }

    /**
     * Assigns the object to the envelope as the outbound message for the soap call.
     * @param soapObject the object to send in the soap call.
     */
    public void setOutputSoapObject(Object soapObject) {
        bodyOut = soapObject;
    }

}
