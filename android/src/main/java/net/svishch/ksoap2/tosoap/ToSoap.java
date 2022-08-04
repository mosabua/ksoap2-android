package net.svishch.ksoap2.tosoap;


import net.svishch.ksoap2.util.NewInstanceObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.lang.reflect.Field;


public class ToSoap {

    private SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
    private NewInstanceObject newInstanceObject;

    public ToSoap() {
        this.newInstanceObject = new NewInstanceObject();
        initEnvelope();
    }

    private void initEnvelope() {
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
    }

    public String getString(Object InObjSoap) {


        SoapObject soapObject = new  SoapObject("http://www.oorsprong.org/websamples.countryinfo", "ListOfContinentsByName");

        envelope.setOutputSoapObject(soapObject);
        envelope.toString();

        return  envelope.toString();
    }

    public SoapObject getSoapObject(Object inObj){

        Field[] fields = inObj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            /*
            if (this.newInstanceObject.isSetValue(field, nameSoap)) {
                addFieldValueType(object, field, value);
                // System.out.println("OK  "+field.getName());
                return;
            }

             */

        }

        return new SoapObject();
    }

}
