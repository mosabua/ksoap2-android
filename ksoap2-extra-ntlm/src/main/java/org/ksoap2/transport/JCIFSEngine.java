package org.ksoap2.transport;

import jcifs.ntlmssp.NtlmFlags;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.util.Base64;

import java.io.IOException;

/**
 * Class taken from http://hc.apache.org/httpcomponents-client-ga/ntlm.html
 */
public final class JCIFSEngine {

    private static final int TYPE_1_FLAGS =
            NtlmFlags.NTLMSSP_NEGOTIATE_56 |
                    NtlmFlags.NTLMSSP_NEGOTIATE_128 |
                    NtlmFlags.NTLMSSP_NEGOTIATE_NTLM2 |
                    NtlmFlags.NTLMSSP_NEGOTIATE_ALWAYS_SIGN |
                    NtlmFlags.NTLMSSP_REQUEST_TARGET;

    public static String generateType1Msg(final String domain, final String workstation)
            throws JCIFSEngineException {
        final Type1Message type1Message = new Type1Message(TYPE_1_FLAGS, domain, workstation);
        return Base64.encode(type1Message.toByteArray());
    }

    public static String generateType3Msg(final String username, final String password,
                                   final String domain, final String workstation, final String challenge)
            throws JCIFSEngineException {
        Type2Message type2Message;
        try {
            type2Message = new Type2Message(Base64.decode(challenge));
        } catch (final IOException exception) {
            throw new JCIFSEngineException("Invalid NTLM type 2 message", exception);
        }
        final int type2Flags = type2Message.getFlags();
        final int type3Flags = type2Flags
                & (0xffffffff ^ (NtlmFlags.NTLMSSP_TARGET_TYPE_DOMAIN | NtlmFlags.NTLMSSP_TARGET_TYPE_SERVER));
        final Type3Message type3Message = new Type3Message(type2Message, password, domain,
                username, workstation, type3Flags);
        return Base64.encode(type3Message.toByteArray());
    }

    private static class JCIFSEngineException extends IOException {
        public JCIFSEngineException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
