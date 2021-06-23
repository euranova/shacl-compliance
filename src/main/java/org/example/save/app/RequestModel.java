package org.example.save.app;

import org.example.SAVERule;

public class RequestModel {
    String requestId;
    String requestDisplayName;
    String requestType;
    boolean active;
    SAVERule saveRequest;

    public RequestModel(String requestId, String requestDisplayName, String requestType, boolean active, SAVERule saveRequest) {
        this.requestId = requestId;
        this.requestDisplayName = requestDisplayName;
        this.requestType = requestType;
        this.active = active;
        this.saveRequest = saveRequest;
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

    public SAVERule getSaveRequest() {
        return saveRequest;
    }

    public void setSaveRequest(SAVERule saveRequest) {
        this.saveRequest = saveRequest;
    }
}
