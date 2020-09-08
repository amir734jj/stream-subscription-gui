package services;

import javazoom.jl.decoder.JavaLayerException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class GuiService {

    private final Logger logger;
    private AudioService audioService;
    private HubService hubService;

    public GuiService(AudioService audioService, HubService hubService) {
        this.audioService = audioService;
        this.hubService = hubService;
        this.logger = LogManager.getLogger(GuiService.class);
    }

    public void init() {
        // creating the frame
        JFrame frame = new JFrame("Stream Subscription");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);

        // creating the panel
        JPanel panel = new JPanel();
        JButton skipButton = new JButton("Skip");
        JButton playButton = new JButton("Play");

        skipButton.setEnabled(false);
        playButton.setEnabled(false);

        JLabel label = new JLabel("Waiting ...");
        panel.add(label);

        skipButton.addActionListener(actionEvent -> {
            CompletableFuture.supplyAsync(() -> {
                try {
                    this.audioService.skip();
                } catch (JavaLayerException e) {
                    logger.error("Failed to handle skip", e);
                }

                return null;
            });
        });

        playButton.addActionListener(actionEvent -> {
            CompletableFuture.supplyAsync(() -> {
                try {
                    this.audioService.toggle();
                } catch (JavaLayerException e) {
                    logger.error("Failed to play/pause skip", e);
                }
                return null;
            });
        });

        panel.add(playButton);
        panel.add(skipButton);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);

        new Timer(500, e -> {
            skipButton.setEnabled(this.audioService.isAvailable());
            playButton.setEnabled(this.audioService.isAvailable() || this.audioService.isPlaying());

            label.setText(!this.audioService.getCurrentSong().isEmpty() ? this.audioService.getCurrentSong() : "Waiting ...");
            playButton.setText(this.audioService.isPlaying() ? "Pause" : "Play");

            SwingUtilities.updateComponentTreeUI(frame);
        }).start();

        new Thread(() ->  {
            try {
                this.hubService.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
