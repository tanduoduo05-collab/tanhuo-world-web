package tanhuo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

final class PixelArt {
    private PixelArt() {}

    static void tile(Graphics2D g, char tile, int x, int y, boolean outside, boolean lit, Game.PlotView plot) {
        int seed = x * 31 + y * 17;
        int[] grass = outside
            ? new int[]{0x629067, 0x68986a, 0x669367}
            : new int[]{0x92ae70, 0x98b574, 0x95b173};
        rect(g, grass[Math.floorMod(seed, 3)], 0, 0, 32, 32);
        for (int i = 0; i < 5; i++) {
            int a = Math.floorMod(seed + i * 11, 28) + 2;
            int b = Math.floorMod(seed * 3 + i * 7, 27) + 2;
            rect(g, outside ? 0x507f59 : 0x7f9e61, a, b, 2, 1);
            rect(g, outside ? 0x82ad79 : 0xafc888, a + 1, b - 1, 1, 1);
        }
        if (seed % 9 == 0 && tile == '.') {
            int petal = outside ? 0xf1b8ca : 0xf8e4a1;
            rect(g, 0x567a4d, 10, 13, 1, 3);
            rect(g, petal, 9, 9, 2, 5);
            rect(g, petal, 8, 10, 4, 3);
            rect(g, 0xecad65, 9, 11, 2, 1);
        }
        switch (tile) {
            case '#' -> {
                if (outside) tree(g, seed);
                else fence(g, y == 0 || y == 10);
            }
            case '-', '|' -> fence(g, tile == '-');
            case 'G' -> gate(g);
            case 'B' -> brazier(g, lit);
            case 'M' -> market(g);
            case 'W' -> armory(g);
            case 'R', 'r' -> bush(g, tile == 'R');
            case '^' -> thorns(g);
            case 'C' -> charcoal(g);
            case 'O' -> rock(g);
            case 'D' -> fruit(g, Item.BAMBOO_SHOOT, 6, 7);
            case 'V' -> fruit(g, Item.GRAPES, 6, 7);
            case 'Q' -> {
                oval(g, 0xfbe3ae, 3, 7, 26, 23);
                fruit(g, Item.BIRTHDAY_CAKE, 6, 5);
            }
            case 'K' -> kitchen(g);
            case 'L', 'F', 'E', 'U' -> counter(g);
            case 'J' -> jar(g);
            case 'S' -> {
                oval(g, 0xf9d5a4, 7, 6, 18, 20);
                fruit(g, Item.HEART_SEED, 7, 7);
            }
            default -> {
                if (plot != null) plot(g, plot);
            }
        }
    }

    static void person(Graphics2D g, boolean companion, boolean left, int step) {
        Graphics2D sprite = (Graphics2D) g.create();
        if (left) {
            sprite.translate(32, 0);
            sprite.scale(-1, 1);
        }

        int coat = companion ? 0xc58b24 : 0xb95846;
        int light = companion ? 0xffdb5c : 0xf29866;
        int stride = step % 2;

        oval(sprite, 0x4c6546, 5, 24, 23, 7);
        rect(sprite, 0x3b333b, 10, 24 - stride, 5, 5);
        rect(sprite, 0x3b333b, 18, 23 + stride, 5, 5);

        if (companion) {
            shape(sprite, 0x402733,
                7, 9, 10, 5, 22, 5, 26, 9, 25, 19,
                28, 26, 21, 27, 17, 23, 12, 27, 5, 25, 7, 19);
            rect(sprite, 0x774451, 6, 20, 2, 5);
            rect(sprite, 0x774451, 24, 19, 2, 5);
        }

        rect(sprite, coat, 8, 16, 17, 9);
        rect(sprite, light, 11, 17, 10, 6);
        rect(sprite, 0xeca982, 6, 18, 3, 5);
        rect(sprite, 0xeca982, 24, 18, 3, 5);
        rect(sprite, 0xf9c89c, 10, 8, 14, 9);
        rect(sprite, 0x493738, 14, 12, 2, 2);
        rect(sprite, 0x493738, 20, 12, 2, 2);
        rect(sprite, 0xe58675, 21, 15, 2, 1);

        if (companion) {
            shape(sprite, 0x402733,
                7, 13, 7, 7, 11, 3, 20, 3, 25, 6, 27, 12,
                26, 23, 22, 24, 23, 15, 22, 10, 18, 7,
                14, 8, 11, 11, 10, 22, 7, 23);
            rect(sprite, 0x88515b, 11, 5, 7, 2);
            rect(sprite, 0x69404b, 8, 13, 2, 7);
            rect(sprite, 0xffce65, 9, 9, 4, 2);
        } else {
            shape(sprite, 0x302832,
                7, 11, 7, 7, 10, 4, 14, 3, 16, 5, 15, 8,
                12, 9, 11, 12, 9, 13);
            shape(sprite, 0x302832,
                17, 5, 19, 3, 23, 4, 26, 7, 26, 11,
                24, 13, 22, 12, 22, 9, 19, 8);
            rect(sprite, 0x6b4d50, 8, 9, 3, 2);
            rect(sprite, 0x6b4d50, 23, 10, 2, 2);
            shape(sprite, 0x202f45,
                7, 8, 8, 5, 11, 2, 20, 2, 24, 4, 26, 8);
            shape(sprite, 0x426185,
                9, 7, 10, 4, 13, 3, 20, 3, 23, 5, 24, 7);
            rect(sprite, 0x6e8cac, 14, 3, 2, 3);
            rect(sprite, 0x202f45, 7, 7, 19, 2);
            shape(sprite, 0x202f45,
                19, 7, 26, 7, 30, 9, 28, 10, 20, 9);
            rect(sprite, 0x6e8cac, 24, 8, 4, 1);
        }

        rect(sprite, companion ? 0xffefac : 0xf7d28a, 14, 18, 4, 2);
        sprite.dispose();
    }

