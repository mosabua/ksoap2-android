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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;

import org.ksoap2.helper.annotations.SoapAnnotation;
import org.ksoap2.serialization.AttributeInfo;
import org.ksoap2.serialization.HasAttributes;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapPrimitive;

/**
 * Helper class that uses reflection along with @SoapAnnotations to detect,read and/or write simple types 
 * @author omarhussain
 *
 */

public class SoapField extends SoapPrimitive implements HasAttributes,KvmSerializable {
    private boolean loadedFields = false;
    Field[] attributes;
    String value;
    public SoapField(){
        super();
        if(loadedFields == false){
            Field[] allFields =this.getClass().getFields();
            ArrayList<Field> arrAttributes= new ArrayList<Field>();
            for(int i = 0; i < allFields.length; i++){
                Field field = allFields[i];
                SoapAnnotation ann = field.getAnnotation(SoapAnnotation.class);
                if(ann == null){
                }else{
                    if(ann.IsAttribute()){
                        arrAttributes.add(field);
                    }else{
                    }
                }
            }
            attributes = new Field[arrAttributes.size()];
            attributes = arrAttributes.toArray(attributes);

        }

    }
    public SoapField(String v){
        super("","",v);
        if(loadedFields == false){
            Field[] allFields =this.getClass().getFields();
            ArrayList<Field> arrAttributes= new ArrayList<Field>();
            for(int i = 0; i < allFields.length; i++){
                Field field = allFields[i];
                SoapAnnotation ann = field.getAnnotation(SoapAnnotation.class);
                if(ann == null){
                }else{
                    if(ann.IsAttribute()){
                        arrAttributes.add(field);
                    }else{
                    }
                }
            }
            attributes = new Field[arrAttributes.size()];
            attributes = arrAttributes.toArray(attributes);

        }
        value = v;
    }
    public SoapField(String namespace, String name, Object v) {
        super(namespace,name,v);
        if(loadedFields == false){
            Field[] allFields =this.getClass().getFields();
            ArrayList<Field> arrAttributes= new ArrayList<Field>();
            for(int i = 0; i < allFields.length; i++){
                Field field = allFields[i];
                SoapAnnotation ann = field.getAnnotation(SoapAnnotation.class);
                if(ann == null){
                }else{
                    if(ann.IsAttribute()){
                        arrAttributes.add(field);
                    }else{
                    }
                }
            }
            attributes = new Field[arrAttributes.size()];
            attributes = arrAttributes.toArray(attributes);

        }
        value = (String) v;
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
    }
    public String getValue(){
        return value;
    }
    @Override
    public Object getProperty(int index) {

        return getValue();
    }
    @Override
    public int getPropertyCount() {

        return 0;
    }
    @Override
    public void setProperty(int index, Object value) {


    }
    @Override
    public void getPropertyInfo(int index, Hashtable properties,
            PropertyInfo info) {


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
    public String getInnerText() {

        return value;
    }
    @Override
    public void setInnerText(String s) {
        value = s;

    }

}
