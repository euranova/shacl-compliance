package org.example;

import org.apache.jena.rdf.model.Model;
import org.topbraid.jenax.util.JenaUtil;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Functional class for automated generation of random SAVE rules/requests
 */
public class SAVERuleGenerator {
    /**
     * model for inferred triples - generated rules and requests
     */
    private Model infModel;
    /**
     * model containing alll necessary taxonomies, individuals and prefixes
     */
    private final Model unionModel;
    /**
     * helper vocabulary with property names
     */
    private final SAVEVocabulary vocab;

    /**
     * Map representing the hierarhy of DataController (root and actual individuals, as the DPT type is constant)
     */
    private final Map<String,String> controllerHierarchy;
    /**
     * Map representing the hierarhy of DataProcessor (root and actual individuals, as the DPT type is constant)
     */
    private final Map<String,String> processorHierarchy;
    /**
     * Map representing the hierarhy of DataSubject (root and actual individuals, as the DPT type is constant)
     */
    private final Map<String,String> dataSubjectHierarchy;
    /**
     * Map representing the hierarhy of Party (root and actual individuals, as the DPT type is constant)
     */
    private final Map<String,String> responsiblePartyHierarchy;
    /**
     * Map representing the hierarhy of Party (root and actual individuals, as the DPT type is constant)
     */
    private final Map<String,String> senderHierarchy;
    /**
     * Map representing the hierarhy of Party (root and actual individuals, as the DPT type is constant)
     */
    private final Map<String,String> recipientHierarchy;

    /**
     * Flat version of the dataHierarchy
     */
    private final List<String> dataHierarchyFlat = new ArrayList<>();
    /**
     * Flat version of the actionHierarchy
     */
    private final List<String> actionHierarchyFlat = new ArrayList<>();
    /**
     * Flat version of the purposeHierarchy
     */
    private final List<String> purposeHierarchyFlat = new ArrayList<>();
    /**
     * Flat version of the legalBasisHierarchy
     */
    private final List<String> legalBasisHierarchyFlat = new ArrayList<>();
    /**
     * Flat version of the measureHierarchy
     */
    private final List<String> measureHierarchyFlat = new ArrayList<>();
    /**
     * Flat version of the controllerHierarchy
     */
    private final List<String> controllerHierarchyFlat = new ArrayList<>();
    /**
     * Flat version of the processorHierarchy
     */
    private final List<String> processorHierarchyFlat = new ArrayList<>();
    /**
     * Flat version of the dataSubjectHierarchy
     */
    private final List<String> dataSubjectHierarchyFlat = new ArrayList<>();
    /**
     * Flat version of the responsiblePartyHierarchy
     */
    private final List<String> responsiblePartyHierarchyFlat = new ArrayList<>();
    /**
     * Flat version of the senderHierarchy
     */
    private final List<String> senderHierarchyFlat = new ArrayList<>();
    /**
     * Flat version of the recipientHierarchy
     */
    private final List<String> recipientHierarchyFlat = new ArrayList<>();

    /**
     * Flat version of the dataHierarchy, containing only leaves
     */
    private final List<String> dataHierarchyAtomic;
    /**
     * Flat version of the actionHierarchy, containing only leaves
     */
    private final List<String> actionHierarchyAtomic;
    /**
     * Flat version of the purposeHierarchy, containing only leaves
     */
    private final List<String> purposeHierarchyAtomic;
    /**
     * Flat version of the legalBasisHierarchy, containing only leaves
     */
    private final List<String> legalBasisHierarchyAtomic;
    /**
     * Flat version of the measureHierarchy, containing only leaves
     */
    private final List<String> measureHierarchyAtomic;
//    private final List<String> controllerHierarchyAtomic;
//    private final List<String> processorHierarchyAtomic;
//    private final List<String> dataSubjectHierarchyAtomic;
//    private final List<String> responsiblePartyHierarchyAtomic;
//    private final List<String> senderHierarchyAtomic;
//    private final List<String> recipientHierarchyAtomic;
    private Random random;