    static void fruit(Graphics2D graphics, Item item, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(x, y);
        switch (item) {
            case WATERMELON -> {
                oval(g, 0x274e42, 0, 4, 19, 12);
                oval(g, 0x55a658, 1, 4, 17, 10);
                rect(g, 0x9bd465, 4, 6, 11, 2);
                rect(g, 0x327d49, 5, 9, 2, 5);
                rect(g, 0x327d49, 11, 8, 2, 6);
            }
            case STRAWBERRY -> {
                shape(g, 0xd7484c, 3, 6, 16, 6, 17, 10, 11, 17, 8, 17, 3, 11);
                shape(g, 0x4e944c, 4, 5, 9, 6, 10, 3, 12, 6, 16, 5, 12, 9, 8, 9);
                rect(g, 0xffc47a, 7, 10, 1, 1);
                rect(g, 0xffc47a, 12, 10, 1, 1);
                rect(g, 0xffc47a, 10, 13, 1, 1);
            }
            case GRAPES -> {
                oval(g, 0x6e4c87, 4, 5, 7, 7);
                oval(g, 0xa374b5, 9, 5, 7, 7);
                oval(g, 0x875ca3, 6, 9, 8, 8);
                oval(g, 0x5c477b, 8, 13, 5, 5);
                rect(g, 0x638948, 9, 3, 5, 2);
            }
            case ORANGE -> {
                oval(g, 0xcd652d, 3, 4, 14, 14);
                oval(g, 0xf6a33e, 3, 4, 12, 11);
                rect(g, 0xffd773, 6, 6, 3, 2);
                rect(g, 0x4d8753, 10, 3, 5, 2);
            }
            case HEART, HEART_SEED -> {
                shape(g, 0xb74a6e, 3, 6, 7, 4, 10, 6, 13, 4, 17, 6, 17, 10, 10, 17, 3, 10);
                shape(g, 0xef7993, 4, 6, 7, 5, 10, 8, 13, 5, 16, 6, 16, 9, 10, 15, 4, 9);
                rect(g, 0xffc2cc, 6, 6, 2, 2);
            }
            case CHARCOAL -> {
                shape(g, 0x413c43, 1, 12, 4, 6, 10, 5, 13, 11, 10, 16, 4, 16);
                shape(g, 0x615764, 8, 13, 12, 8, 17, 8, 20, 14, 17, 17, 10, 17);
                rect(g, 0x8a7981, 5, 7, 4, 2);
            }
            case BAMBOO_SHOOT, SOUR_BAMBOO -> {
                shape(g, 0x82603c, 2, 15, 9, 1, 13, 4, 18, 15, 14, 18, 6, 18);
                shape(g, item == Item.BAMBOO_SHOOT ? 0x91ad57 : 0xe3c77f,
                    4, 14, 10, 3, 16, 14, 13, 16, 7, 16);
                rect(g, 0xece1a0, 8, 10, 6, 2);
            }
            case EGG, FRIED_EGG -> {
                oval(g, 0xbb905d, 2, 5, 16, 13);
                oval(g, 0xfff0cf, 3, 4, 14, 12);
                if (item == Item.FRIED_EGG) oval(g, 0xe9a83b, 7, 7, 7, 7);
            }
            case MILK -> {
                rect(g, 0x6287a2, 6, 5, 9, 12);
                rect(g, 0xeaf3e2, 7, 6, 7, 10);
                rect(g, 0x699ab7, 8, 3, 5, 3);
            }
            case RICE_NOODLES, LUOSIFEN, EGG_LUOSIFEN -> {
                oval(g, 0x694449, 1, 5, 19, 14);
                oval(g, item == Item.RICE_NOODLES ? 0xf4e0ac : 0xd2774d, 2, 5, 17, 9);
                rect(g, 0xf8dd97, 5, 8, 10, 1);
                rect(g, 0xf8dd97, 7, 10, 7, 1);
                if (item == Item.EGG_LUOSIFEN) {
                    oval(g, 0xffefd2, 10, 6, 7, 6);
                    oval(g, 0xffc34d, 12, 7, 3, 3);
                }
            }
            case BIRTHDAY_CAKE -> birthdayCake(g);
            case RAW_BEEF, BEEF_JERKY, PAN_SEARED_BEEF -> {
                if (item == Item.PAN_SEARED_BEEF) oval(g, 0xf1d9ac, 0, 3, 21, 16);
                beef(g, 2, 4, item != Item.RAW_BEEF);
            }
            default -> {
                oval(g, 0xc38353, 5, 4, 10, 13);
                oval(g, 0xf8d391, 6, 4, 7, 10);
                rect(g, 0x785946, 9, 7, 2, 5);
            }
        }
        g.dispose();
    }

