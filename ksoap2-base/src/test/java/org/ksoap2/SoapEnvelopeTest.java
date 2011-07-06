/* Copyright (c) 2006, James Seigel, Calgary, AB., Canada
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

import junit.framework.*;

public class SoapEnvelopeTest extends TestCase {
    
    public void testInitialConfigurationCorrect_Version10() {
        SoapEnvelope envelope = new SoapEnvelope(SoapEnvelope.VER10);
        assertEquals(SoapEnvelope.XSI1999, envelope.xsi);
        assertEquals(SoapEnvelope.XSD1999, envelope.xsd);
        assertEquals(SoapEnvelope.ENC, envelope.enc);
        assertEquals(SoapEnvelope.ENV, envelope.env);
        assertEquals(SoapEnvelope.VER10, envelope.version);
    }

    public void testInitialConfigurationCorrect_Version11() {
        SoapEnvelope envelope = new SoapEnvelope(SoapEnvelope.VER11);
        assertEquals(SoapEnvelope.XSI, envelope.xsi);
        assertEquals(SoapEnvelope.XSD, envelope.xsd);
        assertEquals(SoapEnvelope.ENC, envelope.enc);
        assertEquals(SoapEnvelope.ENV, envelope.env);
        assertEquals(SoapEnvelope.VER11, envelope.version);
    }
    
    public void testInitialConfigurationCorrect_Version12() {
        SoapEnvelope envelope = new SoapEnvelope(SoapEnvelope.VER12);
        assertEquals(SoapEnvelope.XSI, envelope.xsi);
        assertEquals(SoapEnvelope.XSD, envelope.xsd);
        assertEquals(SoapEnvelope.ENC2003, envelope.enc);
        assertEquals(SoapEnvelope.ENV2003, envelope.env);
        assertEquals(SoapEnvelope.VER12, envelope.version);
    }
    
    public void testStringToBoolean() {
        assertTrue(SoapEnvelope.stringToBoolean("true"));
        assertTrue(SoapEnvelope.stringToBoolean(" true "));
        assertTrue(SoapEnvelope.stringToBoolean("TRUE"));
        assertTrue(SoapEnvelope.stringToBoolean("1"));
        assertTrue(SoapEnvelope.stringToBoolean(" 1"));
        assertFalse(SoapEnvelope.stringToBoolean("false"));
        assertFalse(SoapEnvelope.stringToBoolean("FALSE"));
        assertFalse(SoapEnvelope.stringToBoolean("0"));
    }

    public void testStringToBoolean_ExceptionalCases() {
        assertFalse(SoapEnvelope.stringToBoolean(null));
        assertFalse(SoapEnvelope.stringToBoolean("bob"));
        assertFalse(SoapEnvelope.stringToBoolean("1.0"));
    }
}
