package org.firehound.atlas.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceDataModel {

    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("data")
    @Expose
    private String data;

    /**
     * No args constructor for use in serialization
     */
    public DeviceDataModel() {
    }

    /**
     * @param result
     * @param data
     */
    public DeviceDataModel(String result, String data) {
        super();
        this.result = result;
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}