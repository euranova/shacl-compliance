package org.example;

import org.apache.jena.rdf.model.Model;

import java.util.*;


/**
 * Helper class with names for classes and properties used in the system
 */
public class SAVEVocabulary {

    /**
     * basic URI for any SAVE concept and default individuals
     */
    public static final String SAVEURI = "http://example.com/privacy-policy-model/";

    /**
     * URI for policy-specific or request-specific individuals
     */
    public static final String SAVEEXURI = "http://example.com/privacy-policy-model/examples/";

    /**
     * URI for ORCP concepts
     */
    public static final String ORCPURI = "http://example.com/odrl:profile:regulatoryCompliance/";


    /**
     * basic prefix for any SAVE concept and default individuals
     */
    public final String savePrefix;
    /**
     * prefix for policy-specific or request-specific individuals
     */
    public final String saveExPrefix;

    /**
     * prefix for ORCP concepts
     */
    public final String orcpPrefix;

    /**
     * Names for various data-related properties used int the system
     */
    public final String dataPropertyPrefixedName;
    public final String dataListPropertyPrefixedName;
    public final String dataIntersectionPropertyPrefixedName;

    /**
     * Names for various action-related properties used int the system
     */
    public final String actionPropertyPrefixedName;
    public final String actionListPropertyPrefixedName;
    public final String actionIntersectionPropertyPrefixedName;

    /**
     * Names for various purpose-related properties used int the system
     */
    public final String purposePropertyPrefixedName;
    public final String purposeListPropertyPrefixedName;
    public final String purposeIntersectionPropertyPrefixedName;

    /**
     * Names for various legalBasis-related properties used int the system
     */
    public final String legalBasisPropertyPrefixedName;
    public final String legalBasisListPropertyPrefixedName;
    public final String legalBasisIntersectionPropertyPrefixedName;

    /**
     * Names for various measure-related properties used int the system
     */
    public final String measurePropertyPrefixedName;
    public final String measureListPropertyPrefixedName;
    public final String measureIntersectionPropertyPrefixedName;

    /**
     * Names for various controller-related properties used int the system
     */
    public final String controllerPropertyPrefixedName;
    public final String controllerListPropertyPrefixedName;
    public final String controllerIntersectionPropertyPrefixedName;

    /**
     * Names for various processor-related properties used int the system
     */
    public final String processorPropertyPrefixedName;
    public final String processorListPropertyPrefixedName;
    public final String processorIntersectionPropertyPrefixedName;

    /**
     * Names for various dataSubject-related properties used int the system
     */
    public final String dataSubjectPropertyPrefixedName;
    public final String dataSubjectListPropertyPrefixedName;
    public final String dataSubjectIntersectionPropertyPrefixedName;

    /**
     * Names for various responsibleParty-related properties used int the system
     */
    public final String responsiblePartyPropertyPrefixedName;
    public final String responsiblePartyListPropertyPrefixedName;
    public final String responsiblePartyIntersectionPropertyPrefixedName;

    /**
     * Names for various sender-related properties used int the system
     */
    public final String senderPropertyPrefixedName;
    public final String senderListPropertyPrefixedName;
    public final String senderIntersectionPropertyPrefixedName;

    /**
     * Names for various recipient-related properties used int the system
     */
    public final String recipientPropertyPrefixedName;
    public final String recipientListPropertyPrefixedName;
    public final String recipientIntersectionPropertyPrefixedName;

    public final String permissionPropertyPrefixedName;
    public final String prohibitionPropertyPrefixedName;
    public final String obligationPropertyPrefixedName;
    public final String dispensationPropertyPrefixedName;

    /**
     * Names for various other used properties
     */
    public final String parentPropertyPrefixedName;
    public final String childPropertyPrefixedName;
    public final String answerPropertyPrefixedName;
    public final String answerPermittedPropertyPrefixedName;
    public final String answerProhibitedPropertyPrefixedName;
    public final String conformsPropertyPrefixedName;
    public final String prohibitedPropertyPrefixedName;
    public final String nChildrenPropertyPrefixedName;
    public final String nPermittedPropertyPrefixedName;
    public final String nProhibitedPropertyPrefixedName;
    public final String rulePropertyPrefixedName;
    /**
     * Names of attributes related to optimized version of compliance checking
     */
    public final Set<String> classAtributes;
    public final Set<String> valueAtributes;
    public final Set<String> listAtributes;
    public final Set<String> intersectionAtributes;
    /**
     * Root classes for various taxonomies (for default values)
     */
    public final Map<String, String> rootMap;

    public final String requestBaseType;


