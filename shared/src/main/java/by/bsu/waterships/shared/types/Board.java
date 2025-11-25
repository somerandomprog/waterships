package by.bsu.waterships.shared.types;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Board implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public record AttackResult(Point point, boolean missed, Ship destroyedShip,
                               List<Point> markMissed) implements Serializable {
    }

    public static class Ship implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        public int length;
        public Point start;
        public boolean vertical;
        public boolean destroyed = false;

        private final List<Point> points = new ArrayList<>();

        public List<Point> getPoints() {
            return points;
        }

        public Ship(Point start, int length, boolean vertical) {
            this.length = length;
            this.start = start;
            this.vertical = vertical;

            for (int i = 0; i < length; i++)
                points.add(new Point(vertical ? start.x() : start.x() + i, vertical ? start.y() + i : start.y()));
        }
    }

    private final List<Ship> ships = new ArrayList<>();
    private final List<Point> destroyed = new ArrayList<>();
    private final List<Point> missed = new ArrayList<>();

    public void addShip(Point start, int length, boolean vertical) {
        ships.add(new Ship(start, length, vertical));
    }

    @Override
    public String toString() {
        char[][] matrix = new char[12][12];

        matrix[0][0] = '+';
        matrix[0][11] = '+';
        matrix[11][0] = '+';
        matrix[11][11] = '+';
        for (int i = 1; i < 11; i++) {
            matrix[i][0] = '|';
            matrix[i][11] = '|';
            matrix[0][i] = '-';
            matrix[11][i] = '-';
        }

        for (int y = 0; y < 10; y++)
            for (int x = 0; x < 10; x++)
                matrix[y + 1][x + 1] = '.';

        for (Ship ship : ships) {
            for (Point p : ship.getPoints())
                matrix[p.y() + 1][p.x() + 1] = 'x';
        }

        StringBuilder sb = new StringBuilder();
        for (char[] row : matrix) {
            sb.append(row);
            sb.append('\n');
        }
        return sb.toString().trim();
    }

    public AttackResult attack(Point point) {
        Ship ship = ships
                .stream()
                .filter(s -> s.points.contains(point))
                .findFirst()
                .orElse(null);
        if (ship == null) {
            missed.add(point);
            return new AttackResult(point, true, null, Collections.singletonList(point));
        }

        destroyed.add(point);
        boolean destroyedCompletely = destroyed.containsAll(ship.points);
        if (!destroyedCompletely) return new AttackResult(point, false, null, List.of());

        HashSet<Point> surrounding = new HashSet<>();
        for (Point p : ship.points) surrounding.addAll(p.getSurrounding());

        List<Point> markMissed = surrounding.stream().filter(p -> !missed.contains(p) && !destroyed.contains(p)).toList();
        missed.addAll(markMissed);
        ship.destroyed = true;
        return new AttackResult(point, false, ship, markMissed);
    }
}
