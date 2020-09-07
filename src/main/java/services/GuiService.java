package services;

import javazoom.jl.decoder.JavaLayerException;

import javax.swing.*;
import java.io.IOException;

public class GuiService {
    private AudioService audioService;

    public GuiService(AudioService audioService, HubService hubService) throws IOException {
        this.audioService = audioService;
    }

    public void init() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JButton skipButton = new JButton("Skip");

        skipButton.addActionListener(actionEvent -> {
            try {
                this.audioService.skip();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        });
        panel.add(skipButton);

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        frame.getContentPane().add(panel);
        frame.setSize(500, 200);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