    private static void plot(Graphics2D g, Game.PlotView plot) {
        rect(g, 0x6d4f42, 2, 3, 28, 27);
        rect(g, plot.frozen() ? 0xa2b6be : plot.watered() ? 0x765548 : 0xa77453, 3, 4, 26, 23);
        for (int row = 7; row < 25; row += 6) rect(g, 0x906248, 5, row, 22, 2);
        if (plot.frozen()) {
            shape(g, 0x5c7286, 15, 23, 9, 17, 7, 15, 14, 16, 18, 8, 22, 10, 19, 18, 17, 24);
            rect(g, 0xe4f6ff, 9, 7, 4, 2);
            rect(g, 0xe4f6ff, 21, 20, 4, 2);
            return;
        }
        if (plot.fruit() == null) return;
        shape(g, 0x3f714b, 15, 23, 9, 17, 9, 12, 15, 15, 18, 10, 22, 11, 21, 17, 16, 23);
        rect(g, 0x92b759, 15, 14, 2, 7);
        if (plot.mature()) fruit(g, plot.fruit(), 6, 6);
    }

    private static void tree(Graphics2D g, int seed) {
        rect(g, 0x554d3b, 13, 21, 6, 11);
        shape(g, 0x325b49, 1, 25, 4, 11, 11, 5, 15, 0, 21, 3, 26, 9, 31, 22, 27, 28, 8, 28);
        shape(g, seed % 2 == 0 ? 0x527e53 : 0x4d7650, 3, 21, 8, 10, 16, 3, 23, 9, 28, 20, 22, 24, 9, 24);
        rect(g, 0x6e975d, 12, 10, 8, 3);
        rect(g, 0x618954, 7, 17, 6, 2);
    }

    private static void fence(Graphics2D g, boolean horizontal) {
        if (horizontal) {
            rect(g, 0x724f37, 0, 13, 32, 12);
            rect(g, 0xc8985e, 0, 13, 32, 4);
            rect(g, 0xad784a, 0, 21, 32, 3);
        } else {
            rect(g, 0x8b603d, 12, 0, 8, 32);
            rect(g, 0xb78551, 12, 0, 3, 32);
        }
        rect(g, 0x78563c, 12, 8, 8, 20);
        rect(g, 0xe0b376, 12, 8, 8, 4);
        rect(g, 0xc38b55, 13, 13, 3, 12);
    }

    private static void gate(Graphics2D g) {
        oval(g, 0xc5aa74, 1, 16, 30, 15);
        rect(g, 0x6e493d, 5, 4, 4, 24);
        rect(g, 0x6e493d, 23, 4, 4, 24);
        rect(g, 0x9c6847, 7, 4, 18, 5);
        rect(g, 0xe3ba74, 4, 3, 6, 3);
        rect(g, 0xe3ba74, 22, 3, 6, 3);
        rect(g, 0xf4d28b, 13, 5, 6, 2);
    }

