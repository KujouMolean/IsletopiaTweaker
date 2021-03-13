package com.molean.isletopia.message.obj;

public class TeleportResponse {
    private String target;
    private String response;
    private String responseMessage;

    public TeleportResponse() {
    }

    public TeleportResponse(String target, String response, String responseMessage) {
        this.target = target;
        this.response = response;
        this.responseMessage = responseMessage;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