    /**
     * Creates all values based on current model
     * @param model model graph containing all prefixes
     */
    public SAVEVocabulary(Model model) {
        this.savePrefix = (model.getNsURIPrefix(SAVEURI) == null) ? "save" : model.getNsURIPrefix(SAVEURI);
        this.saveExPrefix = (model.getNsURIPrefix(SAVEEXURI) == null) ? "save-ex" : model.getNsURIPrefix(SAVEEXURI);
        this.orcpPrefix = (model.getNsURIPrefix(ORCPURI) == null) ? "orcp" : model.getNsURIPrefix(ORCPURI);
        dataPropertyPrefixedName = savePrefix + ":" + "data";
        dataListPropertyPrefixedName = getListPropertyName(dataPropertyPrefixedName);
        dataIntersectionPropertyPrefixedName = getIntersectionPropertyName(dataPropertyPrefixedName);
        actionPropertyPrefixedName = savePrefix + ":" + "action";
        actionListPropertyPrefixedName = getListPropertyName(actionPropertyPrefixedName);
        actionIntersectionPropertyPrefixedName = getIntersectionPropertyName(actionPropertyPrefixedName);
        purposePropertyPrefixedName = savePrefix + ":" + "purpose";
        purposeListPropertyPrefixedName = getListPropertyName(purposePropertyPrefixedName);
        purposeIntersectionPropertyPrefixedName = getIntersectionPropertyName(purposePropertyPrefixedName);
        legalBasisPropertyPrefixedName = savePrefix + ":" + "legalBasis";
        legalBasisListPropertyPrefixedName = getListPropertyName(legalBasisPropertyPrefixedName);
        legalBasisIntersectionPropertyPrefixedName = getIntersectionPropertyName(legalBasisPropertyPrefixedName);
        measurePropertyPrefixedName = savePrefix + ":" + "hasTechnicalOrganisationalMeasure";
        measureListPropertyPrefixedName = getListPropertyName(measurePropertyPrefixedName);
        measureIntersectionPropertyPrefixedName = getIntersectionPropertyName(measurePropertyPrefixedName);
        controllerPropertyPrefixedName = savePrefix + ":" + "controller";
        controllerListPropertyPrefixedName = getListPropertyName(controllerPropertyPrefixedName);
        controllerIntersectionPropertyPrefixedName = getIntersectionPropertyName(controllerPropertyPrefixedName);
        processorPropertyPrefixedName = savePrefix + ":" + "processor";
        processorListPropertyPrefixedName = getListPropertyName(processorPropertyPrefixedName);
        processorIntersectionPropertyPrefixedName = getIntersectionPropertyName(processorPropertyPrefixedName);
        dataSubjectPropertyPrefixedName = savePrefix + ":" + "hasDataSubject";
        dataSubjectListPropertyPrefixedName = getListPropertyName(dataSubjectPropertyPrefixedName);
        dataSubjectIntersectionPropertyPrefixedName = getIntersectionPropertyName(dataSubjectPropertyPrefixedName);
        responsiblePartyPropertyPrefixedName = savePrefix + ":" + "responsibleParty";
        responsiblePartyListPropertyPrefixedName = getListPropertyName(responsiblePartyPropertyPrefixedName);
        responsiblePartyIntersectionPropertyPrefixedName = getIntersectionPropertyName(responsiblePartyPropertyPrefixedName);
        senderPropertyPrefixedName = savePrefix + ":" + "sender";
        senderListPropertyPrefixedName = getListPropertyName(senderPropertyPrefixedName);
        senderIntersectionPropertyPrefixedName = getIntersectionPropertyName(senderPropertyPrefixedName);
        recipientPropertyPrefixedName = savePrefix + ":" + "recipient";
        recipientListPropertyPrefixedName = getListPropertyName(recipientPropertyPrefixedName);
        recipientIntersectionPropertyPrefixedName = getIntersectionPropertyName(recipientPropertyPrefixedName);
        permissionPropertyPrefixedName = savePrefix + ":" + "permission";
        prohibitionPropertyPrefixedName = savePrefix + ":" + "prohibition";
        obligationPropertyPrefixedName = savePrefix + ":" + "obligation";
        dispensationPropertyPrefixedName = savePrefix + ":" + "dispensation";
        parentPropertyPrefixedName = savePrefix + ":" + "parent";
        childPropertyPrefixedName = savePrefix + ":" + "child";
        answerPropertyPrefixedName = savePrefix + ":" + "answer";
        answerPermittedPropertyPrefixedName = savePrefix + ":" + "answerPermitted";
        answerProhibitedPropertyPrefixedName = savePrefix + ":" + "answerProhibited";
        conformsPropertyPrefixedName = savePrefix + ":" + "conformsTo";
        prohibitedPropertyPrefixedName = savePrefix + ":" + "prohibitedBy";
        nChildrenPropertyPrefixedName = savePrefix + ":" + "nChildren";
        nPermittedPropertyPrefixedName = savePrefix + ":" + "nPermitted";
        nProhibitedPropertyPrefixedName = savePrefix + ":" + "nProhibited";
        rulePropertyPrefixedName = savePrefix + ":" + "rule";
        classAtributes = new HashSet<>(Arrays.asList(actionPropertyPrefixedName, dataPropertyPrefixedName,
                purposePropertyPrefixedName, measurePropertyPrefixedName, legalBasisPropertyPrefixedName));
        valueAtributes = new HashSet<>(Arrays.asList(controllerPropertyPrefixedName, processorPropertyPrefixedName,
                dataSubjectPropertyPrefixedName, responsiblePartyPropertyPrefixedName, senderPropertyPrefixedName,
                recipientPropertyPrefixedName));
        listAtributes = new HashSet<>(Arrays.asList(dataListPropertyPrefixedName, actionListPropertyPrefixedName,
                purposeListPropertyPrefixedName, legalBasisListPropertyPrefixedName, measureListPropertyPrefixedName,
                controllerListPropertyPrefixedName, processorListPropertyPrefixedName, dataSubjectListPropertyPrefixedName,
                responsiblePartyListPropertyPrefixedName, senderListPropertyPrefixedName, recipientListPropertyPrefixedName));
        intersectionAtributes = new HashSet<>(Arrays.asList(dataIntersectionPropertyPrefixedName, actionIntersectionPropertyPrefixedName,
                purposeIntersectionPropertyPrefixedName, legalBasisIntersectionPropertyPrefixedName, measureIntersectionPropertyPrefixedName,
                controllerIntersectionPropertyPrefixedName, processorIntersectionPropertyPrefixedName, dataSubjectIntersectionPropertyPrefixedName,
                responsiblePartyIntersectionPropertyPrefixedName, senderIntersectionPropertyPrefixedName, recipientIntersectionPropertyPrefixedName));
        rootMap = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(dataPropertyPrefixedName, "dpv:PersonalDataCategory"),
                new AbstractMap.SimpleEntry<>(dataListPropertyPrefixedName, "dpv:PersonalDataCategory"),
                new AbstractMap.SimpleEntry<>(actionPropertyPrefixedName, "dpv:Processing"),
                new AbstractMap.SimpleEntry<>(actionListPropertyPrefixedName, "dpv:Processing"),
                new AbstractMap.SimpleEntry<>(purposePropertyPrefixedName, "dpv:Purpose"),
                new AbstractMap.SimpleEntry<>(purposeListPropertyPrefixedName, "dpv:Purpose"),
                new AbstractMap.SimpleEntry<>(legalBasisPropertyPrefixedName, savePrefix + ":LegalBasis"),
                new AbstractMap.SimpleEntry<>(legalBasisListPropertyPrefixedName, savePrefix + ":LegalBasis"),
                new AbstractMap.SimpleEntry<>(measurePropertyPrefixedName, "dpv:TechnicalOrganisationalMeasure"),
                new AbstractMap.SimpleEntry<>(measureListPropertyPrefixedName, "dpv:TechnicalOrganisationalMeasure"),
                new AbstractMap.SimpleEntry<>(controllerPropertyPrefixedName, "dpv:DataController"),
                new AbstractMap.SimpleEntry<>(controllerListPropertyPrefixedName, "dpv:DataController"),
                new AbstractMap.SimpleEntry<>(processorPropertyPrefixedName, "dpv:DataProcessor"),
                new AbstractMap.SimpleEntry<>(processorListPropertyPrefixedName, "dpv:DataProcessor"),
                new AbstractMap.SimpleEntry<>(dataSubjectPropertyPrefixedName, "dpv:DataSubject"),
                new AbstractMap.SimpleEntry<>(dataSubjectListPropertyPrefixedName, "dpv:DataSubject"),
                new AbstractMap.SimpleEntry<>(responsiblePartyPropertyPrefixedName, savePrefix + ":Party"),
                new AbstractMap.SimpleEntry<>(responsiblePartyListPropertyPrefixedName, savePrefix + ":Party"),
                new AbstractMap.SimpleEntry<>(senderPropertyPrefixedName, savePrefix + ":Party"),
                new AbstractMap.SimpleEntry<>(senderListPropertyPrefixedName, savePrefix + ":Party"),
                new AbstractMap.SimpleEntry<>(recipientPropertyPrefixedName, savePrefix + ":Party"),
                new AbstractMap.SimpleEntry<>(recipientListPropertyPrefixedName, savePrefix + ":Party")
        );
        requestBaseType = savePrefix + ":RequestBase";
    }

    private String getListPropertyName(String prefixedName) {
        return prefixedName + "List";
    }

    private String getIntersectionPropertyName(String prefixedName) {
        return prefixedName + "Intersection";
    }

    public boolean isClassAttribute(String attr){
        return classAtributes.contains(attr);
    }
    public boolean isValueAttribute(String attr){
        return valueAtributes.contains(attr);
    }
}
