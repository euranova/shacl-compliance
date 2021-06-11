package org.example;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.ModelPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.stream.Stream;


/**
 * Class for collecting necessary information for inference result, such as:
 * - execution time (for the whole request and AVG per subrequest);
 * - inferred triples (all)
 */
public class SHACLComplianceResult {

    /**
     * # of requests per batch
     */
    private List<Integer> nRequestsPerBatch = new ArrayList<>();

    /**
     * # of rules per policy
     */
    private List<Integer> nRulesPerPolicy = new ArrayList<>();

    /**
     * total time per batch of separate requests (for atomic mostly)
     */
    private List<Double> execTimeTotalPerBatch = new ArrayList<>();

    /**
     * total time for a batch of separate requests
     */
    private double execTimeTotal;
    /**
     * total times for each request in the batch (with all subrequests)
     */
    private List<Double> execTimePerRequest = new ArrayList<>();

    /**
     * number of subrequests per request
     */
    private List<Integer> nSubrequests = new ArrayList<>();

    /**
     * time for each separate subrequest
     */
    private List<Double> execTimePerSubrequest = new ArrayList<>();

    /**
     * map showing average time per request depending on number of subrequests
     */
    private  Map<Integer,Double> avgTimePerNumberOfSubrequests;

    /**
     * map showing average time per request depending on number of subrequests (bucketed)
     */
    private  Map<Integer,Double> avgTimePerNumberOfSubrequestsBucketed;

    /**
     * map showing the number of examples for each number of subrequests (bucketed)
     */
    private Map<Integer, Integer> countPerNumberOfSubrequestsBucketed;


    /**
     * map showing average time per batch depending on the number of requests
     */
    private Map<Integer, Double> avgTimePerNumberOfBatchRequests;

    /**
     * map showing average time per request depending on number of rules in policy
     */
    private Map<Integer, Double> avgTimePerNumberOfRules;


    /**
     * map showing count of results we got depending on number of subrequests
     */
    private Map<Integer, Long> countPerNumberOfSubrequests;
    /**
     * map showing count of results we got depending on the number of requests
     */
    private Map<Integer, Long> countPerNumberOfBatchRequests;
    /**
     * map showing count of results we got depending on the number of rules in a policy
     */

    private Map<Integer, Long> countPerNumberOfRules;


    /**
     * all the inferred triples for the whole batch (or one whole request, depending on what was run)
     */
    private Model infModel;

    /**
     * all the inferred triples for the whole batch (or one whole request, depending on what was run)
     */
    private Model subrequestsModel;


    /**
     * Number of requests to be processed in this result
     */
    private int nRequests;

    /**
     * Number of requests that were actually processed
     */
    private int nProcessed;

    /**
     * Different test modes
     */
    public enum Mode {
        IMDB_ATOMIC_REQUESTS,
        IMDB_SIMPLE_REQUESTS,
        RANDOM_SIMPLE_POLICIES,
        IMDB_TEST_REQUESTS,
        IMDB_ULTIMATE_REQUEST,
        IMDB_TEST_CONFLICT,
        DEMO_APP
    }

    /**
     * Test mode of the current result
     */
    private Mode mode;

    /**
     * Name of the policy (deprecated)
     *
     */
    private String policy;

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    /**
     * Result stsrts with the number of requests, mode and model graph
     * @param nRequests number of requests to be handled
     * @param mode mode of the test
     * @param model graph model
     */
    public SHACLComplianceResult(int nRequests,  Mode mode, Model model) {
        this.nRequests = nRequests;
        this.mode = mode;
        infModel = JenaUtil.createMemoryModel();
        infModel.setNsPrefixes(model.getNsPrefixMap());
        subrequestsModel = JenaUtil.createMemoryModel();
        subrequestsModel.setNsPrefixes(model.getNsPrefixMap());
    }

    public List<Integer> getnRulesPerPolicy() {
        return nRulesPerPolicy;
    }

