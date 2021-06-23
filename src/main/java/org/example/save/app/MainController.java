package org.example.save.app;

//import lombok.extern.slf4j.Slf4j;
import org.apache.jena.util.FileUtils;
import org.example.*;
import org.example.treeUtils.GenericTree;
import org.example.treeUtils.GenericTreeNode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.ModelPrinter;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.example.ModelUtils.RESOURCE_FOLDER;

//@Slf4j
@Controller
public class MainController {

    org.apache.jena.rdf.model.Model unionModel;
    Map<String, PolicyModel> policies;
    Map<String, RequestModel> savedRequests;
    Map<String, String> shaclToSavePolicyFiles;
    String requestsFile;
    SAVEVocabulary vocab;
    String attributeGT, actionsGT, dataGT, purposesGT, legalBasesGT, measuresGT, controllersGT, processorsGT,
            dataSubjectsGT, responsiblePartiesGT, sendersGT, recipientsGT;
    Map<String, String> individToClassVocab;


    public MainController(){
        requestsFile = "testIMDBRequests.ttl";
        shaclToSavePolicyFiles = new HashMap<>();
        shaclToSavePolicyFiles.put("IMDBPolicy.shapes.sparql.ttl", "save.imdb.policy.ttl");
        shaclToSavePolicyFiles.put("IMDBPolicy.shapes.core.ttl", "save.imdb.policy.ttl");
        shaclToSavePolicyFiles.put("IMDBPolicyPreconflictPermissions.shapes.sparql.ttl", "save.imdb.policy.ttl");
        shaclToSavePolicyFiles.put("save.shapes.ttl", "save.ontology.ttl");
        unionModel = ModelUtils.loadFullSAVEModel();
        vocab = new SAVEVocabulary(unionModel);
        policies = getSavedSHACLPolicies();
        savedRequests = getSavedRequests(requestsFile);

//        individToClassVocab = SPARQLUtils.initIndividToClassVocab(unionModel, Arrays.asList("save", "save-ex"));
//        attributeGT = getAttributeTreeGT();
//        actionsGT = SPARQLUtils.getDPVDAGGT(SPARQLUtils.getDPVDAGNLP(unionModel, "dpv:Processing", Collections.singletonList("save:Processing"))).toJsonTree();
//        dataGT = SPARQLUtils.getDPVDAGGT(SPARQLUtils.getDPVDAGNLP(unionModel, "dpv:PersonalDataCategory", Collections.singletonList("save:PersonalDataCategory"))).toJsonTree();
//        purposesGT =  SPARQLUtils.getDPVDAGGT(SPARQLUtils.getDPVDAGNLP(unionModel, "dpv:Purpose",Collections.singletonList("save:Purpose"))).toJsonTree();
//        legalBasesGT =  SPARQLUtils.getDPVDAGGT(SPARQLUtils.getDPVDAGNLP(unionModel, "save:LegalBasis", null)).toJsonTree();
//        measuresGT = SPARQLUtils.getDPVDAGGT(SPARQLUtils.getDPVDAGNLP(unionModel, "dpv:TechnicalOrganisationalMeasure", Collections.singletonList("save:TechnicalOrganisationalMeasure"))).toJsonTree();
//        controllersGT = SPARQLUtils.getDPVDAGGT(SPARQLUtils.getDPVDAGNLP(unionModel, "dpv:DataController", null)).toJsonTree();
//        processorsGT = SPARQLUtils.getDPVDAGGT(SPARQLUtils.getDPVDAGNLP(unionModel, "dpv:DataProcessor", null)).toJsonTree();
//        dataSubjectsGT = SPARQLUtils.getDPVDAGGT(SPARQLUtils.getDPVDAGNLP(unionModel, "dpv:DataSubject", null)).toJsonTree();
//        sendersGT = SPARQLUtils.getDPVDAGGT(SPARQLUtils.getDPVDAGNLP(unionModel, "save:Party", null)).toJsonTree();
//        recipientsGT = SPARQLUtils.getDPVDAGGT(SPARQLUtils.getDPVDAGNLP(unionModel, "dpv:ThirdParty", null)).toJsonTree();

    }


    private String getAttributeTreeGT() {
        GenericTree tree = new GenericTree();
        GenericTreeNode root = new GenericTreeNode("save:root");
        tree.setRoot(root);

        GenericTreeNode dataChild = new GenericTreeNode("data");
        root.addChild(dataChild);
        GenericTreeNode purposeChild = new GenericTreeNode("purpose");
        root.addChild(purposeChild);

        GenericTreeNode actionChild = new GenericTreeNode("action");
        root.addChild(actionChild);

        return tree.toJsonTree();
    }


    @RequestMapping(value="/")
    public String showPage(){
        return "index";
    }

    @RequestMapping(value="/conflict")
    public String showConflict(){
        return "conflict";
    }

