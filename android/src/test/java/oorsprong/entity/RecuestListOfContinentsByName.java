package oorsprong.entity;

import net.svishch.ksoap2.RecuestSOAP;
import org.ksoap2.serialization.SoapObject;

public class RecuestListOfContinentsByName {
   private String NAMESPACE = "http://www.oorsprong.org/websamples.countryinfo";
   private String METHOD_NAME = "";
   private String soapAction = "http://www.w3.org/2003/05/soap-envelope";


    /*
    <?xml version="1.0" encoding="utf-8"?>
        <soap12:Envelope xmlns:soap12=>
        <soap12:Body>
            <RecuestListOfContinentsByName xmlns="http://www.oorsprong.org/websamples.countryinfo">
            </RecuestListOfContinentsByName>
        </soap12:Body>
        </soap12:Envelope>
    */

    public RecuestSOAP getSoap() {

        SoapObject outputSoapObject = new SoapObject(NAMESPACE, METHOD_NAME);
        RecuestSOAP recuestSOAP = new RecuestSOAP();
        recuestSOAP.setSoapAction(soapAction);
        recuestSOAP.setSoapObject(outputSoapObject);
        return recuestSOAP;

    }
}
