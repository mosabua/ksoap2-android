/*
 * Copyright (c) 2006, James Seigel, Calgary, AB., Canada
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

package org.ksoap2.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.transport.ServiceConnectionFixture;
import org.ksoap2.transport.mock.ComplexResponse;
import org.ksoap2.transport.mock.MockTransport;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author James Seigel (original author)
 * @author Manfred Moser <manfred@simpligility.com> (minor test additions)
 */
public class SoapSerializationEnvelopeTest extends TestCase {
    private static final String PARAMETER_NAME = "aParameter";
    private static String FUNCTION_NAME = "FunctionName";
    private static String NAMESPACE_NAME = ServiceConnectionFixture.NAMESPACE;
    private static final String BODY_XML_STRING = "<n0:" + FUNCTION_NAME
            + " id=\"o0\" n1:root=\"1\" xmlns:n0=\"" + NAMESPACE_NAME
            + "\" xmlns:n1=\"http://schemas.xmlsoap.org/soap/encoding/\"";
    private static final String END_XML_STRING = " />";
    private static final String END_XML_FUNCTION_STRING = "</n0:" + FUNCTION_NAME + ">";
    private KXmlSerializer xmlWriter;
    private SoapSerializationEnvelope envelope;
    private ByteArrayOutputStream outputStream;
    private SoapObject soapObject;
    private MockTransport myTransport;

    protected void setUp() throws Exception {
        super.setUp();
        xmlWriter = new KXmlSerializer();
        outputStream = new ByteArrayOutputStream();
        xmlWriter.setOutput(outputStream, "UTF-8");
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.addMapping(NAMESPACE_NAME, ServiceConnectionFixture.RESPONSE_CLASS_NAME,
                ServiceConnectionFixture.RESPONSE_CLASS);
        soapObject = new SoapObject(NAMESPACE_NAME, FUNCTION_NAME);
        myTransport = new MockTransport();
    }

    // public void xx_testTwoDimensionalStringArrays() throws Throwable {
    // // can't handle two dimensional arrays.
    // myTransport.parseResponse(envelope,
    // ServiceConnectionFixture.createTwoDimensionalStringArrayResponseAsStream());
    // Object result = envelope.getResponse();
    // ServiceConnectionFixture.assertComplexResponseCorrect((ComplexResponse) result);
    // }


    // Test that we can parse a response as an "unknown" type an include attributes on primitives.

    public void testInboundWithAttributesOnPrimitives() throws Throwable {
        myTransport.parseResponse(envelope, ServiceConnectionFixture.createWorkingNoMultirefWithAttributesAsStream());
        SoapObject cfr = (SoapObject) envelope.getResponse();
        assertEquals(4, cfr.getPropertyCount());


        SoapPrimitive longResp = (SoapPrimitive) cfr.getProperty("longResponse");
        assertEquals(ServiceConnectionFixture.theLongResponse + "", longResp.toString());
        assertEquals("valueBar", longResp.safeGetAttribute("attrFoo"));

        assertEquals(ServiceConnectionFixture.theStringResponse, cfr.getProperty("stringResponse").toString());
        assertEquals(ServiceConnectionFixture.theIntegerResponse + "",
                cfr.getProperty(ComplexResponse.INTEGER_REPONSE_NAME).toString());
        assertEquals(ServiceConnectionFixture.theBooleanResponse + "",
                cfr.getProperty(ComplexResponse.BOOLEAN_RESPONSE_NAME).toString());


    }

    public void testInbound() throws Throwable {
        myTransport.parseResponse(envelope, ServiceConnectionFixture.createWorkingNoMultirefAsStream());
        Object result = envelope.getResponse();
        ServiceConnectionFixture.assertComplexResponseCorrect((ComplexResponse) result);

        myTransport.parseResponse(envelope, ServiceConnectionFixture.createWorkingAsStream());
        result = envelope.getResponse();
        ServiceConnectionFixture.assertComplexResponseCorrect((ComplexResponse) result);

        myTransport.parseResponse(envelope, ServiceConnectionFixture
                .createWorkingNoMultirefAsStream_reversedResponseParameters());
        result = envelope.getResponse();
        ServiceConnectionFixture.assertComplexResponseCorrect((ComplexResponse) result);

        // Can't handle multirefs yet
        //
        // myTransport.parseResponse(envelope,
        // ServiceConnectionFixture.createMultirefAsStream());
        // result = envelope.getResult();
        // ServiceConnectionFixture.assertComplexResponseCorrect((ComplexResponse)
        // result);

    }

