package net.svishch.ksoap2;

import org.ksoap2.serialization.AttributeInfo;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

public class SoapObjectDebug {

    public void print(Object soapObject) {

        if (soapObject instanceof SoapObject) {
            new SoapObjectDebug().printSoapObject((SoapObject) soapObject);
        }
        if (soapObject instanceof SoapPrimitive) {
            new SoapObjectDebug().printSoapPrimitive((SoapPrimitive) soapObject);
        }
    }

    public void printSoapPrimitive(SoapPrimitive soapObject) {
        printSoapPrimitive(soapObject ,"\t");
    }

    public void printSoapObject(SoapObject soapObject){
        printSOAP(soapObject,"\t");
    }

    private void printSOAP(SoapObject soapObject, String prefix) {


        String strInfo = String.format(
                "%s --------- " +
                "SoapObject {name = %s} " +
                "{property=%d} --------------",prefix,soapObject.getName(),soapObject.getPropertyCount());

        println(strInfo);
       
        if (soapObject.getAttributeCount() > 0) {
            printAttribute(soapObject, prefix);
        }

        if (soapObject.getPropertyCount() > 0) {
            printPropertyInfo(soapObject, prefix);
        }
    }

    private void printPropertyInfo(SoapObject soapObject, String prefix) {
        String strInfo = String.format(
                "%s --------- " +
                "{property = %d} --------------",prefix,soapObject.getPropertyCount());
        println(strInfo);

        for (int i = 0; i < soapObject.getPropertyCount(); i++) {
            PropertyInfo propertyInfo = soapObject.getPropertyInfo(i);
            Object value = propertyInfo.getValue();

            if (value instanceof SoapObject){
                println(String.format("%s [%d - soapObject] %s",prefix,i,propertyInfo.name));
                printSOAP((SoapObject) value, prefix +"\t");
            } else if (value instanceof SoapPrimitive){
                println(String.format("%s [%d - soapPrimitive] %s = %s",prefix,i,propertyInfo.name,((SoapPrimitive) value).getValue()));
                printSoapPrimitive((SoapPrimitive) value,prefix+"\t");
            }

        }

    }

    private void printSoapPrimitive(SoapPrimitive soapPrimitive ,String prefix) {
        String strInfo = String.format(
                "%s --------- SoapPrimitive {attribute=%d} --------------",prefix,soapPrimitive.getAttributeCount());
        println(strInfo);

        if (soapPrimitive.getAttributeCount() > 1) {
        }
        if (soapPrimitive.getAttributeCount() > 0) {
            printAttributeSoapPrimitive(soapPrimitive, prefix);
        }

        // println("$prefix name = "+result.name)

    }

    private void printAttribute(SoapObject soapObject, String prefix) {

     //   println(String.format("%s {attribute=%d} ",prefix,soapObject.getAttributeCount()));
     //   println(String.format("%s *********************",prefix));

        for (int i = 0; i < soapObject.getAttributeCount(); i++) {
            AttributeInfo info = new AttributeInfo();
            soapObject.getAttributeInfo(i,info);
            println(String.format("%s [%d - attribute] %s ",prefix,i,info));
        }

    }

    private void printAttributeSoapPrimitive(SoapPrimitive soapPrimitive,String prefix) {

        for (int i = 0; i < soapPrimitive.getAttributeCount()-1 ; i++) {
            AttributeInfo info = new AttributeInfo();
            soapPrimitive.getAttributeInfo(i,info);
            println(String.format("%s [%d - attributePrimitive] %s ",prefix,i,info));
        }

    }



    private void println(String str){
        System.out.println(str);
    }


}
