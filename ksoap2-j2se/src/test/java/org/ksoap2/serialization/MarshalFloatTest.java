package org.ksoap2.serialization;

import java.io.*;
import java.math.*;

import junit.framework.*;

import org.ksoap2.*;
import org.ksoap2.transport.mock.*;
import org.xmlpull.v1.*;

public class MarshalFloatTest extends TestCase {
    private static final String FLOAT_LABEL = "float";

    private static final String FLOATING_POINT_VALUE = "12.0";

    private MarshalFloat marshalFloat;

    protected void setUp() throws Exception {
        marshalFloat = new MarshalFloat();
    }

    public void testMarshalDateInbound() throws IOException, XmlPullParserException {
        MockXmlPullParser mockXmlPullParser = new MockXmlPullParser();
        mockXmlPullParser.nextText = FLOATING_POINT_VALUE;
        Number floatingPointValue = (Number) marshalFloat.readInstance(mockXmlPullParser, null, FLOAT_LABEL, null);
        assertTrue(floatingPointValue instanceof Float);
        assertEquals(new Float(FLOATING_POINT_VALUE).floatValue(), floatingPointValue.floatValue(), 0.01f);

        floatingPointValue = (Number) marshalFloat.readInstance(mockXmlPullParser, null, "double", null);
        assertTrue(floatingPointValue instanceof Double);
        assertEquals(new Double(FLOATING_POINT_VALUE).doubleValue(), floatingPointValue.doubleValue(), 0.01);

        floatingPointValue = (Number) marshalFloat.readInstance(mockXmlPullParser, null, "decimal", null);
        assertTrue(floatingPointValue instanceof BigDecimal);
        assertEquals(new BigDecimal(FLOATING_POINT_VALUE).doubleValue(), floatingPointValue.doubleValue(), 0.01);

        try {
            floatingPointValue = (Number) marshalFloat.readInstance(mockXmlPullParser, null, "unknown type", null);
            fail();
        } catch (RuntimeException e) {
            assertNotNull(e.getMessage());
        }

    }

    public void testMarshalDateOutbound_Float() throws IOException {
        MockXmlSerializer writer = new MockXmlSerializer();
        marshalFloat.writeInstance(writer, new Float(12.0), null);
        assertEquals(FLOATING_POINT_VALUE, writer.getOutputText());
    }
    
    public void testmarshalDateOutbound_Double() throws IOException {
        MockXmlSerializer writer = new MockXmlSerializer();
        marshalFloat.writeInstance(writer, new Double(12.0), null);
        assertEquals(FLOATING_POINT_VALUE, writer.getOutputText());
    }

    public void testmarshalDateOutbound_Decimal() throws IOException {
        MockXmlSerializer writer = new MockXmlSerializer();
        marshalFloat.writeInstance(writer, new BigDecimal(12.0), null);
        assertEquals("12", writer.getOutputText());
    }
    
    public void testRegistration_moreIntegrationLike() throws IOException, XmlPullParserException {
        MockXmlPullParser pullParser = new MockXmlPullParser();
        pullParser.nextText = FLOATING_POINT_VALUE;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        marshalFloat.register(envelope);
        assertTrue(envelope.classToQName.containsKey(Float.class.getName()));
        assertTrue(envelope.classToQName.containsKey(Double.class.getName()));
        assertTrue(envelope.classToQName.containsKey(BigDecimal.class.getName()));

        Float floatingPointValue = (Float) envelope.readInstance(pullParser, envelope.xsd, FLOAT_LABEL, null);
        assertEquals(new Float(FLOATING_POINT_VALUE).floatValue(), floatingPointValue.floatValue(), 0.01f);

    }

}
