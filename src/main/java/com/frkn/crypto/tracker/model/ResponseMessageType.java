package com.frkn.crypto.tracker.model;

public enum ResponseMessageType {
    DELETED_SUCCESSFULLY("Record Deleted Successfully"),
    RECORD_DOES_NOT_EXIST("Record With The Given Id Does Not Exist"),
    UPDATED_SUCCESSFULLY("Record Updated Successfully");

    private String message;

    ResponseMessageType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
