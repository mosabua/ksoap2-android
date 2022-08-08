package net.svishch.ksoap2;

import net.svishch.ksoap2.formsoap.FormSoap;
import net.svishch.ksoap2.tosoap.ToSoap;
import org.ksoap2.serialization.SoapObject;

public class Soap {
    public String toSoap(Object src) {
        ToSoap toSoap = new ToSoap();
        return toSoap.getString(src);
    }

    public <T> T formSoap(SoapObject soap, Class<T> classOfT) {
        return new FormSoap().formSoap(soap,classOfT);
    }

    public <T> T formSoap(String soap, Class<T> classOfT) {
        return new FormSoap().formSoap(soap,classOfT);
    }

}
