package org.firehound.atlas.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceDataArrayModel {

    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("data")
    @Expose
    private List<String> data = null;

    /**
     * No args constructor for use in serialization
     */
    public DeviceDataArrayModel() {
    }

    /**
     * @param result
     * @param data
     */
    public DeviceDataArrayModel(String result, List<String> data) {
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

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

}