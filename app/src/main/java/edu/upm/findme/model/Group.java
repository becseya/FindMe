package edu.upm.findme.model;

public class Group {
    int id;
    int ownerId;
    String name;

    public Group(int id, int ownerId, String name) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }
}