    public void setnRulesPerPolicy(List<Integer> nRulesPerPolicy) {
        this.nRulesPerPolicy = nRulesPerPolicy;
    }

    public void addnRulesPerPolicy(Integer nRulesPerPolicy) {
        this.nRulesPerPolicy.add(nRulesPerPolicy);
    }

    public List<Integer> getnRequestsPerBatch() {
        return nRequestsPerBatch;
    }

    public void setnRequestsPerBatch(List<Integer> nRequestsPerBatch) {
        this.nRequestsPerBatch = nRequestsPerBatch;
    }

    public List<Double> getExecTimeTotalPerBatch() {
        return execTimeTotalPerBatch;
    }

    public void setExecTimeTotalPerBatch(List<Double> execTimeTotalPerBatch) {
        this.execTimeTotalPerBatch = execTimeTotalPerBatch;
    }

    public int getnProcessed() {
        return nProcessed;
    }

    public void setnProcessed(int nProcessed) {
        this.nProcessed = nProcessed;
    }

    public List<Double> getExecTimePerRequest() {
        return execTimePerRequest;
    }

    public void setExecTimePerRequest(List<Double> execTimePerRequest) {
        this.execTimePerRequest = execTimePerRequest;
    }

    public void addExecTimePerRequest(double execTimePerRequest) {
        this.execTimePerRequest.add(execTimePerRequest);
    }

    public double getExecTimeTotal() {
        return execTimeTotal;
    }

    public void setExecTimeTotal(double execTimeTotal) {
        this.execTimeTotal = execTimeTotal;
    }

    public List<Double> getExecTimePerSubrequest() {
        return execTimePerSubrequest;
    }

    public void setExecTimePerSubrequest(List<Double> execTimePerSubrequest) {
        this.execTimePerSubrequest = execTimePerSubrequest;
    }

    public void addExecTimePerSubrequest(double execTimePerSubrequest) {
        this.execTimePerSubrequest.add(execTimePerSubrequest);
    }

    public List<Integer> getnSubrequests() {
        return nSubrequests;
    }

    public void setnSubrequests(List<Integer> nSubrequests) {
        this.nSubrequests = nSubrequests;
    }

    public void addnSubrequests(Integer nSubrequests) {
        this.nSubrequests.add(nSubrequests);
    }

    public Model getInfModel() {
        return infModel;
    }

    public void setInfModel(Model inferredTriples) {
        this.infModel = inferredTriples;
    }

    public void addInferredTriples(Model newInferredTriples) {
        this.infModel.add(newInferredTriples);
    }

    public Model getSubrequestsModel() {
        return subrequestsModel;
    }

    public void setSubrequestsModel(Model subrequestsModel) {
        this.subrequestsModel = subrequestsModel;
    }

    public void addSubrequests(Model subrequestsModel) {
        this.subrequestsModel.add(subrequestsModel);
    }

    public double getAvgTimePerRequest(){
        return execTimePerRequest.stream().mapToDouble(a -> a).average().getAsDouble();
    }

    /**
     * Get or calculate average execution time of the request per number of subrequests (for the graph)
     * @return the graph series data
     */
    public Map<Integer,Double> getAvgTimePerNumberOfSubrequests(){
        if(avgTimePerNumberOfSubrequests == null) {
            if (execTimePerRequest.isEmpty() || nSubrequests.isEmpty()) {
                return null;
            }
            List<SimpleEntry<Integer, Double>> pairs = Streams.zip(nSubrequests.stream(), execTimePerRequest.stream(), SimpleEntry::new)
                    .collect(Collectors.toList());
            Map<Integer, Double> map = pairs.stream().collect(Collectors.groupingBy(SimpleEntry::getKey,
                    Collectors.averagingDouble(SimpleEntry::getValue)));
            avgTimePerNumberOfSubrequests = map;
        }
        return avgTimePerNumberOfSubrequests;
    }

