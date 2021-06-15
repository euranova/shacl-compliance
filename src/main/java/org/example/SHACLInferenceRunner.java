package org.example;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Funtional class for preparing the requests and running inference
 */
public class SHACLInferenceRunner {

    private Model unionModel;
    private Model saveModel;
    private Model infModel;
    private Model testModel;
    private SAVEVocabulary vocab;

    /**
     * The class needs different models
     * @param unionModel full model (SAVE, policy, etc.)
     * @param saveModel SAVE ontology
     * @param testModel shapes model only
     */
    public SHACLInferenceRunner(Model unionModel, Model saveModel, Model testModel) {
        this.unionModel = unionModel;
        this.saveModel = saveModel;
        this.testModel = testModel;
        this.vocab = new SAVEVocabulary(unionModel);
        createInfModel();
    }

    /**
     * Creates new empty inference model
     */
    private void createInfModel(){
        if (infModel != null) {
            infModel.removeAll();
        } else {
            infModel = createSingleModel();
        }
//        infModel.add(unionModel);

    }

    /**
     * Creates new separate inference model
     * @return the empty model with prefixes
     */
    private Model createSingleModel(){
        Model singleModel = JenaUtil.createMemoryModel();
        singleModel.setNsPrefixes(unionModel.getNsPrefixMap());
        if(singleModel.getNsPrefixURI(vocab.saveExPrefix) == null){
            singleModel.setNsPrefix(vocab.saveExPrefix, SAVEVocabulary.SAVEEXURI);
        }
        if(singleModel.getNsPrefixURI(vocab.savePrefix) == null){
            singleModel.setNsPrefix(vocab.savePrefix, SAVEVocabulary.SAVEURI);
        }
        return singleModel;
    }

    /**
     * Performs compliance checking on one normalized SAVE request in a Core way - by materializing each subrequest
     * and checking in batches (if needed)
     * @param ruleNormalized the request (normalized)
     * @param stopIfTooManySubrequests stop if the amount of subrequests > nBatches*batchSize
     * @param mode mode of the test for the result
     * @param verbose whether to output intermediate logs
     * @return the result object with stats and inferred triples
     * @throws Exception if the number of subrequests is bigger than the batches can handle and stopIfTooManySubrequests == true
     */
    public SHACLComplianceResult checkNormalizedSAVERuleCore(SAVERuleNormalized ruleNormalized,
                                                                boolean stopIfTooManySubrequests,
                                                                SHACLComplianceResult.Mode mode,
                                                                boolean verbose) throws Exception {
        SHACLComplianceResult result = new SHACLComplianceResult(1, mode, unionModel);
        int i = 0;
        List<String> children = new ArrayList<>();
        List<Long> execTimes = new ArrayList<>();
        int nBatches = 10;
        int batchSize = 1000;
        if(stopIfTooManySubrequests && ruleNormalized.getCombinations().size() > nBatches * batchSize + 1){
            throw new Exception(String.format("Too many subrequests - %d, this method could only do %d, try the SPARQL version instead!",
                    ruleNormalized.getCombinations().size(), nBatches*batchSize));
        }
        result.addnSubrequests(ruleNormalized.getCombinations().size());
        for (List<String> comb: ruleNormalized.getCombinations()){
            String subRequestName = addChildSAVERuleToModel(ruleNormalized.getAttributes(), comb, ruleNormalized.getName(), ruleNormalized.getType(),
                    i, ruleNormalized.getName(), null);
            children.add(subRequestName);
            result = runInferenceOnSingleRequest(subRequestName, result, null, true, false, verbose);
            if((i % batchSize == 0 && i > 0) || i == ruleNormalized.getCombinations().size() - 1){
                //add parent to the new batch
                addParentSAVERuleToModel(ruleNormalized, children, null);
                //run test inference on the batch
                execTimes.add(runInferenceOnBatch(i, result, null, true, verbose));
                OptionalDouble elapsed = execTimes.stream().mapToDouble(a -> a).average();
                double sum = execTimes.stream().mapToDouble(a -> a).sum();
                if (verbose) {
                    System.out.println("Full time for all subqueries so far is " + sum
                            + "\n Avg running time on " + children.size() + " requests is " + elapsed.toString()
                            + " ms, i.e., " + elapsed.orElse(0) / children.size() + "ms per request");
                    System.out.println("Avg running time on 1 requests (separately) was " +
                            result.getExecTimePerSubrequest().stream().mapToDouble(a -> a).average().toString() + " ms");
                }
                createInfModel();
                children = new ArrayList<>();
//                requestExecTimes = new ArrayList<>();
                nBatches--;
                if (nBatches == 0){
                    break;
                }
            }
            i++;
        }
        double totalTime = execTimes.stream().mapToDouble(a -> a).sum();
        result.setnProcessed(1);
        result.addExecTimePerRequest(totalTime);
        result.setExecTimeTotal(totalTime);
        return result;
    }