    private static void market(Graphics2D g) {
        rect(g, 0x78523f, 4, 7, 24, 23);
        for (int x = 2; x < 30; x += 4) {
            rect(g, x % 8 == 2 ? 0xb95846 : 0xf9df9f, x, 4, 4, 10);
        }
        rect(g, 0x4a3939, 6, 14, 20, 10);
        rect(g, 0xf0ba80, 12, 15, 8, 7);
        rect(g, 0x556c4c, 11, 13, 10, 3);
        rect(g, 0xb68652, 3, 24, 26, 5);
        rect(g, 0xe5b57d, 3, 24, 26, 2);
        rect(g, 0x624238, 3, 1, 26, 8);
        rect(g, 0xefca8f, 4, 2, 24, 6);
        g.setFont(PixelFont.size(8));
        g.setColor(new Color(0x473039));
        String sign = "STORE";
        g.drawString(sign, (32 - g.getFontMetrics().stringWidth(sign)) / 2, 8);
    }

    private static void armory(Graphics2D g) {
        rect(g, 0x524056, 2, 6, 28, 23);
        rect(g, 0x657795, 2, 5, 28, 7);
        rect(g, 0x8ca6ac, 4, 10, 24, 3);
        rect(g, 0x323446, 5, 13, 22, 14);
        rect(g, 0x9c7864, 2, 26, 28, 3);
        Graphics2D icon = (Graphics2D) g.create();
        icon.translate(12, 23);
        icon.rotate(-Math.PI / 2);
        icon.scale(0.65, 0.65);
        weapon(icon, Weapon.KNIFE);
        icon.dispose();
        icon = (Graphics2D) g.create();
        icon.translate(21, 23);
        icon.rotate(-Math.PI / 2);
        icon.scale(0.6, 0.6);
        weapon(icon, Weapon.AXE);
        icon.dispose();
        rect(g, 0x4b384b, 1, 0, 30, 9);
        rect(g, 0xeac789, 2, 1, 28, 7);
        g.setFont(PixelFont.size(8));
        g.setColor(new Color(0x3f3449));
        String sign = "ARMORY";
        g.drawString(sign, (32 - g.getFontMetrics().stringWidth(sign)) / 2, 7);
    }

    static void weapon(Graphics2D g, Weapon weapon) {
        rect(g, 0x433543, -3, -2, 16, 4);
        rect(g, 0xae764c, -2, -1, 14, 2);
        if (weapon == Weapon.KNIFE) {
            shape(g, 0x424659, 4, -4, 12, -4, 17, -2, 13, 2, 4, 2);
            shape(g, 0xe6eef2, 6, -3, 12, -3, 15, -2, 12, 0, 6, 0);
            rect(g, 0xb98a57, 3, -3, 2, 6);
        } else {
            shape(g, 0x414754, 7, -3, 9, -6, 12, -7, 16, -7, 18, -3, 16, 1, 12, 2, 9, 1);
            shape(g, 0xb2c3ce, 9, -3, 11, -5, 15, -6, 16, -3, 15, -1, 11, 0);
            rect(g, 0xe6eef2, 14, -5, 2, 3);
        }
    }

    private static void birthdayCake(Graphics2D g) {
        rect(g, 0x773852, 1, 8, 18, 10);
        rect(g, 0xf1a0aa, 2, 9, 16, 7);
        rect(g, 0xffddbe, 2, 11, 16, 2);
        rect(g, 0xd96387, 2, 15, 16, 2);
        rect(g, 0xfff2cf, 2, 8, 16, 3);
        rect(g, 0xfff2cf, 4, 10, 2, 3);
        rect(g, 0xfff2cf, 13, 10, 2, 3);
        rect(g, 0xa8e3dc, 0, 18, 20, 2);
        rect(g, 0x985e9c, 9, 4, 2, 4);
        rect(g, 0xe8754b, 9, 1, 2, 3);
        rect(g, 0xffdb76, 10, 0, 1, 3);
        rect(g, 0xd45a75, 4, 8, 2, 1);
        rect(g, 0xd45a75, 14, 8, 2, 1);
    }

