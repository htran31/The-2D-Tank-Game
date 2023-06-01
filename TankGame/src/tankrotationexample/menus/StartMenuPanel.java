package tankrotationexample.menus;


import tankrotationexample.Launcher;
import tankrotationexample.Resources;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class StartMenuPanel extends JPanel {

    private BufferedImage menuBackground;
    private final Launcher lf;

    public StartMenuPanel(Launcher lf) {
        this.lf = lf;
        menuBackground = Resources.getSprite("menu");
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        JButton start = new JButton("Start");
        start.setFont(new Font("Courier New", Font.BOLD, 24));
        start.setBounds(60, 400, 150, 50);
        start.addActionListener(actionEvent -> this.lf.setFrame("game"));

        JButton exit = new JButton("Exit");
        exit.setSize(new Dimension(200, 100));
        exit.setFont(new Font("Courier New", Font.BOLD, 24));
        exit.setBounds(300, 400, 150, 50);
        exit.addActionListener((actionEvent -> this.lf.closeGame()));

        this.add(start);
        this.add(exit);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.menuBackground, 0, 0, null);
    }
}
