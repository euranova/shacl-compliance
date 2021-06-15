package org.example.save.app;

//import lombok.extern.slf4j.Slf4j;
import org.apache.jena.util.FileUtils;
import org.example.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.ModelPrinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        model.addAttribute("policies", policies.values());

        model.addAttribute("requests", savedRequests.values());
        return "compliance";
    }

    @RequestMapping(value = "/compliance", method = RequestMethod.POST)
    public String checkCompliance(@RequestParam(required = false) List<String> activeRequests,
                                  @RequestParam() List<String> activePolicies,
                                  Model model){
        System.out.println(activeRequests);
        System.out.println(activePolicies);



        for(PolicyModel policyModel: policies.values()){
            policyModel.setActive(activePolicies.contains(policyModel.policyId));
        }
        for(RequestModel requestModel: savedRequests.values()){
            requestModel.setActive(activeRequests.contains(requestModel.requestId));
        }
        model.addAttribute("policies", policies.values());

        model.addAttribute("requests", savedRequests.values());

        List<ResultModel> results = checkCompliance(activeRequests, activePolicies);

        model.addAttribute("results", results);
        return "compliance";
    }

    private List<ResultModel> checkCompliance(List<String> activeRequests, List<String> activePolicies) {
        // load the policies
        // load the requests
        org.apache.jena.rdf.model.Model requestModel = JenaUtil.createMemoryModel();
        requestModel.read(ModelUtils.class.getResourceAsStream(RESOURCE_FOLDER + requestsFile), "urn:dummy",
                FileUtils.langTurtle);
        unionModel.add(requestModel);

        List<SAVERule> requests = SPARQLUtils.getRequestsByName(activeRequests, unionModel);
        SAVENormalizer normalizer = new SAVENormalizer(unionModel);

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
            for(SAVERule request: requests){
                SAVERuleNormalized requestNormalized = normalizer.normalizeSAVERule(request, false, false);
                try {
                    SHACLComplianceResult result = null;
                    if (policyName.contains("sparql")) {
                        result = runner.checkNormalizedSAVERuleSPARQL(requestNormalized,
                                SHACLComplianceResult.Mode.DEMO_APP, false);
                    } else {
                        result = runner.checkNormalizedSAVERuleCore(requestNormalized,
                                true, SHACLComplianceResult.Mode.DEMO_APP, true);
                    }
                    results.add(result);
                    ResultModel resultModel = readResultFromRaw(request, policyModel, result, policyName.contains("sparql"));
                    resultModels.add(resultModel);
                } catch (Exception e){
                    System.out.println("Error appeared during compliance check of request " + request.getName());
                    e.printStackTrace();
                }
            }
        }

        SHACLComplianceResult finalResult = SHACLComplianceResult.createTotalResultFromList(results, requests.size(),
                unionModel, SHACLComplianceResult.Mode.DEMO_APP);
        return resultModels;
    }

    private ResultModel readResultFromRaw(SAVERule request, PolicyModel policy, SHACLComplianceResult result, boolean SPARQL) {
        // result is given per request, but there may be multiple rules/policies
        ResultModel resultModel = new ResultModel();
        resultModel.setRequest(savedRequests.get(request.getName()));
        resultModel.setRawResult(result);
        resultModel.setPolicy(policy);
        if(SPARQL){
            Map<String, SAVERule> conformsToRule = SPARQLUtils.getConformsTo(JenaUtil.createMemoryModel().add(unionModel).add(result.getInfModel()));
            for (String rule: conformsToRule.keySet()){
                resultModel.getConformsTo().add(conformsToRule.get(rule));
            }
            Map<String, SAVERule> prohibitedByRule = SPARQLUtils.getProhibitedBy(JenaUtil.createMemoryModel().add(unionModel).add(result.getInfModel()));
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

        org.apache.jena.rdf.model.Model shapesModel = JenaUtil.createMemoryModel();
        filename = "IMDBPolicy.shapes.core.ttl";
        shapesModel.read(ModelUtils.class.getResourceAsStream(RESOURCE_FOLDER + filename), "urn:dummy",
                FileUtils.langTurtle);
        unionModel.add(shapesModel);
        Map<String, String> requestNames = SPARQLUtils.getSavedRequests(unionModel, vocab.requestBaseType);
        Map<String, RequestModel> savedRequests = new HashMap<>();
        for(Map.Entry<String, String> requestName: requestNames.entrySet()){
            RequestModel requestModel = new RequestModel(requestName.getKey(), ResultModel.getRequestDisplayName(requestName.getKey()),
                    requestName.getValue(), false);
            savedRequests.put(requestName.getKey(), requestModel);
        }
        return savedRequests;
    }

    private Map<String, PolicyModel> getSavedSHACLPolicies() {
        List<String> policyNames = ModelUtils.getSHACLPolicyNames();
        Map<String, PolicyModel> savedPolicies = new HashMap<>();
        for(String policyName: policyNames){
            List<String> ruleNames = SPARQLUtils.getPolicyAndRuleNamesForPolicyFile(unionModel, shaclToSavePolicyFiles.get(policyName));
            PolicyModel policyModel = new PolicyModel(policyName, policyName.replace(".shapes", "")
                    .replace(".ttl", ""), (ruleNames.isEmpty())? null : ruleNames.get(0), false,
                    (ruleNames.isEmpty()) ? null: ruleNames.subList(1,ruleNames.size()));
            savedPolicies.put(policyName, policyModel);
        }
        return savedPolicies;

    }
}
