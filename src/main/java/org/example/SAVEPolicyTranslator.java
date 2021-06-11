package org.example;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.jenax.util.JenaUtil;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


/**
 * Class responsible for the translation of SAVE policy into Jena graph model
 */
public class SAVEPolicyTranslator {

    private Model unionModel;
    private Model infModel;
    private SAVEPolicy savePolicy;
    private SAVEVocabulary vocab;

    /**
     * Constructor.
     * @param model the model with necessary prefixes and concepts
     * @param policy the policy to translate
     */
    public SAVEPolicyTranslator(Model model, SAVEPolicy policy) {
        unionModel = model;
        infModel = JenaUtil.createMemoryModel();
        infModel.setNsPrefixes(model.getNsPrefixMap());
        savePolicy = policy;
        vocab = new SAVEVocabulary(model);
        if(infModel.getNsPrefixURI(vocab.saveExPrefix) == null){
            infModel.setNsPrefix(vocab.saveExPrefix, SAVEVocabulary.SAVEEXURI);
        }
        if(infModel.getNsPrefixURI(vocab.savePrefix) == null){
            infModel.setNsPrefix(vocab.savePrefix, SAVEVocabulary.SAVEURI);
        }
    }

    public Statement translateSAVEPolicy(){
        // add the policy statement first
        Statement stmt = infModel.createStatement(infModel.createResource(replacePrefixWithURI(savePolicy.getName())),
                RDF.type,
                infModel.createResource(replacePrefixWithURI(vocab.orcpPrefix + ":" + "Set")));
        infModel.add(stmt);
        if(!savePolicy.getPermissions().isEmpty()){
            for(SAVERule permission: savePolicy.getPermissions()){
                addSAVERuleToModel(permission);
                addRuleToPolicy(permission.getName(), permission.getType());
            }
        }
        if(!savePolicy.getProhibitions().isEmpty()){
            for(SAVERule prohibition: savePolicy.getProhibitions()){
                addSAVERuleToModel(prohibition);
                addRuleToPolicy(prohibition.getName(), prohibition.getType());
            }
        }
        if(!savePolicy.getObligations().isEmpty()){
            for(SAVERule obligation: savePolicy.getObligations()){
                addSAVERuleToModel(obligation);
                addRuleToPolicy(obligation.getName(), obligation.getType());
            }
        }
        if(!savePolicy.getDispensations().isEmpty()){
            for(SAVERule dispensation: savePolicy.getDispensations()){
                addSAVERuleToModel(dispensation);
                addRuleToPolicy(dispensation.getName(), dispensation.getType());
            }
        }
        return stmt;
    }



    public Statement addRuleToPolicy(String ruleName, String ruleType){
        Property prop = null;
        if(ruleType.toLowerCase().endsWith("permission")){
            prop = infModel.createProperty(replacePrefixWithURI(vocab.permissionPropertyPrefixedName));
        } else if(ruleType.toLowerCase().endsWith("prohibition")){
            prop = infModel.createProperty(replacePrefixWithURI(vocab.prohibitionPropertyPrefixedName));
        } else if(ruleType.toLowerCase().endsWith("obligation")){
            prop = infModel.createProperty(replacePrefixWithURI(vocab.obligationPropertyPrefixedName));
        } else if(ruleType.toLowerCase().endsWith("dispensation")){
            prop = infModel.createProperty(replacePrefixWithURI(vocab.dispensationPropertyPrefixedName));
        }
        Statement stmt = infModel.createStatement(infModel.createResource(replacePrefixWithURI(savePolicy.getName())),
                prop,
                infModel.createResource(replacePrefixWithURI(ruleName)));
        infModel.add(stmt);
        return stmt;
    }

