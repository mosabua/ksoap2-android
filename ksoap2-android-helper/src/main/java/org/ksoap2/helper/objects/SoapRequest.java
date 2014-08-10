
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


import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.helper.enums.SoapEnvelopeVersion;
import org.ksoap2.helper.enums.SoapRequestCachePolicy;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class SoapRequest {

    String serviceUrl;
    String soapAction;
    String soapNamespace;
    SoapEnvelopeVersion soapEnvelopeVersion;
    boolean isDotNet;
    String soapMethod;
    ArrayList<PropertyInfo> params;
    org.ksoap2.helper.objects.SoapObject request;
    HashMap<String,Class> typesMapping;
    public SoapRequestCachePolicy policy;
    @SuppressWarnings("rawtypes")
    public SoapRequest(String url, String method, String namespace, SoapEnvelopeVersion version,boolean dotNet, 
            ArrayList<PropertyInfo> params, HashMap<String,Class> typesMapping) {
        this.serviceUrl = url;
        this.soapAction = method;
        this.soapMethod = method;
        this.soapEnvelopeVersion = version;
        this.soapNamespace = namespace;
        this.isDotNet = dotNet;
        this.params = params;
        this.typesMapping = typesMapping;
    }
    @SuppressWarnings("rawtypes")
    public SoapRequest(String url, String method, String namespace, SoapEnvelopeVersion version,boolean dotNet, 
            org.ksoap2.helper.objects.SoapObject param,  HashMap<String,Class> typesMapping) {
        this.serviceUrl = url;
        this.soapAction = method;
        this.soapMethod = method;
        this.soapEnvelopeVersion = version;
        this.soapNamespace = namespace;
        this.isDotNet = dotNet;
        this.request = param;
        this.params = new ArrayList<PropertyInfo>();
        this.typesMapping = typesMapping;
    }
    public final SoapSerializationEnvelope getSoapSerializationEnvelope() {
        SoapSerializationEnvelope envelope = null;
        switch(this.soapEnvelopeVersion){
        case SCASoapEnvelopeVersion10:
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
            break;
        case SCASoapEnvelopeVersion11:
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            break;
        case SCASoapEnvelopeVersion12:
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            break;
        }
        envelope.dotNet = this.isDotNet;
        envelope.implicitTypes = true;
        envelope.avoidExceptionForUnknownProperty = true;
        envelope.setAddAdornments(false);
        if(this.request != null){
            envelope.setOutputSoapObject(this.request); 
        }else{
            SoapObject request = new SoapObject(this.soapNamespace,this.soapAction);
            for(PropertyInfo pInfo: this.params){
                request.addProperty(pInfo);
            }
            envelope.setOutputSoapObject(request);
        }
        envelope.implicitTypes = true;

        for(String key: this.typesMapping.keySet()){
            envelope.addMapping(this.soapNamespace, key, this.typesMapping.get(key));
        }
        return envelope;
    }

    public String getServiceUrl(){
        return this.serviceUrl;
    }
    public String getSoapAction(){
        return this.soapAction;
    }
    public String getNamespace(){
        return this.soapNamespace;
    }
    public SoapEnvelopeVersion  getSoapEnveloperVersion(){
        return this.soapEnvelopeVersion;
    }
    public ArrayList<PropertyInfo> getParams(){
        return this.params;
    }
}
