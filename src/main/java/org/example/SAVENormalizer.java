package org.example;

import com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Model;

import java.util.*;


/**
 * The functional class for normalization of SAVE rules
 */
public class SAVENormalizer {

    private Model unionModel;
    private SAVEVocabulary vocab;

    /**
     * Returns the full model (SAVE + whatever else is needed)
     * @return model graph
     */
    public Model getUnionModel() {
        return unionModel;
    }


    /**
     * Constructor
     * @param unionModel the full model needed for normalization (at least full SAVE model and the policy)
     */
    public SAVENormalizer(Model unionModel) {
        this.unionModel = unionModel;
        vocab = new SAVEVocabulary(unionModel);
    }

    /**
     * Normalizes one rule
     * @param rule original rule/request to normalize
     * @param leaves whether the rule is already atomic
     * @param verbose whether to show the intermediate logs
     * @return object containing the normalized rule (attributes, values and combinations)
     */
    public SAVERuleNormalized normalizeSAVERule(SAVERule rule, boolean leaves, boolean verbose) {
        //go through the rule one by one attribute
        SAVERuleNormalized saveRuleNormalized = new SAVERuleNormalized(rule);

        if(!rule.getActions().isEmpty()) {
            Set<String> actionLeaves = new HashSet<>();
            if(leaves) {
                //ideally should be only one value
                actionLeaves.addAll(rule.getActions().values());
            } else {
                for (String actionClass : rule.getActions().values()) {
                    actionLeaves.addAll(JenaUtils.findLeaves(actionClass, unionModel));
                }
            }

            if (!actionLeaves.isEmpty()) {
                saveRuleNormalized.addValues(new ArrayList<>(actionLeaves));
                saveRuleNormalized.addAttribute(vocab.actionPropertyPrefixedName);
            }
        }
        if(!rule.getData().isEmpty()) {
            Set<String> dataLeaves = new HashSet<>();
            if(leaves) {
                //ideally should be only one value
                dataLeaves.addAll(rule.getData().values());
            } else {
                for (String dataClass : rule.getData().values()) {
                    dataLeaves.addAll(JenaUtils.findLeaves(dataClass, unionModel));
                }
            }

            if (!dataLeaves.isEmpty()) {
                saveRuleNormalized.addValues(new ArrayList<>(dataLeaves));
                saveRuleNormalized.addAttribute(vocab.dataPropertyPrefixedName);
            }
        }
        if (!rule.getPurposes().isEmpty()) {
            Set<String> purposeLeaves = new HashSet<>();
            if(leaves) {
                //ideally should be only one value
                purposeLeaves.addAll(rule.getPurposes().values());
            } else {
                for (String purposeClass : rule.getPurposes().values()) {
                    purposeLeaves.addAll(JenaUtils.findLeaves(purposeClass, unionModel));
                }
            }
            if (!purposeLeaves.isEmpty()) {
                saveRuleNormalized.addValues(new ArrayList<>(purposeLeaves));
                saveRuleNormalized.addAttribute(vocab.purposePropertyPrefixedName);
            }
        }
        if(!rule.getLegalBases().isEmpty()) {
            Set<String> legalBasisLeaves = new HashSet<>();
            if(leaves) {
                //ideally should be only one value
                legalBasisLeaves.addAll(rule.getLegalBases().values());
            } else {
                for (String legalBasisClass : rule.getLegalBases().values()) {
                    legalBasisLeaves.addAll(JenaUtils.findLeaves(legalBasisClass, unionModel));
                }
            }
            if (!legalBasisLeaves.isEmpty()) {
                saveRuleNormalized.addValues(new ArrayList<>(legalBasisLeaves));
                saveRuleNormalized.addAttribute(vocab.legalBasisPropertyPrefixedName);
            }
        }
        if(!rule.getMeasures().isEmpty()) {
            Set<String> measuresLeaves = new HashSet<>();
            if(leaves) {
                //ideally should be only one value
                measuresLeaves.addAll(rule.getMeasures().values());
            } else {
                for (String measureClass : rule.getMeasures().values()) {
                    measuresLeaves.addAll(JenaUtils.findLeaves(measureClass, unionModel));
                }
            }
            if (!measuresLeaves.isEmpty()) {
                saveRuleNormalized.addValues(new ArrayList<>(measuresLeaves));
                saveRuleNormalized.addAttribute(vocab.measurePropertyPrefixedName);
            }
        }
        //the rest of the fields concern parties and need to be set by value, i.e. are not really normalizable
        // all we do it break down if there is more than 1
        if(!rule.getControllers().isEmpty()) {
            Set<String> controllerLeaves = new HashSet<>(rule.getControllers().keySet());
            if (!controllerLeaves.isEmpty()) {
                saveRuleNormalized.addValues(new ArrayList<>(controllerLeaves));
                saveRuleNormalized.addAttribute(vocab.controllerPropertyPrefixedName);
            }
        }
        if (!rule.getProcessors().isEmpty()) {
            Set<String> processorLeaves = new HashSet<>(rule.getProcessors().keySet());
            if (!processorLeaves.isEmpty()) {
                saveRuleNormalized.addValues(new ArrayList<>(processorLeaves));
                saveRuleNormalized.addAttribute(vocab.processorPropertyPrefixedName);
            }
        }
        if (!rule.getDataSubjects().isEmpty()) {
            Set<String> dataSubjectLeaves = new HashSet<>(rule.getDataSubjects().keySet());
            if (!dataSubjectLeaves.isEmpty()) {
                saveRuleNormalized.addValues(new ArrayList<>(dataSubjectLeaves));
                saveRuleNormalized.addAttribute(vocab.dataSubjectPropertyPrefixedName);
            }
        }
        if (!rule.getResponsibleParties().isEmpty()) {
            Set<String> responsiblePartyLeaves = new HashSet<>(rule.getResponsibleParties().keySet());
            if (!responsiblePartyLeaves.isEmpty()) {
                saveRuleNormalized.addValues(new ArrayList<>(responsiblePartyLeaves));
                saveRuleNormalized.addAttribute(vocab.responsiblePartyPropertyPrefixedName);
            }
        }
        if(!rule.getSenders().isEmpty()) {
            Set<String> senderLeaves = new HashSet<>(rule.getSenders().keySet());
            if (!senderLeaves.isEmpty()) {
                saveRuleNormalized.addValues(new ArrayList<>(senderLeaves));
                saveRuleNormalized.addAttribute(vocab.senderPropertyPrefixedName);
            }
        }
        if(!rule.getRecipients().isEmpty()) {
            Set<String> recipientLeaves = new HashSet<>(rule.getRecipients().keySet());
            if (!recipientLeaves.isEmpty()) {
                saveRuleNormalized.addValues(new ArrayList<>(recipientLeaves));
                saveRuleNormalized.addAttribute(vocab.recipientPropertyPrefixedName);
            }
        }

        // now lets create the child rules
        List<List<String>> combinations = Lists.cartesianProduct(saveRuleNormalized.getValues());
        saveRuleNormalized.setCombinations(combinations);
        if(verbose) {
            System.out.println("The size of the combinations is " + combinations.size());
        }
//        System.out.println(combinations.subList(1, 5));
        return saveRuleNormalized;
    }



}
