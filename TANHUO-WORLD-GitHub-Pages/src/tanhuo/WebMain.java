package tanhuo;

import javax.swing.SwingUtilities;

public final class WebMain {
    private WebMain() {}

    public static void main(String[] args) {
        int width = args.length == 2 ? Integer.parseInt(args[0]) : 920;
        int height = args.length == 2 ? Integer.parseInt(args[1]) : 460;
        SwingUtilities.invokeLater(() -> new DesktopGame(new Game(), true).open(width, height));
    }
}