    @RequestMapping(value="/compliance", method = RequestMethod.GET)
    public String showCompliance(Model model){
        //get the list of policies in the system
        resetModelAttributes();
        model.addAttribute("policies", policies.values());

        model.addAttribute("requests", savedRequests.values());
//        addTreesToModel(model);
        return "compliance";

    }

    private void resetModelAttributes() {
        boolean first = true;
        for(PolicyModel policyModel: policies.values()){
            policyModel.setActive(first);
            first = false;
        }
        first = true;
        for(RequestModel requestModel: savedRequests.values()){
            requestModel.setActive(first);
            first = false;
        }
    }

    private void addTreesToModel(Model model) {
        model.addAttribute("actionsGT", actionsGT);
        model.addAttribute("dataGT", dataGT);
        model.addAttribute("purposesGT", purposesGT);
        model.addAttribute("attributeGT", attributeGT);
        model.addAttribute("legalBasesGT", legalBasesGT);
        model.addAttribute("controllersGT", controllersGT);
        model.addAttribute("processorsGT", processorsGT);
        model.addAttribute("dataSubjectsGT", dataSubjectsGT);
        model.addAttribute("recipientsGT", recipientsGT);
    }

    @RequestMapping(value = "/compliance", method = RequestMethod.POST)
    public String checkComplianceOnSavedRequests(@RequestParam(required = false) List<String> activeRequests,
                                                 @RequestParam(required = false) List<String> activePolicies,
//                                                 @RequestParam(required = false) String selectedAction,
//                                                 @RequestParam(required = false) String selectedData,
//                                                 @RequestParam(required = false) String selectedPurpose,
//                                                 @RequestParam(required = false) String selectedLegalBasis,
//                                                 @RequestParam(required = false) String selectedController,
//                                                 @RequestParam(required = false) String selectedProcessor,
//                                                 @RequestParam(required = false) String selectedDataSubject,
//                                                 @RequestParam(required = false) String selectedRecipient,
//                                                 @RequestParam(required = false) Boolean requestSwitch,
                                                 Model model){
        Instant start = Instant.now();
        List<ResultModel> results;
        System.out.println(activeRequests);
        System.out.println(activePolicies);

        for (PolicyModel policyModel : policies.values()) {
            policyModel.setActive(activePolicies.contains(policyModel.policyId));
        }
        for (RequestModel requestModel : savedRequests.values()) {
            requestModel.setActive(activeRequests.contains(requestModel.requestId));
        }
        model.addAttribute("policies", policies.values());

        model.addAttribute("requests", savedRequests.values());

//        model.addAttribute("selectedAction", selectedAction);
//        model.addAttribute("selectedData", selectedData);
//        model.addAttribute("selectedPurpose", selectedPurpose);
//        model.addAttribute("selectedLegalBasis", selectedLegalBasis);
//        model.addAttribute("selectedController", selectedController);
//        model.addAttribute("selectedProcessor", selectedProcessor);
//        model.addAttribute("selectedDataSubject", selectedDataSubject);
//        model.addAttribute("selectedRecipient", selectedRecipient);

//        if(!requestSwitch) {

            results = checkComplianceOnSavedRequests(activeRequests, activePolicies);
//        } else {

//            results = checkComplianceOnNewRequest(activePolicies, selectedAction, selectedData, selectedPurpose, selectedLegalBasis,
//                    selectedController, selectedProcessor, selectedDataSubject, selectedRecipient);

//        }
        model.addAttribute("results", results);
        addTreesToModel(model);
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time took to run the whole test: " + timeElapsed);
        return "compliance";
    }

    private List<ResultModel> checkComplianceOnNewRequest(List<String> activePolicies,
                                                          String selectedAction, String selectedData,
                                                          String selectedPurpose, String selectedLegalBasis,
                                                          String selectedController, String selectedProcessor,
                                                          String selectedDataSubject, String selectedRecipient) {

        List<SAVERuleNormalized> requests = new ArrayList<>();
        SAVERule request = new SAVERule("save-ex:NewRequest", "save-ex:RequestIMDBPolicy");
        if(selectedAction != null && !selectedAction.equals("")){
            request.addAction(selectedAction, individToClassVocab.get(selectedAction));
        }
        if(selectedData != null && !selectedData.equals("")){
            request.addData(selectedData, individToClassVocab.get(selectedData));
        }
        if(selectedPurpose != null && !selectedPurpose.equals("")){
            request.addPurpose(selectedPurpose, individToClassVocab.get(selectedPurpose));
        }
        if(selectedLegalBasis != null && !selectedLegalBasis.equals("")){
            request.addLegalBasis(selectedLegalBasis, individToClassVocab.get(selectedLegalBasis));
        }
        if(selectedController != null && !selectedController.equals("")){
            request.addController(selectedController, individToClassVocab.get(selectedController));
        }
        if(selectedProcessor != null && !selectedProcessor.equals("")){
            request.addProcessor(selectedProcessor, individToClassVocab.get(selectedProcessor));
        }
        if(selectedDataSubject != null && !selectedDataSubject.equals("")){
            request.addDataSubject(selectedDataSubject, individToClassVocab.get(selectedDataSubject));
        }
        if(selectedRecipient != null && !selectedRecipient.equals("")){
            request.addRecipient(selectedRecipient, individToClassVocab.get(selectedRecipient));
        }
        requests.add(new SAVERuleNormalized(request));
        return checkComplianceOnRequests(requests, activePolicies);

    }

