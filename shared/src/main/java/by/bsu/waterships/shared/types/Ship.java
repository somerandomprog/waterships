package by.bsu.waterships.shared.types;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Ship")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"index", "length", "start", "vertical", "destroyed", "points"})
public class Ship {

    @XmlElement(required = true)
    public int index;

    @XmlElement(required = true)
    public int length;

    @XmlElement(required = true)
    public Point start;

    @XmlElement(required = true)
    public boolean vertical;

    @XmlElement(required = true)
    public boolean destroyed = false;

    @XmlElement(name = "ShipPoint")
    public final List<Point> points = new ArrayList<>();

    public Ship() {
    }

    public Ship(int index, Point start, int length, boolean vertical) {
        this.index = index;
        this.length = length;
        this.start = start;
        this.vertical = vertical;

        for (int i = 0; i < length; i++)
            points.add(new Point(vertical ? start.x() : start.x() + i, vertical ? start.y() + i : start.y()));
    }

    public List<Point> getPoints() {
        return points;
    }
}