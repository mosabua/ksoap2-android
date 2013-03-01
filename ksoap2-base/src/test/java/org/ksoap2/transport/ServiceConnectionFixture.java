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

package org.ksoap2.transport;

import junit.framework.Assert;
import org.ksoap2.transport.mock.ComplexResponse;

import java.io.*;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ServiceConnectionFixture implements ServiceConnection {
    public static final String FAULT_MESSAGE_STRING = "The ISBN value contains invalid characters";
    public static final String FAULT_MESSAGE_STRING_12 ="Authentication failed";
    public static final Class RESPONSE_CLASS = new ComplexResponse().getClass();
    public static final String RESPONSE_CLASS_NAME = "ComplexFunctionResponse";
    private static final String UNKNOWN_CLASS_NAME = "Unknown";
    public static final String NAMESPACE = "http://namespace.com/";
    static public final String AXIS_FAULT_MESSAGE = "java.io.FileNotFoundException: /WebService.jws";
    public static String theStringResponse = "theStringResponse";
    public static long theLongResponse = 1234567890;
    public static int theIntegerResponse = 12;
    public static boolean theBooleanResponse = true;
    public HashMap requestPropertyMap = new HashMap();

    public static String TWO_DIMENSIONAL_STRING_ARRAY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "   <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "      <soapenv:Body>" +
            "         <getStringResponse soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
            "            <getStringReturn soapenc:arrayType=\"xsd:string[][2]\" xsi:type=\"soapenc:Array\" " +
            "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
            "               <getStringReturn soapenc:arrayType=\"xsd:string[2]\" xsi:type=\"soapenc:Array\">" +
            "                  <getStringReturn xsi:type=\"xsd:string\">1</getStringReturn>" +
            "                  <getStringReturn xsi:type=\"xsd:string\">test1</getStringReturn>" +
            "               </getStringReturn>" +
            "               <getStringReturn soapenc:arrayType=\"xsd:string[2]\" xsi:type=\"soapenc:Array\">" +
            "                  <getStringReturn xsi:type=\"xsd:string\">2</getStringReturn>" +
            "                  <getStringReturn xsi:type=\"xsd:string\">test2</getStringReturn>" +
            "               </getStringReturn>" +
            "            </getStringReturn>" +
            "         </getStringResponse>" +
            "      </soapenv:Body>" +
            "   </soapenv:Envelope>";

    public static String STRING_ARRAY_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "   <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "      <soapenv:Body>" +
            "         <listResponse soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
            "            <listReturn soapenc:arrayType=\"xsd:string[9]\" xsi:type=\"soapenc:Array\" " +
            "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
            "               <listReturn xsi:type=\"xsd:string\">user-agent:kSOAP/2.0</listReturn>" +
            "               <listReturn xsi:type=\"xsd:string\">soapaction:&quot;&quot;</listReturn>" +
            "               <listReturn xsi:type=\"xsd:string\">content-type:text/xml</listReturn>" +
            "               <listReturn xsi:type=\"xsd:string\">connection:close</listReturn>" +
            "               <listReturn xsi:type=\"xsd:string\">content-length:282</listReturn>" +
            "               <listReturn xsi:type=\"xsd:string\">cache-control:no-cache</listReturn>" +
            "               <listReturn xsi:type=\"xsd:string\">pragma:no-cache</listReturn>" +
            "               <listReturn xsi:type=\"xsd:string\">host:127.0.0.1:8081</listReturn>" +
            "               <listReturn xsi:type=\"xsd:string\">" +
            "accept:text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2</listReturn>" +
            "            </listReturn>" +
            "         </listResponse>" +
            "       </soapenv:Body>" +
            "   </soapenv:Envelope>";

    public static String FAULT_STRING = "" + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "    <soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:mh=\"http://www.Monson-Haefel.com/jwsbook/BookQuote\" >" +
            "    <soap:Body>" +
            "        <soap:Fault>" +
            "            <faultcode>soap:Client</faultcode>" +
            "            <faultstring>" + FAULT_MESSAGE_STRING + "</faultstring>" +
            "            <faultactor>http://www.xyzcorp.com</faultactor>" +
            "            <detail>" +
            "                <mh:InvalidIsbnFaultDetail>" +
            "                <offending-value>19318224-D</offending-value>" +
            "                <conformance-rules>" +
            "                    The first nine characters must be digits. The last" +
            "                    character may be a digit or the letter 'X'. Case is" +
            "                    not important." +
            "                </conformance-rules>" +
            "                </mh:InvalidIsbnFaultDetail>" +
            "            </detail>" +
            "        </soap:Fault>" +
            "     </soap:Body>" +
            "    </soap:Envelope>";
    
    public static String FAULT_STRING_12 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
    "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
    "<soap:Body>\n" + 
    "<soap:Fault>\n" + 
    "<soap:Code>\n" + 
    "<soap:Value xmlns:q0=\"http://schemas.xmlsoap.org/envelope/\">q0:Client.AuthenticationFailed</soap:Value>\n" + 
    "</soap:Code>\n" + 
    "<soap:Reason>\n" + 
    "<soap:Text xml:lang=\"en\">Authentication failed</soap:Text>\n" + 
    "</soap:Reason>\n" + 
    "<soap:Detail />\n" + 
    "</soap:Fault>\n" + 
    "</soap:Body>\n" + 
    "</soap:Envelope>";

    // have to see how this one works
    public static String AXIS_FAULT_STRING = "    <?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "    <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> " +
            "       <soapenv:Body>  " +
            "          <soapenv:Fault>   " +
            "             <faultcode>soapenv:Server.userException</faultcode>   " +
            "             <faultstring>" + AXIS_FAULT_MESSAGE + "</faultstring>   " +
            "             <detail>    " +
            "                <ns1:hostname xmlns:ns1=\"http://xml.apache.org/axis/\">widebook.local</ns1:hostname>   " +
            "             </detail>  " +
            "          </soapenv:Fault> " +
            "       </soapenv:Body>" +
            "    </soapenv:Envelope>";


    public static final String BROKEN_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
            "   <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "\n" +
            "      <soapenv:Body>" + "\n" +
            "         <" + RESPONSE_CLASS_NAME + " " +
            "soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" + "\n" +
            "            <ComplexFunctionReturn href=\"#id0\"/>" + "\n" +
            "         </" + RESPONSE_CLASS_NAME + ">" + "\n" +
            "         <multiRef id=\"id0\" soapenc:root=\"0\" " +
            "soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
            "xsi:type=\"ns1:" + RESPONSE_CLASS_NAME + "\" " +
            "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
            "xmlns:ns1=\"" + NAMESPACE + "\">" + "\n" +
            "             <longResponse href=\"#id1\"/>" + "\n" +
            "             <stringResponse xsi:type=\"xsd:string\">" + theStringResponse + "</stringResponse>" + "\n" +
            "             <" + ComplexResponse.INTEGER_REPONSE_NAME + " " +
            "xsi:type=\"xsd:int\">" + theIntegerResponse + "</" + ComplexResponse.INTEGER_REPONSE_NAME + "> \n" +
            "             <" + ComplexResponse.BOOLEAN_RESPONSE_NAME + " " +
            "xsi:type=\"xsd:boolean\">" + theBooleanResponse + "</" + ComplexResponse.BOOLEAN_RESPONSE_NAME + "> \n" +
            "         </multiRef>" + "\n" +
            "         <multiRef id=\"id1\" soapenc:root=\"0\" " +
            "soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
            "xsi:type=\"xsd:long\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
            theLongResponse + "</multiRef>" + "\n" +
            "      </soapenv:Body>" + "\n" +
            "   </soapenv:Envelope>";
    public static final String WORKING_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
            "   <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "\n" +
            "      <soapenv:Body>" + "\n" +
            "         <" + RESPONSE_CLASS_NAME + " " +
            "soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" + "\n" +
            "            <ComplexFunctionReturn href=\"#id0\"/>" + "\n" +
            "         </" + RESPONSE_CLASS_NAME + ">" + "\n" +
            "         <multiRef id=\"id0\" soapenc:root=\"0\" " +
            "soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
            "xsi:type=\"ns1:" + RESPONSE_CLASS_NAME + "\" " +
            "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\"" + NAMESPACE + "\">" + "\n" +
            "             <longResponse xsi:type=\"xsd:long\">" + theLongResponse + "</longResponse>" + "\n" +
            "             <stringResponse xsi:type=\"xsd:string\">" + theStringResponse + "</stringResponse>" + "\n" +
            "             <" + ComplexResponse.INTEGER_REPONSE_NAME + " " +
            "xsi:type=\"xsd:int\">" + theIntegerResponse + "</" + ComplexResponse.INTEGER_REPONSE_NAME + "> \n" +
            "             <" + ComplexResponse.BOOLEAN_RESPONSE_NAME + " " +
            "xsi:type=\"xsd:boolean\">" + theBooleanResponse + "</" + ComplexResponse.BOOLEAN_RESPONSE_NAME + "> \n" +
            "         </multiRef>" + "\n" +
            "         <multiRef id=\"id1\" soapenc:root=\"0\" " +
            "soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
            "xsi:type=\"xsd:long\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">10</multiRef>" + "\n" +
            "      </soapenv:Body>" + "\n" +
            "   </soapenv:Envelope>";

    public static final String WORKING_NOMULTIREF = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "\n" +
            "    <soapenv:Body>" + "\n" +
            "       <" + RESPONSE_CLASS_NAME + " " +
            "soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" + "\n" +
            "          <ComplexFunctionReturn xsi:type=\"ns1:" + RESPONSE_CLASS_NAME + "\" " +
            "xmlns:ns1=\"" + NAMESPACE + "\">" + "\n" +
            "             <longResponse xsi:type=\"xsd:long\">" + theLongResponse + "</longResponse>" + "\n" +
            "             <stringResponse xsi:type=\"xsd:string\">" + theStringResponse + "</stringResponse>" + "\n" +
            "             <" + ComplexResponse.INTEGER_REPONSE_NAME + " " +
            "xsi:type=\"xsd:int\">" + theIntegerResponse + "</" + ComplexResponse.INTEGER_REPONSE_NAME + "> \n" +
            "             <" + ComplexResponse.BOOLEAN_RESPONSE_NAME + " " +
            "xsi:type=\"xsd:boolean\">" + theBooleanResponse + "</" + ComplexResponse.BOOLEAN_RESPONSE_NAME + "> \n" +
            "          </ComplexFunctionReturn>" + "\n" +
            "       </" + RESPONSE_CLASS_NAME + ">" + "\n" +
            "    </soapenv:Body>" + "\n" +
            "</soapenv:Envelope>";


    // Note:  The attrFoo attribute on stringResponse is nonsense:  the typing of stringResponse to xsd:string means
    // that the parser returns a String object.  There is no object to which to attach the attributes the purpose of
    // adding this attribute here is to make sure its presense doesn't break existing code.
    public static final String WORKING_NOMULTIREF_WITH_ATTRIBUTES = "<?xml version=\"1.0\" " +
            "encoding=\"UTF-8\"?>" + "\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "\n" +
            "    <soapenv:Body>" + "\n" +
            "       <" + UNKNOWN_CLASS_NAME + " " +
            "soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" + "\n" +
            "          <ComplexFunctionReturn xsi:type=\"ns1:" + UNKNOWN_CLASS_NAME + "\" " +
            "xmlns:ns1=\"" + NAMESPACE + "\">" + "\n" +
            "             <longResponse attrFoo=\"valueBar\">" + theLongResponse + "</longResponse>" + "\n" +
            "             <stringResponse attrFoo=\"valueBar\" " +
            "xsi:type=\"xsd:string\">" + theStringResponse + "</stringResponse>" + "\n" +
            "             <" + ComplexResponse.INTEGER_REPONSE_NAME + " " +
            "xsi:type=\"xsd:int\">" + theIntegerResponse + "</" + ComplexResponse.INTEGER_REPONSE_NAME + "> \n" +
            "             <" + ComplexResponse.BOOLEAN_RESPONSE_NAME + " " +
            "xsi:type=\"xsd:boolean\">" + theBooleanResponse + "</" + ComplexResponse.BOOLEAN_RESPONSE_NAME + "> \n" +
            "          </ComplexFunctionReturn>" + "\n" +
            "       </" + UNKNOWN_CLASS_NAME + ">" + "\n" +
            "    </soapenv:Body>" + "\n" +
            "</soapenv:Envelope>";

   public static final String REAL_LIFE_QUERY =
    "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <SOAP-ENV:Envelope " +
            "xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/' " +
            "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:s='http://www.w3.org/2001/XMLSchema'>"
  + "<SOAP-ENV:Body>"
  + "<GetCensusByUrgentCarePROD2Response xmlns=\"http://default.org\">"
  + "<GetCensusByUrgentCarePROD2Result>"
  +"    <GetCensusByUrgentCarePROD2ResultItem GetCensusByUrgentCarePROD2ResultKey=\"Alpine Urgent Care\">1" +
            "</GetCensusByUrgentCarePROD2ResultItem>"
  +"    <GetCensusByUrgentCarePROD2ResultItem GetCensusByUrgentCarePROD2ResultKey=\"Broadmoor Urgent Care\">1" +
            "</GetCensusByUrgentCarePROD2ResultItem>"
  +"    <GetCensusByUrgentCarePROD2ResultItem GetCensusByUrgentCarePROD2ResultKey=\"Plainfield Urgent Care\">2" +
            "</GetCensusByUrgentCarePROD2ResultItem>"
  +"    <GetCensusByUrgentCarePROD2ResultItem GetCensusByUrgentCarePROD2ResultKey=\"South Pavilion Urgent Care\">1" +
            "</GetCensusByUrgentCarePROD2ResultItem>"
  +"    <GetCensusByUrgentCarePROD2ResultItem GetCensusByUrgentCarePROD2ResultKey=\"West Pavilion Urgent Care\">0" +
            "</GetCensusByUrgentCarePROD2ResultItem>"
  +" </GetCensusByUrgentCarePROD2Result>"
  +"</GetCensusByUrgentCarePROD2Response>  </SOAP-ENV:Body>"
  +"</SOAP-ENV:Envelope>";




    public static final String WORKING_NOMULTIREF_REVERSED_RESPONSE_PARAMETERS = "<?xml version=\"1.0\" " +
            "encoding=\"UTF-8\"?>" + "\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "\n" +
            "    <soapenv:Body>" + "\n" +
            "       <" + RESPONSE_CLASS_NAME + " " +
            "soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" + "\n" +
            "          <ComplexFunctionReturn xsi:type=\"ns1:" + RESPONSE_CLASS_NAME + "\" " +
            "xmlns:ns1=\"" + NAMESPACE + "\">" + "\n" +
            "             <stringResponse xsi:type=\"xsd:string\">" + theStringResponse + "</stringResponse>" + "\n" +
            "             <longResponse xsi:type=\"xsd:long\">" + theLongResponse + "</longResponse>" + "\n" +
            "             <" + ComplexResponse.INTEGER_REPONSE_NAME + " " +
            "xsi:type=\"xsd:int\">" + theIntegerResponse + "</" + ComplexResponse.INTEGER_REPONSE_NAME + "> \n" +
            "             <" + ComplexResponse.BOOLEAN_RESPONSE_NAME + " " +
            "xsi:type=\"xsd:boolean\">" + theBooleanResponse + "</" + ComplexResponse.BOOLEAN_RESPONSE_NAME + "> \n" +
            "          </ComplexFunctionReturn>" + "\n" +
            "       </" + RESPONSE_CLASS_NAME + ">" + "\n" +
            "    </soapenv:Body>" + "\n" +
            "</soapenv:Envelope>";
    public static final String HASH_TABLE_PARAMETERS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "\n" +
            "    <soapenv:Body>" + "\n" +
            "           <hashthingy n3:type=\"n2:Map\" " +
            "xmlns:n2=\"http://xml.apache.org/xml-soap\" xmlns:n3=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "               <item>\n" +
            "                   <key n3:type=\"n4:string\" " +
            "xmlns:n4=\"http://www.w3.org/2001/XMLSchema\">key1</key>\n" +
            "                   <value n3:type=\"n5:string\" " +
            "xmlns:n5=\"http://www.w3.org/2001/XMLSchema\">value1</value>\n" +
            "               </item>\n" +
            "           </hashthingy>\n" +
            "    </soapenv:Body>" + "\n" +
            "</soapenv:Envelope>";

    public static final String HASH_TABLE_PARAMETERS_DUP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "\n" +
            "    <soapenv:Body>" + "\n" +
            "           <hashthingy n3:type=\"n2:Map\" xmlns:n2=\"http://xml.apache.org/xml-soap\" " +
            "xmlns:n3=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "               <item>\n" +
            "                   <key n3:type=\"n4:string\" xmlns:n4=\"http://www.w3.org/2001/XMLSchema\">key1</key>\n" +
            "                   <value n3:type=\"n5:string\" " +
            "xmlns:n5=\"http://www.w3.org/2001/XMLSchema\">value1</value>\n" +
            "               </item>\n" +
            "               <item>\n" +
            "                   <key n3:type=\"n4:string\" xmlns:n4=\"http://www.w3.org/2001/XMLSchema\">key1</key>\n" +
            "                   <value n3:type=\"n5:string\" " +
            "xmlns:n5=\"http://www.w3.org/2001/XMLSchema\">value1</value>\n" +
            "               </item>\n" +
            "               <item>\n" +
            "                   <key n3:null=\"true\" xmlns:n4=\"http://www.w3.org/2001/XMLSchema\"/>\n" +
            "                   <value n3:type=\"n5:string\" " +
            "xmlns:n5=\"http://www.w3.org/2001/XMLSchema\">blah</value>\n" +
            "               </item>\n" +
            "               <item>\n" +
            "                   <key n3:type=\"n4:string\" " +
            "xmlns:n4=\"http://www.w3.org/2001/XMLSchema\">another</key>\n" +
            "                   <value n3:null=\"true\" xmlns:n5=\"http://www.w3.org/2001/XMLSchema\"/>\n" +
            "               </item>\n" +
            "           </hashthingy>\n" +
            "    </soapenv:Body>" + "\n" +
            "</soapenv:Envelope>";


    private ByteArrayInputStream inputStream;
    public ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    public boolean connected;

    public ServiceConnectionFixture() {
        connected = true;
    }

    public static InputStream hashTableWithDupAsStream() {
        return messsageAsStream(HASH_TABLE_PARAMETERS_DUP);
    }

    public static InputStream hashTableAsStream() {
        return messsageAsStream(HASH_TABLE_PARAMETERS);
    }

    public static InputStream faultStringAsStream() {
        return messsageAsStream(FAULT_STRING);
    }

    public static InputStream createWorkingAsStream() {
        return messsageAsStream(WORKING_STRING);
    }

    public static InputStream createWorkingNoMultirefAsStream() {
        return messsageAsStream(WORKING_NOMULTIREF);
    }

    public static InputStream createWorkingNoMultirefWithAttributesAsStream() {
        return messsageAsStream(WORKING_NOMULTIREF_WITH_ATTRIBUTES);
    }


    public static InputStream createWorkingNoMultirefAsStream_reversedResponseParameters() {
        return messsageAsStream(WORKING_NOMULTIREF_REVERSED_RESPONSE_PARAMETERS);
    }

    public static InputStream createMultirefAsStream() {
        return messsageAsStream(BROKEN_STRING);
    }

    private static InputStream messsageAsStream(String message) {
        return new ByteArrayInputStream(message.getBytes());
    }


    public void setInputSring(String inputString) {
        inputStream = new ByteArrayInputStream(inputString.getBytes());
    }

    public void connect() throws IOException {
        connected = true;
    }

    public void disconnect() throws IOException {
        throw new RuntimeException("ServiceConnectionFixture.disconnect is not implemented yet");
    }

    public List getResponseProperties() {
    return new LinkedList();
    }

    public int getResponseCode() throws IOException {
        return 200;
    }

    public void setRequestProperty(String propertyName, String value) throws IOException {
        requestPropertyMap.put(propertyName, value);
    }

    public void setRequestMethod(String post) throws ProtocolException, IOException {
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        
    }

    public OutputStream openOutputStream() throws IOException {
        return outputStream;
    }

    public InputStream openInputStream() throws IOException {
        return inputStream;
    }

    public InputStream getErrorStream() {
        throw new RuntimeException("ServiceConnectionFixture.getErrorStream is not implemented yet");
    }

    public static void assertComplexResponseCorrect(ComplexResponse complexResponse) {
        Assert.assertEquals("theStringResponse", complexResponse.stringResponse);
        Assert.assertEquals(1234567890, complexResponse.longResponse);
    }

    public static InputStream createTwoDimensionalStringArrayResponseAsStream() {
        return messsageAsStream(TWO_DIMENSIONAL_STRING_ARRAY);
    }

    public static InputStream createRealLifeResponse() {
        return messsageAsStream(REAL_LIFE_QUERY);
    }

public String getHost() {
return "";
}

public int getPort() {
return 80;
}

public String getPath() {
return "";
}
}
