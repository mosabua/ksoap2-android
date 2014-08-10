package org.ksoap2.transport;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.params.HttpParams;

public class NTLMSchemeFactory implements AuthSchemeFactory {
    public AuthScheme newInstance(final HttpParams params) {
    // see http://www.robertkuzma.com/2011/07/
    // manipulating-sharepoint-list-items-with-android-java-and-ntlm-authentication/
        return new NTLMScheme(new JCIFSEngine());
    }
}