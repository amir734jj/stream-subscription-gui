package services;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import io.reactivex.Completable;
import io.reactivex.Single;
import javazoom.jl.decoder.JavaLayerException;
import models.Config;
import models.SongMetadata;
import models.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HubService {
    private final Logger logger;
    private Config config;
    private AuthService authService;
    private FileService fileService;
    private AudioService audioService;
    private HubConnection hubConnection;

    public HubService(Config config, AuthService authService, FileService fileService, AudioService audioService) {
        this.config = config;
        this.authService = authService;
        this.fileService = fileService;
        this.audioService = audioService;
        this.logger = LogManager.getLogger(HubService.class);
    }

    private void connect() throws IOException {
        this.hubConnection = HubConnectionBuilder.create(this.config.getUrl() + "/hub")
                .withAccessTokenProvider(Single.just(this.authService.token()))
                .build();
    }
    public Completable listen() throws IOException {
        this.logger.trace("Started the stream");

        this.connect();
        hubConnection.onClosed(e -> {
            logger.trace("Reconnecting");
            try {
                this.connect();
                this.listen();
            } catch (IOException ioException) {
                this.logger.error("Failed to reconnect", ioException);
            }
        });

        hubConnection.on("log", (x) -> { }, String.class);
        hubConnection.on("count", x -> { }, Integer.class);
        hubConnection.on("download", (String filename, SongMetadata songMetadata, String base64, Stream stream) -> new Thread(() -> {
            logger.trace("Downloaded: " + filename);
            try {
                this.audioService.queue(filename, this.fileService.base64ToStream(base64));
            } catch (JavaLayerException | UnsupportedEncodingException e) {
                logger.error(e);
            }
        }).start(), String.class, SongMetadata.class, String.class, Stream.class);

        return hubConnection.start();
    }
}
