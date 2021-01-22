package com.iot.airqualitymonitoring;

public class Measurement {
    private String date;
    private int air_quality;

    public Measurement(String date, int air_quality){
        this.date = date;
        this.air_quality = air_quality;
    }

    public int getAir_quality() {
        return air_quality;
    }

    public void setAir_quality(int air_quality) {
        this.air_quality = air_quality;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatusImg(){
        if(this.air_quality < 100){
            return R.drawable.great; //GREAT
        }else if(this.air_quality >= 100 && this.air_quality < 200){
            return R.drawable.okay; //OKAY
        }else{
            return R.drawable.unhealthy; //BAD
        }
    }

    public String getStatusTxt(){
        if(this.air_quality < 100){
            return "Great!";
        }else if(this.air_quality >= 100 && this.air_quality < 200){
            return "Okay.";
        }else{
            return "Unhealthy!";
        }
    }
}