    /**
     * Get or calculate count of examples per number of subrequests (for the graph)
     * @return the counts map
     */
    public Map<Integer, Long> getCountPerNumberOfSubrequests(){
        if(countPerNumberOfSubrequests == null) {
            if (execTimePerRequest.isEmpty() || nSubrequests.isEmpty()) {
                return null;
            }
            List<SimpleEntry<Integer, Double>> pairs = Streams.zip(nSubrequests.stream(), execTimePerRequest.stream(), SimpleEntry::new)
                    .collect(Collectors.toList());
            Map<Integer, Long> map = pairs.stream().collect(Collectors.groupingBy(SimpleEntry::getKey,
                    Collectors.counting()));
            countPerNumberOfSubrequests = map;
        }
        return countPerNumberOfSubrequests;
    }

    /**
     * Get or calculate average execution time of the request per number of subrequests - binned version (for the graph)
     * @return the graph series data (binned)
     */
    public Map<Integer,Double> getAvgTimePerNumberOfSubrequestsBucketed(){
        if(avgTimePerNumberOfSubrequestsBucketed == null) {
            if (execTimePerRequest.isEmpty() || nSubrequests.isEmpty()) {
                return null;
            }
            List<SimpleEntry<Integer, Double>> pairs = Streams.zip(nSubrequests.stream(), execTimePerRequest.stream(), SimpleEntry::new)
                    .collect(Collectors.toList());

            BucketHashMap map = new BucketHashMap();
            for (SimpleEntry<Integer, Double> entry: pairs){
                map.update(entry.getKey(), entry.getValue());
            }

            avgTimePerNumberOfSubrequestsBucketed = map.generate();
        }
        return avgTimePerNumberOfSubrequestsBucketed;
    }

    /**
     * Get or calculate counts per number of subrequests - binned version (for the graph)
     * @return counts map
     */
    public Map<Integer,Integer> getCountPerNumberOfSubrequestsBucketed(){
        if(countPerNumberOfSubrequestsBucketed == null) {
            if (execTimePerRequest.isEmpty() || nSubrequests.isEmpty()) {
                return null;
            }
            List<SimpleEntry<Integer, Double>> pairs = Streams.zip(nSubrequests.stream(), execTimePerRequest.stream(), SimpleEntry::new)
                    .collect(Collectors.toList());

            BucketHashMap map = new BucketHashMap();
            for (SimpleEntry<Integer, Double> entry: pairs){
                map.update(entry.getKey(), entry.getValue());
            }

            countPerNumberOfSubrequestsBucketed = map.generateCounts();
        }
        return countPerNumberOfSubrequestsBucketed;
    }

    /**
     * Get or calculate average execution time of the batch per batch size (for the graph)
     * @return the graph series data
     */
    public Map<Integer,Double> getAvgTimePerNumberOfBatchRequests(){
        if( avgTimePerNumberOfBatchRequests == null) {
            if (execTimeTotalPerBatch.isEmpty() || nRequestsPerBatch.isEmpty()) {
                return null;
            }
            List<SimpleEntry<Integer, Double>> pairs = Streams.zip(nRequestsPerBatch.stream(), execTimeTotalPerBatch.stream(), SimpleEntry::new)
                    .collect(Collectors.toList());
            Map<Integer, Double> map = pairs.stream().collect(Collectors.groupingBy(SimpleEntry::getKey,
                    Collectors.averagingDouble(SimpleEntry::getValue)));
            avgTimePerNumberOfBatchRequests = map;
        }
        return avgTimePerNumberOfBatchRequests;
    }

    /**
     * Get or calculate counts of examples per batch size (for the graph)
     * @return the count map
     */
    public Map<Integer,Long> getCountPerNumberOfBatchRequests(){
        if( countPerNumberOfBatchRequests == null) {
            if (execTimeTotalPerBatch.isEmpty() || nRequestsPerBatch.isEmpty()) {
                return null;
            }
            List<SimpleEntry<Integer, Double>> pairs = Streams.zip(nRequestsPerBatch.stream(), execTimeTotalPerBatch.stream(), SimpleEntry::new)
                    .collect(Collectors.toList());
            Map<Integer, Long> map = pairs.stream().collect(Collectors.groupingBy(SimpleEntry::getKey,
                    Collectors.counting()));
            countPerNumberOfBatchRequests = map;
        }
        return countPerNumberOfBatchRequests;
    }

