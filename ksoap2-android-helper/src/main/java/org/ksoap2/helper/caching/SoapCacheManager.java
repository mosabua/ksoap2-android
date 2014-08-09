/* Copyright (c) 2014, Omar Hussain., Pakistan
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

package org.ksoap2.helper.caching;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/**
 * Helper class to provide rudimentary caching solution as request/response file
 * cache
 * 
 * @author omarhussain
 * 
 */
public class SoapCacheManager {

    public SoapCacheManager() {

    }

    public static String readFile(File file) {
        // Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
        }
        return text.toString();
    }

    public static void writeFile(File file, String data) {
        try {
            if (file.exists()) {
                file.delete();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
            writer.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static String serializeSoapRequest(SoapSerializationEnvelope envelope) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(256 * 1024);
        byte result[] = null;
        try {
            bos.write("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>".getBytes());
            XmlSerializer xw = new KXmlSerializer();
            xw.setOutput(bos, "UTF-8");
            envelope.write(xw);
            xw.flush();
            bos.write('\r');
            bos.write('\n');
            bos.flush();
            result = bos.toByteArray();
            xw = null;
            bos = null;
            return new String(result, "UTF-8");
        } catch (Exception e) {

            e.printStackTrace();
        }
        return "";
    }

    public static Object parseSoapResponse(SoapSerializationEnvelope envelope,
            InputStream is) throws Exception {
        XmlPullParser xp = new KXmlParser();
        try {
            xp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            xp.setInput(is, null);
            envelope.parse(xp);
            /*
             * Fix memory leak when running on android in strict mode. Issue 133
             */
            is.close();
            return envelope.bodyIn;
        } catch (Exception e) {
            throw e;
        }

    }
}
