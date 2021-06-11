package org.example.save.app;

public class RequestModel {
    String requestId;
    String requestDisplayName;
    String requestType;
    boolean active;

    public RequestModel(String requestId, String requestDisplayName, String requestType, boolean active) {
        this.requestId = requestId;
        this.requestDisplayName = requestDisplayName;
        this.requestType = requestType;
        this.active = active;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestDisplayName() {
        return requestDisplayName;
    }

    public void setRequestDisplayName(String requestDisplayName) {
        this.requestDisplayName = requestDisplayName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
