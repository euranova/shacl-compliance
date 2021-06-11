package org.example.save.app;

import java.util.ArrayList;
import java.util.List;

import org.example.SAVERule;
import org.example.SHACLComplianceResult;

public class ResultModel {
    RequestModel request;
    PolicyModel policy;
    SHACLComplianceResult rawResult;
    String finalResultPermitted, finalResultProhibited;
    List<SAVERule> conformsTo, prohibitedBy;
    public ResultModel() {
        conformsTo = new ArrayList<>();
        prohibitedBy = new ArrayList<>();
    }

    public static String getRequestDisplayName(String requestPrefixedName) {
        return  requestPrefixedName.split(":")[1];
    }

    public RequestModel getRequest() {
        return request;
    }

    public void setRequest(RequestModel request) {
        this.request = request;
    }

    public PolicyModel getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyModel policy) {
        this.policy = policy;
    }

    public SHACLComplianceResult getRawResult() {
        return rawResult;
    }

    public void setRawResult(SHACLComplianceResult rawResult) {
        this.rawResult = rawResult;
    }

    public List<SAVERule> getConformsTo() {
        return conformsTo;
    }

    public void setConformsTo(List<SAVERule> conformsTo) {
        this.conformsTo = conformsTo;
    }

    public List<SAVERule> getProhibitedBy() {
        return prohibitedBy;
    }

    public void setProhibitedBy(List<SAVERule> prohibitedBy) {
        this.prohibitedBy = prohibitedBy;
    }

    public String getFinalResultPermitted() {
        return finalResultPermitted;
    }

    public void setFinalResultPermitted(String finalResultPermitted) {
        this.finalResultPermitted = finalResultPermitted;
    }

    public String getFinalResultProhibited() {
        return finalResultProhibited;
    }

    public void setFinalResultProhibited(String finalResultProhibited) {
        this.finalResultProhibited = finalResultProhibited;
    }
}
