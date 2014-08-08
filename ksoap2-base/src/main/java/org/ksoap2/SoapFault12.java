/*
 * Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 * 
 * Copyright (c) 2011, Petter Uvesten, Everichon AB, Sweden
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ksoap2;

import java.io.IOException;

import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/**
 * Exception class encapsulating SOAP 1.2 Faults
 * 
 * see http://www.w3.org/TR/soap12-part1/#soapfault for explanation of fields
 *
 * @author Petter Uvesten
 */

public class SoapFault12 extends SoapFault {
    private static final long serialVersionUID = 1012001L;
                    
    /** Top-level nodes */
    public Node Code;
    public Node Reason;
    public Node Node;
    public Node Role;
    public Node Detail;
                    
    public SoapFault12() {
        super();
        this.version = SoapEnvelope.VER12;
    }
                    
    public SoapFault12(int version) {
        super();
        this.version = version;
    }
                    

    /** Fills the fault details from the given XML stream */
    public void parse(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        parseSelf(parser);
        // done parsing, populate some of the legacy public members
        this.faultcode = Code.getElement(SoapEnvelope.ENV2003, "Value").getText(0);
        this.faultstring = Reason.getElement(SoapEnvelope.ENV2003, "Text").getText(0);
        this.detail = this.Detail;
        this.faultactor = null;
    }
                    
                    
    private void parseSelf(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, SoapEnvelope.ENV2003, "Fault");

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String name = parser.getName();
            String namespace = parser.getNamespace();
            parser.nextTag();
            if (name.toLowerCase().equals("Code".toLowerCase())) {
                this.Code = new Node();
                this.Code.parse(parser);
            } else if (name.toLowerCase().equals("Reason".toLowerCase())) {
                this.Reason = new Node();
                this.Reason.parse(parser);
            } else if (name.toLowerCase().equals("Node".toLowerCase())) {
                this.Node = new Node();
                this.Node.parse(parser);
            } else if (name.toLowerCase().equals("Role".toLowerCase())) {
                this.Role = new Node();
                this.Role.parse(parser);
            } else if (name.toLowerCase().equals("Detail".toLowerCase())) {
                this.Detail = new Node();
                this.Detail.parse(parser);
            } else {
                throw new RuntimeException("unexpected tag:" + name);
            }

            parser.require(XmlPullParser.END_TAG, namespace, name);
        }
        parser.require(XmlPullParser.END_TAG, SoapEnvelope.ENV2003, "Fault");
        parser.nextTag();

    }
                    
    /** Writes the fault to the given XML stream */
    public void write(XmlSerializer xw) throws IOException
    {
        xw.startTag(SoapEnvelope.ENV2003, "Fault");
        //this.Code.write(xw);

        xw.startTag(SoapEnvelope.ENV2003, "Code");
        this.Code.write(xw);
        xw.endTag(SoapEnvelope.ENV2003, "Code");
        xw.startTag(SoapEnvelope.ENV2003, "Reason");
        this.Reason.write(xw);
        xw.endTag(SoapEnvelope.ENV2003, "Reason");
                                        
        if (this.Node != null) {
            xw.startTag(SoapEnvelope.ENV2003, "Node");
            this.Node.write(xw);
            xw.endTag(SoapEnvelope.ENV2003, "Node");
        }
        if (this.Role != null) {
            xw.startTag(SoapEnvelope.ENV2003, "Role");
            this.Role.write(xw);
            xw.endTag(SoapEnvelope.ENV2003, "Role");
        }
                                        
        if (this.Detail != null) {
            xw.startTag(SoapEnvelope.ENV2003, "Detail");
            this.Detail.write(xw);
            xw.endTag(SoapEnvelope.ENV2003, "Detail");
        }
        xw.endTag(SoapEnvelope.ENV2003, "Fault");
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        return Reason.getElement(SoapEnvelope.ENV2003, "Text").getText(0);
    }

    /** Returns a string representation of the fault */
    public String toString() {
        String reason = Reason.getElement(SoapEnvelope.ENV2003, "Text").getText(0);
        String code = Code.getElement(SoapEnvelope.ENV2003, "Value").getText(0);
        return "Code: " + code + ", Reason: " + reason;
    }
}
