package tanhuo;

import tanhuo.Grid.Point;

record TrailAttack(Point origin, Point target, Kind kind, String message) {
    enum Kind { ARROW, CREATURE }
}
