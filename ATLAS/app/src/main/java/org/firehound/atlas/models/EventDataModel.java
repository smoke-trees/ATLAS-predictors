package org.firehound.atlas.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventDataModel {

    @SerializedName("eventStatus")
    @Expose
    private Boolean eventStatus;
    @SerializedName("eventData")
    @Expose
    private String eventData;
    @SerializedName("result")
    @Expose
    private String result;

    /**
     * No args constructor for use in serialization
     */
    public EventDataModel() {
    }

    /**
     * @param result
     * @param eventData
     * @param eventStatus
     */
    public EventDataModel(Boolean eventStatus, String eventData, String result) {
        super();
        this.eventStatus = eventStatus;
        this.eventData = eventData;
        this.result = result;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}