import com.fasterxml.jackson.databind.ObjectMapper;
import eu.lestard.easydi.EasyDI;
import models.Config;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.*;
import services.GuiService;
import services.HubService;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws IOException, InterruptedException {

        Logger rootLogger = LogManager.getRootLogger();
        rootLogger.setLevel(Level.ERROR);

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

        easyDI.getInstance(GuiService.class).init();

        rootLogger.trace("Terminated the app");
    }
}
