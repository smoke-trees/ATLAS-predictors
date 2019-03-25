package org.firehound.atlas.models;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BlockchainAPI {
    @GET("getDeviceCount")
    Call<DeviceDataModel> getDeviceCount();

    @GET("getDeviceAtIndex")
    Call<DeviceDataModel> getDeviceAtIndex(@Query("index") int index);

    @GET("getDeviceTimeStamp")
    Call<DeviceDataArrayModel> getDeviceTimeStamp(@Query("id") String id);

    @GET("getDeviceData")
    Call<DeviceDataModel> getDeviceData(@Query("deviceId") String deviceId, @Query("time") int time);

    @GET("getSavedEvent")
    Call<EventDataModel> getSavedEvent(@Query("accessHash") String accessHash);

    @POST("sendData")
    Call<DeviceDataModel> sendData(@Body EventBodyModel eventBodyModel);
}