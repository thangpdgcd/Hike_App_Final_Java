package com.example.hikeapplication.Observation;

public class Observation {
    private int id;
    private String observation;
    private String time;
    private String comment;

    public Observation() {

    }

    public Observation(
            int id,
            String observation,
            String time,
            String comment
    ) {
        this.id = id;
        this.observation = observation;
        this.time = time;
        this.comment = comment;
    }

    public int getId() {return id;}
    public void setId(int id) { this.id = id;}
    public String getObservation() {return observation;}
    public void setObservation(String observation) {this.observation = observation;}
    public String getTime() {return time;}

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {return comment;}

    public void setComment(String comment) {
        this.comment = comment;
    }
}
