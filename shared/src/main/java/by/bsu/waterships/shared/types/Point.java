package by.bsu.waterships.shared.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public record Point(int x, int y) implements Serializable {
    public boolean isValid() {
        return x >= 0 && x <= 9 && y >= 0 && y <= 9;
    }

    public List<Point> getSurrounding() {
        List<Point> result = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if(dx == 0 && dy == 0) continue;
                Point np = new Point(x + dx, y + dy);
                if (np.isValid()) result.add(np);
            }
        }
        return result;
    }
}
