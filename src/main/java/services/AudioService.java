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
    private AdvancedPlayer player;

    private boolean playing = false;
    private boolean pauseRequested = false;
    private String currentSong = "";

    public boolean isPlaying() {
        return playing;
    }

    public String getCurrentSong() {
        return currentSong;
    }

    public boolean isAvailable() {
        return !this.queue.isEmpty();
    }

    public AudioService() {
        this.logger = LogManager.getLogger(AudioService.class);
    }

    public void queue(String name, byte[] bytes) throws JavaLayerException {
        this.queue.add(Pair.of(name,  ArrayUtils.toObject(bytes)));
    }

    public void skip() throws JavaLayerException {
        boolean wasPlaying = this.playing;
        this.playing = false;
        this.player.stop();
        this.player.close();
        this.player = null;
        this.setupPlayer();
        if (wasPlaying) {
            this.play();
        }
    }

    public void toggle() throws JavaLayerException {
        if (!this.playing) {
            this.play();
        } else {
            this.pause();
        }
    }

    private void pause() {
        if (this.playing && this.player != null) {
            this.playing = false;
            pauseRequested = true;
            this.player.stop();
            pauseRequested = false;
        }
    }

    private void play() throws JavaLayerException {
        if (!this.playing) {
            if (this.player == null) {
                this.setupPlayer();
            }

            this.player.play();
        }
    }

    private void setupPlayer() throws JavaLayerException {
        if (playing) {
            return;
        }

        Pair<String, Byte[]> item = this.queue.poll();

        // Nothing to play
        if (item == null) {
            return;
        }

        this.currentSong = item.getKey();
        logger.trace("Playing: " + item.getKey());

        InputStream stream = new ByteArrayInputStream(ArrayUtils.toPrimitive(item.getValue()));
        this.player = new AdvancedPlayer(stream);
        this.player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackFinished(PlaybackEvent evt) {
                if (pauseRequested) return;
                logger.trace("Finished playing");
                playing = false;
                try {
                    setupPlayer();
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
    }
}
