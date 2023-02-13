package com.capstone.foodify.Model.DistrictWard;

public class DisctrictWardResponse {
    private int exitcode;
    private Data data;
    private String message;

    public int getExitcode() {
        return exitcode;
    }

    public void setExitcode(int exitcode) {
        this.exitcode = exitcode;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
