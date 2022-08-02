package net.svishch.ksoap2;

import org.ksoap2.serialization.SoapObject;

public interface CallbackSOAP {
    void result(SoapObject result);
}
