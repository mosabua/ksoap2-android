package net.svishch.ksoap2.formsoap;


import net.svishch.ksoap2.ParseSoapUtil;
import net.svishch.ksoap2.annotations.SerializedName;
import net.svishch.ksoap2.client.OkHttp3Transport;
import net.svishch.ksoap2.debug.SoapObjectDebug;
import net.svishch.ksoap2.util.NewInstanceObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormSoap {

    private Logger logger;
    private NewInstanceObject newInstanceObject;

    public FormSoap() {
        this.logger = Logger.getLogger(FormSoap.class.getName());
        this.logger.setLevel(Level.WARNING);
        this.newInstanceObject = new NewInstanceObject();
    }

    public <T> T formSoap(SoapObject soap, Class<T> classOfT) {

        T object = this.newInstanceObject.create(classOfT);

        if (soap == null) {
            return object;
        }

        //System.out.println(classOfT.getTypeName());
        //printFields(classOfT.getDeclaredFields());

        for (int i = 0; i < soap.getPropertyCount(); i++) {
            PropertyInfo propertyInfo = soap.getPropertyInfo(i);
            Object value = propertyInfo.getValue();
            // System.out.println("Soap name: " + propertyInfo.name);
            try {
                setFieldValue(object, propertyInfo.name, value, classOfT);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return object;
    }

    public <T> T formSoap(String soap, Class<T> classOfT) {

        SoapObject soapObject = new FormStringSoap().getSoapObject(soap);
        System.out.println(soap);
        new SoapObjectDebug().printSoapObject(soapObject);

        return formSoap(soapObject,classOfT);

    }

    private <T> void setFieldValue(Object object, String nameSoap, Object value, Class<T> classOfT) throws IllegalAccessException {

        // System.out.println(nameSoap);
        Field[] fields = classOfT.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (this.newInstanceObject.isSetValue(field, nameSoap)) {
                addFieldValueType(object, field, value);
                // System.out.println("OK  "+field.getName());
                return;
            }

        }

        sendLog("Error: " + nameSoap);
    }



    private void addFieldValueType(Object object, Field field, Object value) throws IllegalAccessException {
        Class<?> fieldType = field.getType();
        if (fieldType.equals(String.class)) {
            field.set(object, ParseSoapUtil.checkString(value.toString()));
        } else if (fieldType.equals(long.class)) {
            field.setLong(object, ParseSoapUtil.checkLong(value.toString()));
        } else if (fieldType.equals(int.class)) {
            field.setInt(object, ParseSoapUtil.checkInt(value.toString()));
        } else if (fieldType.equals(boolean.class)) {
            field.setBoolean(object, ParseSoapUtil.checkBoolean(value));
        } else if (fieldType.equals(List.class)) {
            this.setList(object, field, value);
        } else if (value.getClass().getTypeName().equals(SoapObject.class.getTypeName())) {
            //System.out.println(field.getType() + " == " + value.getClass().getName());
            field.set(object, formSoap((SoapObject) value, field.getType()));
        } else {
            System.err.println(field.getType() + " != " + value.getClass().getName());
        }
    }

    private void setList(Object object, Field field, Object value) {

        Type type = field.getGenericType();
        String genericType =  type.getTypeName().substring(type.getTypeName().indexOf('<')+1,type.getTypeName().indexOf('>'));

        List objFiled = null;
        try {
            objFiled = (List) field.get(object);
            if (objFiled == null) {
                objFiled = new ArrayList();
                field.set(object, objFiled);
            }


            if (value instanceof SoapObject) {
                SoapObject soapObject = (SoapObject) value;

                objFiled.add(this.formSoap(soapObject, Class.forName(genericType)));
            }
        } catch (IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }




    /*
       public <T> T  formSoap(String soap, Class<T> classOfT){

         //  Object object = formSoap(soap, (Type) classOfT);
           return Primitives.wrap(classOfT).cast(object);
       }

       public <T> T formSoap(String soap, Type typeOfT)  {
           if (soap == null) {
               return null;
           }
           StringReader reader = new StringReader(soap);
           T target = (T) formSoap(reader, typeOfT);

           return target;
       }
   //throws JsonIOException, JsonSyntaxException
       public <T> T formSoap(Reader soap, Type typeOfT)  {
           SoapReader jsonReader = SoapReader(soap);
           T object = (T) formSoap(jsonReader, typeOfT);
           //assertFullConsumption(object, jsonReader);
           return object;
       }
   */

    // Первую букву в верхний регистр
    public String firstUpperCase(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private void printFields(Field[] fields) {
        for (Field field : fields) {
            field.setAccessible(true);
            System.out.printf("%s %s %s%n",
                    Modifier.toString(field.getModifiers()),
                    field.getType().getSimpleName(),
                    field.getName()
            );
        }
    }

    private void printSoap(SoapObject soapObject) {
        for (int i = 0; i < soapObject.getPropertyCount(); i++) {
            PropertyInfo propertyInfo = soapObject.getPropertyInfo(i);
            Object value1 = propertyInfo.getValue();
            System.out.println(propertyInfo.name + " = " + value1);
        }
    }

    private void sendLog(String mess){
        this.logger.fine(mess);
    }
}
