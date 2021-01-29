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
        if (this.value < 250) {
            return R.drawable.great; //GREAT
        } else if (this.value >= 250 && this.value<350){
            return  R.drawable.okay;
        }
        else if (this.value >= 350 && this.value < 400) {
            return R.drawable.beware; //OKAY
        }
        else if (this.value>=400 && this.value<500){
            return R.drawable.unhealthy;
        }
        else if(this.value>=500 && this.value<650){
            return R.drawable.vunhealthy;
        }
        else {
            return R.drawable.hazardous; //BAD
        }
    }

    public String getStatusTxt() {
        if (this.value < 250) {
            return "Great!";
        } else if (this.value >= 250 && this.value < 350) {
            return "Okay.";
        } else if (this.value>=350&&this.value<400){
            return "Sensitive beware.";
        }
        else if (this.value>=400 && this.value<500){
            return "Unhealthy";
        }
        else if (this.value>=500 && this.value<650){
            return "Very Unhealthy.";
        }
            else {
            return "HAZARDOUS";
        }
    }
}
