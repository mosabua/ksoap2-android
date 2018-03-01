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

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.io.*;
import java.util.List;

public class HttpTransportSETest extends TransportTestCase {

    public void testOutbound() throws Throwable {
        MyTransport ht = new MyTransport("http://www.webservicex.net/globalweather.asmx");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = true;

        SoapObject request = new SoapObject("http://www.webserviceX.NET", "GetWeather");
        request.addProperty(getStringPropertyInfoEnvelope("CountryName", "Turkey"));
        request.addProperty(getStringPropertyInfoEnvelope("CityName", "Ankara"));

        envelope.setOutputSoapObject(request);

        List call = ht.call("http://www.webserviceX.NET/GetWeather", envelope, null);

        assertNotNull(call);

        SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

        assertNotNull(response);

        assertTrue(response.getValue().toString().length() > 0);
    }
    
    public void testOutbound_WithNoSoapAction() throws Throwable {
        MyTransport ht = new MyTransport("http://www.webservicex.net/globalweather.asmx");
        ht.call(null, envelope);
        soapAction = "\"\"";// expected answer for null
        assertSerializationDeserialization();
        //assertTrue();
    }

    private PropertyInfo getStringPropertyInfoEnvelope(String key, String value) {
        PropertyInfo pi = new PropertyInfo();
        pi.setName(key);
        pi.setValue(value);
        pi.setType(value.getClass());

        return pi;
    }

    class MyTransport extends HttpTransportSE {
        public MyTransport(String url) {
            super(url);
        }

        public ServiceConnection getServiceConnection() throws IOException {
            return serviceConnection;
        }
    }

}
