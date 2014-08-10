package org.ksoap2.samples.soccer;

import org.ksoap2.serialization.*;

public class StadiumNamesResult extends LiteralArrayVector {

    // in the resultant xml message, the array elements can
    // be described with different tags depending on a number
    // of factors (doc literal, rpc, etc...).  This tells
    // our parent class to look for "string"
    protected String getItemDescriptor() {
        return "string";
    }

    // This describes what type of objects are to be contained in the Array
    protected Class getElementClass() {
        return PropertyInfo.STRING_CLASS;
    }
    public String getInnerText() {
        return null;
    }

    public void setInnerText(String s) {
    }
}
