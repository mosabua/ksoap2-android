package org.ksoap2.samples.amazon.search.messages;

import java.util.*;

import org.ksoap2.serialization.*;

public class BookItems extends LiteralArrayVector {
    private String request;

    protected String getItemDescriptor() {
        return "Item";
    }

    public Object getProperty(int index) {
        throw new RuntimeException("BookItems.getProperty is not implemented yet");
    }

    public int getPropertyCount() {
        return 4;
    }

    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        info.type = new SoapObject(BaseObject.NAMESPACE, "").getClass();
        switch (index) {
        case 0:
            info.name = "Request";
            break;
        case 1:
            info.name = "TotalResults";
            break;
        case 2:
            info.name = "TotalPages";
            break;
        case 3:
            super.getPropertyInfo(index, properties, info);
        default:
            break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
        case 0:
            request = value.toString();
            break;
        case 3:
            super.setProperty(index, value);
        default:
            break;
        }
    }

    protected Class getElementClass() {
        return new Book().getClass();
    }

    public void register(SoapSerializationEnvelope envelope) {
        super.register(envelope, BaseObject.NAMESPACE, "Items");
    }
    
    public synchronized String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Request: ");
        buffer.append(request);
        buffer.append("\n");
        for (int i = 0; i < size(); i++) {
            buffer.append("\n=== BOOK ===\n");
            buffer.append(elementAt(i).toString());
        }
        return buffer.toString();
    }
    public String getInnerText() {
        return null;
    }

    public void setInnerText(String s) {
    }
}
