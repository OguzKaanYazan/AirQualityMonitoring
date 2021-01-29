package com.iot.airqualitymonitoring;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface UbidotsApi {

    @Headers({"X-Auth-Token: BBFF-odArm0cDuLKxiP1UzVLc7v8DEvPFN8MukM04pVebQo2gHmT2jxcQRhb"})
    @GET("variables/5ffb67f51d847259aadf54e2/values")
    Call<Result> getMeasurements();
}
