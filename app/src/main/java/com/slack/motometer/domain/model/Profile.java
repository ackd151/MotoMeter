package com.slack.motometer.domain.model;

import java.util.ArrayList;

// Profile is the main POJO representing a single dirtbike the user wishes to track
public class Profile {

    private String id; // Profile's id (primary key) in db
    private String year, make, model, hours; // Profile info
    private ArrayList<Task> tasks; // Profile's maintenance tasks
    private ArrayList<ChecklistItem> clItems; // Profile's checklist items

    public Profile(String id, String year, String make, String model, String hours,
                   ArrayList<Task> tasks, ArrayList<ChecklistItem> clItems) {
        this.id = id;
        this.year = year;
        this.make = make;
        this.model = model;
        this.hours = hours;
        this.tasks = tasks;
        this.clItems = clItems;
    }

    public Profile(String year, String make, String model, String hours) {
        this.year = year;
        this.make = make;
        this.model = model;
        this.hours = hours;
    }

    public Profile(String id, String year, String make, String model, String hours) {
        this.id = id;
        this.year = year;
        this.make = make;
        this.model = model;
        this.hours = hours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<ChecklistItem> getClItems() {
        return clItems;
    }

    public void setClItems(ArrayList<ChecklistItem> clItems) {
        this.clItems = clItems;
    }
}
