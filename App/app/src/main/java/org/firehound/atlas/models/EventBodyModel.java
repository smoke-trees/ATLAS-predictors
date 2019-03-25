package org.firehound.atlas.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventBodyModel {

    @SerializedName("eventStatus")
    @Expose
    private Boolean eventStatus;
    @SerializedName("eventData")
    @Expose
    private String eventData;
    @SerializedName("deviceId")
    @Expose
    private String deviceId;

    /**
     * No args constructor for use in serialization
     */
    public EventBodyModel() {
    }

    /**
     * @param deviceId
     * @param eventData
     * @param eventStatus
     */
    public EventBodyModel(Boolean eventStatus, String eventData, String deviceId) {
        super();
        this.eventStatus = eventStatus;
        this.eventData = eventData;
        this.deviceId = deviceId;
    }

    public Boolean getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Boolean eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}