    /**
     * Constructor
     * @param model the model to use for concepts and prefixes
     * @param seed the seed to start with
     */
    public SAVERuleGenerator(Model model, int seed){
        random = new Random(seed);
        infModel = JenaUtil.createMemoryModel();
        infModel.setNsPrefixes(model.getNsPrefixMap());
        unionModel = model;
        vocab = new SAVEVocabulary(unionModel);
//        unionModel = ModelUtils.loadFullSAVEModel();
        //to join all the prefixes and individuals that may be needed
//        unionModel.add(model);
        Map<String, Boolean> dataHierarchy = SPARQLUtils.getClassHierarchy(unionModel, "dpv:PersonalDataCategory");
        dataHierarchyFlat.addAll(dataHierarchy.keySet());
        dataHierarchyAtomic = getLeavesFromHierarchy(dataHierarchy);

        Map<String, Boolean> actionHierarchy = SPARQLUtils.getClassHierarchy(unionModel, "dpv:Processing");
        actionHierarchyFlat.addAll(actionHierarchy.keySet());
        actionHierarchyAtomic = getLeavesFromHierarchy(actionHierarchy);

        Map<String, Boolean> purposeHierarchy = SPARQLUtils.getClassHierarchy(unionModel, "dpv:Purpose");
        purposeHierarchyFlat.addAll(purposeHierarchy.keySet());
        purposeHierarchyAtomic = getLeavesFromHierarchy(purposeHierarchy);

        Map<String, Boolean> legalBasisHierarchy = SPARQLUtils.getClassHierarchy(unionModel, "save:LegalBasis");
        legalBasisHierarchyFlat.addAll(legalBasisHierarchy.keySet());
        legalBasisHierarchyAtomic = getLeavesFromHierarchy(legalBasisHierarchy);

        Map<String, Boolean> measureHierarchy = SPARQLUtils.getClassHierarchy(unionModel, "dpv:TechnicalOrganisationalMeasure");
        measureHierarchyFlat.addAll(measureHierarchy.keySet());
        measureHierarchyAtomic = getLeavesFromHierarchy(measureHierarchy);

        controllerHierarchy = SPARQLUtils.getIndividuals(unionModel, "dpv:DataController");
        controllerHierarchyFlat.addAll(controllerHierarchy.keySet());

        processorHierarchy = SPARQLUtils.getIndividuals(unionModel, "dpv:DataProcessor");
        processorHierarchyFlat.addAll(processorHierarchy.keySet());

        dataSubjectHierarchy = SPARQLUtils.getIndividuals(unionModel, "dpv:DataSubject");
        dataSubjectHierarchyFlat.addAll(dataSubjectHierarchy.keySet());

        responsiblePartyHierarchy = SPARQLUtils.getIndividuals(unionModel, "save:Party");
        responsiblePartyHierarchyFlat.addAll(responsiblePartyHierarchy.keySet());

        senderHierarchy = SPARQLUtils.getIndividuals(unionModel, "save:Party");
        senderHierarchyFlat.addAll(senderHierarchy.keySet());

        recipientHierarchy = SPARQLUtils.getIndividuals(unionModel, "save:Party");
        recipientHierarchyFlat.addAll(recipientHierarchy.keySet());
    }