    /**
     * Get or calculate average execution time of the request per policy size (for the graph)
     * @return the graph series data
     */
    public Map<Integer,Double> getAvgTimePerNumberOfRules(boolean divideByBatch){
        if( avgTimePerNumberOfRules == null) {
            if (execTimeTotalPerBatch.isEmpty() || nRulesPerPolicy.isEmpty()) {
                return null;
            }
            List<Double> execTimePerUnit = execTimeTotalPerBatch;
            if(divideByBatch) {
                execTimePerUnit = Streams.zip(execTimeTotalPerBatch.stream(), nRequestsPerBatch.stream(), SimpleEntry::new)
                        .map(x -> x.getKey() / x.getValue()).collect(Collectors.toList());
            }
            List<SimpleEntry<Integer, Double>> pairs = Streams.zip(nRulesPerPolicy.stream(), execTimePerUnit.stream(), SimpleEntry::new)
                    .collect(Collectors.toList());
            Map<Integer, Double> map = pairs.stream().collect(Collectors.groupingBy(SimpleEntry::getKey,
                    Collectors.averagingDouble(SimpleEntry::getValue)));
            avgTimePerNumberOfRules = map;
        }
        return avgTimePerNumberOfRules;
    }

    /**
     * Get or calculate number of examples per policy size (for the graph)
     * @return counts map
     */
    public Map<Integer,Long> getCountPerNumberOfRules(){
        if( countPerNumberOfRules == null) {
            if (execTimeTotalPerBatch.isEmpty() || nRulesPerPolicy.isEmpty()) {
                return null;
            }
            List<SimpleEntry<Integer, Double>> pairs = Streams.zip(nRulesPerPolicy.stream(), execTimeTotalPerBatch.stream(), SimpleEntry::new)
                    .collect(Collectors.toList());
            Map<Integer, Long> map = pairs.stream().collect(Collectors.groupingBy(SimpleEntry::getKey,
                    Collectors.counting()));
            countPerNumberOfRules = map;
        }
        return countPerNumberOfRules;
    }

    public double getAvgTimePerSubrequest(){
        if(execTimePerSubrequest.isEmpty()){
            return 0.0;
        }
        return execTimePerSubrequest.stream().mapToDouble(a -> a).average().getAsDouble();
    }

