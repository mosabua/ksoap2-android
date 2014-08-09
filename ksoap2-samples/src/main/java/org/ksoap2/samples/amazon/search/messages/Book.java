package org.ksoap2.samples.amazon.search.messages;

import java.util.*;

import org.ksoap2.serialization.*;

public class Book extends BaseObject {

    private String asin;
    private String detailPageUrl;
    private BookAttributes itemAttributes;

    public Object getProperty(int index) {
        throw new RuntimeException("Book.getProperty is not implemented yet");
    }

    public int getPropertyCount() {
        return 3;
    }

    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        info.type = PropertyInfo.STRING_CLASS;
        switch (index) {
        case 0:
            info.name = "ASIN";
            break;
        case 1:
            info.name = "DetailPageURL";
            break;
        case 2:
            info.name = "ItemAttributes";
            info.type = new BookAttributes().getClass();
        default:
            break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
        case 0:
            asin = value.toString();
            break;
        case 1:
            detailPageUrl = value.toString();
            break;
        case 2: 
            itemAttributes = (BookAttributes) value;
        default:
            break;
        }
    }
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ASIN: ");
        buffer.append(asin);
        buffer.append("\n");
        buffer.append("Detail page URL: ");
        buffer.append(detailPageUrl);
        buffer.append("\n");
        buffer.append(itemAttributes.toString());
        return buffer.toString();
    }

    public String getInnerText() {
        return null;
    }

    public void setInnerText(String s) {
    }

}
