package com.iot.airqualitymonitoring;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Measurement {
    private String timestamp;
    private Double value;

    public Measurement(String date, Double air_quality) {
        this.timestamp = date;
        this.value = air_quality;
    }

    public Double getAir_quality() {
        return value;
    }

    public void setAir_quality(Double air_quality) {
        this.value = air_quality;
    }

    public String getDate() {
        Timestamp stamp = new Timestamp(Long.valueOf(timestamp));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(date);
    }

    public void setDate(String date) {
        this.timestamp = date;
    }

    public int getStatusImg() {
        if (this.value < 200) {
            return R.drawable.great; //GREAT
        } else if (this.value >= 200 && this.value < 350) {
            return R.drawable.okay;
        } else if (this.value >= 350 && this.value < 500) {
            return R.drawable.beware; //OKAY
        } else if (this.value >= 500 && this.value < 600) {
            return R.drawable.unhealthy;
        } else if (this.value >= 600 && this.value < 700) {
            return R.drawable.vunhealthy;
        } else {
            return R.drawable.hazardous; //BAD
        }
    }

    public String getStatusTxt() {
        if (this.value < 200) {
            return "Great";
        } else if (this.value >= 200 && this.value < 350) {
            return "Okay";
        } else if (this.value >= 350 && this.value < 500) {
            return "Sensitive beware";
        } else if (this.value >= 500 && this.value < 600) {
            return "Unhealthy";
        } else if (this.value >= 600 && this.value < 700) {
            return "Very Unhealthy";
        } else {
            return "Hazardous";
        }
    }
}
