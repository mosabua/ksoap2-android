package org.ksoap2.transport;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

/**
 * A transport to be used with NTLM.
 * @author Lian Hwang lian_hwang@hotmail.com
 */
public class NtlmTransportExample {

    public static final String SOAP_ACTION = "http://a.com.sg/FilesToBinaryXML";

    public static final String OPERATION_NAME = "FilesToBinaryXML";

    public static final String WSDL_TARGET_NAMESPACE = "http://a.com.sg/";

    public static final String SOAP_ADDRESS = "http://10.10.2.135:9091/TestNTLM/FileToBinaryXML.asmx";

    private static final int IMAGE_NAME = 0;
    private static final int IMAGE_EXTENSION = 1;
    private static final int IMAGE_DIRECTORY = 2;
    private static final int IMAGE_DATECREATED = 3;
    private static final int IMAGE_LASTMODIFIED = 4;
    private static final int IMAGE_BINARYDATA = 5;

    public static void test() {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

        envelope.dotNet = true;

        PropertyInfo pi = new PropertyInfo();
        pi.setName("CutOffTime");
        pi.setValue("1 Jan 1900 00:00");
        request.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("Paths");
        pi.setValue("/");
        request.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("FilterExtension");
        pi.setValue("*.jpg;*.jpeg;*.gif;*.png");
        request.addProperty(pi);

        envelope.setOutputSoapObject(request);

        NtlmTransport httpTransport = new NtlmTransport(SOAP_ADDRESS);
        httpTransport.setCredentials("test6", "12345678", "ntDomain", "ws");

        try    {

            httpTransport.call(SOAP_ACTION, envelope);

            SoapObject array = (SoapObject) envelope.getResponse();

            for (int i = 0; i < array.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) array.getProperty(i);
                String name = obj.getPropertyAsString(IMAGE_NAME).toLowerCase();
                // String ext = obj.getPropertyAsString(IMAGE_EXTENSION).toLowerCase();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
