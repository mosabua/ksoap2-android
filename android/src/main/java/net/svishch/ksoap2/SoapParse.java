package net.svishch.ksoap2;

import org.ksoap2.serialization.SoapObject;

public class SoapParse {
    public String toSoap(Object src) {

        return "";
    }


    public <T> T formSoap(SoapObject soap, Class<T> classOfT) {
        return new FormSoap().formSoap(soap,classOfT);
    }
}
