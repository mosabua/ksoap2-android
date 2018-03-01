package org.ksoap2.transport;

import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class NtlmAuthenticator implements Authenticator {
    //private final NtlmEngineImpl engine;
    private final JCIFSEngine engine;

    private final String user;
    private final String password;
    private final String ntDomain;
    private final String ntWorkstation;

    NtlmAuthenticator(String user, String password, String ntDomain, String ntWorkstation) {
        this.user = user;
        this.password = password;
        this.ntDomain = ntDomain;
        this.ntWorkstation = ntWorkstation;

        //engine = new NtlmEngineImpl();
        engine = new JCIFSEngine();
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        List<String> authHeaders = response.headers("WWW-Authenticate");
        if (authHeaders != null) {
            boolean negociate = false;
            boolean ntlm = false;
            String ntlmValue = null;
            for (String authHeader : authHeaders) {
                if (authHeader.equalsIgnoreCase("Negotiate")) {
                    negociate = true;
                }
                if (authHeader.equalsIgnoreCase("NTLM")) {
                    ntlm = true;
                }
                if (authHeader.startsWith("NTLM ")) {
                    ntlmValue = authHeader.substring(5);
                }
            }

            if (negociate && ntlm) {
                String type1Msg = JCIFSEngine.generateType1Msg(ntDomain, ntWorkstation);
                String header = "NTLM " + type1Msg;
                return response.request().newBuilder().header("Authorization", header).build();
            } else if (ntlmValue != null) {
                String type3Msg = JCIFSEngine.generateType3Msg(user, password, ntDomain, ntWorkstation, ntlmValue);
                String ntlmHeader = "NTLM " + type3Msg;
                return response.request().newBuilder().header("Authorization", ntlmHeader).build();
            }
        }

        if (responseCount(response) <= 3) {
            String credential = Credentials.basic(user, password);
            return response.request().newBuilder().header("Authorization", credential).build();
        }

        return null;
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

}
