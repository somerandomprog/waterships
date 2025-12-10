package by.bsu.waterships.shared.types;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Ship")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"start", "points"})
public class Ship {

    @XmlAttribute(required = true)
    public int index;

    @XmlAttribute(required = true)
    public int length;

    @XmlAttribute(required = true)
    public boolean vertical;

    @XmlAttribute(required = true)
    public boolean destroyed = false;

    @XmlElement(required = true)
    public Point start;

    @XmlElementWrapper(name = "points") // Creates the <Points> container
    @XmlElement(name = "ShipPoint")     // Creates <ShipPoint> items inside
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