package org.ksoap2.serialization;

import java.io.*;

import org.kobjects.base64.*;
import org.ksoap2.*;
import org.ksoap2.transport.mock.*;
import org.xmlpull.v1.*;

import junit.framework.*;

public class MarshalBase64Test extends TestCase {

    private static final String TEST_STRING = "A very quick test string 13412341234";
    private static final String ENCODED_TEST_STRING = Base64.encode(TEST_STRING.getBytes());

    private MarshalBase64 marshalBase64;

    protected void setUp() throws Exception {
        marshalBase64 = new MarshalBase64();
    }

    public void testConvertFromBase64() throws IOException, XmlPullParserException {
        MockXmlPullParser mockXmlPullParser = new MockXmlPullParser();
        mockXmlPullParser.nextText = ENCODED_TEST_STRING;
        byte[] byteArray = (byte[]) marshalBase64.readInstance(mockXmlPullParser, null, null, null);
        assertEquals(TEST_STRING, new String(byteArray));
    }

    public void testConvertToBase64() throws IOException {
        MockXmlSerializer writer = new MockXmlSerializer();
        marshalBase64.writeInstance(writer , TEST_STRING.getBytes(), null);
        assertEquals(ENCODED_TEST_STRING, writer.getOutputText());
    }
    
    public void testRegistration_moreIntegrationLike() throws IOException, XmlPullParserException {
        MockXmlPullParser pullParser = new MockXmlPullParser();
        pullParser.nextText = ENCODED_TEST_STRING;
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        marshalBase64.register(envelope);
        assertTrue(envelope.classToQName.containsKey(MarshalBase64.BYTE_ARRAY_CLASS.getName()));
        
        byte[] decodedArray = (byte[]) envelope.readInstance(pullParser, envelope.xsd, "base64Binary", null);
        assertEquals(TEST_STRING, new String(decodedArray));
        
        decodedArray = (byte[]) envelope.readInstance(pullParser, SoapEnvelope.ENC, "base64", null);
        assertEquals(TEST_STRING, new String(decodedArray));
    }

}
