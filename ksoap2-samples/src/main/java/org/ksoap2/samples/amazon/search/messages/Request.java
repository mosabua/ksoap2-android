package org.ksoap2.samples.amazon.search.messages;

import java.util.*;

import org.ksoap2.serialization.*;

public class Request extends BaseObject {

    public String author;
    public String searchIndex;

    public Object getProperty(int index) {
        if(index == 0) {
            return author;
        } else {
            return searchIndex;
        }
    }

    public int getPropertyCount() {
        return 2;
    }

    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        info.type = PropertyInfo.STRING_CLASS;
        if(index == 0) {
            info.name = "Author";
        } else {
            info.name = "SearchIndex";
        }
    }

    public void setProperty(int index, Object value) {
        throw new RuntimeException("Request.setProperty is not implemented yet");
    }

    public void register(SoapSerializationEnvelope envelope) {
        envelope.addMapping(NAMESPACE, "ItemSearchRequest", this.getClass());
    }
    public String getInnerText() {
        return null;
    }

    public void setInnerText(String s) {
    }
}