    /**
     * Given a hierarchy, get only leaves
     * @param hierarchyMap the map containing concepts and boolean indicating if the concept is the leaf
     * @return
     */
    private List<String> getLeavesFromHierarchy(Map<String, Boolean> hierarchyMap){
        return hierarchyMap.entrySet()
                .stream()
                .filter(map -> Boolean.TRUE.equals(map.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Generate a rule/request
     * @param ruleName prefixed name of the rule/request
     * @param ruleType the prefixed type
     * @param atomic should the rule/request be atomic or not
     * @param attributes lest of attributes to generate (not all may be present in a rule)
     * @return the generated random rule/request
     */
    public SAVERule generateRandomRule(String ruleName, String ruleType, boolean atomic, Set<String> attributes){
        if (attributes == null){
            //all attributes
            attributes = new HashSet<>(Arrays.asList(vocab.dataPropertyPrefixedName, vocab.actionPropertyPrefixedName,
                    vocab.purposePropertyPrefixedName, vocab.legalBasisPropertyPrefixedName,
                    vocab.measurePropertyPrefixedName, vocab.controllerPropertyPrefixedName,
                    vocab.processorPropertyPrefixedName, vocab.dataSubjectPropertyPrefixedName,
                    vocab.responsiblePartyPropertyPrefixedName, vocab.senderPropertyPrefixedName,
                    vocab.recipientPropertyPrefixedName));
        }
        SAVERule rule = new SAVERule(ruleName, ruleType);
        if(attributes.contains(vocab.dataPropertyPrefixedName)) {
            String data = generateRandomValue(atomic ? dataHierarchyAtomic : dataHierarchyFlat, random);
            String dataInd = SPARQLUtils.getPrefixedLabel(JenaUtils.getAnyIndividualFromConcept(data, vocab.savePrefix, unionModel), unionModel);
            rule.addData(dataInd, data);
        }
        if(attributes.contains(vocab.actionPropertyPrefixedName)) {
            String action = generateRandomValue(atomic ? actionHierarchyAtomic : actionHierarchyFlat, random);
            String actionInd = SPARQLUtils.getPrefixedLabel(JenaUtils.getAnyIndividualFromConcept(action, vocab.savePrefix, unionModel), unionModel);
            rule.addAction(actionInd, action);
        }
        if(attributes.contains(vocab.purposePropertyPrefixedName)) {
            String purpose = generateRandomValue(atomic ? purposeHierarchyAtomic : purposeHierarchyFlat, random);
            String purposeInd = SPARQLUtils.getPrefixedLabel(JenaUtils.getAnyIndividualFromConcept(purpose, vocab.savePrefix, unionModel), unionModel);
            rule.addPurpose(purposeInd, purpose);
        }
        if(attributes.contains(vocab.legalBasisPropertyPrefixedName)) {
            String legalBasis = generateRandomValue(atomic ? legalBasisHierarchyAtomic : legalBasisHierarchyFlat, random);
            String legalBasisInd = SPARQLUtils.getPrefixedLabel(JenaUtils.getAnyIndividualFromConcept(legalBasis, vocab.savePrefix, unionModel), unionModel);
            rule.addLegalBasis(legalBasisInd, legalBasis);
        }
        if(attributes.contains(vocab.measurePropertyPrefixedName)) {
            String measure = generateRandomValue(atomic ? measureHierarchyAtomic : measureHierarchyFlat, random);
            String measureInd = SPARQLUtils.getPrefixedLabel(JenaUtils.getAnyIndividualFromConcept(measure, vocab.savePrefix, unionModel), unionModel);
            rule.addMeasure(measureInd, measure);
        }
        if(attributes.contains(vocab.controllerPropertyPrefixedName)) {
            String controller = generateRandomValue(controllerHierarchyFlat, random);
            rule.addController(controller, controllerHierarchy.get(controller));
        }
        if(attributes.contains(vocab.processorPropertyPrefixedName)) {
            String processor = generateRandomValue(processorHierarchyFlat, random);
            rule.addProcessor(processor, processorHierarchy.get(processor));
        }
        if(attributes.contains(vocab.dataSubjectPropertyPrefixedName)) {
            String dataSubject = generateRandomValue(dataSubjectHierarchyFlat, random);
            rule.addDataSubject(dataSubject, dataSubjectHierarchy.get(dataSubject));
        }
        if(attributes.contains(vocab.responsiblePartyPropertyPrefixedName)) {
            String responsibleParty = generateRandomValue(responsiblePartyHierarchyFlat, random);
            rule.addResponsibleParty(responsibleParty, responsiblePartyHierarchy.get(responsibleParty));
        }
        if(attributes.contains(vocab.senderPropertyPrefixedName)) {
            String sender = generateRandomValue(senderHierarchyFlat, random);
            rule.addSender(sender, senderHierarchy.get(sender));
        }
        if(attributes.contains(vocab.recipientPropertyPrefixedName)) {
            String recipient = generateRandomValue(recipientHierarchyFlat, random);
            rule.addRecipient(recipient, recipientHierarchy.get(recipient));
        }
        return rule;
    }

    /**
     * Generate one value out of the list
     * @param values the values to generate from
     * @param random the random object to use
     * @return the value (prefixed)
     */
    private String generateRandomValue(List<String> values, Random random) {
        int index = random.nextInt(values.size());
        return values.get(index);
    }

    /**
     * Generate a policy with random rukes
     * @param policyName prefixed name of the policy
     * @param policySize the number of simple rules
     * @param atomic whether the rules should also be atomic
     * @param attributes which attributes to use
     * @return generated policy
     */
    public SAVEPolicy generateRandomPolicy(String policyName, int policySize, boolean atomic,
                                           Set<String> attributes) {
        SAVEPolicy policy = new SAVEPolicy(policyName);
        for (int i = 0; i < policySize; i++){
            int ruleTypeNumber = random.nextInt(2);
            String ruleType = (ruleTypeNumber == 0) ? "orcp:Permission" : "orcp:Prohibition";
            String ruleName = policyName + ((ruleTypeNumber == 0) ? "permission" : "prohibition") + "_" + i;
            SAVERule rule = generateRandomRule(ruleName, ruleType, atomic, attributes);
            policy.addRule(rule);
        }

        return policy;
    }
}