    /**
     * Performs compliance checking for a batch of atomic requests using Core procedure
     * @param rulesNormalized batch of requests (atomic, in normalized format)
     * @param mode mode of the test for the result
     * @param verbose whether to output the logs
     * @return the result with stats and inferred triples
     */
    public SHACLComplianceResult checkNormalizedSAVERulesBatchAtomicCore(List<SAVERuleNormalized> rulesNormalized,
                                                                            SHACLComplianceResult.Mode mode, boolean verbose) {
        SHACLComplianceResult result = new SHACLComplianceResult(rulesNormalized.size(), mode, unionModel);
        Model singleModel = createSingleModel();
        for (SAVERuleNormalized ruleNormalized: rulesNormalized){
            if(ruleNormalized.getCombinations().size() > 1){
                throw new IllegalArgumentException(String.format("%s: this rule is not atomic", ruleNormalized.getName()));
            }
            result.addnSubrequests(1);
            singleModel.removeAll();
            List<String> comb = ruleNormalized.getCombinations().get(0);
            String subRequestName = addChildSAVERuleToModel(ruleNormalized.getAttributes(), comb, ruleNormalized.getName(), ruleNormalized.getType(),
                    0, ruleNormalized.getName(), singleModel);
            runInferenceOnSingleRequest(subRequestName, result, singleModel, true, false, verbose);
            //add parent to the new batch
            addParentSAVERuleToModel(ruleNormalized, Collections.singletonList(subRequestName), singleModel);
            result.addExecTimePerRequest(runInferenceOnBatch(0, result, singleModel, false, verbose));

        }
        result.setExecTimeTotal(runInferenceOnBatch(0, result, null, true, verbose));
        result.setnProcessed(rulesNormalized.size());
        return result;
    }

    /**
     * Performs compliance checking for a batch of atomic requests using SPARQL procedure
     * @param rulesNormalized batch of requests (atomic, in normalized format)
     * @param mode mode of the test for the result
     * @param verbose whether to output the logs
     * @return the result with stats and inferred triples
     */
    public SHACLComplianceResult checkNormalizedSAVERulesBatchAtomicSPARQL(List<SAVERuleNormalized> rulesNormalized,
                                                                              SHACLComplianceResult.Mode mode,
                                                                               boolean verbose) {
        SHACLComplianceResult result = new SHACLComplianceResult(rulesNormalized.size(), mode, unionModel);
        for (SAVERuleNormalized ruleNormalized: rulesNormalized){
            if(ruleNormalized.getCombinations().size() > 1){
                throw new IllegalArgumentException(String.format("%s: this rule is not atomic", ruleNormalized.getName()));
            }
            result.addnSubrequests(1);
            String normRequestName= addSPARQLSAVERuleToModel(ruleNormalized);
            result  = runInferenceOnSingleRequest(normRequestName, result, null,false, false, verbose);
            if(verbose) {
                System.out.println("Avg running time on 1 requests (separately) was " +
                        result.getExecTimePerRequest().get(result.getExecTimePerRequest().size() - 1) + " ms");
            }

        }
        result.setExecTimeTotal(runInferenceOnBatch(0, result, null, true, verbose));
        result.setnProcessed(rulesNormalized.size());
        return result;
    }