    public void writeToFile(String dirPath, String experimentName){
        try {

            File fileStats = new File(dirPath);
            boolean dirCreated = fileStats.mkdirs();
            fileStats = new File(dirPath + experimentName + ".txt");

            FileWriter writerStats = new FileWriter(fileStats);
            String stats = "Experiment: " + experimentName + "\n"
                    + "\tPolicy: " + policy + "\n";
            if(mode != Mode.IMDB_TEST_CONFLICT) {
                stats += "\t#Requests: " + nRequests + "\n"
                        + "\t#Processed: " + nProcessed + "\n";
            }
            stats += "\tMode: " + mode.name() + "\n"
                    + "\tTriples inferred: " + infModel.listStatements().toList().size() + "\n";
            if(mode != Mode.IMDB_TEST_CONFLICT) {
                stats += "\tTotal exec time: " + execTimeTotal + "\n"
                        + "\tAvg time per request: " + getAvgTimePerRequest() + "\n"
                        + "\tAvg time per subrequest: " + getAvgTimePerSubrequest() + "\n";
            }
            if (mode == Mode.IMDB_ATOMIC_REQUESTS){
                stats += "\tCount per # of atomic requests in a batch: " + getCountPerNumberOfBatchRequests() + "\n"
                        + "\tAvg time per # of atomic requests in a batch: " + getAvgTimePerNumberOfBatchRequests() + "\n";
            } else if (mode == Mode.IMDB_SIMPLE_REQUESTS){
                stats += "\tCount per # of subrequests: " + getCountPerNumberOfSubrequests() + "\n"
                        + "\tAvg time per # of subrequests: " + getAvgTimePerNumberOfSubrequests() + "\n"
                        + "\tCount per # of subrequests (binned): " + getCountPerNumberOfSubrequestsBucketed() + "\n"
                        + "\tAvg time per # of subrequests (binned): " + getAvgTimePerNumberOfSubrequestsBucketed() + "\n";
            } else if (mode == Mode.RANDOM_SIMPLE_POLICIES){
                stats += "\tCount per # of simple rules in a policy: " + getCountPerNumberOfRules() + "\n"
                        + "\tAvg time per # of simple rules in a policy: " + getAvgTimePerNumberOfRules(true) + "\n";
            } else if (mode == Mode.IMDB_TEST_REQUESTS){

            } else if (mode == Mode.IMDB_ULTIMATE_REQUEST){

            }
            writerStats.write(stats);
            writerStats.close();

            File fileTriples = new File(dirPath + experimentName + "_triples.ttl");
            FileWriter writerTriples = new FileWriter(fileTriples);
            writerTriples.write(ModelPrinter.get().print(infModel));
            writerTriples.close();
            if(!subrequestsModel.isEmpty()) {
                File fileSubrequests = new File(dirPath + experimentName + "_subrequests.ttl");
                FileWriter writerSubrequests = new FileWriter(fileSubrequests);
                writerSubrequests.write(ModelPrinter.get().print(subrequestsModel));
                writerSubrequests.close();
            }
        } catch (IOException e) {
            System.out.println("Cannot write into " + dirPath + experimentName + ": " + e.toString());
        }

    }

    public static SHACLComplianceResult createTotalResultFromList(List<SHACLComplianceResult> resultsList, int nRequests, Model model,
                                                                   SHACLComplianceResult.Mode mode) {
        SHACLComplianceResult resultTotal = new SHACLComplianceResult(nRequests, mode, model);
        resultTotal.setnRequestsPerBatch(resultsList.stream().map(SHACLComplianceResult::getnProcessed).collect(Collectors.toList()));
        resultTotal.setnRulesPerPolicy(resultsList.stream().flatMap(r -> r.getnRulesPerPolicy().stream()).collect(Collectors.toList()));
        resultTotal.setExecTimeTotalPerBatch(resultsList.stream().map(SHACLComplianceResult::getExecTimeTotal).collect(Collectors.toList()));
        resultTotal.setnProcessed(resultsList.stream().mapToInt(SHACLComplianceResult::getnProcessed).sum());
        resultTotal.setExecTimeTotal(resultsList.stream().mapToDouble(SHACLComplianceResult::getExecTimeTotal).sum());
        resultTotal.setExecTimePerRequest(resultsList.stream().flatMap(r -> r.getExecTimePerRequest().stream()).collect(Collectors.toList()));
        resultTotal.setExecTimePerSubrequest(resultsList.stream().flatMap(r -> r.getExecTimePerSubrequest().stream()).collect(Collectors.toList()));
        resultTotal.setnSubrequests(resultsList.stream().flatMap(r -> r.getnSubrequests().stream()).collect(Collectors.toList()));
        Model totalInfModel = JenaUtil.createMemoryModel();
        for (SHACLComplianceResult res : resultsList) {
            totalInfModel.add(res.getInfModel());
        }
        resultTotal.setInfModel(totalInfModel);

        Model totalSubrequestsModel = JenaUtil.createMemoryModel();
        for (SHACLComplianceResult res : resultsList) {
            totalSubrequestsModel.add(res.getSubrequestsModel());
        }
        resultTotal.setSubrequestsModel(totalSubrequestsModel);
        return resultTotal;
    }

}
