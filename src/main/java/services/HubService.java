package services;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import io.reactivex.Completable;
import io.reactivex.Single;
import javazoom.jl.decoder.JavaLayerException;
import models.Config;
import models.SongMetadata;
import models.Stream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class HubService {
    private Config config;
    private AuthService authService;
    private FileService fileService;
    private AudioService audioService;

    public HubService(Config config, AuthService authService, FileService fileService, AudioService audioService) {
        this.config = config;
        this.authService = authService;
        this.fileService = fileService;
        this.audioService = audioService;
    }

    public Completable listen() throws IOException {
        HubConnection hubConnection = HubConnectionBuilder.create(this.config.getUrl() + "/hub")
                .withAccessTokenProvider(Single.just(this.authService.token()))
                .build();

//        hubConnection.on("log", thing -> System.out.println(thing), String.class);

        hubConnection.on("download", (String filename, SongMetadata songMetadata, String base64, Stream stream) -> new FutureTask(() -> {
            this.audioService.Play(filename, this.fileService.base64ToStream(base64));
            return null;
        }).run(), String.class, SongMetadata.class, String.class, Stream.class);

        return hubConnection.start();
    }
}
