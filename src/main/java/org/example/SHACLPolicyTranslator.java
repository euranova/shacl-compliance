package org.example;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.vocabulary.SH;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * Class responsible for the translation of SAVE policy to SHACL policy
 */
public class SHACLPolicyTranslator {

    private Model unionModel;
    private Model infModel;
    private SAVEPolicy savePolicy;
    private SAVEVocabulary vocab;

    /**
     * Constructor.
     * @param model the model with necessary prefixes and concepts
     * @param policy the policy to translate
     */
    public SHACLPolicyTranslator(Model model, SAVEPolicy policy) {
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

    public Model getInfModel() {
        return infModel;
    }

    public SAVEPolicy getSavePolicy() {
        return savePolicy;
    }

    public void setSavePolicy(SAVEPolicy savePolicy) {
        this.savePolicy = savePolicy;
    }

    /**
     * Main method, taking the policy and translaitng every rule into SHACL rule automatically
     * for the regular procedure
     */
    public void translateSAVEPolicyToSHACL() {
        Statement[] policyTypeStatements = addPolicyTypes();
        Resource baseNode = policyTypeStatements[0].getSubject();
        addPolicyLabel(baseNode);
        addPolicySubClass(baseNode);
        for (SAVERule rule : savePolicy.getPermissions()) {
            addSAVERule(rule, baseNode);
        }
        for (SAVERule rule : savePolicy.getProhibitions()) {
            addSAVERule(rule, baseNode);
        }
        for (SAVERule rule : savePolicy.getDispensations()) {
            addSAVERule(rule, baseNode);
        }
        addFinalRule(baseNode);
    }

    /**
     * Main method, taking the policy and translaitng every rule into SHACL rule automatically
     * for the optimized procedure
     */
    public void translateSAVEPolicyToSHACLOptimized() {
        Statement[] policyTypeStatements = addPolicyTypes();
        Resource baseNode = policyTypeStatements[0].getSubject();
        addPolicyLabel(baseNode);
        addPolicySubClass(baseNode);
        for (SAVERule rule : savePolicy.getPermissions()) {
            addSAVERuleOptimized(rule, baseNode);
        }
        for (SAVERule rule : savePolicy.getProhibitions()) {
            addSAVERuleOptimized(rule, baseNode);
        }
        for (SAVERule rule : savePolicy.getDispensations()) {
            addSAVERuleOptimized(rule, baseNode);
        }
        addFinalRuleOptimized(baseNode);
    }

    /**
     * Adds "final" rule to the optimized policy, deciding the final answer to the request
     * @param subject subject of the triple where the final rule should be attached
     * @return the triple with the final rule added to the model
     */
    private Statement addFinalRuleOptimized(Resource subject) {

        Statement typeStatement = addRuleType();
        Resource baseNode = typeStatement.getSubject();
        Statement labelStatement = infModel.createStatement(baseNode,
                RDFS.label,
                infModel.createLiteral("Infer if $this is compliant"));
        infModel.add(labelStatement);
//        addParentChildCondition(baseNode, true);
        addPolicyConstructOptimized(baseNode);
        addOrder(100, baseNode);
        //addPrefixes(baseNode);
        return addRuleStmt(baseNode, subject);

    }

    /**
     * Adds the construct SPARQL statement for the final rule of the optimized SHACL policy
     * @param baseNode the node to attach the construct statement to
     * @return the triple added to the model
     */
    private Statement addPolicyConstructOptimized(Resource baseNode) {

        String queryBase = """
                {SELECT (COUNT(*) as ?cnt%1$s) (SUM(?answer%5$s) as ?su%1$s)
                        WHERE
                        {
                            {SELECT $this ?attrLabel%1$s (COUNT(DISTINCT ?item%1$s) AS ?attrs%5$s)
                                WHERE {
                                $this %6$s ?%2$s .
                                        ?%2$s ?attr%1$s ?item%1$s .
                                        FILTER (?attr%1$s IN (%3$s))
                                bind(strbefore(strafter(str(?attr%1$s), "model/"), "Intersection") as ?attrLabel%1$s)
                            }
                                group by $this ?attrLabel%1$s
                            }
                            {SELECT $this ?requestAttrLabel%1$s (COUNT(DISTINCT ?requestItem%1$s) AS ?requestAttrsTotal%1$s)
                                WHERE {
                                $this ?requestAttr%1$s ?requestBNode%1$s .
                                        ?requestBNode%1$s rdf:rest*/rdf:first ?requestItem%1$s .
                                        FILTER (?requestAttr%1$s IN (%4$s))
                                bind(strbefore(strafter(str(?requestAttr%1$s), "model/"), "List") as ?requestAttrLabel%1$s)
                            }
                                group by $this ?requestAttrLabel%1$s
                            }
                            FILTER(?attrLabel%1$s = ?requestAttrLabel%1$s)
                            BIND(IF(?attrs%5$s = ?requestAttrsTotal%1$s, 1, 0) as ?answer%5$s)
                        }
                    }
                """;
        String queryPermitted = String.format(queryBase, "", "permission",
                String.join(", ", vocab.intersectionAtributes), String.join(", ", vocab.listAtributes),
                "Permitted", vocab.conformsPropertyPrefixedName);
        String queryProhibited = String.format(queryBase, "Proh", "prohibition",
                String.join(", ", vocab.intersectionAtributes), String.join(", ", vocab.listAtributes),
                "Prohibited", vocab.prohibitedPropertyPrefixedName);

        String query = """
                CONSTRUCT {
                        ?bnode  a save-ex:RequestAnswer ;
                                    save:answerPermitted ?ans ;
                                    save:answerProhibited ?ansProh ;
                                    save:countPermitted ?cnt ;
                                    save:sumPermitted ?su ;
                                    save:countProhibited ?cntProh ;
                                    save:sumProhibited ?suProh 
                                    .
                        $this   save:result ?bnode .
                }
                WHERE
                {
                    BIND(BNODE() as ?bnode) .
                    %1$s 
                    %2$s                
                    BIND(IF(?cnt = 0, "not granted", IF(?cnt = ?su, "granted", "part-granted")) as ?ans)
                    BIND(IF(?cntProh = 0, "not prohibited", IF(?cntProh = ?suProh, "prohibited", "part-prohibited")) as ?ansProh)
                }
                """;
        query = String.format(query, queryPermitted, queryProhibited);
//        System.out.println(query);
//        String query = """
//                CONSTRUCT {
//                        ?bnode  a save-ex:RequestAnswer ;
//                                    save:answerPermitted ?ans ;
//                                    save:answerProhibited ?ansProh .
//                        $this   save:result ?bnode .
//                }
//                WHERE
//                {
//                    BIND(BNODE() as ?bnode) .
//                    {SELECT (COUNT(*) as ?cnt) (SUM(?answer) as ?su)
//                        WHERE
//                        {
//                            {SELECT $this ?attrLabel (COUNT(DISTINCT *) AS ?attrsPermitted)
//                                WHERE {
//                                $this save:conformsTo ?permission .
//                                        ?permission ?attr ?item .
//                                        FILTER (?attr IN (%1$s))
//                                bind(strbefore(strafter(str(?attr), "model/"), "Intersection") as ?attrLabel)
//                            }
//                                group by $this ?attrLabel
//                            }
//                            {SELECT $this ?requestAttrLabel (COUNT(DISTINCT *) AS ?requestAttrsTotal)
//                                WHERE {
//                                $this ?requestAttr ?requestBNode .
//                                        ?requestBNode rdf:rest*/rdf:first ?requestItem .
//                                        FILTER (?requestAttr IN (%2$s))
//                                bind(strbefore(strafter(str(?requestAttr), "model/"), "List") as ?requestAttrLabel)
//                            }
//                                group by $this ?requestAttrLabel
//                            }
//                            FILTER(?attrLabel = ?requestAttrLabel)
//                            BIND(IF(?attrsPermitted = ?requestAttrsTotal, 1, 0) as ?answer)
//                        }
//                    }
//
//
//                    {SELECT (COUNT(*) as ?cntProh) (SUM(?answerProh) as ?suProh)
//                        WHERE
//                        {
//                        {SELECT $this ?attrLabelProh (COUNT(DISTINCT *) AS ?attrsProh)
//                           WHERE {
//                               $this save:prohibitedBy ?prohibition .
//                               ?prohibition ?attrProh ?itemProh .
//                               FILTER (?attrProh IN (%1$s))
//                               bind(strbefore(strafter(str(?attrProh), "model/"), "Intersection") as ?attrLabelProh)
//                            }
//                            group by $this ?attrLabelProh
//                            }
//                        {SELECT $this ?requestAttrLabelProh (COUNT(DISTINCT *) AS ?requestAttrsTotalProh)
//                          WHERE {
//                              $this ?requestAttrProh ?requestBNodeProh .
//                              ?requestBNodeProh rdf:rest*/rdf:first ?requestItemProh .
//                              FILTER (?requestAttrProh IN (%2$s))
//                              bind(strbefore(strafter(str(?requestAttrProh), "model/"), "List") as ?requestAttrLabelProh)
//                           }
//                           group by $this ?requestAttrLabelProh
//                            }
//                         FILTER(?attrLabelProh = ?requestAttrLabelProh)
//                         BIND(IF(?attrsProh = ?requestAttrsTotalProh, 1, 0) as ?answerProh)
//                         }
//                         }
//
//                    BIND(IF(?cnt = ?su, "fully-permitted", "partly-permitted") as ?ans)
//                    BIND(IF(?cntProh = ?suProh, "fully-prohibited", "partly-prohibited") as ?ansProh)
//                }
//                """;
//        query = String.format(query, String.join(", ", vocab.intersectionAtributes), String.join(", ", vocab.listAtributes));
        query = SPARQLUtils.addPrefixesToSparqlString(query, infModel);
//        System.out.println(query);
        return addConstruct(query, baseNode);

    }

    /**
     * Adds the drf:type triple(s) to the policy
     * @return the triple added to the model
     */
    private Statement[] addPolicyTypes() {
        Statement classStmt = infModel.createStatement(infModel.createResource(
                replacePrefixWithURI(String.format(vocab.saveExPrefix + ":" + "Request%s",
                        savePolicy.getName().contains(":") ? savePolicy.getName().split(":")[1] : savePolicy.getName()))),
                RDF.type,
                RDFS.Class);
        infModel.add(classStmt);
        Statement shapeStmt = infModel.createStatement(classStmt.getSubject(),
                RDF.type,
                SH.NodeShape);
        infModel.add(shapeStmt);
        return new Statement[]{classStmt, shapeStmt};
    }

    /**
     * Adds a label to the policy
     * @param baseNode the node to attach the triple to
     * @return the triple added to the model
     */
    private Statement addPolicyLabel(Resource baseNode) {
        Statement labelStmt = infModel.createStatement(baseNode,
                RDFS.label,
                infModel.createLiteral(String.format("Request%s", savePolicy.getName())));
        infModel.add(labelStmt);
        return labelStmt;
    }

    /**
     * Adds the rdfs:subClassOf statement to the SHACL policy
     * @param baseNode subject for the triple
     * @return triple added to the model
     */
    private Statement addPolicySubClass(Resource baseNode) {
        Statement subClassStmt = infModel.createStatement(baseNode,
                RDFS.subClassOf,
                infModel.createResource(replacePrefixWithURI(vocab.savePrefix + ":" + "RequestBase")));
        infModel.add(subClassStmt);
        return subClassStmt;

    }

    /**
     * Adds the sh:condition of conforming to the parent shape (for all policies)
     * @param baseNode subject of the triple
     * @return triple added to the model
     */
    private Statement addParentShapeCondition(Resource baseNode) {
        Statement conditionStmt = infModel.createStatement(baseNode,
                SH.condition,
                infModel.createResource(replacePrefixWithURI(vocab.savePrefix + ":" + "RequestBase")));
        infModel.add(conditionStmt);
        return conditionStmt;

    }

    /**
     * For regular version: translates a SAVE rule into SHACL and adds it to the model
     * @param rule the rule to add
     * @param baseNode the node to attach the shape to
     * @return triple added to the model
     */
    private Statement addSAVERule(SAVERule rule, Resource baseNode) {
        Statement typeStatement = addRuleType();
        Statement labelStatement = addRuleLabel(typeStatement.getSubject(), rule.getName(), rule.getType());
        Statement parentShapeStatement = addParentShapeCondition(typeStatement.getSubject());
        Statement childCondition = addParentChildCondition(typeStatement.getSubject(), false);

        addDataCondition(rule, typeStatement.getSubject());
        addActionCondition(rule, typeStatement.getSubject());
        addPurposeCondition(rule, typeStatement.getSubject());
        addLegalBaseCondition(rule, typeStatement.getSubject());
        addMeasureCondition(rule, typeStatement.getSubject());
        addControllerCondition(rule, typeStatement.getSubject());
        addProcessorCondition(rule, typeStatement.getSubject());
        addDataSubjectCondition(rule, typeStatement.getSubject());
        addResponsiblePartyCondition(rule, typeStatement.getSubject());
        addSenderCondition(rule, typeStatement.getSubject());
        addRecipientCondition(rule, typeStatement.getSubject());

        addRuleConstruct(typeStatement.getSubject(), rule.getName(), rule.getType());
        addOrder(1, typeStatement.getSubject());
//        addPrefixes(typeStatement.getSubject());
        return addRuleStmt(typeStatement.getSubject(), baseNode);
    }

    /**
     * For optimized version: translates a SAVE rule into SHACL and adds it to the model
     * @param rule the rule to add
     * @param baseNode the node to attach the shape to
     * @return triple added to the model
     */
    private Statement addSAVERuleOptimized(SAVERule rule, Resource baseNode) {
        Statement typeStatement = addRuleType();
        Statement labelStatement = addRuleLabel(typeStatement.getSubject(), rule.getName(), rule.getType());
        Statement parentShapeStatement = addParentShapeCondition(typeStatement.getSubject());

        addDataListCondition(rule, typeStatement.getSubject());
        addActionListCondition(rule, typeStatement.getSubject());
        addPurposeListCondition(rule, typeStatement.getSubject());
        addLegalBaseListCondition(rule, typeStatement.getSubject());
        addMeasureListCondition(rule, typeStatement.getSubject());
        addControllerListCondition(rule, typeStatement.getSubject());
        addProcessorListCondition(rule, typeStatement.getSubject());
        addDataSubjectListCondition(rule, typeStatement.getSubject());
        addResponsiblePartyListCondition(rule, typeStatement.getSubject());
        addSenderListCondition(rule, typeStatement.getSubject());
        addRecipientListCondition(rule, typeStatement.getSubject());

        addRuleConstructOptimized(typeStatement.getSubject(), rule);
        addOrder(1, typeStatement.getSubject());
        return addRuleStmt(typeStatement.getSubject(), baseNode);
    }

    /**
     * Adds the rdf:type for the rule
     * @return the triple added to the model
     */
    private Statement addRuleType() {
        Statement typeStatement = infModel.createStatement(infModel.createResource(),
                RDF.type,
                SH.SPARQLRule);
        infModel.add(typeStatement);
        return typeStatement;
    }

    /**
     * Adds rule label to the model
     * @param baseNode the node to attach the triple to
     * @param ruleName the name of the rule
     * @param ruleType the type of the rule
     * @return triple added to the model
     */
    private Statement addRuleLabel(Resource baseNode, String ruleName, String ruleType) {
        Statement labelStatement = infModel.createStatement(baseNode,
                RDFS.label,
                String.format("Infer if $this is covered by %s %s", ruleType, ruleName));
        infModel.add(labelStatement);
        return labelStatement;
    }

    /**
     * For regular version, add either parent or child condition to the rule, depending on if the rule is for a subrequest or the final one
     * @param subject node that is the subject of the triple
     * @param parent whether we add parent or child condition
     * @return triple added to the model
     */
    private Statement addParentChildCondition(Resource subject, boolean parent) {
        return addParentChildCondition(subject, parent, SH.maxCount, 0);
    }

    /**
     * Adds path triple with the property to check
     * @param value name of the property
     * @param baseNode subject of the triple
     * @return triple added to the model
     */
    private Statement addPathStatement(String value, Resource baseNode) {
        Resource subject;
        if (baseNode != null) {
            subject = baseNode;
        } else {
            subject = infModel.createResource();
        }
        Property predicate = SH.path;
        value = replacePrefixWithURI(value);
        Resource object = infModel.createResource(value);
        Statement node = infModel.createStatement(subject, predicate, object);
        infModel.add(node);
        return node;
    }

    /**
     * Adds min/maxCount statement to the rule
     * @param value the min/maxCount value
     * @param baseNode the subject of the triple
     * @param prop sh:minCount or sh:maxCount
     * @return triple added to the model
     */
    private Statement addCountStatement(int value, Resource baseNode, Property prop) {
        Resource subject;
        if (baseNode != null) {
            subject = baseNode;
        } else {
            subject = infModel.createResource();
        }
        Literal object = infModel.createTypedLiteral(value);
        Statement node = infModel.createStatement(subject, prop, object);
        infModel.add(node);
        return node;
    }

    /**
     * Adds property triple to the rule
     * @param value value (usually blank node) containing nested properties
     * @return triple added to the model
     */
    private Statement addPropertyStatement(Resource value) {
        Resource subject = infModel.createResource();
        Property predicate = SH.property;
        Statement node = infModel.createStatement(subject, predicate, value);
        infModel.add(node);
        return node;
    }

    /**
     * Adds the condition statement for the rule
     * @param value (usually blank) node with the nested properties
     * @param baseNode subject of the triple
     * @return triple added to the model
     */
    private Statement addConditionStatement(Resource value, Resource baseNode) {
        Resource subject;
        if (baseNode != null) {
            subject = baseNode;
        } else {
            subject = infModel.createResource();
        }
        Property predicate = SH.condition;
        Statement node = infModel.createStatement(subject, predicate, value);
        infModel.add(node);
        return node;
    }

    /**
     * (Regular version) Adds the condition for save:data property
     * @param rule rule to take data from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addDataCondition(SAVERule rule, Resource baseNode) {
        if (rule.getData().size() > 0) {
            return addAttributeCondition(rule.getData().values(), vocab.dataPropertyPrefixedName, false, baseNode);
        }
        return null;
    }

    /**
     * (Optimized version) Adds the condition for save:data property (save:dataList)
     * @param rule rule to take data from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addDataListCondition(SAVERule rule, Resource baseNode) {
        if (rule.getData().size() > 0) {
            return addSPARQLListAttributeCondition(rule.getData().values(), vocab.dataPropertyPrefixedName, false, baseNode);
        } else {
            // add default data
            return null;
//            return addSPARQLListAttributeCondition(
//                    Collections.singletonList(vocab.rootMap.get(vocab.dataPropertyPrefixedName)),
//                    vocab.dataPropertyPrefixedName, false, baseNode);
        }
    }

    /**
     * (Regular version) Adds the condition for save:action property
     * @param rule rule to take actions from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addActionCondition(SAVERule rule, Resource baseNode) {
        if (rule.getActions().size() > 0) {
            return addAttributeCondition(rule.getActions().values(), vocab.actionPropertyPrefixedName, false, baseNode);
        }
        return null;
    }

    /**
     * (Optimized version) Adds the condition for save:action property (save:actionList)
     * @param rule rule to take actions from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addActionListCondition(SAVERule rule, Resource baseNode) {
        if (rule.getActions().size() > 0) {
            return addSPARQLListAttributeCondition(rule.getActions().values(), vocab.actionPropertyPrefixedName, false, baseNode);
        } else {
        // add default data
            return  null;
//            return addSPARQLListAttributeCondition(
//                Collections.singletonList(vocab.rootMap.get(vocab.actionPropertyPrefixedName)),
//                vocab.actionPropertyPrefixedName, false, baseNode);
        }
    }

    /**
     * (Regular version) Adds the condition for save:purpose property
     * @param rule rule to take purposes from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addPurposeCondition(SAVERule rule, Resource baseNode) {
        if (rule.getPurposes().size() > 0) {
            return addAttributeCondition(rule.getPurposes().values(), vocab.purposePropertyPrefixedName, false, baseNode);
        }
        return null;
    }

    /**
     * (Optimized version) Adds the condition for save:purpose property (save:purposeList)
     * @param rule rule to take purposes from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addPurposeListCondition(SAVERule rule, Resource baseNode) {
        if (rule.getPurposes().size() > 0) {
            return addSPARQLListAttributeCondition(rule.getPurposes().values(), vocab.purposePropertyPrefixedName, false, baseNode);
        } else {
            // add default data
            return null;
//            return addSPARQLListAttributeCondition(
//                    Collections.singletonList(vocab.rootMap.get(vocab.purposePropertyPrefixedName)),
//                    vocab.purposePropertyPrefixedName, false, baseNode);
        }
    }

    /**
     * (Regular version) Adds the condition for save:legalBasis property
     * @param rule rule to take legal bases from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addLegalBaseCondition(SAVERule rule, Resource baseNode) {
        if (rule.getLegalBases().size() > 0) {
            return addAttributeCondition(rule.getLegalBases().values(), vocab.legalBasisPropertyPrefixedName, false, baseNode);
        }
        return null;
    }

    /**
     * (Optimized version) Adds the condition for save:legalBasis property (save:legalBasisList)
     * @param rule rule to take legal bases from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addLegalBaseListCondition(SAVERule rule, Resource baseNode) {
        if (rule.getLegalBases().size() > 0) {
            return addSPARQLListAttributeCondition(rule.getLegalBases().values(), vocab.legalBasisPropertyPrefixedName, false, baseNode);
        } else {
            // add default data
            return null;
//            return addSPARQLListAttributeCondition(
//                    Collections.singletonList(vocab.rootMap.get(vocab.legalBasisPropertyPrefixedName)),
//                    vocab.legalBasisPropertyPrefixedName, false, baseNode);
        }
    }

    /**
     * (Regular version) Adds the condition for save:hasTechnicalOrganisationalMeasures property
     * @param rule rule to take measures from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addMeasureCondition(SAVERule rule, Resource baseNode) {
        if (rule.getMeasures().size() > 0) {
            return addAttributeCondition(rule.getMeasures().values(), vocab.measurePropertyPrefixedName, false,
                    baseNode);
        }
        return null;
    }

    /**
     * (Optimized version) Adds the condition for save:hasTechnicalOrganisationalMeasure property (save:hasTechnicalOrganisationalMeasureList)
     * @param rule rule to take measures from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addMeasureListCondition(SAVERule rule, Resource baseNode) {
        if (rule.getMeasures().size() > 0) {
            return addSPARQLListAttributeCondition(rule.getMeasures().values(), vocab.measurePropertyPrefixedName, false,
                    baseNode);
        } else {
            // add default data
            return null;
//            return addSPARQLListAttributeCondition(
//                    Collections.singletonList(vocab.rootMap.get(vocab.measurePropertyPrefixedName)),
//                    vocab.measurePropertyPrefixedName, false, baseNode);
        }
    }

    /**
     * (Regular version) Adds the condition for save:controller property
     * @param rule rule to take controllers from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addControllerCondition(SAVERule rule, Resource baseNode) {
        if (rule.getControllers().size() > 0) {
            return addAttributeCondition(rule.getControllers().keySet(), vocab.controllerPropertyPrefixedName, true, baseNode);
        }
        return null;
    }

    /**
     * (Optimized version) Adds the condition for save:controller property (save:controllerList)
     * @param rule rule to take controllers from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addControllerListCondition(SAVERule rule, Resource baseNode) {
        if (rule.getControllers().size() > 0) {
            return addSPARQLListAttributeCondition(rule.getControllers().keySet(), vocab.controllerPropertyPrefixedName, true, baseNode);
        } else {
            // add default data
            return null;
//            return addSPARQLListAttributeCondition(
//                    Collections.singletonList(vocab.rootMap.get(vocab.controllerPropertyPrefixedName)),
//                    vocab.controllerPropertyPrefixedName, false, baseNode);
        }
    }

    /**
     * (Regular version) Adds the condition for save:processor property
     * @param rule rule to take processors from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addProcessorCondition(SAVERule rule, Resource baseNode) {
        if (rule.getProcessors().size() > 0) {
            return addAttributeCondition(rule.getProcessors().keySet(), vocab.processorPropertyPrefixedName, true, baseNode);
        }
        return null;
    }

    /**
     * (Optimized version) Adds the condition for save:processor property (save:processorList)
     * @param rule rule to take processors from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addProcessorListCondition(SAVERule rule, Resource baseNode) {
        if (rule.getProcessors().size() > 0) {
            return addSPARQLListAttributeCondition(rule.getProcessors().keySet(), vocab.processorPropertyPrefixedName, true, baseNode);
        } else {
            // add default data
            return null;
//            return addSPARQLListAttributeCondition(
//                    Collections.singletonList(vocab.rootMap.get(vocab.processorPropertyPrefixedName)),
//                    vocab.processorPropertyPrefixedName, false, baseNode);
        }
    }

    /**
     * (Regular version) Adds the condition for save:hasDataSubject property
     * @param rule rule to take data subjects from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addDataSubjectCondition(SAVERule rule, Resource baseNode) {
        if (rule.getDataSubjects().size() > 0) {
            return addAttributeCondition(rule.getDataSubjects().keySet(), vocab.dataSubjectPropertyPrefixedName, true, baseNode);
        }
        return null;
    }

    /**
     * (Optimized version) Adds the condition for save:hasDataSubject property (save:hasDataSubjectList)
     * @param rule rule to take data subjects from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addDataSubjectListCondition(SAVERule rule, Resource baseNode) {
        if (rule.getDataSubjects().size() > 0) {
            return addSPARQLListAttributeCondition(rule.getDataSubjects().keySet(), vocab.dataSubjectPropertyPrefixedName, true, baseNode);
        } else {
            // add default data
            return null;
//            return addSPARQLListAttributeCondition(
//                    Collections.singletonList(vocab.rootMap.get(vocab.dataSubjectPropertyPrefixedName)),
//                    vocab.dataSubjectPropertyPrefixedName, false, baseNode);
        }
    }

    /**
     * (Regular version) Adds the condition for save:responsibleParty property
     * @param rule rule to take responsible parties from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addResponsiblePartyCondition(SAVERule rule, Resource baseNode) {
        if (rule.getResponsibleParties().size() > 0) {
            return addAttributeCondition(rule.getResponsibleParties().keySet(), vocab.responsiblePartyPropertyPrefixedName, true, baseNode);
        }
        return null;
    }

    /**
     * (Optimized version) Adds the condition for save:responsibleParty property (save:responsiblePartyList)
     * @param rule rule to take responsible parties from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addResponsiblePartyListCondition(SAVERule rule, Resource baseNode) {
        if (rule.getResponsibleParties().size() > 0) {
            return addSPARQLListAttributeCondition(rule.getResponsibleParties().keySet(), vocab.responsiblePartyPropertyPrefixedName, true, baseNode);
        } else {
            // add default data
            return null;
//            return addSPARQLListAttributeCondition(
//                    Collections.singletonList(vocab.rootMap.get(vocab.responsiblePartyPropertyPrefixedName)),
//                    vocab.responsiblePartyPropertyPrefixedName, false, baseNode);
        }
    }

    /**
     * (Regular version) Adds the condition for save:sender property
     * @param rule rule to take senders from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addSenderCondition(SAVERule rule, Resource baseNode) {
        if (rule.getSenders().size() > 0) {
            return addAttributeCondition(rule.getSenders().keySet(), vocab.senderPropertyPrefixedName, true, baseNode);
        }
        return null;
    }

    /**
     * (Optimized version) Adds the condition for save:sender property (save:senderList)
     * @param rule rule to take senders from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addSenderListCondition(SAVERule rule, Resource baseNode) {
        if (rule.getSenders().size() > 0) {
            return addSPARQLListAttributeCondition(rule.getSenders().keySet(), vocab.senderPropertyPrefixedName, true, baseNode);
        } else {
            // add default data
            return null;
//            return addSPARQLListAttributeCondition(
//                    Collections.singletonList(vocab.rootMap.get(vocab.senderPropertyPrefixedName)),
//                    vocab.senderPropertyPrefixedName, false, baseNode);
        }
    }

    /**
     * (Regular version) Adds the condition for save:recipient property
     * @param rule rule to take recipients from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addRecipientCondition(SAVERule rule, Resource baseNode) {
        if (rule.getRecipients().size() > 0) {
            return addAttributeCondition(rule.getRecipients().keySet(), vocab.recipientPropertyPrefixedName, true, baseNode);
        }
        return null;
    }

    /**
     * (Optimized version) Adds the condition for save:recipient property (save:recipientList)
     * @param rule rule to take recipients from
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addRecipientListCondition(SAVERule rule, Resource baseNode) {
        if (rule.getRecipients().size() > 0) {
            return addSPARQLListAttributeCondition(rule.getRecipients().keySet(), vocab.recipientPropertyPrefixedName, true, baseNode);
        } else {
            // add default data
            return null;
//            return addSPARQLListAttributeCondition(
//                    Collections.singletonList(vocab.rootMap.get(vocab.recipientPropertyPrefixedName)),
//                    vocab.recipientPropertyPrefixedName, false, baseNode);
        }
    }

    /**
     * For regular version adds a sh:class or sh:value conditions
     * @param values values to add
     * @param attribute name of the attribute
     * @param isValue whether the values are classes or individuals
     * @param baseNode the node to attach to
     * @return triple added to the model
     */
    private Statement addAttributeCondition(Collection<String> values, String attribute, boolean isValue,
                                            Resource baseNode) {
        Statement classNodes;
        if (values.size() > 1) {
            RDFList dataList = null;
            Statement node;
            for (String val : values) {
                if (isValue) {
                    node = addValueStatement(val);
                } else {
                    node = addClassStatement(val);
                }

                if (dataList == null) {
                    dataList = infModel.createList(node.getSubject());
                } else {
                    dataList.add(node.getSubject());
                }
            }
            classNodes = addOrListStatement(dataList, null);
        } else {
            if (isValue) {
                classNodes = addValueStatement(values.iterator().next());
            } else {
                classNodes = addClassStatement(values.iterator().next());
            }
        }
        addCountStatement(1, classNodes.getSubject(), SH.minCount);
        addPathStatement(attribute, classNodes.getSubject());
        Statement propertyNode = addPropertyStatement(classNodes.getSubject());
        return addConditionStatement(propertyNode.getSubject(), baseNode);
    }

    /**
     * For optimized version adds the SPARQL constraint to the rule
     * @param values values to add
     * @param attribute the name of the attribute
     * @param isValue whether the values are classed or individuals
     * @param baseNode the node to attach to
     * @return triple added to the model
     */
    private Statement addSPARQLListAttributeCondition(Collection<String> values, String attribute,
                                                      boolean isValue, Resource baseNode) {

        Statement sparqlNode;
        sparqlNode = addSparqlStatement(values, null, isValue);
        boolean isRoot = values.contains(vocab.rootMap.get(attribute));
        if(!isRoot && isValue) {
            addCountStatement(1, sparqlNode.getSubject(), SH.minCount);
        }
        addPathStatement(attribute + "List", sparqlNode.getSubject());
        Statement propertyNode = addPropertyStatement(sparqlNode.getSubject());
        return addConditionStatement(propertyNode.getSubject(), baseNode);
    }

    /**
     * For optimized version, adds SPARQL condition constraint to the rule
     * @param listValues values to insert into the query
     * @param baseNode the node to attach to
     * @param isValue whether the values are classes or individuals
     * @return triple added to the model
     */
    private Statement addSparqlStatement(Collection<String> listValues, Resource baseNode, boolean isValue) {
        Resource subject;
        if (baseNode != null) {
            subject = baseNode;
        } else {
            subject = infModel.createResource();
        }
        Statement selectStmt = addSelectStatement(listValues, null, isValue);
        Statement sparqlStmt = infModel.createStatement(subject, SH.sparql, selectStmt.getSubject());
        infModel.add(sparqlStmt);
        return sparqlStmt;
    }

    /**
     * For optimized version, adds to select for attribute condition
     * @param listValues values to insert
     * @param baseNode the node to attach to
     * @param isValue whether the values are classes or individuals
     * @return triple added to the model
     */
    private Statement addSelectStatement(Collection<String> listValues, Resource baseNode, boolean isValue) {
        Resource subject;
        if (baseNode != null) {
            subject = baseNode;
        } else {
            subject = infModel.createResource();
        }
        String select = "";
        if (isValue) {
            select += """
                    SELECT $this ?value
                          WHERE
                            {   { SELECT  $this (SUM(if(?intersects, 1, 0)) AS ?nIntersect)
                                  WHERE
                                    { 
                                       OPTIONAL {
                                        $this $PATH ?value .
                                        ?value rdf:rest*/rdf:first ?item .
                                        BIND (?item IN (%s) as ?intersects)
                                      }
                                    }
                                  GROUP BY $this
                                }
                                FILTER (?nIntersect = 0)
                                
                                
                           } 
                    """;
        } else {
            select += """
                        SELECT $this ?value
                          WHERE
                            {   { SELECT  $this (SUM(if(?intersects, 1, 0)) AS ?nIntersect)
                                  WHERE
                                    { 
                                       OPTIONAL {
                                        $this $PATH ?value .
                                        ?value (rdf:rest)*/rdf:first ?item .
                                        ?item rdf:type/(rdfs:subClassOf)* ?attrClass .
                                        BIND(( ?attrClass IN (%s) ) AS ?intersects)
                                      }
                                    }
                                  GROUP BY $this
                                }
                                FILTER (?nIntersect = 0)
                                
                                
                           }                
                    """;
        }

        select = String.format(select, String.join(", ", listValues));
//        System.out.println(select);
        select = SPARQLUtils.addPrefixesToSparqlString(select, infModel);
        Statement selectStmt = infModel.createStatement(subject, SH.select, infModel.createLiteral(select));
        infModel.add(selectStmt);
        return selectStmt;
    }

    /**
     * For regular version, adds a sh:or for the multiple values of the attribute constraint
     * @param value the RDFList with values
     * @param baseNode node to attach the triple to
     * @return the triple added to the model
     */
    private Statement addOrListStatement(RDFList value, Resource baseNode) {
        Resource subject;
        if (baseNode != null) {
            subject = baseNode;
        } else {
            subject = infModel.createResource();
        }
        Property predicate = SH.or;
        Statement node = infModel.createStatement(subject, predicate, value);
        infModel.add(node);
        return node;
    }

    /**
     * For regular version, add a constraint on the value of the constant DPT
     * @param value the individual name
     * @return triple added to the model
     */
    private Statement addValueStatement(String value) {
        return addClassOrValueStatement(value, SH.hasValue);
    }

    /**
     * For regular version, add a constraint on the class of the hierarchical DPT
     * @param value the class name
     * @return triple added to the model
     */
    private Statement addClassStatement(String value) {
        return addClassOrValueStatement(value, SH.class_);
    }

    /**
     * For regular version, add a constraint on the class or value of the DPT
     * @param value the value name
     * @return triple added to the model
     */
    private Statement addClassOrValueStatement(String value, Property property) {
        Resource subject = infModel.createResource();

        value = replacePrefixWithURI(value);
        Resource object = infModel.createResource(value);
        Statement node = infModel.createStatement(subject, property, object);
        infModel.add(node);
        return node;
    }

    /**
     * For regular version, add a construct SPARQL statement for one rule
     * @param baseNode node to attach the triple to
     * @param ruleName the name of the rule to add to the query
     * @param ruleType the type of the rule
     * @return triple added to the model
     */
    private Statement addRuleConstruct(Resource baseNode, String ruleName, String ruleType) {
        String query = """
                CONSTRUCT {
                	$this %s %s .
                	$this %s %s .
                }
                WHERE {}
                """;
        query = String.format(query,
                (ruleType.endsWith("Permission") || ruleType.endsWith("Dispensation")) ? vocab.conformsPropertyPrefixedName : vocab.prohibitedPropertyPrefixedName,
                ruleName, vocab.answerPropertyPrefixedName,
                (ruleType.endsWith("Permission") || ruleType.endsWith("Dispensation")) ? "\"permitted\"" : "\"prohibited\"");
        query = SPARQLUtils.addPrefixesToSparqlString(query, infModel);
//        System.out.println(query);
        return addConstruct(query, baseNode);
    }

    /**
     * For optimized version, add a construct SPARQL statement for one rule
     * @param baseNode node to attach the triple to
     * @param rule the rule to add to the query
     * @return triple added to the model
     */
    private Statement addRuleConstructOptimized(Resource baseNode, SAVERule rule) {
        String query = """
                
                CONSTRUCT {
                        ?bnode  a save-ex:RuleIntersection ;
                       %1$s
                       %2$s %3$s ;
                       %4$s %5$s .
                       
                 
                $this   %6$s ?bnode .
                }
                WHERE {
                BIND(BNODE() AS ?bnode) 
                %7$s
                }
                """;
        String subselects = "";
        String intersections = "";
        if (!rule.getData().isEmpty()) {
            intersections += getRuleConstructIntersectionAssertion(vocab.dataPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.dataPropertyPrefixedName, rule.getData().values(), false);
        } else {
            intersections += getRuleConstructIntersectionAssertion(vocab.dataPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.dataPropertyPrefixedName, Collections.singletonList(vocab.rootMap.get(vocab.dataPropertyPrefixedName)), false);
        }
        if (!rule.getActions().isEmpty()) {
            intersections += getRuleConstructIntersectionAssertion(vocab.actionPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.actionPropertyPrefixedName, rule.getActions().values(), false);
        } else {
            intersections += getRuleConstructIntersectionAssertion(vocab.actionPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.actionPropertyPrefixedName, Collections.singletonList(vocab.rootMap.get(vocab.actionPropertyPrefixedName)), false);
        }
        if (!rule.getPurposes().isEmpty()) {
            intersections += getRuleConstructIntersectionAssertion(vocab.purposePropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.purposePropertyPrefixedName, rule.getPurposes().values(), false);
        } else {
            intersections += getRuleConstructIntersectionAssertion(vocab.purposePropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.purposePropertyPrefixedName, Collections.singletonList(vocab.rootMap.get(vocab.purposePropertyPrefixedName)), false);
        }
        if (!rule.getLegalBases().isEmpty()) {
            intersections += getRuleConstructIntersectionAssertion(vocab.legalBasisPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.legalBasisPropertyPrefixedName, rule.getLegalBases().values(), false);
        } else {
            intersections += getRuleConstructIntersectionAssertion(vocab.legalBasisPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.legalBasisPropertyPrefixedName, Collections.singletonList(vocab.rootMap.get(vocab.legalBasisPropertyPrefixedName)), false);
        }
        if (!rule.getMeasures().isEmpty()) {
            intersections += getRuleConstructIntersectionAssertion(vocab.measurePropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.measurePropertyPrefixedName, rule.getMeasures().values(), false);
        } else {
            intersections += getRuleConstructIntersectionAssertion(vocab.measurePropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.measurePropertyPrefixedName, Collections.singletonList(vocab.rootMap.get(vocab.measurePropertyPrefixedName)), false);
        }
        if (!rule.getControllers().isEmpty()) {
            intersections += getRuleConstructIntersectionAssertion(vocab.controllerPropertyPrefixedName, true);
            subselects += getRuleConstructSubselect(vocab.controllerPropertyPrefixedName, rule.getControllers().keySet(), true);
        } else {
            intersections += getRuleConstructIntersectionAssertion(vocab.controllerPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.controllerPropertyPrefixedName, Collections.singletonList(vocab.rootMap.get(vocab.controllerPropertyPrefixedName)), false);
        }
        if (!rule.getProcessors().isEmpty()) {
            intersections += getRuleConstructIntersectionAssertion(vocab.processorPropertyPrefixedName, true);
            subselects += getRuleConstructSubselect(vocab.processorPropertyPrefixedName, rule.getProcessors().keySet(), true);
        } else {
            intersections += getRuleConstructIntersectionAssertion(vocab.processorPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.processorPropertyPrefixedName, Collections.singletonList(vocab.rootMap.get(vocab.processorPropertyPrefixedName)), false);
        }
        if (!rule.getDataSubjects().isEmpty()) {
            intersections += getRuleConstructIntersectionAssertion(vocab.dataSubjectPropertyPrefixedName, true);
            subselects += getRuleConstructSubselect(vocab.dataSubjectPropertyPrefixedName, rule.getDataSubjects().keySet(), true);
        } else {
            intersections += getRuleConstructIntersectionAssertion(vocab.dataSubjectPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.dataSubjectPropertyPrefixedName, Collections.singletonList(vocab.rootMap.get(vocab.dataSubjectPropertyPrefixedName)), false);
        }
        if (!rule.getResponsibleParties().isEmpty()) {
            intersections += getRuleConstructIntersectionAssertion(vocab.responsiblePartyPropertyPrefixedName, true);
            subselects += getRuleConstructSubselect(vocab.responsiblePartyPropertyPrefixedName, rule.getResponsibleParties().keySet(), true);
        } else {
            intersections += getRuleConstructIntersectionAssertion(vocab.responsiblePartyPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.responsiblePartyPropertyPrefixedName, Collections.singletonList(vocab.rootMap.get(vocab.responsiblePartyPropertyPrefixedName)), false);
        }
        if (!rule.getSenders().isEmpty()) {
            intersections += getRuleConstructIntersectionAssertion(vocab.senderPropertyPrefixedName, true);
            subselects += getRuleConstructSubselect(vocab.senderPropertyPrefixedName, rule.getSenders().keySet(), true);
        } else {
            intersections += getRuleConstructIntersectionAssertion(vocab.senderPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.senderPropertyPrefixedName, Collections.singletonList(vocab.rootMap.get(vocab.senderPropertyPrefixedName)), false);
        }
        if (!rule.getRecipients().isEmpty()) {
            intersections += getRuleConstructIntersectionAssertion(vocab.recipientPropertyPrefixedName, true);
            subselects += getRuleConstructSubselect(vocab.recipientPropertyPrefixedName, rule.getRecipients().keySet(), true);
        } else {
            intersections += getRuleConstructIntersectionAssertion(vocab.recipientPropertyPrefixedName, false);
            subselects += getRuleConstructSubselect(vocab.recipientPropertyPrefixedName, Collections.singletonList(vocab.rootMap.get(vocab.recipientPropertyPrefixedName)), false);
        }

        query = String.format(query,
                intersections, vocab.rulePropertyPrefixedName,
                rule.getName(), vocab.answerPropertyPrefixedName,
                (rule.getType().endsWith("Permission") || rule.getType().endsWith("Dispensation")) ? "\"permitted\"" : "\"prohibited\"",
                (rule.getType().endsWith("Permission") || rule.getType().endsWith("Dispensation")) ? vocab.conformsPropertyPrefixedName : vocab.prohibitedPropertyPrefixedName,
                subselects);
        query = SPARQLUtils.addPrefixesToSparqlString(query, infModel);
//        System.out.println(query);
        return addConstruct(query, baseNode);
    }

    /**
     * For optimized procedure, create an assertion of the intersection for the attribute
     * @param attributePrefixedName the name of the attribute
     * @param isValue whether the attribute is hierarchical (by class) or constant (by value)
     * @return the intersection part of the query
     */
    private String getRuleConstructIntersectionAssertion(String attributePrefixedName, boolean isValue) {
        String intersection = String.format("%1$s:%2$sIntersection ?%2$sItem ;\n", vocab.savePrefix, attributePrefixedName.split(":")[1]);
        if (!isValue) {
            intersection += String.format("%1$s:%2$sClassIntersection ?%2$sClass ;\n", vocab.savePrefix, attributePrefixedName.split(":")[1]);
        }
        return intersection;
    }

    /**
     * For optimized version, constructs the subselect for one attribute
     * @param attribute the attribute name
     * @param values the attribute values
     * @param isValue whether the attribute is hierarchical (by class) or constant (by value)
     * @return the subquery
     */
    private String getRuleConstructSubselect(String attribute, Collection<String> values, boolean isValue) {
        String query;
        if (!isValue) {
            query = """
                    {SELECT *
                       WHERE {
                           {
                                $this save:%1$sList ?%1$sVal .
                                ?%1$sVal rdf:rest*/rdf:first ?%1$sItem .
                                ?%1$sItem rdf:type/rdfs:subClassOf* ?%1$sClass .
                                FILTER (?%1$sClass IN (%2$s))
                           }
                           UNION {
                                FILTER NOT EXISTS {$this save:%1$sList ?%1$sVal .}
                                
                    """;

        } else {
            query = """
                    {SELECT *
                       WHERE {
                           {
                                $this save:%1$sList ?%1$sVal .
                                ?%1$sVal rdf:rest*/rdf:first ?%1$sItem .
                                FILTER (?%1$sItem IN (%2$s))
                           }
                           UNION {
                                FILTER NOT EXISTS {$this save:%1$sList ?%1$sVal .}
                                
                    """;


        }
        query = String.format(query, attribute.split(":")[1], String.join(", ", values));
        boolean firstBind = true;
        for(String value: values){
            String bind = (firstBind ? "\t\t\t" : "\t\t\tUNION") +( (isValue)? "{BIND(%1$s as ?%2$sItem) . }\n"
                    : "{BIND(%1$s as ?%2$sItem) . BIND(%3$s as ?%2$sClass) .}\n");
            bind = String.format(bind, isValue ? value : getDefaultIndividual(value), attribute.split(":")[1], 
                    isValue ? "" : value );
            query += bind;
            firstBind = false;
        }
        query += "}}}";
//        System.out.println(query);
        return query;
    }

    /**
     * For a SAVE concept, get the first individual that can be found - default
     * @param concept the SAVE class
     * @return the individual or null if nothing is found
     */
    private Object getDefaultIndividual(String concept) {
        String individ = null;
        StmtIterator stmt = unionModel.listStatements(null, RDF.type, unionModel.getResource(replacePrefixWithURI(concept)));
        if(stmt.hasNext()){
            individ = SPARQLUtils.getPrefixedLabel(stmt.next().getSubject(), unionModel);
        }
        return individ;
        
    }


    /**
     * Adds construct statement to a SHACL rule
     * @param query the construct SPARQL query
     * @param baseNode the node to attach to
     * @return the triple added to the model
     */
    private Statement addConstruct(String query, Resource baseNode) {
        Statement constructStatement = infModel.createStatement(baseNode,
                SH.construct,
                infModel.createTypedLiteral(query));
        infModel.add(constructStatement);
        return constructStatement;
    }

    /**
     * Adds sh:order to SHACL rule
     * @param value the order value
     * @param baseNode the node to attach to
     * @return triple added to the model
     */
    private Statement addOrder(int value, Resource baseNode) {
        Statement orderStatement = infModel.createStatement(baseNode,
                SH.order,
                infModel.createTypedLiteral(value));
        infModel.add(orderStatement);
        return orderStatement;
    }

    /**
     * Adds sh:prefixes to SHACL shapes
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addPrefixes(Resource baseNode) {
        Statement prefixesStatement = infModel.createStatement(baseNode,
                SH.prefixes,
                infModel.createTypedLiteral(":"));
        infModel.add(prefixesStatement);
        return prefixesStatement;
    }

    /**
     * Adds a rule statement to the model
     * @param baseNode object of the rule (usually blank node)
     * @param subject subject to attach to
     * @return triple added to the model
     */
    private Statement addRuleStmt(Resource baseNode, Resource subject) {
        Statement ruleStatement = infModel.createStatement(subject,
                SH.rule,
                baseNode);
        infModel.add(ruleStatement);
        return ruleStatement;
    }

    /**
     * For regular procedure, adds the final rule (for subrequests) that infers the answer for one rule
     * @param subject the subject to asstach to (usually blank node)
     * @return triple added to the model
     */
    private Statement addFinalRule(Resource subject) {
        Statement typeStatement = addRuleType();
        Resource baseNode = typeStatement.getSubject();
        Statement labelStatement = infModel.createStatement(baseNode,
                RDFS.label,
                infModel.createLiteral("Infer if $this is compliant"));
        infModel.add(labelStatement);
        addParentChildCondition(baseNode, true);
        addParentChildCondition(baseNode, false, SH.minCount, 1);
        addPolicyConstruct(baseNode);
        addOrder(100, baseNode);
        //addPrefixes(baseNode);
        return addRuleStmt(baseNode, subject);
    }

    /**
     * For regular version, add the condition for parent or child
     * so subrequests/requests don't get checked for the rules that are not for them
     * @param subject subject node to attach to
     * @param parent whther it is a parent condition
     * @param prop property - sh:minCount or sh:maxCount
     * @param value the value of the count
     * @return triple added to the model
     */
    private Statement addParentChildCondition(Resource subject, boolean parent, Property prop, int value) {
        Statement pathStatement = addPathStatement(parent ? vocab.parentPropertyPrefixedName : vocab.childPropertyPrefixedName, null);
        addCountStatement(value, pathStatement.getSubject(), prop);
        Statement propertyStatement = addPropertyStatement(pathStatement.getSubject());
        return addConditionStatement(propertyStatement.getSubject(), subject);
    }

    /**
     * For regular version, add the final policy construct that infers the answer to the whole query
     * @param baseNode node to attach to
     * @return triple added to the model
     */
    private Statement addPolicyConstruct(Resource baseNode) {
        String query = """
                CONSTRUCT {
                	$this %1$s ?total .
                	$this %2$s ?granted .
                	$this %3$s ?prohibited .
                    $this %4$s ?perm .
                    $this %5$s ?proh .
                }
                WHERE {
                	{SELECT $this (COUNT(*) AS ?total)
                	WHERE {
                		?s %6$s $this .
                	}
                	group by $this
                	}
                	{SELECT $this (SUM(IF(?p,1,0)) AS ?granted)
                	WHERE {
                		?s %6$s $this .
                		bind(exists { ?s %7$s \"permitted\" } as ?p)
                	}
                	group by $this
                	}
                			{
                	SELECT $this (SUM(IF(?proh,1,0)) AS ?prohibited)
                	WHERE {
                		?s %6$s $this .
                		bind(exists { ?s %7$s \"prohibited\" } as ?proh)
                	}
                	group by $this
                	}
                	BIND ( IF ( ?total = ?granted, \"granted\", IF( ?granted > 0, \"part-granted\", 'not granted' )) AS ?perm )
                	BIND ( IF ( ?total = ?prohibited, \"prohibited\" , IF ( ?prohibited > 0, \"part-prohibited\", 'not prohibited' ) ) AS ?proh )
                }
                """;
        query = String.format(query, vocab.nChildrenPropertyPrefixedName, vocab.nPermittedPropertyPrefixedName,
                vocab.nProhibitedPropertyPrefixedName, vocab.answerPermittedPropertyPrefixedName,
                vocab.answerProhibitedPropertyPrefixedName, vocab.parentPropertyPrefixedName, vocab.answerPropertyPrefixedName);
        query = SPARQLUtils.addPrefixesToSparqlString(query, infModel);
        return addConstruct(query, baseNode);
    }

    /**
     * Helper that turns prefixed names into URIs
     * @param prefixedName the name of the class or individual
     * @return full URI
     */
    private String replacePrefixWithURI(String prefixedName) {
        return ModelUtils.replacePrefixWithURI(prefixedName, infModel);
    }

    /**
     * Writes the generated policy to the file
     * @param folder folder to write into
     * @param optimized whether the SHACL shapes were created with regular or optimized procedure
     */
    public void writeSHACLPolicyToFile(String folder, boolean optimized) {
        File f = new File(folder);
        boolean dirCreated = f.mkdirs();
        String filename = String.format(folder + "%s.shapes%s.ttl", savePolicy.getName().split(":")[1], optimized ? ".optimized" : "");
        File out = new File(filename);
        try {
            infModel.write(new FileWriter(out), "Turtle");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
