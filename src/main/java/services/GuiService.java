package services;

import javazoom.jl.decoder.JavaLayerException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GuiService {

    private final Logger logger;
    private AudioService audioService;
    private HubService hubService;

    public GuiService(AudioService audioService, HubService hubService) {
        this.audioService = audioService;
        this.hubService = hubService;
        this.logger = LogManager.getLogger(GuiService.class);
    }

    public void init() throws IOException {
        // creating the frame
        JFrame frame = new JFrame("Stream Subscription");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);

        // creating the panel
        JPanel panel = new JPanel();
        JButton skipButton = new JButton("Skip");
        skipButton.setEnabled(false);

        JLabel label = new JLabel("Waiting ...");
        panel.add(label);

        skipButton.addActionListener(actionEvent -> {
            try {
                this.audioService.skip();
            } catch (JavaLayerException e) {
                logger.error("Failed to handle skip", e);
            }
        });

        panel.add(skipButton);
        frame.getContentPane().add(BorderLayout.CENTER, panel);

        frame.setVisible(true);

        new Thread(() -> {
            while (true) {
                try {
                    skipButton.setEnabled(this.audioService.isPlaying());
                    label.setText(this.audioService.isPlaying() ? this.audioService.getCurrentSong() : "Waiting ...");

                    SwingUtilities.updateComponentTreeUI(frame);
                    frame.doLayout();

//                    frame.invalidate();
                    frame.validate();
//                    frame.repaint();

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error("Stream watcher failed", e);
                }
            }
        }).start();

        this.hubService.listen();
    }
}
