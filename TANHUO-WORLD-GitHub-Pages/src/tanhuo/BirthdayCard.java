package tanhuo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

final class BirthdayCard {
    static final String GREETING = "Happy birthday to the developer's mom!";

    private BirthdayCard() {}

    static void show(Component parent) {
        JOptionPane.showOptionDialog(parent, create(), "A Birthday Card", JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE, null, new String[]{"Continue"}, "Continue");
    }

    static JPanel create() {
        JPanel card = new JPanel(new BorderLayout(0, 7));
        card.setBackground(new Color(0xffecd7));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xb36381), 3),
            BorderFactory.createEmptyBorder(13, 23, 15, 23)));

        BufferedImage art = new BufferedImage(240, 76, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pixels = art.createGraphics();
        pixels.scale(3, 3);
        pixels.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        PixelArt.fruit(pixels, Item.BIRTHDAY_CAKE, 30, 2);
        PixelArt.fruit(pixels, Item.HEART, 2, 8);
        PixelArt.fruit(pixels, Item.HEART, 58, 6);
        pixels.setColor(new Color(0xe7a75e));
        for (int x : new int[]{23, 54}) {
            pixels.fillRect(x, 2, 1, 5);
            pixels.fillRect(x - 2, 4, 5, 1);
        }
        pixels.dispose();
        card.add(new JLabel(new ImageIcon(art)), BorderLayout.NORTH);

        JLabel title = new JLabel(GREETING, SwingConstants.CENTER);
        title.setFont(PixelFont.size(21));
        title.setForeground(new Color(0x733950));
        card.add(title, BorderLayout.CENTER);
        JLabel signature = new JLabel("With love from TANHUO WORLD", SwingConstants.CENTER);
        signature.setFont(PixelFont.size(13));
        signature.setForeground(new Color(0xa2596f));
        card.add(signature, BorderLayout.SOUTH);
        return card;
    }
}
