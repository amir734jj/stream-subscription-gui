package services;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AudioService {

    public void Play(String name, byte[] bytes) throws JavaLayerException {
        InputStream stream = new ByteArrayInputStream(bytes);
        AdvancedPlayer player = new AdvancedPlayer(stream);
        player.play();
    }
}
