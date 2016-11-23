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

import java.io.*;

import org.ksoap2.serialization.Base64;

public class HttpTransportBasicAuthTest extends TransportTestCase {

    public void testSimpleMessage_nullUsernamePassword() throws Throwable {
        String username = null;
        String password = null;
        MyTransport ht = new MyTransport("a url", username, password);
        ht.call(soapAction, envelope);

        assertSerializationDeserialization();

        assertNull((String) serviceConnection.requestPropertyMap.get("Authorization"));

    }

    public void testSimpleMessage() throws Throwable {
        String username = "username";
        String password = "password";
        MyTransport ht = new MyTransport("a url", username, password);
        ht.call(soapAction, envelope);

        assertSerializationDeserialization();

        String authorizationProperty = (String) serviceConnection.requestPropertyMap.get("Authorization");
        assertEquals(basicAuthenticationStringFor(username, password), authorizationProperty);

    }

    private String basicAuthenticationStringFor(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    class MyTransport extends HttpTransportBasicAuth {
        public MyTransport(String url, String username, String password) {
            super(url, username, password);
        }

        public ServiceConnection getServiceConnection() throws IOException {
            addBasicAuthentication(serviceConnection);
            return serviceConnection;
        }
    }

}