    private long runInferenceOnBatch(int i, SHACLComplianceResult result, Model model, boolean infer, boolean verbose) {
        if(model == null){
            model = infModel;
        }
        if(verbose){
            //add subrequests for explainability
            result.addSubrequests(model);
        }
        model.add(unionModel);
        Instant start = Instant.now();
        Model triples = RuleUtil.executeRules(model, testModel, null, null);
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        if (infer) {
            result.addInferredTriples(triples);
        }
        final List<Statement> statements = triples.listStatements().toList();
//        if (statements != null && !statements.isEmpty()){
//            System.out.println("Batch "+ i + "\tInferred " + statements.size() +
//                    " triples in " + timeElapsed + " ms");
//        }
        model.remove(unionModel);
//        triples.setNsPrefixes(infModel.getNsPrefixMap());
//        System.out.println(ModelPrinter.get().print(result));
        return timeElapsed;
    }

    /**
     * Performs the inference using focusNode on one single request
     * @param name name of the request
     * @param result result object to add triples to
     * @param model model to check against
     * @param subrequest whether it is a subrequest (or full request for SPARQL version)
     * @param infer whether to add triples to inference model
     * @param verbose whether to output the logs
     * @return the result with stats and inferred triples
     */
    private SHACLComplianceResult runInferenceOnSingleRequest(String name, SHACLComplianceResult result, Model model,
                                                              boolean subrequest, boolean infer, boolean verbose) {
        if (model == null){
            model = infModel;
        }
        if(verbose){
            result.addSubrequests(model);
        }
        model.add(unionModel);
        RDFNode focusNode = model.getRDFNode(model.getResource(replacePrefixWithURI(name)).asNode());
//        System.out.println(ModelPrinter.get().print(infModel));
        Instant start = Instant.now();
        Model triples = RuleUtil.executeRules(focusNode, testModel, null, null);
        triples.setNsPrefixes(model.getNsPrefixMap());
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        if(subrequest) {
            result.addExecTimePerSubrequest(timeElapsed);
        } else {
            // it's a full "SPARQL" request
            result.addExecTimePerRequest(timeElapsed);
            if(infer) {
                result.addInferredTriples(triples);
            }
        }
//
//        final List<Statement> triples = result.listStatements().toList();
//
//        if (triples != null && !triples.isEmpty()){
////            System.out.println("Request "+ name + "\tInferred triples " + triples.size() +
////                    "\tIn " + timeElapsed + " milliseconds");
//        }
        model.remove(unionModel);
//        System.out.println(ModelPrinter.get().print(result));
        return result;
    }

    /**
     * Add an attribute to the materialized subrequest
     * @param attribute attribute name
     * @param value attribute value
     * @param baseNode subject/object for the triple
     * @param valueIsClass whether the value passed is an individual or a class name
     * @param singleModel if not null, use this model to add the triple
     * @return the triple with the attribute added
     */
    private Statement addAttributeStatement(String attribute, String value, Resource baseNode, boolean valueIsClass, Model singleModel){
        // first, if the value is a class, check if there is a corresponding individual
        // if not, create one
        // check in both models, inference and union, write into union!
        Resource valueIndividual;
        if (valueIsClass) {
            valueIndividual = JenaUtils.getAnyIndividualFromConcept(value, vocab.savePrefix, saveModel);
        } else {
            valueIndividual = infModel.createResource(replacePrefixWithURI(value));
        }
        Statement stmt = infModel.createStatement(baseNode,
                infModel.createProperty(replacePrefixWithURI(attribute)),
                //here replace with creation of same-named individual if it doesn't exist in the model or infModel
                valueIndividual);
        infModel.add(stmt);
        if(singleModel != null) {
            singleModel.add(stmt);
        }
        return stmt;
    }


