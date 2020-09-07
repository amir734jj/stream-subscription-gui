import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import eu.lestard.easydi.EasyDI;
import io.reactivex.Single;
import models.Config;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import services.AuthService;
import services.HubService;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws IOException, InterruptedException {

        EasyDI easyDI = new EasyDI();

        Config config = new Config();
        config.setUrl("https://stream-subscription-api.herokuapp.com");
        config.setUsername("username");
        config.setPassword("password");

        easyDI.bindInstance(Config.class, config);
        easyDI.bindInstance(ObjectMapper.class, new ObjectMapper());
        easyDI.bindInstance(HttpClient.class, HttpClientBuilder.create().build());

        HubService hubService = easyDI.getInstance(HubService.class);

        hubService.listen().subscribe();
    }
}
