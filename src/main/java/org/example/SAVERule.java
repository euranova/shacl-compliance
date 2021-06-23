package org.example;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

/**
 * Representation of SAVE rule or request
 * All concepts, all values are prefixed
 */
public class SAVERule {

    /**
     * prefixed name of the individual representing the rule
     */
    private String name;
    /**
     * prefixed type of the individual representing the rule, such as orcp:Permission
     */
    private String type;

    /**
     * prefixed map of values of save:action property;
     * key - prefixed individual, value - prefixed class of the individual
     */
    private Map<String, String>  actions;

    /**
     * prefixed map of values of save:data property;
     * key - prefixed individual, value - prefixed class of the individual
     */
    private Map<String, String> data;
    /**
     * prefixed map of values of save:action property;
     * key - prefixed individual, value - prefixed class of the individual
     */
    private Map<String, String>  purposes;
    /**
     * prefixed map of values of save:legalBasis property;
     * key - prefixed individual, value - prefixed class of the individual
     */
    private Map<String, String>  legalBases;
    /**
     * prefixed map of values of save:hasTechnicalOrganisationlMeasures property;
     * key - prefixed individual, value - prefixed class of the individual
     */
    private Map<String, String>  measures;
    /**
     * prefixed map of values of save:controller property;
     * key - prefixed individual, value - prefixed class of the individual
     */
    private Map<String, String>  controllers;
    /**
     * prefixed map of values of save:processor property;
     * key - prefixed individual, value - prefixed class of the individual
     */
    private Map<String, String>  processors;
    /**
     * prefixed map of values of save:responsibleParty property;
     * key - prefixed individual, value - prefixed class of the individual
     */
    private Map<String, String>  responsibleParties;
    /**
     * prefixed map of values of save:hasDataSubject property;
     * key - prefixed individual, value - prefixed class of the individual
     */
    private Map<String, String>  dataSubjects;
    /**
     * prefixed map of values of save:sender property;
     * key - prefixed individual, value - prefixed class of the individual
     */
    private Map<String, String>  senders;
    /**
     * prefixed map of values of save:recipient property;
     * key - prefixed individual, value - prefixed class of the individual
     */
    private Map<String, String>  recipients;
    /**
     * For subrequests: parent rule
     */
    private SAVERule parentRule;
    /**
     * For parent rules: subrequests
     */
    private List<SAVERule> childRules;

    public SAVERule(String name, String type) {
        this.name = name;
        this.type = type;
        actions = new HashMap<>();
        data = new HashMap<>();
        purposes = new HashMap<>();
        legalBases = new HashMap<>();
        measures = new HashMap<>();
        controllers = new HashMap<>();
        processors = new HashMap<>();
        responsibleParties = new HashMap<>();
        dataSubjects = new HashMap<>();
        senders = new HashMap<>();
        recipients = new HashMap<>();
    }