    /**
     * For Core procedure - adds the final "parent" request to the model for inference
     * @param rule parent request
     * @param childrenNames names of all subrequests
     * @param singleModel if not null, use this model to add the triple
     * @return name of the parent request
     */
    public String addParentSAVERuleToModel(SAVERule rule, List<String> childrenNames, Model singleModel){
        //add to model the rule, and all the individuals if they don't exist yet
        //1. create initial statement
//        Statement mainStmt = infModel.createStatement(infModel.createResource(replacePrefixWithURI(rule.getName())),
//                infModel.createProperty(replacePrefixWithURI("rdf:type")),
//                infModel.createResource(replacePrefixWithURI(rule.getType())));
        Statement mainStmt = infModel.createStatement(infModel.createResource(replacePrefixWithURI(rule.getName())),
                RDF.type,
                infModel.createResource(replacePrefixWithURI(rule.getType())));
        infModel.add(mainStmt);
        Resource baseNode = mainStmt.getSubject();
        if (!rule.getData().isEmpty()){
            for (String value: rule.getData().keySet()){
                addAttributeStatement(vocab.dataPropertyPrefixedName, value, baseNode, false, singleModel);
            }
        }
        if (!rule.getActions().isEmpty()){
            for (String value: rule.getActions().keySet()){
                addAttributeStatement(vocab.actionPropertyPrefixedName, value, baseNode, false, singleModel);
            }
        }
        if (!rule.getPurposes().isEmpty()){
            for (String value: rule.getPurposes().keySet()){
                addAttributeStatement(vocab.purposePropertyPrefixedName, value, baseNode, false, singleModel);
            }
        }
        if (!rule.getLegalBases().isEmpty()){
            for (String value: rule.getLegalBases().keySet()){
                addAttributeStatement(vocab.legalBasisPropertyPrefixedName, value, baseNode, false, singleModel);
            }
        }
        if (!rule.getMeasures().isEmpty()){
            for (String value: rule.getMeasures().keySet()){
                addAttributeStatement(vocab.measurePropertyPrefixedName, value, baseNode, false, singleModel);
            }
        }
        if (!rule.getControllers().isEmpty()){
            for (String value: rule.getControllers().keySet()){
                addAttributeStatement(vocab.controllerPropertyPrefixedName, value, baseNode, false, singleModel);
            }
        }
        if (!rule.getProcessors().isEmpty()){
            for (String value: rule.getProcessors().keySet()){
                addAttributeStatement(vocab.processorPropertyPrefixedName, value, baseNode, false, singleModel);
            }
        }
        if (!rule.getDataSubjects().isEmpty()){
            for (String value: rule.getDataSubjects().keySet()){
                addAttributeStatement(vocab.dataSubjectPropertyPrefixedName, value, baseNode, false, singleModel);
            }
        }
        if (!rule.getResponsibleParties().isEmpty()){
            for (String value: rule.getResponsibleParties().keySet()){
                addAttributeStatement(vocab.responsiblePartyPropertyPrefixedName, value, baseNode, false, singleModel);
            }
        }
        if (!rule.getSenders().isEmpty()){
            for (String value: rule.getSenders().keySet()){
                addAttributeStatement(vocab.senderPropertyPrefixedName, value, baseNode, false, singleModel);
            }
        }
        if (!rule.getRecipients().isEmpty()){
            for (String value: rule.getRecipients().keySet()){
                addAttributeStatement(vocab.recipientPropertyPrefixedName, value, baseNode, false, singleModel);
            }
        }
        // add child and parent things
        if (childrenNames != null){
            //it is a child
            for (String name: childrenNames){
                Statement stmt = addAttributeStatement(vocab.childPropertyPrefixedName, name, baseNode, false, singleModel);
            }
        }
        return rule.getName();
    }

    /**
     * For Core procedure, adds a subrequest to the model
     * @param attributes list of attribute names of the subrequest
     * @param combination list of attribute values of the subrequest
     * @param ruleName name of the sunrequest
     * @param ruleType type of the subrequest
     * @param number index of the subrequest
     * @param parentName name of the parent request
     * @param singleModel if not null, use this model to add the request triples
     * @return name of the child request (subrequest)
     */
    public String addChildSAVERuleToModel(List<String> attributes, List<String> combination, String ruleName, String ruleType, int number,
                                          String parentName, Model singleModel){
        //add to model the rule, and all the individuals if they don't exist yet
        //create initial statement
        Statement mainStmt = infModel.createStatement(infModel.createResource(replacePrefixWithURI(ruleName + "_"+ number)),
                RDF.type,
                infModel.createResource(replacePrefixWithURI(ruleType)));
        infModel.add(mainStmt);
        if (singleModel != null) {
            singleModel.add(mainStmt);
        }
        Resource baseNode = mainStmt.getSubject();
        for (int i=0; i < attributes.size(); i++){
            Statement stmt = addAttributeStatement(attributes.get(i), combination.get(i), baseNode, vocab.isClassAttribute(attributes.get(i)),
                    singleModel);
        }
        // add child and parent things
        if (parentName != null){
            Statement stmt = addAttributeStatement(vocab.parentPropertyPrefixedName, parentName, baseNode, false,
                    singleModel);
        }
        return ruleName + "_"+ number;
    }