    private List<ResultModel> checkComplianceOnSavedRequests(List<String> activeRequests, List<String> activePolicies) {
        // load the policies
        // load the requests

        List<SAVERuleNormalized> requests = new ArrayList<>();
        for(String activeRequest: activeRequests){
            //the rule should be normalized by now
            requests.add((SAVERuleNormalized) savedRequests.get(activeRequest).getSaveRequest());
        }

        return checkComplianceOnRequests(requests, activePolicies);
    }

    private List<ResultModel> checkComplianceOnRequests(List<SAVERuleNormalized> requests, List<String> activePolicies) {
        //        SPARQLUtils.getRequestsByName(activeRequests, unionModel);
//        SAVENormalizer normalizer = new SAVENormalizer(unionModel);

        List<SHACLComplianceResult> results = new ArrayList<>();
        List<ResultModel> resultModels = new ArrayList<>();
        for(String policyName: activePolicies){
            PolicyModel policyModel = policies.get(policyName);
            String savePolicyName = shaclToSavePolicyFiles.get(policyName);
            org.apache.jena.rdf.model.Model policyOriginalModel = JenaUtil.createMemoryModel();
            policyOriginalModel.read(ModelUtils.class.getResourceAsStream(RESOURCE_FOLDER + savePolicyName), "urn:dummy",
                    FileUtils.langTurtle);
            unionModel.add(policyOriginalModel);
            org.apache.jena.rdf.model.Model policyShapeModel = JenaUtil.createMemoryModel();
            policyShapeModel.read(ModelUtils.class.getResourceAsStream(RESOURCE_FOLDER + policyName), "urn:dummy",
                    FileUtils.langTurtle);
            unionModel.add(policyShapeModel);
            SHACLInferenceRunner runner = new SHACLInferenceRunner(unionModel, ModelUtils.loadSAVEModel(), policyShapeModel);
            for(SAVERuleNormalized requestNormalized: requests){
//                SAVERuleNormalized requestNormalized = normalizer.normalizeSAVERule(request, false, false);
                try {
                    SHACLComplianceResult result;
                    if (policyName.contains("sparql")) {
                        result = runner.checkNormalizedSAVERuleSPARQL(requestNormalized,
                                SHACLComplianceResult.Mode.DEMO_APP, false);
                    } else {
                        result = runner.checkNormalizedSAVERuleCore(requestNormalized,
                                true, SHACLComplianceResult.Mode.DEMO_APP, true);
                    }
                    results.add(result);
                    ResultModel resultModel = readResultFromRaw(requestNormalized, policyModel, result, policyName.contains("sparql"));
                    resultModels.add(resultModel);
                } catch (Exception e){
                    System.out.println("Error appeared during compliance check of request " + requestNormalized.getName());
                    e.printStackTrace();
                }
            }
            unionModel.remove(policyOriginalModel);
            unionModel.remove(policyShapeModel);
        }
//        SHACLComplianceResult finalResult = SHACLComplianceResult.createTotalResultFromList(results, requests.size(),
//                unionModel, SHACLComplianceResult.Mode.DEMO_APP);
        return resultModels;
    }