    public SAVERule(SAVERule rule) {
        this.setName(rule.getName());
        this.setType(rule.getType());
        this.setActions(rule.getActions());
        this.setData(rule.getData());
        this.setPurposes(rule.getPurposes());
        this.setLegalBases(rule.getLegalBases());
        this.setMeasures(rule.getMeasures());
        this.setControllers(rule.getControllers());
        this.setProcessors(rule.getProcessors());
        this.setResponsibleParties(rule.getResponsibleParties());
        this.setDataSubjects(rule.getDataSubjects());
        this.setSenders(rule.getSenders());
        this.setRecipients(rule.getRecipients());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getActions() {
        return actions;
    }

    public void setActions(Map<String, String> actions) {
        this.actions = actions;
    }

    public void addAction(String action, String actionType) {
        this.actions.put(action, actionType);
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public void addData(String data, String dataType) {
        this.data.put(data, dataType);
    }

    public Map<String, String> getPurposes() {
        return purposes;
    }

    public void setPurposes(Map<String, String> purposes) {
        this.purposes = purposes;
    }

    public void addPurpose(String purpose, String purposeType) {
        this.purposes.put(purpose, purposeType);
    }

    public Map<String, String> getLegalBases() {
        return legalBases;
    }

    public void setLegalBases(Map<String, String> legalBases) {
        this.legalBases = legalBases;
    }

    public void addLegalBasis(String legalBasis, String legalBasisType) {
        this.legalBases.put(legalBasis, legalBasisType);
    }

    public Map<String, String> getMeasures() {
        return measures;
    }

    public void setMeasures(Map<String, String> measures) {
        this.measures = measures;
    }

    public void addMeasure(String measure, String measureType) {
        this.measures.put(measure, measureType);
    }

    public Map<String, String> getControllers() {
        return controllers;
    }

    public void setControllers(Map<String, String> controllers) {
        this.controllers = controllers;
    }

    public void addController(String controller, String controllerType) {
        this.controllers.put(controller, controllerType);
    }

    public Map<String, String> getProcessors() {
        return processors;
    }

    public void setProcessors(Map<String, String> processors) {
        this.processors = processors;
    }

    public void addProcessor(String processor, String processorType) {
        this.processors.put(processor, processorType);
    }

    public Map<String, String> getResponsibleParties() {
        return responsibleParties;
    }

    public void setResponsibleParties(Map<String, String> responsibleParties) {
        this.responsibleParties = responsibleParties;
    }

    public void addResponsibleParty(String responsibleParty, String responsiblePartyType) {
        this.responsibleParties.put(responsibleParty, responsiblePartyType);
    }

    public Map<String, String> getDataSubjects() {
        return dataSubjects;
    }

    public void setDataSubjects(Map<String, String> dataSubjects) {
        this.dataSubjects = dataSubjects;
    }

    public void addDataSubject(String dataSubject, String dataSubjectType) {
        this.dataSubjects.put(dataSubject, dataSubjectType);
    }

    public Map<String, String> getSenders() {
        return senders;
    }

    public void setSenders(Map<String, String> senders) {
        this.senders = senders;
    }

    public void addSender(String sender, String senderType) {
        this.senders.put(sender, senderType);
    }

    public Map<String, String> getRecipients() {
        return recipients;
    }

    public void setRecipients(Map<String, String> recipients) {
        this.recipients = recipients;
    }

    public void addRecipient(String recipient, String recipientType) {
        this.recipients.put(recipient, recipientType);
    }

    public SAVERule getParentRule() {
        return parentRule;
    }

    public void setParentRule(SAVERule parentRule) {
        this.parentRule = parentRule;
    }

    public List<SAVERule> getChildRules() {
        return childRules;
    }

    public void setChildRules(List<SAVERule> childRules) {
        this.childRules = childRules;
    }

    public void addChildRequest(SAVERule childRequest) {
        this.childRules.add(childRequest);
    }

    public void addProperty(String type, String value, String valueType){
        if (type.endsWith("data") || type.endsWith("dataIntersection")){
            addData(value, valueType);
        } else if(type.endsWith("action") || type.endsWith("actionIntersection")){
            addAction(value, valueType);
        } else if(type.endsWith("purpose") || type.endsWith("purposeIntersection")){
            addPurpose(value, valueType);
        } else if (type.endsWith("legalBasis") || type.endsWith("legalBasisIntersection")){
            addLegalBasis(value, valueType);
        } else if(type.endsWith("hasTechnicalOrganisationalMeasure") || type.endsWith("hasTechnicalOrganisationalMeasureIntersection")){
            addMeasure(value, valueType);
        } else if(type.endsWith("controller") || type.endsWith("controllerIntersection")){
            addController(value, valueType);
        } else if(type.endsWith("processor") || type.endsWith("processorIntersection")){
            addProcessor(value, valueType);
        } else if(type.endsWith("responsibleParty") || type.endsWith("responsiblePartyIntersection")){
            addResponsibleParty(value, valueType);
        } else if(type.endsWith("hasDataSubject") || type.endsWith("hasDataSubjectIntersection")){
            addDataSubject(value, valueType);
        } else if(type.endsWith("sender") || type.endsWith("senderIntersection")){
            addSender(value, valueType);
        } else if(type.endsWith("recipient") || type.endsWith("recipientIntersection")){
            addRecipient(value, valueType);
        } else {
//            System.out.println("Auxiliary property");
        }
    }

    public void addPropertyConditional(SAVERule conditionRule, String type, String value, String valueType){
        if (!conditionRule.getData().isEmpty() && (type.endsWith("data") || type.endsWith("dataIntersection"))){
            addData(value, valueType);
        } else if(!conditionRule.getActions().isEmpty() && (type.endsWith("action") || type.endsWith("actionIntersection"))){
            addAction(value, valueType);
        } else if(!conditionRule.getPurposes().isEmpty() && (type.endsWith("purpose") || type.endsWith("purposeIntersection"))){
            addPurpose(value, valueType);
        } else if (!conditionRule.getLegalBases().isEmpty() && (type.endsWith("legalBasis") || type.endsWith("legalBasisIntersection"))){
            addLegalBasis(value, valueType);
        } else if(!conditionRule.getMeasures().isEmpty() && (type.endsWith("hasTechnicalOrganisationalMeasure") || type.endsWith("hasTechnicalOrganisationalMeasureIntersection"))){
            addMeasure(value, valueType);
        } else if(!conditionRule.getControllers().isEmpty() && (type.endsWith("controller") || type.endsWith("controllerIntersection"))){
            addController(value, valueType);
        } else if(!conditionRule.getProcessors().isEmpty() && (type.endsWith("processor") || type.endsWith("processorIntersection"))){
            addProcessor(value, valueType);
        } else if(!conditionRule.getResponsibleParties().isEmpty() && (type.endsWith("responsibleParty") || type.endsWith("responsiblePartyIntersection"))){
            addResponsibleParty(value, valueType);
        } else if(!conditionRule.getDataSubjects().isEmpty() && (type.endsWith("hasDataSubject") || type.endsWith("hasDataSubjectIntersection"))){
            addDataSubject(value, valueType);
        } else if(!conditionRule.getSenders().isEmpty() && (type.endsWith("sender") || type.endsWith("senderIntersection"))){
            addSender(value, valueType);
        } else if(!conditionRule.getRecipients().isEmpty() && (type.endsWith("recipient") || type.endsWith("recipientIntersection"))){
            addRecipient(value, valueType);
        } else {
//            System.out.println("Auxiliary property");
        }
    }

    public void addConstraint(List<Triple<String, String, String>> constraint) {
        //find left operand, no matter the prefix
        String property = null;
        Map<String, String> values = new HashMap<>();

        for(Triple<String, String, String> triple : constraint){
            if(triple.getLeft().endsWith("leftOperand")){
                property = triple.getMiddle();
            } else if(triple.getLeft().endsWith("rightOperand")){
                values.put(triple.getMiddle(), triple.getRight());
            }
        }
        if (property == null || values.size() == 0){
            System.out.println("Invalid constraint");
            return;
        }
        String finalProperty = property;
        values.forEach((key, value) -> addProperty(finalProperty, key, value ));
    }

    @Override
    public String toString() {

        return name + ":" + type + "\n" +
                "\tdata: " + Joiner.on(",").withKeyValueSeparator("=").join(data) + "\n" +
                "\tactions: " + Joiner.on(",").withKeyValueSeparator("=").join(actions) + "\n" +
                "\tpurposes: " + Joiner.on(",").withKeyValueSeparator("=").join(purposes) + "\n" +
                "\tlegalBases: " + Joiner.on(",").withKeyValueSeparator("=").join(legalBases) + "\n" +
                "\tmeasures: " + Joiner.on(",").withKeyValueSeparator("=").join(measures) + "\n" +
                "\tcontrollers: " + Joiner.on(",").withKeyValueSeparator("=").join(controllers) + "\n" +
                "\tprocessors: " + Joiner.on(",").withKeyValueSeparator("=").join(processors) + "\n" +
                "\tdataSubjects: " + Joiner.on(",").withKeyValueSeparator("=").join(dataSubjects) + "\n" +
                "\tresponsibleParties: " + Joiner.on(",").withKeyValueSeparator("=").join(responsibleParties) + "\n" +
                "\tsenders: " + Joiner.on(",").withKeyValueSeparator("=").join(senders) + "\n" +
                "\trecipients: " + Joiner.on(",").withKeyValueSeparator("=").join(recipients) + "\n" +
                "\n";
    }
}
