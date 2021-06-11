package org.example.save.app;


import java.util.List;

public class PolicyModel {
    String policyId;
    String policyDisplayName;
    String policyPrefixedName;
    boolean active;
    List<String> ruleNames;

    public PolicyModel(String policyId, String policyDisplayName, String policyPrefixedName, boolean active, List<String> ruleNames) {
        this.policyId = policyId;
        this.policyDisplayName = policyDisplayName;
        this.policyPrefixedName = policyPrefixedName;
        this.active = active;
        this.ruleNames = ruleNames;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getPolicyDisplayName() {
        return policyDisplayName;
    }

    public void setPolicyDisplayName(String policyDisplayName) {
        this.policyDisplayName = policyDisplayName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getRuleNames() {
        return ruleNames;
    }

    public void setRuleNames(List<String> ruleNames) {
        this.ruleNames = ruleNames;
    }

    public String getPolicyPrefixedName() {
        return policyPrefixedName;
    }

    public void setPolicyPrefixedName(String policyPrefixedName) {
        this.policyPrefixedName = policyPrefixedName;
    }


}
