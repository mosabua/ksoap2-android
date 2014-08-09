/* Copyright (c) 2014, Omar Hussain., Pakistan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */

package org.ksoap2.helper.objects;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import org.kobjects.isodate.IsoDate;
import org.ksoap2.helper.annotations.SoapAnnotation;
import org.ksoap2.serialization.AttributeInfo;
import org.ksoap2.serialization.HasAttributes;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapPrimitive;

/**
 * Helper class that uses reflection along with @SoapAnnotations to detect,read and/or write complex types 
 * @author omarhussain
 *
 */

public class SoapObject implements KvmSerializable,Serializable,HasAttributes{

    private static final long serialVersionUID = 1L;
    private boolean loadedFields = false;
    Field[] fields;
    Field[] attributes;
    String value;
    public HashMap<String,Object> values; 
    public SoapObject(){
        values = new HashMap<String,Object>();
        if(loadedFields == false){
            Field[] allFields =this.getClass().getFields();
            ArrayList<Field> arrFields = new ArrayList<Field>();
            ArrayList<Field> arrAttributes= new ArrayList<Field>();
            for(int i = 0; i < allFields.length; i++){

                Field field = allFields[i];
                if(field.getName().compareTo("values") != 0 && field.getName().compareTo("value") != 0){
                    SoapAnnotation ann = field.getAnnotation(SoapAnnotation.class);
                    if(ann == null){
                        arrFields.add(field);

                    }else{
                        if(ann.IsAttribute()){
                            arrAttributes.add(field);
                        }else{
                            arrFields.add(field);
                        }
                    }
                }
            }
            fields = new Field[arrFields.size()];
            attributes = new Field[arrAttributes.size()];
            fields = arrFields.toArray(fields);
            attributes = arrAttributes.toArray(attributes);

        }
    }
    public SoapObject(String v){
        values = new HashMap<String,Object>();
        if(loadedFields == false){
            Field[] allFields =this.getClass().getFields();
            ArrayList<Field> arrFields = new ArrayList<Field>();
            ArrayList<Field> arrAttributes= new ArrayList<Field>();
            for(int i = 0; i < allFields.length; i++){
                Field field = allFields[i];
                if(field.getName().compareTo("values") != 0 && field.getName().compareTo("value") != 0){
                    SoapAnnotation ann = field.getAnnotation(SoapAnnotation.class);
                    if(ann == null){
                        arrFields.add(field);

                    }else{
                        if(ann.IsAttribute()){
                            arrAttributes.add(field);
                        }else{
                            arrFields.add(field);
                        }
                    }
                }
            }
            fields = new Field[arrFields.size()];
            attributes = new Field[arrAttributes.size()];
            fields = arrFields.toArray(fields);
            attributes = arrAttributes.toArray(attributes);

        }
        value = v;
    }
    @Override
    public Object getProperty(int arg0) 
    {

        try {
            Field field = fields[arg0];
            if(field.getType().equals(Date.class)){
                String dt = IsoDate.dateToString(((Date)fields[arg0].get(this)), IsoDate.DATE_TIME);
                return dt;
            }else if(field.getType().getSuperclass() != null){
                if(field.getType().getSuperclass().equals(SoapVector.class)){
                    SoapVector sVector = (SoapVector) field.get(this);
                    SoapAnnotation ann = field.getAnnotation(SoapAnnotation.class);
                    if(ann == null){
                        return sVector;
                    }else{
                        if(ann.HasRoot()){
                            return sVector; 
                        }else{
                            return sVector.get(0);
                        }
                    }
                }else{
                    return fields[arg0].get(this); 
                }
            } else if(field.getType().equals(SoapField.class)){
                SoapField sfield =  (SoapField)field.get(this);
                return sfield.getValue();
            }else{

                return fields[arg0].get(this);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getPropertyCount() 
    {
        return fields.length;
    }

    @Override
    public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) 
    {
        Field field = fields[arg0];
        if(field.getType().getSuperclass() != null){
            if(field.getType().getSuperclass().equals(SoapVector.class)){
                SoapVector sVector;
                try {
                    SoapAnnotation ann = field.getAnnotation(SoapAnnotation.class);
                    if(ann == null){
                        arg2.name = field.getName().replace("_", "");
                        arg2.type = field.getType();
                    }else{
                        if(ann.HasRoot()){
                            arg2.name = field.getName().replace("_", "");
                            arg2.type = field.getType(); 
                        }else{
                            sVector = (SoapVector) field.getType().newInstance();
                            sVector.getPropertyInfo(arg0, arg1, arg2);
                        }
                    }

                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }else{
                arg2.name = field.getName().replace("_", "");
                arg2.type = field.getType(); 
            }



        }else{
            arg2.name = field.getName().replace("_", "");
            arg2.type = field.getType();
        }

    }

    @Override
    public void setProperty(int arg0, Object arg1) 
    {
        if(arg1 == null){
            return;
        }
        Field field = fields[arg0];
        field.setAccessible(true);
        try {
            if(field.getType().equals(Date.class)){
                SoapPrimitive prim = (SoapPrimitive)arg1;
                Object obj =prim.getValue();
                Date dt = IsoDate.stringToDate((String)obj, IsoDate.DATE_TIME);
                if(field.getType().equals(dt.getClass())){
                    field.set(this,dt);
                }
            }else{
                field.set(this, arg1);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            if(field.getType().getSuperclass().equals(SoapVector.class)){
                SoapVector soapV;
                try {
                    soapV = (SoapVector) field.get(this);
                    if(soapV == null){
                        soapV = (SoapVector)field.getType().newInstance();
                    }
                    if(arg1.getClass() == SoapPrimitive.class){
                        SoapPrimitive prim = (SoapPrimitive)arg1;
                        Object obj =prim.getValue();
                        soapV.add( (String)obj);
                    }else{
                        soapV.add(arg1);
                    }
                    try {
                        field.set(this, soapV);
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    }
                } catch (InstantiationException e2) {

                    e2.printStackTrace();
                } catch (IllegalAccessException e2) {

                    e2.printStackTrace();
                }catch(Exception e1){
                    e1.printStackTrace();
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        field.setAccessible(false);
    }
    @Override
    public int getAttributeCount() {
        return attributes.length;
    }
    @Override
    public void getAttributeInfo(int arg0, AttributeInfo arg1) {
        arg1.name = attributes[arg0].getName();
        arg1.type = attributes[arg0].getType();

    }
    @Override
    public void setAttribute(AttributeInfo info){
        for(int i = 0; i  < attributes.length; i++){
            Field attribute = attributes[i];
            if(attribute.getName().equals(info.getName())){
                try {
                    attribute.set(this, info.getValue());

                } catch (IllegalAccessException e) {

                    e.printStackTrace();
                } catch (IllegalArgumentException e) {

                    e.printStackTrace();
                }
            }
        }

        values.put(info.getName(), info.getValue());

    }
    @Override
    public void getAttribute(int index, AttributeInfo info) {
        for(int i = 0; i  < attributes.length; i++){
            Field attribute = attributes[i];
            if(attribute.getName().equals(info.getName())){
                try {
                    info.setValue(attribute.get(this));
                } catch (IllegalAccessException e) {

                    e.printStackTrace();
                } catch (IllegalArgumentException e) {

                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void setInnerText(String s){
        value = s;
    }
    @Override
    public String getInnerText() {

        return value;
    }


}
