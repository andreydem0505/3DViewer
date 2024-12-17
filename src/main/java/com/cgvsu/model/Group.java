package com.cgvsu.model;

import java.util.ArrayList;

public class Group {
    private final String name;
    private final ArrayList<Polygon> polygons = new ArrayList<>();

    public Group(String name) {
        this.name = name;
    }

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public int getPolygonsSize() {
        return polygons.size();
    }
}