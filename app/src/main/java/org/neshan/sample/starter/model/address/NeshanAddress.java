package org.neshan.sample.starter.model.address;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NeshanAddress implements Serializable {

    // when address is found
    @SerializedName("neighbourhood")
    private String neighbourhood;
    @SerializedName("formatted_address")
    private String address;
    @SerializedName("municipality_zone")
    private String municipality_zone;
    @SerializedName("in_traffic_zone")
    private Boolean in_traffic_zone;
    @SerializedName("in_odd_even_zone")
    private Boolean in_odd_even_zone;
    @SerializedName("city")
    private String city;
    @SerializedName("state")
    private String state;

    // when address is not found
    @SerializedName("status")
    private String status;
    @SerializedName("code")
    private Integer code;
    @SerializedName("message")
    private String message;


    public NeshanAddress(String neighbourhood, String address, String municipality_zone, Boolean in_traffic_zone, Boolean in_odd_even_zone, String city, String state) {
        this.neighbourhood = neighbourhood;
        this.address = address;
        this.municipality_zone = municipality_zone;
        this.in_traffic_zone = in_traffic_zone;
        this.in_odd_even_zone = in_odd_even_zone;
        this.city = city;
        this.state = state;
    }

    public NeshanAddress(String status, Integer code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMunicipality_zone() {
        return municipality_zone;
    }

    public void setMunicipality_zone(String municipality_zone) {
        this.municipality_zone = municipality_zone;
    }

    public Boolean getIn_traffic_zone() {
        return in_traffic_zone;
    }

    public void setIn_traffic_zone(Boolean in_traffic_zone) {
        this.in_traffic_zone = in_traffic_zone;
    }

    public Boolean getIn_odd_even_zone() {
        return in_odd_even_zone;
    }

    public void setIn_odd_even_zone(Boolean in_odd_even_zone) {
        this.in_odd_even_zone = in_odd_even_zone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}