    private ResultModel readResultFromRaw(SAVERule request, PolicyModel policy, SHACLComplianceResult result, boolean SPARQL) {
        // result is given per request, but there may be multiple rules/policies
        ResultModel resultModel = new ResultModel();
        if(savedRequests.containsKey(request.getName())) {
            resultModel.setRequest(savedRequests.get(request.getName()));
        }else {
            //it's the new request
            RequestModel requestModel = new RequestModel(request.getName(), request.getName().split(":")[1], request.getType(),
                    false, request);
            resultModel.setRequest(requestModel);
        }
        resultModel.setRawResult(result);
        resultModel.setPolicy(policy);
        if(SPARQL){
            Map<String, SAVERule> conformsToRule = SPARQLUtils.getConformsTo(request, JenaUtil.createMemoryModel().add(unionModel).add(result.getInfModel()));
            for (String rule: conformsToRule.keySet()){
                resultModel.getConformsTo().add(conformsToRule.get(rule));
            }
            Map<String, SAVERule> prohibitedByRule = SPARQLUtils.getProhibitedBy(request, JenaUtil.createMemoryModel().add(unionModel).add(result.getInfModel()));
            for (String rule: prohibitedByRule.keySet()) {
                resultModel.getProhibitedBy().add(prohibitedByRule.get(rule));
            }
            List<String> answerTuple = SPARQLUtils.getFinalResult(result.getInfModel());
            resultModel.setFinalResultPermitted(answerTuple.get(0));
            resultModel.setFinalResultProhibited(answerTuple.get(1));
        } else {
            Map<String, SAVERule> conformsToRule = SPARQLUtils.getSubrequestsPermitted(
                    JenaUtil.createMemoryModel().add(unionModel).add(result.getInfModel()).add(result.getSubrequestsModel()),
                    request.getName());
            for (String rule: conformsToRule.keySet()){
                resultModel.getConformsTo().add(conformsToRule.get(rule));
            }
            Map<String, SAVERule> prohibitedByRule = SPARQLUtils.getSubrequestsProhibited(
                    JenaUtil.createMemoryModel().add(unionModel).add(result.getInfModel()).add(result.getSubrequestsModel()),
                    request.getName());
            for (String rule: prohibitedByRule.keySet()) {
                resultModel.getProhibitedBy().add(prohibitedByRule.get(rule));
            }
            List<String> answerTuple = SPARQLUtils.getFinalResultCore(result.getInfModel(), request.getName());
            resultModel.setFinalResultPermitted(answerTuple.get(0));
            resultModel.setFinalResultProhibited(answerTuple.get(1));
        }
        return resultModel;
    }

    private Map<String, RequestModel> getSavedRequests(String filename) {
        org.apache.jena.rdf.model.Model requestsModel = JenaUtil.createMemoryModel();
        requestsModel.read(ModelUtils.class.getResourceAsStream(RESOURCE_FOLDER + filename), "urn:dummy",
                FileUtils.langTurtle);
        unionModel.add(requestsModel);
        String policyName = "IMDBPolicy.shapes.core.ttl";
        String savePolicyName = shaclToSavePolicyFiles.get(policyName);
        org.apache.jena.rdf.model.Model policyOriginalModel = JenaUtil.createMemoryModel();
        policyOriginalModel.read(ModelUtils.class.getResourceAsStream(RESOURCE_FOLDER + savePolicyName), "urn:dummy",
                FileUtils.langTurtle);
        unionModel.add(policyOriginalModel);
        org.apache.jena.rdf.model.Model shapesModel = JenaUtil.createMemoryModel();
        shapesModel.read(ModelUtils.class.getResourceAsStream(RESOURCE_FOLDER + policyName), "urn:dummy",
                FileUtils.langTurtle);
        unionModel.add(shapesModel);
        Map<String, SAVERule> requests = SPARQLUtils.getSavedRequests(unionModel);
        SAVENormalizer normalizer = new SAVENormalizer(unionModel);
        Map<String, RequestModel> savedRequests = new HashMap<>();
        for(Map.Entry<String, SAVERule> requestName: requests.entrySet()){
            SAVERuleNormalized ruleNormalized = normalizer.normalizeSAVERule(requestName.getValue(), false, false);
            RequestModel requestModel = new RequestModel(requestName.getKey(), ResultModel.getRequestDisplayName(requestName.getKey()),
                    ruleNormalized.getType(), false, ruleNormalized);
            savedRequests.put(requestName.getKey(), requestModel);
        }
        return savedRequests;
    }

    private Map<String, PolicyModel> getSavedSHACLPolicies() {
        List<String> policyNames = ModelUtils.getSHACLPolicyNames();
        if (policyNames.isEmpty()){
            policyNames.add("IMDBPolicy.shapes.core.ttl");
            policyNames.add("IMDBPolicy.shapes.sparql.ttl");
        }
        Map<String, PolicyModel> savedPolicies = new HashMap<>();
        for(String policyName: policyNames){
            List<String> ruleNames = SPARQLUtils.getPolicyAndRuleNamesForPolicyFile(unionModel, shaclToSavePolicyFiles.get(policyName));
            String savePolicyName = shaclToSavePolicyFiles.get(policyName);
            org.apache.jena.rdf.model.Model savePolicyModel = ModelUtils.loadModelFromResourceFile(savePolicyName);
            PolicyModel policyModel = new PolicyModel(policyName, policyName.replace(".shapes", "")
                    .replace(".ttl", ""), (ruleNames.isEmpty())? null : ruleNames.get(0), false,
                    ModelPrinter.get().print(savePolicyModel),
                    (ruleNames.isEmpty()) ? null: ruleNames.subList(1,ruleNames.size()));
            savedPolicies.put(policyName, policyModel);
        }
        return savedPolicies;

    }
}