    public Statement addSAVERuleToModel(SAVERule rule){
        Statement mainStmt = infModel.createStatement(infModel.createResource(replacePrefixWithURI(rule.getName())),
                RDF.type,
                infModel.createResource(replacePrefixWithURI(rule.getType())));
        infModel.add(mainStmt);
        Resource baseNode = mainStmt.getSubject();
        if (!rule.getData().isEmpty()){
            for (String value: rule.getData().keySet()){
                addAttributeStatement(vocab.dataPropertyPrefixedName, value, baseNode);
            }
        }
        if (!rule.getActions().isEmpty()){
            for (String value: rule.getActions().keySet()){
                addAttributeStatement(vocab.actionPropertyPrefixedName, value, baseNode);
            }
        }
        if (!rule.getPurposes().isEmpty()){
            for (String value: rule.getPurposes().keySet()){
                addAttributeStatement(vocab.purposePropertyPrefixedName, value, baseNode);
            }
        }
        if (!rule.getLegalBases().isEmpty()){
            for (String value: rule.getLegalBases().keySet()){
                addAttributeStatement(vocab.legalBasisPropertyPrefixedName, value, baseNode);
            }
        }
        if (!rule.getMeasures().isEmpty()){
            for (String value: rule.getMeasures().keySet()){
                addAttributeStatement(vocab.measurePropertyPrefixedName, value, baseNode);
            }
        }
        if (!rule.getControllers().isEmpty()){
            for (String value: rule.getControllers().keySet()){
                addAttributeStatement(vocab.controllerPropertyPrefixedName, value, baseNode);
            }
        }
        if (!rule.getProcessors().isEmpty()){
            for (String value: rule.getProcessors().keySet()){
                addAttributeStatement(vocab.processorPropertyPrefixedName, value, baseNode);
            }
        }
        if (!rule.getDataSubjects().isEmpty()){
            for (String value: rule.getDataSubjects().keySet()){
                addAttributeStatement(vocab.dataSubjectPropertyPrefixedName, value, baseNode);
            }
        }
        if (!rule.getResponsibleParties().isEmpty()){
            for (String value: rule.getResponsibleParties().keySet()){
                addAttributeStatement(vocab.responsiblePartyPropertyPrefixedName, value, baseNode);
            }
        }
        if (!rule.getSenders().isEmpty()){
            for (String value: rule.getSenders().keySet()){
                addAttributeStatement(vocab.senderPropertyPrefixedName, value, baseNode);
            }
        }
        if (!rule.getRecipients().isEmpty()){
            for (String value: rule.getRecipients().keySet()){
                addAttributeStatement(vocab.recipientPropertyPrefixedName, value, baseNode);
            }
        }
        return mainStmt;
    }


    /**
     * Add an attribute to the rule in the model graph
     * @param attribute attribute name
     * @param value attribute value
     * @param baseNode subject/object for the triple
     * @return the triple with the attribute added
     */
    private Statement addAttributeStatement(String attribute, String value, Resource baseNode){
        // first, if the value is a class, check if there is a corresponding individual
        // if not, create one
        // check in both models, inference and union, write into union!
        Resource valueIndividual;
        valueIndividual = infModel.createResource(replacePrefixWithURI(value));
        Statement stmt = infModel.createStatement(baseNode,
                infModel.createProperty(replacePrefixWithURI(attribute)),
                //here replace with creation of same-named individual if it doesn't exist in the model or infModel
                valueIndividual);
        infModel.add(stmt);
        return stmt;
    }

    private String replacePrefixWithURI(String prefixedName){
        return ModelUtils.replacePrefixWithURI(prefixedName, infModel);
    }

    /**
     * Writes the generated policy to the file
     * @param folder folder to write into
     */
    public void writeSAVEPolicyToFile(String folder) {
        File f = new File(folder);
        boolean dirCreated = f.mkdirs();
        String filename = String.format(folder + "%s.policy.ttl", savePolicy.getName().split(":")[1]);
        File out = new File(filename);
        try {
            infModel.write(new FileWriter(out), "Turtle");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
