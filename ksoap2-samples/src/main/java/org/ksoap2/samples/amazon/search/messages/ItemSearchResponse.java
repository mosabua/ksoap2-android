package org.ksoap2.samples.amazon.search.messages;

import java.util.*;

import org.ksoap2.serialization.*;

public class ItemSearchResponse extends BaseObject {

    private BookItems bookItems;
    private String operationRequest;

    public Object getProperty(int index) {
        if (index == 0) {
            return bookItems;
        } else {
            return operationRequest;
        }
    }

    public int getPropertyCount() {
        return 2;
    }

    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        switch (index) {
        case 0:
            info.name = "Items";
            info.type = new BookItems().getClass();
            break;
        case 1:
            info.name = "OperationRequest";
            info.type = new SoapObject(NAMESPACE, "OperationRequest").getClass();
        default:
            break;
        }
    }

    public void setProperty(int index, Object value) {
        if (index == 0) {
            bookItems = (BookItems) value;
        } else {
            operationRequest = value.toString();
        }
    }

    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "ItemSearchResponse", this.getClass());
        new BookItems().register(envelope);
        new BookAttributes().register(envelope);
    }
    public String getInnerText() {
        return null;
    }

    public void setInnerText(String s) {
    }
}
