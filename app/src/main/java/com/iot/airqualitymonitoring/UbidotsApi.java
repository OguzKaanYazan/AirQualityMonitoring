package com.iot.airqualitymonitoring;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UbidotsApi {

    @GET("variables/{id}/values")
    Call<Result> getMeasurements(@Header("X-Auth-Token") String token, @Path("id") String id);

    @POST("devices/air-quality-monitoring/airQuality/values")
    Call<Measurement> insertMeasurement(@Header("X-Auth-Token") String token, @Body Measurement measurement);

    @POST("auth/token/")
    Call<Object> auth(@Header("x-ubidots-apikey") String apikey);
}
