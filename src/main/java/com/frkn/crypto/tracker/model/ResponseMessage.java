package com.frkn.crypto.tracker.model;

import org.json.JSONObject;
import org.json.JSONPropertyName;

public class ResponseMessage {

    private String message;

    public ResponseMessage(Object value) {
        this.message = String.valueOf(value);
    }

    public ResponseMessage(String value) {
        this.message = value;
    }

    public ResponseMessage() {
    }

    public ResponseMessage(ResponseMessageType responseMessageType) {
        this.message = responseMessageType.getMessage();
    }

    @JSONPropertyName("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ResponseMessage buildResponseMessage(ResponseMessageType responseMessageType){
        return new ResponseMessage(responseMessageType);
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }
}
