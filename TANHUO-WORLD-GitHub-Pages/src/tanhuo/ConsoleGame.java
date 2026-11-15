package tanhuo;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import tanhuo.Game.Choice;

final class ConsoleGame {
    private final Game game;
    private final Scanner input;
    private final PrintStream output;

    ConsoleGame(Game game, Scanner input, PrintStream output) {
        this.game = game;
        this.input = input;
        this.output = output;
    }

    void play() {
        output.println("TANHUO WORLD");
        output.println("tanduoduo and tanhoohoo have just moved into Home Land.");
        output.println("The watermelon is ripe, but the brazier is cold. Look for charcoal along the Wildflower Trail.");
        output.println("Home Land: 1-5 plots, M merchant, W armory, B brazier, G gate, T tanhoohoo, @ you.");
        output.println("Outside World: C charcoal, S heart seed, R rustling bushes, H wild cow, Q cake, ^ thorns.");
        while (true) {
            List<Choice> choices = game.choices();
            showTurn(choices);
            if (!input.hasNextLine()) break;
            String command = input.nextLine().strip().toLowerCase(Locale.ROOT);
            if (command.equals("q")) break;
            String result = switch (command) {
                case "w" -> game.move(0, -1);
                case "a" -> game.move(-1, 0);
                case "s" -> game.move(0, 1);
                case "d" -> game.move(1, 0);
                case "f" -> game.attackNearest();
                default -> choose(command, choices);
            };
            if (!result.isEmpty()) output.println(result);
            if (game.takeBirthdayCard()) output.println(BirthdayCard.GREETING);
            game.takeMeleeAnimation();
        }
        output.println("See you back in Home Land.");
    }

    private void showTurn(List<Choice> choices) {
        output.println();
        output.println(game.heading());
        output.println(game.status());
        game.mapView().forEach(output::println);
        for (int i = 0; i < choices.size(); i++) {
            output.printf("%d. %s%n", i + 1, choices.get(i).label());
        }
        output.print("W/A/S/D move | F attack | Enter a menu number | Q quit > ");
        output.flush();
    }

    private String choose(String command, List<Choice> choices) {
        try {
            int number = Integer.parseInt(command);
            if (number >= 1 && number <= choices.size()) {
                return choices.get(number - 1).action().get();
            }
        } catch (NumberFormatException ignored) {
            return "Enter W, A, S, D, F, Q, or a menu number.";
        }
        return "That option is not on the current menu.";
    }
}