    public void testRealLifeResponse() throws Exception {
        myTransport.parseResponse(envelope, ServiceConnectionFixture.createRealLifeResponse());
        SoapObject result = (SoapObject) envelope.getResponse();

        SoapPrimitive item = (SoapPrimitive) result.getProperty(0);
        assertEquals("Alpine Urgent Care", item.safeGetAttribute("GetCensusByUrgentCarePROD2ResultKey"));
        assertEquals("1", item.toString());


    }


    public void testReadInstance_SoapObject_Reversed() throws Throwable {
        KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture
                .createWorkingNoMultirefAsStream_reversedResponseParameters());
        SoapSerializationEnvelope localEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject aSoapObject = (SoapObject) localEnvelope.readUnknown(parser, NAMESPACE_NAME,
                ServiceConnectionFixture.RESPONSE_CLASS_NAME);
        assertEquals(ServiceConnectionFixture.theStringResponse, aSoapObject.getProperty(0));
        assertEquals("" + ServiceConnectionFixture.theLongResponse, aSoapObject.getProperty(1).toString());
    }

    public void testReadInstance_SoapObject() throws Throwable {
        KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture
                .createWorkingNoMultirefAsStream());
        SoapSerializationEnvelope localEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject aSoapObject = (SoapObject) localEnvelope.readUnknown(parser, NAMESPACE_NAME,
                ServiceConnectionFixture.RESPONSE_CLASS_NAME);
        assertEquals(ServiceConnectionFixture.theStringResponse, aSoapObject.getProperty(1));
        assertEquals("" + ServiceConnectionFixture.theLongResponse, aSoapObject.getProperty(0).toString());
    }

    public void testReadSerializable_ParameterOrderReverse() throws Throwable {
        ComplexResponse complexResponse = new ComplexResponse();
        KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture
                .createWorkingNoMultirefAsStream_reversedResponseParameters());
        envelope.readSerializable(parser, complexResponse);
        ServiceConnectionFixture.assertComplexResponseCorrect(complexResponse);
    }

    public void testReadSerializable_ParameterOrderNormal() throws Throwable {
        ComplexResponse complexResponse = new ComplexResponse();
        KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture
                .createWorkingNoMultirefAsStream());
        envelope.readSerializable(parser, complexResponse);
        ServiceConnectionFixture.assertComplexResponseCorrect(complexResponse);
    }

    public void testReadSerializable_ParameterOrderNormal_NullNamespace() throws Throwable {
        ComplexResponse complexResponse = new ComplexResponse();
        complexResponse.namespace = null;
        KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture
                .createWorkingNoMultirefAsStream());
        envelope.readSerializable(parser, complexResponse);
        ServiceConnectionFixture.assertComplexResponseCorrect(complexResponse);
    }

    public void testReadSerializable_ParameterOrderNormal_NullNamespace_NullName() {
        // SF Bug # 1442028
        try {
            ComplexResponse complexResponse = new ComplexResponse();
            complexResponse.namespace = null;
            complexResponse.parameterCount = 2;
            complexResponse.responseOne_Name = null;
            KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture
                    .createWorkingNoMultirefAsStream());
            envelope.readSerializable(parser, complexResponse);
            ServiceConnectionFixture.assertComplexResponseCorrect(complexResponse);
        }
        catch (Throwable e) {
            assertFalse(e instanceof NullPointerException);
        }
    }

    private KXmlParser primedParserForSerializableParameterTest(InputStream inputStream) throws Throwable {
        KXmlParser parser = new KXmlParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(inputStream, null);
        parser.nextTag();
        parser.nextTag();
        parser.nextTag();
        parser.nextTag();
        return parser;
    }

    public void testWriteHashtable() throws IOException {
        Hashtable hashtable = new Hashtable();
        hashtable.put("key1", "value1");
        soapObject.addProperty("hashthingy", hashtable);
        new MarshalHashtable().register(envelope);
        writeBodyWithSoapObject(soapObject);
        assertEquals(
                BODY_XML_STRING
                        + ">"
                        + "<hashthingy n3:type=\"n2:Map\" xmlns:n2=\"http://xml.apache.org/xml-soap\" " +
                        "xmlns:n3=\"http://www.w3.org/2001/XMLSchema-instance\"><item><key n3:type=\"n4:string\" " +
                        "xmlns:n4=\"http://www.w3.org/2001/XMLSchema\">key1</key><value n3:type=\"n5:string\" " +
                        "xmlns:n5=\"http://www.w3.org/2001/XMLSchema\">value1</value></item>" +
                        "</hashthingy></n0:FunctionName>",

                new String(outputStream.toByteArray()));
    }

    public void testReadHashtable() throws XmlPullParserException, IOException {
        new MarshalHashtable().register(envelope);
        myTransport.parseResponse(envelope, ServiceConnectionFixture.hashTableAsStream());
        Hashtable result = (Hashtable) envelope.bodyIn;
        assertEquals("value1", result.get("key1"));
    }

    public void testReadHashtable_dupkeyvalueandnulls() throws XmlPullParserException, IOException {
        new MarshalHashtable().register(envelope);
        myTransport.parseResponse(envelope, ServiceConnectionFixture.hashTableWithDupAsStream());
        Hashtable result = (Hashtable) envelope.bodyIn;
        assertEquals("value1", result.get("key1"));
        assertEquals(1, result.values().size());
    }

    public void testWritingBody_WithNullParameter() throws Exception {
        soapObject.addProperty(PARAMETER_NAME, null);
        writeBodyWithSoapObject(soapObject);
        assertEquals(BODY_XML_STRING + ">" + "<" + PARAMETER_NAME + " n2:null=\"true\" xmlns:n2=\""
                + envelope.xsi + "\" />" + END_XML_FUNCTION_STRING, new String(outputStream.toByteArray()));
    }

    public void testWritingBody_WithPrimitiveBooleanParameters() throws Exception {
        assertPrimitiveParameterCorrect(Boolean.TRUE, "boolean");
    }

    public void testWritingBody_WithPrimitiveStringParameters() throws Exception {
        assertPrimitiveParameterCorrect("aStringValue", "string");
    }

    public void testWritingBody_WithPrimitiveIntegerParameters() throws Exception {
        assertPrimitiveParameterCorrect(new Integer(2), "int");
    }

    public void testWritingBody_WithPrimitiveLongParameters() throws Exception {
        assertPrimitiveParameterCorrect(new Long(2), "long");
    }

    private void assertPrimitiveParameterCorrect(Object primtiveValue, String type) throws IOException {
        soapObject.addProperty(PARAMETER_NAME, primtiveValue);
        writeBodyWithSoapObject(soapObject);
        assertEquals(BODY_XML_STRING + ">" + getParameterBody(type, primtiveValue) + END_XML_FUNCTION_STRING,
                new String(outputStream.toByteArray()));
    }

    public void testWritingBody_NullBody() throws IOException {
        envelope.setOutputSoapObject(null);
        envelope.writeBody(xmlWriter);
        // null body no longer fails
    }

    public void testWritingBody_SetEmpty() throws IOException {
        envelope.setBodyOutEmpty(true);
        envelope.writeBody(xmlWriter);
    }

    public void testWritingBody_EmptyBody() throws Exception {
        writeBodyWithSoapObject(soapObject);
        assertEquals(BODY_XML_STRING + END_XML_STRING, new String(outputStream.toByteArray()));
    }

    public void testWritingBody_NestedSoapObject() throws Exception {
        soapObject.addSoapObject(createNestedSoapObject());
        writeBodyWithSoapObject(soapObject);
    }

    public void testWritingBody_PropertyOfTypeSoapObject() throws Exception {
        soapObject.addProperty("objectproperty", createNestedSoapObject());
        writeBodyWithSoapObject(soapObject);
    }

    private SoapObject createNestedSoapObject() {
        SoapObject nested = new SoapObject(null, "nested");
        nested.addProperty("nestedprop", "nestedpropvalue");
        return nested;
    }

    private String getParameterBody(String type, Object aValue) {
        return "<" + PARAMETER_NAME + " n3:type=\"n2:" + type + "\" xmlns:n2=\"" + envelope.xsd
                + "\" xmlns:n3=\"" + envelope.xsi + "\">" + aValue + "</" + PARAMETER_NAME + ">";
    }

    private void writeBodyWithSoapObject(SoapObject soapObject) throws IOException {
        envelope.setOutputSoapObject(soapObject);
        envelope.writeBody(xmlWriter);
        xmlWriter.flush();
    }

    /**
     * Proof that the service was called, and how often.
     */
    static protected int serviceCallCount;

    /*
      * We're going to execute "GetStatus" according to the following WSDL snippet. This is done in pieces as
      * unit tests. The definition is from the OPCFoundation OPC XML DA WSDL
      * (http://opcfoundation.org/webservices/XMLDA/1.0/). The document from http://www.opcfoundation.com also
      * provides examples of the transactions.
      *
      * <types><s:schema elementFormDefault="qualified"
      * targetNamespace="http://opcfoundation.org/webservices/XMLDA/1.0/"> <s:element name="getStatus">
      * <s:complexType> <s:attribute name="LocaleID" type="s:string"/> <s:attribute name="ClientRequestHandle"
      * type="s:string"/> </s:complexType> </s:element> <s:element name="GetStatusResponse"> <s:complexType>
      * <s:sequence> <s:element minOccurs="0" maxOccurs="1" name="GetStatusResult" type="s0:ReplyBase"/>
      * <s:element minOccurs="0" maxOccurs="1" name="Status" type="s0:ServerStatus"/> </s:sequence>
      * </s:complexType> </s:element><s:complexType name="ReplyBase"> <s:attribute name="RcvTime"
      * type="s:dateTime" use="required"/> <s:attribute name="ReplyTime" type="s:dateTime" use="required"/>
      * <s:attribute name="ClientRequestHandle" type="s:string"/> <s:attribute name="RevisedLocaleID"
      * type="s:string"/> <s:attribute name="ServerState" type="s0:serverState" use="required"/>
      * </s:complexType><s:simpleType name="serverState"> <s:restriction base="s:string"> <s:enumeration
      * value="running"/> <s:enumeration value="failed"/> <s:enumeration value="noConfig"/> <s:enumeration
      * value="suspended"/> <s:enumeration value="test"/> <s:enumeration value="commFault"/> </s:restriction>
      * </s:simpleType> <s:complexType name="ServerStatus"> <s:sequence> <s:element minOccurs="0" maxOccurs="1"
      * name="StatusInfo" type="s:string"/> <s:element minOccurs="0" maxOccurs="1" name="VendorInfo"
      * type="s:string"/> <s:element minOccurs="0" maxOccurs="unbounded" name="SupportedLocaleIDs"
      * type="s:string"/> <s:element minOccurs="0" maxOccurs="unbounded" name="SupportedInterfaceVersions"
      * type="s0:interfaceVersion"/> </s:sequence> <s:attribute name="StartTime" type="s:dateTime"
      * use="required"/> <s:attribute name="ProductVersion" type="s:string"/> </s:complexType> <s:simpleType
      * name="interfaceVersion"> <s:restriction base="s:string"> <s:enumeration value="XML_DA_Version_1_0"/>
      * </s:restriction> </s:simpleType>
      *
      * In other words, we call "getStatus" on the service, with "LocaleID" and "ClientRequestHandle" available
      * as attributes. The response has two objects "ReplyBase" and "ServerStatus". Each of these have a
      * hodgepog of attributes, sequences and enumerations.
      *
      * The output message should look, more or less like this (from the specification):
      *
      * <soap:Body> <getStatus LocaleID="de-AT" xmlns="http://opcfoundation.org/webservices/XMLDA/1.0/" />
      * </soap:Body>
      *
      * and the response more or less like this (from the specification):
      *
      * <soap:Body> <GetStatusResponse xmlns="http://opcfoundation.org/webservices/XMLDA/1.0/">
      * <GetStatusResult RcvTime="2003-05-26T20:17:42.4781250-07:00"
      * ReplyTime="2003-05-26T20:17:42.5781250-07:00" RevisedLocaleID="de" ServerState="running" /> <Status
      * StartTime="2003-05-26T20:16:45.0937500-07:00" ProductVersion="1.00.1.00" > <VendorInfo>OPC XML Data
      * Access 1.00 Sample Server</VendorInfo> <SupportedLocaleIDs>en</SupportedLocaleIDs>
      * <SupportedLocaleIDs>en-US</SupportedLocaleIDs> <SupportedLocaleIDs>de</SupportedLocaleIDs>
      * <SupportedInterfaceVersions>XML_DA_Version_1_0</SupportedInterfaceVersions> </Status>
      * </GetStatusResponse></soap:Body>
      */

    /**
     * This will generate an XML string from a SoapObject/SoapEnvelope that represents the "getStatus" call in
     * the WSDL above. The resulting XML will be passed to "public void testGetStatus(String xmlString)" to
     * ensure AXIS compatibility (and compatibility with the published standards).
     */
    public void testGetStatusSoapObject() throws Throwable {
        SoapObject getStatus = new SoapObject("http://opcfoundation.org/webservices/XMLDA/1.0/", "getStatus");
        getStatus.addAttribute("LocaleID", "de-AT");
        getStatus.addAttribute("ClientRequestHandle", "ClientHandle");
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = getStatus;
        envelope.setAddAdornments(false);

        byte[] request = myTransport.createRequestData(envelope);
        String xmlString = new String(request);
        assertGetStatus(xmlString);
    }

    /**
     * The string that represents the "getStatus". This is copied from an Axis call.
     */
    protected static final String getStatusRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
            + "<soapenv:Body><getStatus ClientRequestHandle=\"ClientHandle\" " +
            "LocaleID=\"de-AT\" xmlns=\"http://opcfoundation.org/webservices/XMLDA/1.0/\"/></soapenv:Body>"
            + "</soapenv:Envelope>";

    /**
     * Test the status using the AXIS generated xml.
     *
     * @throws Throwable
     */
    public void testGetStatus() throws Throwable {
        assertGetStatus(getStatusRequest);
    }

    /**
     * Test getStatus by passing in an xmlString. The result should be the same with an Axis or ksoap
     * generated string (clearly).
     *
     * @param xmlString the xml string to test.
     * @throws Throwable
     */
    public void assertGetStatus(String xmlString) throws Throwable {
        serviceCallCount = 0; // zero this
        Object service = new GetStatus();
        envelope.addMapping("http://opcfoundation.org/webservices/XMLDA/1.0/", "getStatus", SoapObject.class);
        envelope.setAddAdornments(false);
        ByteArrayInputStream bis = new ByteArrayInputStream(xmlString.getBytes());
        XmlPullParser parser = new KXmlParser();
        parser.setInput(bis, "UTF-8");
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        envelope.parse(parser);
        SoapObject soapReq = (SoapObject) envelope.bodyIn;
        assertEquals("ClientHandle", soapReq.getAttribute("ClientRequestHandle"));
        SoapObject result = null;
        result = MockTransport.invoke(service, soapReq);

        assertEquals("Service Called ", 1, serviceCallCount);
        // assert contents of result;
    }

    /**
     * This will generate an XML string from a SoapObject/SoapEnvelope that represents the "GetStatusResponse"
     * call in the WSDL above. The resulting XML will be passed to
     * "public void testGetStatusResponse(String xmlString)" to ensure AXIS compatibility (and compatibility
     * with the published standards).
     */
    public void testGetStatusResponseSoapObject() throws Throwable {
        SoapObject getStatusResponse = new SoapObject("http://opcfoundation.org/webservices/XMLDA/1.0/",
                "GetStatusResponse");
        SoapObject getStatusResult = new SoapObject("http://opcfoundation.org/webservices/XMLDA/1.0/",
                "GetStatusResult");
        getStatusResult.addAttribute("RcvTime", "2003-05-26T20:17:42.4781250-07:00");
        getStatusResult.addAttribute("ReplyTime", "2003-05-26T20:17:42.5781250-07:00");
        getStatusResult.addAttribute("RevisedLocaleID", "de");
        getStatusResult.addAttribute("ServerState", "running");
        SoapObject status = new SoapObject("http://opcfoundation.org/webservices/XMLDA/1.0/", "Status");
        status.addAttribute("StartTime", "2003-05-26T20:16:45.0937500-07:00");
        status.addAttribute("ProductVersion", "1.00.1.00");
        status.addProperty("VendorInfo", "OPC XML Data Access 1.00 Sample Server");
        status.addProperty("SupportedLocaleIDs", "en");
        status.addProperty("SupportedLocaleIDs", "en-US");
        status.addProperty("SupportedLocaleIDs", "de");
        status.addProperty("SupportedInterfaceVersions", "XML_DA_Version_1_0");
        getStatusResponse.addProperty("GetStatusResult", getStatusResult);
        getStatusResponse.addProperty("Status", status);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = getStatusResponse;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        byte[] request = null;
        request = myTransport.createRequestData(envelope);
        String xmlString = new String(request);
        assertGetStatusResponse(xmlString, true);
    }

    /**
     * GetStatusResponse XML. The body is from the OPC Foundation manual, the envelope is from the AXIS
     * envelope above.
     */
    protected static final String getStatusResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
            + "  <soapenv:Body>"
            + "  <GetStatusResponse xmlns=\"http://opcfoundation.org/webservices/XMLDA/1.0/\">"
            + "    <GetStatusResult" + "       RcvTime=\"2003-05-26T20:17:42.4781250-07:00\""
            + "       ReplyTime=\"2003-05-26T20:17:42.5781250-07:00\"" + "       RevisedLocaleID=\"de\""
            + "       ServerState=\"running\"" + "    />" + "    <Status"
            + "       StartTime=\"2003-05-26T20:16:45.0937500-07:00\""
            + "       ProductVersion=\"1.00.1.00\"" + "    >"
            + "      <VendorInfo>OPC XML Data Access 1.00 Sample Server</VendorInfo>"
            + "      <SupportedLocaleIDs>en</SupportedLocaleIDs>"
            + "      <SupportedLocaleIDs>en-US</SupportedLocaleIDs>"
            + "      <SupportedLocaleIDs>de</SupportedLocaleIDs>"
            + "      <SupportedInterfaceVersions>XML_DA_Version_1_0</SupportedInterfaceVersions>"
            + "    </Status>" + "  </GetStatusResponse>" + "</soapenv:Body>" + "</soapenv:Envelope>";

    /**
     * Test the response from "getStatus". The body is from the OPC Foundation manual, the envelope is from
     * the AXIS envelope above.
     *
     * @throws Throwable
     */
    public void testGetStatusResponse() throws Throwable {
        assertGetStatusResponse(getStatusResponse, false);
    }

    /**
     * Test the response from "getStatus".
     *
     * @throws Throwable
     */
    public void assertGetStatusResponse(String xmlString, boolean nestedSoap) throws Throwable {
        ByteArrayInputStream is = new ByteArrayInputStream(xmlString.toString().getBytes());
        envelope.addMapping("http://opcfoundation.org/webservices/XMLDA/1.0/", "GetStatusResponse",
                SoapObject.class);
        envelope.addMapping("http://opcfoundation.org/webservices/XMLDA/1.0/", "Status", SoapObject.class);
        envelope.addMapping("http://opcfoundation.org/webservices/XMLDA/1.0/", "GetStatusResult",
                SoapObject.class);
        myTransport.parseResponse(envelope, is);
        is.close();
        SoapObject response = (SoapObject) envelope.bodyIn;
        assertEquals(" Two Parameter (GetStatusResponse)", 2, ((SoapObject) response).getPropertyCount());
        assertEquals(" No Attributes (GetStatusResponse)", 0, ((SoapObject) response).getAttributeCount());
        // first property is "GetStatusResult"
        SoapObject getStatusResult = (SoapObject) response.getProperty("GetStatusResult");
        assertNotNull(" GetStatusResult ", getStatusResult);
        assertEquals(" No Parameters (GetStatusResult)", 0, getStatusResult.getPropertyCount());
        if(nestedSoap) {
            assertEquals(" Five Attributes (GetStatusResult)", 5, getStatusResult.getAttributeCount());
        } else {
            assertEquals(" Four Attributes (GetStatusResult)", 4, getStatusResult.getAttributeCount());
        }
        assertEquals("2003-05-26T20:17:42.4781250-07:00", getStatusResult.getAttribute("RcvTime"));
        assertEquals("2003-05-26T20:17:42.5781250-07:00", getStatusResult.getAttribute("ReplyTime"));
        assertEquals("running", getStatusResult.getAttribute("ServerState"));
        // first property is "Status"
        SoapObject status = (SoapObject) response.getProperty("Status");
        assertEquals(" Five Parameters (Status)", 5, status.getPropertyCount());
        if(nestedSoap) {
            assertEquals(" Three Attributes (Status)", 3, status.getAttributeCount());
        } else {
        assertEquals(" Two Attributes (Status)", 2, status.getAttributeCount());
        }
        assertEquals("2003-05-26T20:16:45.0937500-07:00", status.getAttribute("StartTime"));
        assertEquals("1.00.1.00", status.getAttribute("ProductVersion"));
    }

    public static class GetStatus {
        public SoapObject getStatus(SoapObject in) {
            serviceCallCount++;
            assertEquals("Should be two attributes. ", 2, in.getAttributeCount());
            assertEquals("LocaleID ", "de-AT", in.getAttribute("LocaleID"));
            assertEquals("ClientRequestHandle ", "ClientHandle", in.getAttribute("ClientRequestHandle"));
            return null;
        }
    }

    public void testAttributesOnPrimitives() throws Exception {
        String testXML = "<body><response><result name=\"fred\" anotherAttr=\"anotherValue\">Barney</result>" +
                "</response></body>";
        InputStream inputStream = new ByteArrayInputStream(testXML.getBytes());
        SoapSerializationEnvelope sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);


        KXmlParser parser = new KXmlParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(inputStream, null);
        parser.nextTag();

        sse.parseBody(parser);

        SoapPrimitive result = (SoapPrimitive) sse.getResponse();
        assertEquals("fred", result.safeGetAttribute("name"));
        assertEquals("anotherValue", result.safeGetAttribute("anotherAttr"));
        assertEquals("Barney", result.toString());
    }

    public void testAttributesOnPrimitives2() throws Exception {
        String testXML = "<body><response><result>" +
                "<person grade=\"F\">Fred</person>" +
                "<person grade=\"C\">Chris</person>" +
                "<person grade=\"A\">Allison</person>" +
                "</result></response></body>";
        InputStream inputStream = new ByteArrayInputStream(testXML.getBytes());
        SoapSerializationEnvelope sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);


        KXmlParser parser = new KXmlParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(inputStream, null);
        parser.nextTag();

        sse.parseBody(parser);

        SoapObject result = (SoapObject) sse.getResponse();
        SoapPrimitive fred = (SoapPrimitive) result.getProperty(0);

        assertEquals("F", fred.safeGetAttribute("grade"));
        assertEquals("Fred", fred.toString());

        SoapPrimitive chris = (SoapPrimitive) result.getProperty(1);
        assertEquals("C", chris.safeGetAttribute("grade"));
        assertEquals("Chris", chris.toString());

        SoapPrimitive allison = (SoapPrimitive) result.getProperty(2);
        assertEquals("A", allison.safeGetAttribute("grade"));
        assertEquals("Allison", allison.toString());
    }


    /**
     * This test ensures that name set on the SoapObject creation as well as when adding it as a property to another
     * SoapObject are kept around and used. This used to be the default behaviour up to including 2.6.0 and a regression
     * broke this with 2.6.1. The 2.6.2 release fixes this again.
     *
     * @throws Exception
     */
    public void testEnsureNameAndTypeNotLost() throws Exception {
        String namespace = "namespace";
        String typeDetails = "ArrayOfDetails";
        String nameDetails = "Details";

        String typeDetail = "DetailBase";
        String nameDetail = "Detail";

        String expectedResponse
                = "<n0:PlaceOrder xmlns:n0=\"namespace\">" +
                "<n0:Details n1:type=\"n0:ArrayOfDetails\" xmlns:n1=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<n0:Detail n1:type=\"n0:DetailBase\"><PartNumber>abc0</PartNumber><Quantity>0</Quantity></n0:Detail>" +
                "<n0:Detail n1:type=\"n0:DetailBase\"><PartNumber>abc1</PartNumber><Quantity>1</Quantity></n0:Detail>" +
                "<n0:Detail n1:type=\"n0:DetailBase\"><PartNumber>abc2</PartNumber><Quantity>2</Quantity></n0:Detail>" +
                "</n0:Details>" +
                "</n0:PlaceOrder>";


        SoapObject requestSoap = new SoapObject(namespace, "PlaceOrder");

        SoapObject details = new SoapObject(namespace, typeDetails);
        int detailTotal = 3;
        for (int detailCount = 0; detailCount < detailTotal; detailCount++) {
            SoapObject detailSoap = new SoapObject(namespace, typeDetail);
            detailSoap.addProperty("PartNumber", "abc" + detailCount);
            detailSoap.addProperty("Quantity", Integer.toString(detailCount));

            details.addProperty(nameDetail, detailSoap);
        }
        requestSoap.addProperty(nameDetails, details);

        envelope.implicitTypes = true;
        envelope.addAdornments = false;
        writeBodyWithSoapObject(requestSoap);
        String request = new String(outputStream.toByteArray());
        assertEquals(expectedResponse, request);
    }

    /**
     * Test created for issue 112 that uncovered a problem with writing a SoapPrimitive with Attributes. They used to
     * get lost. Not anymore about fixes to DM#writeInstance .
     * @throws Exception
     * @see DM#writeInstance(org.xmlpull.v1.XmlSerializer, Object)
     * @see "http://code.google.com/p/ksoap2-android/issues/detail?id=112"
     */
    public void testIssue112() throws Exception {
        String expectedResponse
                = "<n0:CostOfRepairs xmlns:n0=\"MyNameSpace\">" +
                "<Power Unit=\"kW\">1500</Power>" +
                "</n0:CostOfRepairs>";

        String namespace = "MyNameSpace";

        SoapObject requestSoap = new SoapObject(namespace, "CostOfRepairs");
        SoapPrimitive power = new SoapPrimitive(namespace, "Power", Integer.toString(1500));
        power.addAttribute("Unit", "kW");
        requestSoap.addProperty("Power", power);

        envelope.implicitTypes = true;
        envelope.addAdornments = false;
        writeBodyWithSoapObject(requestSoap);
        String request = new String(outputStream.toByteArray());
        assertEquals(expectedResponse, request);
    }


    /**
     * Test created for issue 99 SoapObject newInstance() doesn't work
     * @throws Exception
     * @see "http://code.google.com/p/ksoap2-android/issues/detail?id=99"
     */
    public void testIssue99() throws Exception {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        
        SoapObject requestSoap = new SoapObject("", "getFavouriteFilters");
        requestSoap.addProperty("token", "123123");
        
        envelope.setOutputSoapObject(requestSoap);
        String originalRequest = envelope.bodyOut.toString();
        
        SoapObject reconnect = ((SoapObject) envelope.bodyOut).newInstance();
        reconnect.setProperty(0, "666");
        
        String expectedResponse = envelope.bodyOut.toString();
                
        assertTrue("Original "+originalRequest+" != Expected " + expectedResponse, 
                expectedResponse.equals(originalRequest) );
    }
}
