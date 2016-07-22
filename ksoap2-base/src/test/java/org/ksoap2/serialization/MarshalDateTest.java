package org.ksoap2.serialization;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.ksoap2.*;
import org.ksoap2.transport.mock.*;
import org.xmlpull.v1.*;

public class MarshalDateTest extends TestCase {
    private static final Date TEST_DATE;
    private static final String ENCODED_DATE_STRING = "2016-07-22T11:50Z";
    
    static {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(0);
        cal.set(2016, Calendar.JULY, 22, 11, 50, 0);
        TEST_DATE = cal.getTime();
    }

    private MarshalDate marshalDate;

    protected void setUp() throws Exception {
        marshalDate = new MarshalDate();
    }

    public void testMarshalDateInbound() throws IOException, XmlPullParserException {
        MockXmlPullParser mockXmlPullParser = new MockXmlPullParser();
        mockXmlPullParser.nextText = ENCODED_DATE_STRING;
        Date date =  (Date) marshalDate.readInstance(mockXmlPullParser, null, null, null);
        assertEquals(TEST_DATE, date);
    }

    public void testMarshalDateOutbound() throws IOException {
        MockXmlSerializer writer = new MockXmlSerializer();
        marshalDate.writeInstance(writer , TEST_DATE);
        assertEquals(ENCODED_DATE_STRING, writer.getOutputText());
    }
    
    public void testRegistration_moreIntegrationLike() throws IOException, XmlPullParserException {
        MockXmlPullParser pullParser = new MockXmlPullParser();
        pullParser.nextText = ENCODED_DATE_STRING;
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        marshalDate.register(envelope);
        assertTrue(envelope.classToQName.containsKey(MarshalDate.DATE_CLASS.getName()));
        
        Date date = (Date) envelope.readInstance(pullParser, envelope.xsd, "dateTime", null);
        assertEquals(TEST_DATE, date);
        
    }
    
}
