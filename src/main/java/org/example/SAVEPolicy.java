package org.example;

import java.util.ArrayList;
import java.util.List;


/**
 * Representation of the SAVE policy
 */
public class SAVEPolicy {
    /**
     * prefixed name of the individual representing the policy
     */
    private String name;
    /**
     * List of policy permission rules
     */
    private List<SAVERule> permissions;
    /**
     * List of policy prohibition rules
     */
    private List<SAVERule> prohibitions;
    /**
     * List of policy obligation rules (not used currently for compliance checking)
     */
    private List<SAVERule> obligations;
    /**
     * List of policy dispensation rules (exception to prohibition, i.e., permission)
     */
    private List<SAVERule> dispensations;


    /**
     * To create a policy, only name is needed
     * @param name desired name (prefixed)
     */
    public SAVEPolicy(String name) {
        this.name = name;
        this.permissions = new ArrayList<>();
        this.prohibitions = new ArrayList<>();
        this.obligations = new ArrayList<>();
        this.dispensations = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SAVERule> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<SAVERule> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(SAVERule permission) {
        this.permissions.add(permission);
    }

    public List<SAVERule> getProhibitions() {
        return prohibitions;
    }

    public void setProhibitions(List<SAVERule> prohibitions) {
        this.prohibitions = prohibitions;
    }

    public void addProhibition(SAVERule prohibition) {
        this.prohibitions.add(prohibition);
    }

    public List<SAVERule> getObligations() {
        return obligations;
    }

    public void setObligations(List<SAVERule> obligations) {
        this.obligations = obligations;
    }

    public void addObligation(SAVERule obligation) {
        this.obligations.add(obligation);
    }

    public List<SAVERule> getDispensations() {
        return dispensations;
    }

    public void setDispensations(List<SAVERule> dispensations) {
        this.dispensations = dispensations;
    }

    public void addDispensation(SAVERule dispensation) {
        this.dispensations.add(dispensation);
    }

    /**
     * Adds a rule to the policy, depending on its type
     * @param rule
     */
    public void addRule(SAVERule rule){
        if (rule.getType().endsWith("Permission")){
            addPermission(rule);
        } else if(rule.getType().endsWith("Prohibition")){
            addProhibition(rule);
        } else if(rule.getType().endsWith("Obligation")){
            addObligation(rule);
        } else if(rule.getType().endsWith("Dispensation")){
            addDispensation(rule);
        }
    }
}
