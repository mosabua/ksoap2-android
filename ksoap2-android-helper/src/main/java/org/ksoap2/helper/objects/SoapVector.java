/* The MIT License (MIT)
 *
 *Copyright (c) 2014 Omar Hussain
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy
 *of this software and associated documentation files (the "Software"), to deal
 *in the Software without restriction, including without limitation the rights
 *to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *copies of the Software, and to permit persons to whom the Software is
 *furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in
 *all copies or substantial portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *THE SOFTWARE.
 * 
 *
 * */

package org.ksoap2.helper.objects;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;

import org.ksoap2.helper.annotations.SoapAnnotation;
import org.ksoap2.serialization.AttributeInfo;
import org.ksoap2.serialization.HasAttributes;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;


/**
 * Helper class that uses reflection along with SoapAnnotations to detect,read and/or write vectors 
 * @author omarhussain
 *
 */

public abstract class SoapVector<T> extends ArrayList<T> implements KvmSerializable,HasAttributes{

    private static final long serialVersionUID = 1L;

    private boolean loadedFields = false;
    Field[] fields;
    Field[] attributes;
    String value;
    public SoapVector(){

        if(loadedFields == false){
            Field[] allFields =this.getClass().getFields();
            ArrayList<Field> arrFields = new ArrayList<Field>();
            ArrayList<Field> arrAttributes= new ArrayList<Field>();
            for(int i = 0; i < allFields.length; i++){
                Field field = allFields[i];
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
            fields = new Field[arrFields.size()];
            attributes = new Field[arrAttributes.size()];
            fields = arrFields.toArray(fields);
            attributes = arrAttributes.toArray(attributes);

        }
    }
    public SoapVector(String v){

        if(loadedFields == false){
            Field[] allFields =this.getClass().getFields();
            ArrayList<Field> arrFields = new ArrayList<Field>();
            ArrayList<Field> arrAttributes= new ArrayList<Field>();
            for(int i = 0; i < allFields.length; i++){
                Field field = allFields[i];
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

        return (T)this.get(arg0);
    }

    @Override
    public int getPropertyCount() 
    {
        return 1;
    }

    @Override
    public abstract void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2);

    @Override
    public void setProperty(int arg0, Object arg1) 
    {
        this.add((T) arg1);
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
