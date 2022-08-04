package oorsprong;

import oorsprong.entity.RecuestListOfContinentsByName;
import oorsprong.entity.Response;
import net.svishch.ksoap2.client.SoapClient;
import net.svishch.ksoap2.client.UrlSettings;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.logging.Logger;

public class TestApi {
    public static void main(String[] args) {
        // http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL
        String url =  "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso";
        String user = "";
        String password = "";

        Logger LOG = Logger.getLogger(TestApi.class.getName());

        UrlSettings urlSettings = new UrlSettings()
                .setUrl(url)
                .setUser(user)
                .setPassword(password)
                .setDebug(true);

        getCountryInfo(urlSettings);

    }

    private static void getCountryInfo(UrlSettings urlSettings) {

        Response response = null;


        SoapClient soapClient = new SoapClient(urlSettings);
        // Response // Recuest
        try {
            response = soapClient.get(new RecuestListOfContinentsByName(), Response.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

      // Recuest request = new Soap().formSoap(soapObject, Recuest.class);
    }
}
