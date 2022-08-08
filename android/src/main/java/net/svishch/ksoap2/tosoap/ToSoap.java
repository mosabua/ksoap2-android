package net.svishch.ksoap2.tosoap;

import net.svishch.ksoap2.annotations.*;
import net.svishch.ksoap2.client.OkHttp3Transport;
import net.svishch.ksoap2.util.NewInstanceObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToSoap {

    private final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
    private final NewInstanceObject newInstanceObject;
    private Logger logger;
    public ToSoap() {
        this.newInstanceObject = new NewInstanceObject();
        initLog();
        initEnvelope();
    }

    private void initLog() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    private void initEnvelope() {
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
    }

    public String getString(Object obj) {

        SoapObject soapObject = getSoapObject(obj);
        envelope.setOutputSoapObject(soapObject);
        envelope.toString();

        return envelope.toString();
    }


    public SoapObject getSoapObject(Object obj) {

        Field[] fields = obj.getClass().getDeclaredFields();
        SoapObject soapObject = new SoapObject();

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (this.newInstanceObject.isAnnotation(field, SoapAction.class)) {
                } else
                if (this.newInstanceObject.isAnnotation(field, NameSpace.class) && field.getType().equals(String.class)) {
                    soapObject.setNamespace((String) field.get(obj));
                } else if (this.newInstanceObject.isAnnotation(field, NameMethod.class) && field.getType().equals(String.class)) {
                    soapObject.setName((String) field.get(obj));
                } else {
                    addProperty(obj, field,soapObject);
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return soapObject;
    }

    private void addProperty(Object obj, Field field, SoapObject soapObject) {

        String valueName = firstUpperCase(field.getName());
        if (this.newInstanceObject.isAnnotation(field, SerializedName.class)) {
            valueName =  new SerializedNameUtil().getAnnotationValue(field);
        }
        Object value = new NewInstanceObject().getValue(obj,field);
        if (value != null) {

            System.out.println(String.format(" %s = %s",valueName, value));
        }

        soapObject.addProperty(valueName,value);
    }

    // First letter to uppercase
    public String firstUpperCase(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private void sendLogger(String mess) {
        this.logger.info(mess);
    }
}
