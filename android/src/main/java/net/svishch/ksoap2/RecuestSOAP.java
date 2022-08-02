package net.svishch.ksoap2;

import org.ksoap2.serialization.SoapObject;

public class RecuestSOAP {

    private String soapAction = "";
    private SoapObject soapObject = new SoapObject();

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public SoapObject getSoapObject() {
        return soapObject;
    }

    public void setSoapObject(SoapObject soapObject) {
        this.soapObject = soapObject;
    }
}
