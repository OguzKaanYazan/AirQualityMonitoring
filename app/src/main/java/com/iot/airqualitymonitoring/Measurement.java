package com.iot.airqualitymonitoring;

public class Measurement {
    private String date;
    private Double value;

    public Measurement(String date, Double air_quality) {
        this.date = date;
        this.value = air_quality;
    }

    public Double getAir_quality() {
        return value;
    }

    public void setAir_quality(Double air_quality) {
        this.value = air_quality;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatusImg() {
        if (this.value < 100) {
            return R.drawable.great; //GREAT
        } else if (this.value >= 100 && this.value < 200) {
            return R.drawable.okay; //OKAY
        } else {
            return R.drawable.unhealthy; //BAD
        }
    }

    public String getStatusTxt() {
        if (this.value < 100) {
            return "Great!";
        } else if (this.value >= 100 && this.value < 200) {
            return "Okay.";
        } else {
            return "Unhealthy!";
        }
    }
}
