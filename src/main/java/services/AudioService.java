package services;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Queue;

public class AudioService {

    private final Queue<Pair<String, Byte[]>> queue = new CircularFifoQueue<>(10);
    private final Logger logger;

    private boolean playing = false;

    public AudioService() {
        this.logger = LogManager.getLogger(AudioService.class);
    }

    public void queue(String name, byte[] bytes) throws JavaLayerException {
        this.queue.add(Pair.of(name,  ArrayUtils.toObject(bytes)));
        play();
    }

    private void play() throws JavaLayerException {
        if (playing) {
            return;
        }

        Pair<String, Byte[]> item = this.queue.poll();

        logger.trace("Playing: " + item.getKey());

        InputStream stream = new ByteArrayInputStream(ArrayUtils.toPrimitive(item.getValue()));
        AdvancedPlayer player = new AdvancedPlayer(stream);
        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackFinished(PlaybackEvent evt) {
                logger.trace("Finished playing");
                playing = false;
                try {
                    play();
                } catch (JavaLayerException e) {
                    logger.error("Failed playing", e);
                }
            }

            @Override
            public void playbackStarted(PlaybackEvent evt) {
                logger.trace("Started playing");
                playing = true;
            }
        });
        player.play();
    }
}