    private static void brazier(Graphics2D g, boolean lit) {
        if (lit) oval(g, 0xdac072, 1, 3, 30, 28);
        oval(g, 0x7b826a, 3, 12, 26, 17);
        oval(g, 0x50474b, 6, 14, 20, 12);
        rect(g, 0x8d7a64, 8, 20, 16, 2);
        if (lit) {
            shape(g, 0xc75638, 9, 21, 10, 11, 13, 14, 17, 4, 22, 13, 23, 21, 17, 24);
            shape(g, 0xffa749, 12, 21, 16, 12, 17, 16, 20, 14, 21, 21, 17, 23);
            rect(g, 0xffe69a, 16, 18, 3, 4);
        }
    }

    private static void bush(Graphics2D g, boolean occupied) {
        oval(g, 0x345c4d, 2, 10, 28, 20);
        oval(g, 0x477653, 4, 8, 24, 18);
        oval(g, 0x6d975a, 8, 8, 14, 10);
        if (occupied) {
            rect(g, 0xf8d590, 12, 17, 2, 2);
            rect(g, 0xf8d590, 18, 17, 2, 2);
        }
    }

    static void creature(Graphics2D g) {
        shape(g, 0x422e51, -12, 7, -12, -5, -9, -13, -4, -8, 4, -8, 9, -13,
            12, -5, 12, 7, 7, 12, -7, 12);
        shape(g, 0x9364a1, -10, 4, -10, -4, -8, -9, -3, -6, 5, -6, 8, -9,
            10, -4, 10, 5, 6, 9, -6, 9);
        rect(g, 0xffd790, -6, -3, 4, 3);
        rect(g, 0xffd790, 2, -3, 4, 3);
        rect(g, 0x422e51, -4, 3, 8, 3);
        rect(g, 0xffecca, -4, 3, 2, 2);
        rect(g, 0xffecca, 2, 3, 2, 2);
    }

    private static void thorns(Graphics2D g) {
        shape(g, 0x694c52, 2, 25, 9, 13, 6, 6, 14, 11, 19, 3, 20, 15, 29, 10, 25, 23, 15, 28);
        shape(g, 0x927369, 5, 24, 13, 13, 18, 12, 16, 20, 23, 18, 20, 25, 11, 25);
    }

    private static void charcoal(Graphics2D g) {
        oval(g, 0xc8aa77, 4, 11, 25, 18);
        fruit(g, Item.CHARCOAL, 6, 6);
    }

    private static void rock(Graphics2D g) {
        oval(g, 0x4b6755, 3, 19, 26, 9);
        shape(g, 0x514e58, 3, 21, 8, 11, 14, 7, 24, 10, 29, 21, 23, 26, 8, 26);
        shape(g, 0x93919a, 6, 19, 11, 11, 17, 9, 23, 12, 26, 19, 19, 22, 9, 22);
        rect(g, 0xb8b4ad, 12, 12, 8, 2);
    }

    static void servedMeal(Graphics2D g, Item meal) {
        rect(g, 0xf9dda4, 17, 3, 2, 4);
        rect(g, 0xffebbe, 22, 1, 2, 5);
        fruit(g, meal, 6, 6);
    }

    static void arrow(Graphics2D g) {
        rect(g, 0x553941, -16, -2, 19, 4);
        rect(g, 0xe9bc77, -14, -1, 16, 2);
        shape(g, 0xf8ead4, -18, -4, -12, -2, -10, 0, -12, 2, -18, 4, -15, 0);
        shape(g, 0x453a48, -3, -5, 7, 0, -3, 5, -1, 0);
        shape(g, 0xdde8e3, -2, -3, 5, 0, -2, 3, 0, 0);
    }

    private static void kitchen(Graphics2D g) {
        rect(g, 0x584042, 3, 8, 26, 21);
        rect(g, 0xb47e59, 4, 14, 24, 13);
        rect(g, 0xe0b77c, 3, 7, 26, 6);
        oval(g, 0x423941, 6, 7, 12, 7);
        rect(g, 0x423941, 15, 9, 9, 2);
        rect(g, 0x6c4c41, 10, 17, 12, 8);
        rect(g, 0xf7aa55, 13, 20, 6, 3);
    }

    private static void counter(Graphics2D g) {
        rect(g, 0x584042, 0, 8, 32, 21);
        rect(g, 0xb47e59, 0, 13, 32, 14);
        rect(g, 0xe0b77c, 0, 7, 32, 12);
        rect(g, 0xa37352, 30, 14, 2, 13);
        oval(g, 0xc69967, 5, 9, 22, 12);
    }

