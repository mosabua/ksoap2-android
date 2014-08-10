package org.ksoap2.samples.amazon.search.messages;

import java.util.*;

import org.ksoap2.serialization.*;

public class BookAttributes extends BaseObject {

    private String author = "";
    private String manufacturer;
    private String productGroup;
    private String title;
    private String creator;

    public Object getProperty(int index) {
        throw new RuntimeException("BookAttributes.getProperty is not implemented yet");
    }

    public int getPropertyCount() {
        return 5;
    }

    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        info.type = PropertyInfo.STRING_CLASS;
        switch (index) {
        case 0:
            info.name = "Author";
            break;
        case 1:
            info.name = "Manufacturer";
            break;
        case 2:
            info.name = "ProductGroup";
            break;
        case 3:
            info.name = "Title";
            break;
        case 4:
            info.name = "Creator";
            break;
        default:
            break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
        case 0:
            author += value.toString() + ";";
            break;
        case 1:
            manufacturer = value.toString();
            break;
        case 2:
            productGroup = value.toString();
            break;
        case 3:
            title = value.toString();
            break;
        case 4:
            creator = value.toString();
        default:
            break;
        }
    }

    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "ItemAttributes", this.getClass());
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer("*** Attributes ***\n");
        buffer.append("Author: ");
        buffer.append(author);
        buffer.append("\n");
        buffer.append("Manufacturer: ");
        buffer.append(manufacturer);
        buffer.append("\n");
        buffer.append("Product Group: ");
        buffer.append(productGroup);
        buffer.append("\n");
        buffer.append("Title: ");
        buffer.append(title);
        buffer.append("\n");
        if (creator != null) {
            buffer.append("Creator: ");
            buffer.append(creator);
            buffer.append("\n");
        }
        return buffer.toString();
    }
    public String getInnerText() {
        return null;
    }

    public void setInnerText(String s) {
    }
}
