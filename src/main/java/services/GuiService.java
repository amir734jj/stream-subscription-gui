package services;

import javazoom.jl.decoder.JavaLayerException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GuiService {
    private AudioService audioService;

    public GuiService(AudioService audioService, HubService hubService) throws IOException {
        this.audioService = audioService;
    }

    public void init() {
        // creating the frame
        JFrame frame = new JFrame("Stream Subscription");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 200);

        // creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("FILE");
        JMenu m2 = new JMenu("Help");
        mb.add(m1);
        mb.add(m2);
        JMenuItem m11 = new JMenuItem("Open");
        JMenuItem m22 = new JMenuItem("Save as");
        m1.add(m11);
        m1.add(m22);

        // creating the panel
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Some Text");
        JButton skipButton = new JButton("Skip");


        skipButton.addActionListener(actionEvent -> {
            try {
                this.audioService.skip();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        });

        panel.add(label);
        panel.add(skipButton);

//        GroupLayout layout = new GroupLayout(panel);  // problematic!
//        panel.setLayout(layout);

        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, panel);


        frame.setVisible(true);
    }
}