    static void dryingRack(Graphics2D g, Item hanging) {
        oval(g, 0x667951, 3, 23, 27, 6);
        rect(g, 0x694637, 4, 5, 4, 23);
        rect(g, 0x694637, 24, 5, 4, 23);
        rect(g, 0xa8754f, 5, 6, 2, 20);
        rect(g, 0xa8754f, 25, 6, 2, 20);
        rect(g, 0xcd9b63, 3, 5, 26, 4);
        rect(g, 0x9d724e, 6, 10, 20, 2);
        if (hanging != null) {
            rect(g, 0xeed0a0, 15, 9, 2, 6);
            beef(g, 8, 13, hanging == Item.BEEF_JERKY);
        }
    }

    private static void beef(Graphics2D g, int x, int y, boolean dried) {
        Graphics2D meat = (Graphics2D) g.create();
        meat.translate(x, y);
        shape(meat, dried ? 0x4d2b2b : 0x9c383e,
            1, 7, 4, 2, 12, 0, 16, 3, 17, 9, 12, 13, 5, 13);
        shape(meat, dried ? 0x814332 : 0xea6b6b, 3, 7, 6, 3, 12, 2, 15, 4, 15, 8, 11, 11, 5, 11);
        rect(meat, dried ? 0xae734f : 0xf8bd9d, 7, 5, 6, 2);
        meat.dispose();
    }

    private static void jar(Graphics2D g) {
        oval(g, 0x654844, 6, 9, 20, 21);
        oval(g, 0xb07351, 8, 11, 16, 17);
        rect(g, 0xd89763, 10, 14, 3, 9);
        rect(g, 0x6f4c41, 7, 8, 18, 4);
        rect(g, 0xd1a06b, 9, 6, 14, 3);
        rect(g, 0xe4bf82, 13, 17, 7, 5);
    }

    static void chicken(Graphics2D g) {
        oval(g, 0x54734d, 6, 23, 22, 5);
        oval(g, 0xb8a89b, 6, 13, 19, 12);
        oval(g, 0xffedd2, 7, 12, 17, 10);
        rect(g, 0xffedd2, 18, 8, 8, 10);
        rect(g, 0xd65e51, 19, 6, 5, 3);
        rect(g, 0xe9a146, 25, 12, 4, 3);
        rect(g, 0x3c3439, 22, 10, 2, 2);
        rect(g, 0xe9a146, 13, 23, 3, 4);
        rect(g, 0xe9a146, 19, 23, 3, 4);
    }

    static void cow(Graphics2D g) {
        oval(g, 0x54734d, 3, 24, 27, 5);
        rect(g, 0x53464a, 6, 20, 4, 7);
        rect(g, 0x53464a, 18, 20, 4, 7);
        rect(g, 0xf3e1c8, 4, 10, 21, 13);
        rect(g, 0x5c4b53, 7, 11, 7, 7);
        rect(g, 0x5c4b53, 16, 17, 6, 5);
        rect(g, 0xf3e1c8, 20, 9, 9, 13);
        rect(g, 0xe6a58f, 21, 16, 9, 6);
        rect(g, 0x4a3d44, 24, 12, 2, 2);
    }

    static void beefCow(Graphics2D g) {
        oval(g, 0x54734d, 2, 24, 28, 5);
        rect(g, 0x523832, 5, 20, 5, 7);
        rect(g, 0x523832, 18, 20, 5, 7);
        rect(g, 0x794838, 3, 10, 23, 13);
        rect(g, 0xb8754b, 5, 10, 15, 8);
        rect(g, 0xe9c58f, 19, 7, 3, 4);
        rect(g, 0xe9c58f, 27, 7, 3, 4);
        rect(g, 0x955e43, 20, 9, 10, 14);
        rect(g, 0xd0a178, 21, 17, 9, 5);
        rect(g, 0x362c2c, 25, 13, 2, 2);
    }

    private static void rect(Graphics2D g, int color, int x, int y, int width, int height) {
        g.setColor(new Color(color));
        g.fillRect(x, y, width, height);
    }

    private static void oval(Graphics2D g, int color, int x, int y, int width, int height) {
        g.setColor(new Color(color));
        g.fillOval(x, y, width, height);
    }

    private static void shape(Graphics2D g, int color, int... points) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < points.length; i += 2) polygon.addPoint(points[i], points[i + 1]);
        g.setColor(new Color(color));
        g.fillPolygon(polygon);
    }
}