    /**
     * Performs compliance check of the request suing the SPARQL version
     * @param ruleNormalized request in normalized form
     * @param mode mode of the test for the result
     * @param verbose whether to output the logs
     * @return result with stats and inferred triples
     */
    public SHACLComplianceResult checkNormalizedSAVERuleSPARQL(SAVERuleNormalized ruleNormalized,
                                                                  SHACLComplianceResult.Mode mode,
                                                                  boolean verbose) {
        SHACLComplianceResult result = new SHACLComplianceResult(1, mode, unionModel);
        result.addnSubrequests(ruleNormalized.getCombinations().size());
        String normRequestName= addSPARQLSAVERuleToModel(ruleNormalized);
        result  = runInferenceOnSingleRequest(normRequestName, result, null,false, true, verbose);
        if(verbose) {
            System.out.println("Avg running time on 1 requests (separately) was " +
                    result.getExecTimePerRequest().get(result.getExecTimePerRequest().size() - 1) + " ms");
        }
        result.setExecTimeTotal(result.getExecTimePerRequest().get(result.getExecTimePerRequest().size() - 1));
        result.setnProcessed(1);
        return result;

    }

    /**
     * For SPARQL procedure - adds th whole normalized request to the model using RDFList properties
     * @param ruleNormalized the request in normalized form
     * @return the name of the request
     */
    public String addSPARQLSAVERuleToModel(SAVERuleNormalized ruleNormalized){
        Statement mainStmt = infModel.createStatement(infModel.createResource(replacePrefixWithURI(ruleNormalized.getName() + "_opt")),
                RDF.type,
                infModel.createResource(replacePrefixWithURI(ruleNormalized.getType())));
        infModel.add(mainStmt);
        List<String> valueAttrs = new ArrayList<>();
        valueAttrs.add(vocab.controllerPropertyPrefixedName);
        valueAttrs.add(vocab.processorPropertyPrefixedName);
        valueAttrs.add(vocab.dataSubjectPropertyPrefixedName);
        valueAttrs.add(vocab.responsiblePartyPropertyPrefixedName);
        valueAttrs.add(vocab.senderPropertyPrefixedName);
        valueAttrs.add(vocab.recipientPropertyPrefixedName);
        Resource baseNode = mainStmt.getSubject();
        for (int i=0; i < ruleNormalized.getAttributes().size(); i++){
            String attribute = ruleNormalized.getAttributes().get(i);
            Statement stmt = addListAttributeStatement(attribute,
                    ruleNormalized.getValues().get(i), baseNode,
                    !valueAttrs.contains(attribute));
        }
        return ruleNormalized.getName() + "_opt";
    }

    /**
     * For SPARQL procedure, adds one attribute (RDFList) to the request model
     * @param attribute name of the attribute
     * @param values the list of values for this attribute
     * @param baseNode subject/object to attach the triples to
     * @param valueIsClass whether the values sent are classes or individuals
     * @return triple with attribute
     */
    private Statement addListAttributeStatement(String attribute, List<String> values, Resource baseNode, boolean valueIsClass) {
        // first, if the value is a class, check if there is a corresponding individual
        // if not, create one
        // check in both models, inference and union, write into union!
        RDFList list = null;
        for (String value : values) {
            Resource valueIndividual;
            if(valueIsClass){
                valueIndividual = JenaUtils.getAnyIndividualFromConcept(value, vocab.savePrefix, saveModel);
            } else {
                valueIndividual = infModel.createResource(replacePrefixWithURI(value));
            }

            if (list == null) {
                list = infModel.createList(valueIndividual);
            } else {
                list.add(valueIndividual);
            }
            // add an "OR" condition
        }
        Statement stmt = infModel.createStatement(baseNode,
                infModel.createProperty(replacePrefixWithURI(attribute + "List")),
                //here replace with creation of same-named individual if it doesn't exist in the model or infModel
                list);
        infModel.add(stmt);
//        System.out.println(ModelPrinter.get().print(infModel));
        return stmt;
    }

    private String replacePrefixWithURI(String prefixedName){
        return ModelUtils.replacePrefixWithURI(prefixedName, infModel);
    }
}
