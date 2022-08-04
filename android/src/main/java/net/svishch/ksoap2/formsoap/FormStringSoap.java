package net.svishch.ksoap2.formsoap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FormStringSoap {

    private SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);

    public FormStringSoap() {
        initEnvelope();
    }

    private void initEnvelope() {
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
    }

    SoapObject getSoapObject(String soap) {
        InputStream stream = new ByteArrayInputStream(soap.getBytes(StandardCharsets.UTF_8));
        try {
            envelope.parse(stream);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return new SoapObject();
    }



}
