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

package org.ksoap2.transport.mock;

import java.util.*;

import org.ksoap2.serialization.*;

public class ComplexParameter implements KvmSerializable {

    public String name;
    public int count;
    public String innerText; 
    
    public Object getProperty(int index) {
        if (index == 0) {
            return name;
        } else if (index == 1) {
            return new Integer(count);
        } else {
            throw new RuntimeException("invalid parameter");
        }
    }

    public int getPropertyCount() {
        return 2;
    }

    public void setProperty(int index, Object value) {
        if (index == 0 && value instanceof String) {
            name = (String) value;
        } else if (index == 1 && value instanceof Integer) {
            count = ((Integer) value).intValue();
        } else {
            throw new RuntimeException("invalid parameter");
        }
    }

    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        if (index == 0) {
            info.name = "name";
            info.type = PropertyInfo.STRING_CLASS;
            info.namespace = "";
        } else if (index == 1) {
            info.name = "count";
            info.type = PropertyInfo.INTEGER_CLASS;
            info.namespace = "";
        } else {
            throw new RuntimeException("invalid parameter");
        }
    }
    public String getInnerText() {
       return innerText;
    }

    public void setInnerText(String s) {
       innerText = s;
    }
    
}
