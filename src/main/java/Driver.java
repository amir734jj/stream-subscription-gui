import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import eu.lestard.easydi.EasyDI;
import io.reactivex.Single;
import models.Config;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.*;
import services.AuthService;
import services.HubService;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws IOException, InterruptedException {

        Logger rootLogger = LogManager.getRootLogger();
        rootLogger.setLevel(Level.ALL);

        ConsoleAppender consoleAppender = new ConsoleAppender();
        String PATTERN = "%d [%p|%c|%C{1}] %m%n";
        consoleAppender.setLayout(new PatternLayout(PATTERN));
        consoleAppender.setThreshold(Level.TRACE);
        consoleAppender.activateOptions();
        rootLogger.addAppender(consoleAppender);

        EasyDI easyDI = new EasyDI();

        Config config = new Config();
        config.setUrl("https://stream-subscription-api.herokuapp.com");
        config.setUsername(System.getenv("username"));
        config.setPassword(System.getenv("password"));

        easyDI.bindInstance(Config.class, config);
        easyDI.bindInstance(ObjectMapper.class, new ObjectMapper());
        easyDI.bindInstance(HttpClient.class, HttpClientBuilder.create().build());

        HubService hubService = easyDI.getInstance(HubService.class);

        hubService.listen().blockingAwait();

        rootLogger.trace("Terminated the app");
    }
}
