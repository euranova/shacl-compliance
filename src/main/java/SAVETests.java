
import org.apache.commons.cli.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import org.example.*;
import org.topbraid.jenax.util.JenaUtil;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.example.SHACLComplianceResult.createTotalResultFromList;
import static org.example.SPARQLUtils.*;


public class SAVETests {

    /**
     * Main file for evaluation of SHACL rules based on SAVE model
     */
    public static void main(String[] args) throws Exception {
//        System.setProperty("java.awt.headless", "true");
//        boolean headless = GraphicsEnvironment.isHeadless();
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.INFO);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.INFO);
        }
        boolean headless = GraphicsEnvironment.isHeadless();
        System.out.println("Headless: " + headless);
        if(headless){
            System.out.println("WARNING: Running in headless mode, the charts will not be rendered! Only logs can be provided.");
        }
        Options options = new Options();

        Option modeOption = new Option("m", "mode", true, "mode: inference (inf, 0), evaluation (eval, 1) or conflict (confl, 2)");
        modeOption.setRequired(true);
        options.addOption(modeOption);

        Option outputOption = new Option("o", "outputFolder", true, "output folder");
        outputOption.setRequired(true);
        options.addOption(outputOption);

        Option sparqlOption = new Option("p", "sparql", true, "whether to use sparql inference (true/false), default = false");
        sparqlOption.setRequired(false);
        options.addOption(sparqlOption);

        Option ultimateOption = new Option("u", "ultimate", true, "whether to use ultimate request (true/false, only works with sparql option), default = false");
        ultimateOption.setRequired(false);
        options.addOption(ultimateOption);

        Option evalModeOption = new Option("e", "evalMode", true, "eval mode: imdb_simple (default), imdb_atomic, random_atomic");
        evalModeOption.setRequired(false);
        options.addOption(evalModeOption);

        Option seedsOption = new Option("s", "seeds", true, "the list of seeds to use");
        seedsOption.setRequired(false);
        options.addOption(seedsOption);

        Option policySizesOption = new Option("l", "policySizes", true, "the list of policy sizes to use");
        policySizesOption.setRequired(false);
        options.addOption(policySizesOption);

        Option nRulesOption = new Option("r", "nRules", true, "the # of rules (or  the list of numbers) to use");
        nRulesOption.setRequired(false);
        options.addOption(nRulesOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            String mode = cmd.getOptionValue("mode");
            String outputFolder = cmd.getOptionValue("outputFolder");
            boolean sparql = Boolean.parseBoolean(cmd.getOptionValue("sparql", "false"));
            boolean ultimate = Boolean.parseBoolean(cmd.getOptionValue("ultimate", "false"));
            String evalMode = cmd.getOptionValue("evalMode", "imdb_simple");
            List<Integer> seeds = Arrays.stream(cmd.getOptionValue("seeds", "1,5,10,100,200,500,1000").split(","))
                    .map(Integer::parseInt).collect(Collectors.toList());
            List<Integer> policySizes = Arrays.stream(cmd.getOptionValue("policySizes", "1,10,100,1000,10000").split(","))
                    .map(Integer::parseInt).collect(Collectors.toList());
            List<Integer> nRules = Arrays.stream(cmd.getOptionValue("nRules", (evalMode.equals("imdb_atomic"))? "10,50,100,1000,5000" : "100").split(","))
                    .map(Integer::parseInt).collect(Collectors.toList());

            System.out.println("Mode: " + mode);
            System.out.println("Output folder: " + outputFolder);

            if (mode.equals("inf")){
                //test the compliance checking
                System.out.println("sparql: " + sparql);
                System.out.println("Ultimate Request: " + ultimate);
                if (!sparql && ultimate){
                    System.out.println("Cannot process ultimate request with SHACL-Core procedure, please choose sparql version by setting -p True");
                    System.exit(1);
                }
                testComplianceChecking(sparql, outputFolder, ultimate, headless);
            } else if(mode.equals("eval")){
                System.out.println("Seeds to use: " + seeds);
                if(evalMode.equals("imdb_simple")){
                    System.out.println("Number of rules: " + nRules.get(0));
                    testEvaluationIMDBSimple(outputFolder, headless, seeds, nRules);
                }else if (evalMode.equals("imdb_atomic")){
                    System.out.println("Number of rules: " + nRules);
                    testEvaluationIMDBAtomic(outputFolder, headless, seeds, nRules);
                } else if(evalMode.equals("random_atomic")){
                    System.out.println("Number of rules: " + nRules.get(0));
                    testEvaluationRandomAtomic(outputFolder, headless, seeds, policySizes, nRules);
                } else {
                    //wrong value
                }
            } else if(mode.equals("confl")){
                System.out.println("Checking for conflicts in IMDB policy by using permissions as policy and prohibitions as requests in sparql mode");
                testConflictDetection(outputFolder);
            }
            System.out.println("Test done. The output can be found in " + outputFolder + " folder");

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

    }

    private static void testEvaluationRandomAtomic(String outputFolder, boolean headless, List<Integer> seeds, List<Integer> policySizes, List<Integer> nRules) {
        System.out.println("Atomic test with random policies consisting of simple rules (1 attr = 1 value");
//        int[] policySizes = new int[]{1, 10, 100, 1000, 10000};
//        int[] seeds = {1, 5, 10, 100, 200, 500, 1000}; // we generate policies of each size 7 times
        Model unionModel = ModelUtils.loadFullSAVEModel();
        Model saveModel = ModelUtils.loadSAVEModel();
        List<SHACLComplianceResult> resultsTotalCore = new ArrayList<>();
        List<SHACLComplianceResult> resultsTotalSPARQL = new ArrayList<>();
        SAVEVocabulary vocab = new SAVEVocabulary(unionModel);
//        int[] nRules = new int[seeds.size()];
//        Arrays.fill(nRules, 100);
        for (int j=0; j < seeds.size(); j++) {
            int seed = seeds.get(j);
            int nRulesj = nRules.get(0);
            System.out.println("Seed: " + seed);
            for (int policySize : policySizes) {
                System.out.println("# of simple rules in policy: " + policySize);
                SAVERuleGenerator generator = new SAVERuleGenerator(unionModel, seed);
                String policyName = "save-ex:test_policy_" + seed + "_" + policySize;
                Set<String> attributes = new HashSet<>(Arrays.asList(vocab.dataPropertyPrefixedName,
                        vocab.actionPropertyPrefixedName, vocab.purposePropertyPrefixedName));
                SAVEPolicy policy = generator.generateRandomPolicy(policyName, policySize, false, attributes);
//                Model policyModel = ;
//                unionModel.add(policyModel);
                SHACLPolicyTranslator shaclPolicyTranslatorSPARQL = new SHACLPolicyTranslator(unionModel, policy);
                SHACLPolicyTranslator shaclPolicyTranslatorCore = new SHACLPolicyTranslator(unionModel, policy);
//                shaclPolicyTranslatorSPARQL.writeSHACLPolicyToFile("src/main/resources/", SPARQL);
//                finalModel.add(shaclPolicyTranslatorSPARQL.getInfModel());
                shaclPolicyTranslatorCore.translateSAVEPolicyToSHACL();
                shaclPolicyTranslatorSPARQL.translateSAVEPolicyToSHACLSPARQL();
                Model shapeModelSPARQL = shaclPolicyTranslatorSPARQL.getInfModel();
                Model shapeModelCore = shaclPolicyTranslatorCore.getInfModel();
                shaclPolicyTranslatorCore.writeSHACLPolicyToFile(outputFolder+"generated_policies/", false);
                shaclPolicyTranslatorSPARQL.writeSHACLPolicyToFile(outputFolder+"generated_policies/", true);
                SAVENormalizer normalizer = new SAVENormalizer(unionModel);
                SHACLInferenceRunner runnerCore = new SHACLInferenceRunner(unionModel, saveModel, shapeModelCore);
                SHACLInferenceRunner runnerSPARQL = new SHACLInferenceRunner(unionModel, saveModel, shapeModelSPARQL);
                List<SAVERuleNormalized> saveRules = new ArrayList<>();
                String shaclPolocyName = String.format("save-ex:Request%s", policyName.split(":")[1]);
//                System.out.println("PolicyName: " + shaclPolocyName);
                for (int i = 1; i <= nRulesj; i++) {
                    SAVERule rule = generator.generateRandomRule("save-ex:test_" + i + "_" + policySize + "_"  + seed, shaclPolocyName, true,
                            null);
                    SAVERuleNormalized ruleNormalized = normalizer.normalizeSAVERule(rule, true, false);
                    saveRules.add(ruleNormalized);
//                    System.out.println("Request type: " + ruleNormalized.getType());
                }
                //do Core with a whole batch and separately, then do "SPARQL" one by one (since it's not one request)
                // actually, SPARQL is not needed, since for atomic requests almost always the Core way will be faster,
                // just need to prove it empirically
                SHACLComplianceResult resultBatchCore = runnerCore.checkNormalizedSAVERulesBatchAtomicCore(saveRules, SHACLComplianceResult.Mode.RANDOM_SIMPLE_POLICIES, false);
                resultBatchCore.setPolicy(policyName);
                resultBatchCore.addnRulesPerPolicy(policySize);
                resultBatchCore.writeToFile(outputFolder + "logs/", "test_random_atomic_Core_" + policySize + "_" + seed);
                resultsTotalCore.add(resultBatchCore);

                SHACLComplianceResult resultBatchSPARQL = runnerSPARQL.checkNormalizedSAVERulesBatchAtomicSPARQL(saveRules, SHACLComplianceResult.Mode.RANDOM_SIMPLE_POLICIES, false);
                resultBatchSPARQL.setPolicy(policyName);
                resultBatchSPARQL.addnRulesPerPolicy(policySize);
                resultBatchSPARQL.writeToFile(outputFolder + "logs/", "test_random_atomic_SPARQL_" + policySize + "_" + seed);
                resultsTotalSPARQL.add(resultBatchSPARQL);
            }
        }
        //consolidate results for all seeds
        SHACLComplianceResult resultCore = createTotalResultFromList(resultsTotalCore,
                nRules.get(0)*policySizes.size()*seeds.size(), unionModel, SHACLComplianceResult.Mode.RANDOM_SIMPLE_POLICIES);
        resultCore.setPolicy("Random");
        resultCore.writeToFile(outputFolder, "test_random_atomic_Core_total_stats");
        SHACLComplianceResult resultSPARQL = createTotalResultFromList(resultsTotalSPARQL,
                nRules.get(0)*policySizes.size()*seeds.size(), unionModel, SHACLComplianceResult.Mode.RANDOM_SIMPLE_POLICIES);
        resultSPARQL.setPolicy("Random");
        resultSPARQL.writeToFile(outputFolder, "test_random_atomic_SPARQL_total_stats");
        //chart
        if(!headless) {
            EvaluationChart chart = new EvaluationChart("Batch of 100 atomic requests against random policies",
                    "# of simple rules per policy", "Avg time per request (ms)");
            chart.addSeries(resultCore.getAvgTimePerNumberOfRules(true), "Core");
            chart.addSeries(resultSPARQL.getAvgTimePerNumberOfRules(true), "SPARQL");
            chart.initChart();
            try {
                chart.saveAsPng(outputFolder, "random_policies");
            } catch (IllegalAccessException e) {
                System.out.println("Initialize chart first!");
            }
        }

    }

    private static void testEvaluationIMDBAtomic(String outputFolder, boolean headless, List<Integer> seeds, List<Integer> nRules) {
        System.out.println("Atomic batches test");
        Model unionModel = ModelUtils.loadFullSAVEModel();
        Model saveModel = ModelUtils.loadSAVEModel();
        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        unionModel.add(policyModel);
        Model shapeModelSPARQL = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.sparql.ttl");
        Model shapeModelCore = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.core.ttl");
        List<SHACLComplianceResult> resultsTotalCore = new ArrayList<>();
        List<SHACLComplianceResult> resultsTotalSPARQL = new ArrayList<>();
//        int[] seeds = {1, 5, 10, 100, 200, 500, 1000};
//        int[] nRules = new int[]{10, 50, 100, 1000, 5000};
        for (int seed : seeds) {
            System.out.println("Seed: " + seed);
            for (int nRulesj : nRules) {
                System.out.println("# of requests: " + nRulesj);
                SAVENormalizer normalizer = new SAVENormalizer(unionModel);
                SHACLInferenceRunner runnerCore = new SHACLInferenceRunner(unionModel, saveModel, shapeModelCore);
                SHACLInferenceRunner runnerSPARQL = new SHACLInferenceRunner(unionModel, saveModel, shapeModelSPARQL);
                List<SAVERuleNormalized> saveRules = new ArrayList<>();
                SAVERuleGenerator generator = new SAVERuleGenerator(unionModel, seed);
                for (int i = 1; i <= nRulesj; i++) {
                    SAVERule rule = generator.generateRandomRule("save-ex:test_" + i + "_" + seed, "save-ex:RequestIMDBPolicy",
                            true, null);
                    SAVERuleNormalized ruleNormalized = normalizer.normalizeSAVERule(rule, true, false);
                    saveRules.add(ruleNormalized);
//            System.out.println(ruleNormalized);
                }
                //do Core with a whole batch and separately, then do "SPARQL" one by one (since it's not one request)
                // actually, SPARQL is not needed, since for atomic requests almost always the Core way will be faster,
                // just need to prove it empirically
                SHACLComplianceResult resultBatchCore = runnerCore.checkNormalizedSAVERulesBatchAtomicCore(saveRules, SHACLComplianceResult.Mode.IMDB_ATOMIC_REQUESTS, false);
                resultBatchCore.setPolicy("IMDB");
                resultBatchCore.writeToFile(outputFolder + "logs/", "test_imdb_atomic_Core_" + nRulesj + "_" + seed);
                resultsTotalCore.add(resultBatchCore);

                SHACLComplianceResult resultBatchSPARQL = runnerSPARQL.checkNormalizedSAVERulesBatchAtomicSPARQL(saveRules, SHACLComplianceResult.Mode.IMDB_ATOMIC_REQUESTS, false);
                resultBatchSPARQL.setPolicy("IMDB");
                resultBatchSPARQL.writeToFile(outputFolder + "logs/", "test_imdb_atomic_SPARQL_" + nRulesj + "_" + seed);
                resultsTotalSPARQL.add(resultBatchSPARQL);
            }
        }
        //consolidate results for all seeds
        SHACLComplianceResult resultCore = createTotalResultFromList(resultsTotalCore,
                nRules.stream().mapToInt(Integer::intValue).sum() *seeds.size(), unionModel, SHACLComplianceResult.Mode.IMDB_ATOMIC_REQUESTS);
        resultCore.setPolicy("IMDB");
        resultCore.writeToFile(outputFolder, "test_imdb_atomic_Core_total_stats");
        SHACLComplianceResult resultSPARQL = createTotalResultFromList(resultsTotalSPARQL,
                nRules.stream().mapToInt(Integer::intValue).sum()*seeds.size(), unionModel, SHACLComplianceResult.Mode.IMDB_ATOMIC_REQUESTS);
        resultSPARQL.setPolicy("IMDB");
        resultSPARQL.writeToFile(outputFolder, "test_imdb_atomic_SPARQL_total_stats");
        //chart
        if(!headless) {
            EvaluationChart chart = new EvaluationChart("Batch of atomic requests",
                    "# of atomic requests in batch", "Avg time per batch (ms)");
            chart.addSeries(resultCore.getAvgTimePerNumberOfBatchRequests(), "Core");
            chart.addSeries(resultSPARQL.getAvgTimePerNumberOfBatchRequests(), "SPARQL");
            chart.initChart();
            try {
                chart.saveAsPng(outputFolder, "atomic");
            } catch (IllegalAccessException e) {
                System.out.println("Initialize chart first!");
            }
        }
    }

    private static void testEvaluationIMDBSimple(String outputFolder, boolean headless, List<Integer> seeds, List<Integer> nRules) {
        System.out.println("Simple random test");
        Model unionModel = ModelUtils.loadFullSAVEModel();
        Model saveModel = ModelUtils.loadSAVEModel();
        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        unionModel.add(policyModel);
        Model shapeModelSPARQL = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.sparql.ttl");
        Model shapeModelCore = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.core.ttl");
        List<SHACLComplianceResult> resultsTotalCore = new ArrayList<>();
        List<SHACLComplianceResult> resultsTotalSPARQL = new ArrayList<>();
//        List<Integer> seeds = seeds.//{1, 5, 10, 100, 200, 500, 1000};
//        int[] nRules = new int[seeds.size()];
//        Arrays.fill(nRules, 100);
        for (int j = 0; j < seeds.size(); j++) {
            int seed = seeds.get(j);
            int nRulesj;
            nRulesj = nRules.get(0);
            System.out.println("Seed: " + seed);
            SAVENormalizer normalizer = new SAVENormalizer(unionModel);
            SHACLInferenceRunner runnerCore = new SHACLInferenceRunner(unionModel, saveModel, shapeModelCore);
            SHACLInferenceRunner runnerSPARQL = new SHACLInferenceRunner(unionModel, saveModel, shapeModelSPARQL);
            List<SAVERuleNormalized> saveRules = new ArrayList<>();
            SAVERuleGenerator generator = new SAVERuleGenerator(unionModel, seed);
            for (int i = 1; i <= nRulesj; i++) {
                SAVERule rule = generator.generateRandomRule("save-ex:test_" + i + "_" + seed, "save-ex:RequestIMDBPolicy",
                        false, null);
                SAVERuleNormalized ruleNormalized = normalizer.normalizeSAVERule(rule, false, false);
                saveRules.add(ruleNormalized);
//            System.out.println(ruleNormalized);
            }
            //do each request one by one Corely and SPARQL, compare time
            List<SHACLComplianceResult> resultsCore = new ArrayList<>();
            List<SHACLComplianceResult> resultsSPARQL = new ArrayList<>();
            for (SAVERuleNormalized rule : saveRules) {
//                System.out.println("Rule " + rule.getName());
                try {
                    resultsCore.add(runnerCore.checkNormalizedSAVERuleCore(rule, true, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS, false));
                } catch (Exception e) {
                    System.out.println("Request " + rule.getName() + " could not be processed, too many subrequests!");
                }
                resultsSPARQL.add(runnerSPARQL.checkNormalizedSAVERuleSPARQL(rule, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS, false));
            }
            SHACLComplianceResult resultBatchCore = createTotalResultFromList(resultsCore, saveRules.size(), unionModel, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS);
            resultBatchCore.setPolicy("IMDB");
            resultBatchCore.writeToFile(outputFolder + "logs/", "test_imdb_simple_Core_" + nRulesj + "_" + seed);
            resultsTotalCore.add(resultBatchCore);

            SHACLComplianceResult resultBatchSPARQL = createTotalResultFromList(resultsSPARQL, saveRules.size(), unionModel, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS);
            resultBatchSPARQL.setPolicy("IMDB");
            resultBatchSPARQL.writeToFile(outputFolder + "logs/", "test_imdb_simple_SPARQL_" + nRulesj + "_" + seed);
            resultsTotalSPARQL.add(resultBatchSPARQL);
        }
        //consolidate results for all seeds
        SHACLComplianceResult resultCore = createTotalResultFromList(resultsTotalCore, nRules.get(0)*seeds.size(), unionModel, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS);
        resultCore.setPolicy("IMDB");
        resultCore.writeToFile(outputFolder, "test_imdb_simple_Core_total_stats");
        SHACLComplianceResult resultSPARQL = createTotalResultFromList(resultsTotalSPARQL, nRules.get(0)*seeds.size(), unionModel, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS);
        resultSPARQL.setPolicy("IMDB");
        resultSPARQL.writeToFile(outputFolder, "test_imdb_simple_SPARQL_total_stats");
        //chart
        if(!headless) {
//            EvaluationChart chart = new EvaluationChart("Batch of simple requests",
//                    "# of subrequests", "Avg time per request (ms)");
//            chart.addSeries(resultCore.getAvgTimePerNumberOfSubrequests(), "Core");
//            chart.addSeries(resultSPARQL.getAvgTimePerNumberOfSubrequests(), "SPARQL");
//            chart.initChart();


            //bucketed chart
            EvaluationChart chartBucketed = new EvaluationChart("Batch of simple requests",
                    "# of subrequests (bins)", "Avg time per request (ms)");
            chartBucketed.addSeries(resultCore.getAvgTimePerNumberOfSubrequestsBucketed(), "Core");
            chartBucketed.addSeries(resultSPARQL.getAvgTimePerNumberOfSubrequestsBucketed(), "SPARQL");
            chartBucketed.initChart();
            try {
//                chart.saveAsPng(outputFolder, "simple");
                chartBucketed.saveAsPng(outputFolder, "simple_bin");
            } catch (IllegalAccessException e) {
                System.out.println("Initialize chart first!");
            }
        }
    }



    private static void testComplianceChecking(boolean SPARQL, String outputFolder, boolean ultimate, boolean headless) {
        Model unionModel = ModelUtils.loadFullSAVEModel();
        Model saveModel = ModelUtils.loadSAVEModel();
        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        Model shapeModel;
        if (SPARQL) {
            shapeModel = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.sparql.ttl");
        } else {
            shapeModel = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.core.ttl");
        }
        Model requestsModel = ModelUtils.loadModelFromResourceFile(ultimate ? "testIMDBRequestUltimate.ttl" : "testIMDBRequests.ttl");
//        requestsModel.add(shapeModel);
        unionModel.add(shapeModel).add(policyModel);//.add(requestsModel);

        List<SAVERule> requests = extractRequestsFromModel(JenaUtil.createMemoryModel().add(unionModel).add(requestsModel));
        SAVENormalizer normalizer = new SAVENormalizer(unionModel);
        List<SHACLComplianceResult> results = new ArrayList<>();
        for (SAVERule request : requests) {
            SAVERuleNormalized requestNormalized = normalizer.normalizeSAVERule(request, false, true);
            SHACLInferenceRunner runner = new SHACLInferenceRunner(unionModel, saveModel, shapeModel);
            SHACLComplianceResult result = null;
            if (SPARQL) {
                result = runner.checkNormalizedSAVERuleSPARQL(requestNormalized,
                        (ultimate) ? SHACLComplianceResult.Mode.IMDB_ULTIMATE_REQUEST : SHACLComplianceResult.Mode.IMDB_TEST_REQUESTS, true);
            } else {
                try {
                    result = runner.checkNormalizedSAVERuleCore(requestNormalized, false,
                            (ultimate) ? SHACLComplianceResult.Mode.IMDB_ULTIMATE_REQUEST : SHACLComplianceResult.Mode.IMDB_TEST_REQUESTS, true);
                } catch (Exception e) {
                    System.out.println("The Core method should not have stopped but it did!");
                }
            }
            if(result != null) {
                result.setPolicy("IMDB");
                results.add(result);

            }

        }
        SHACLComplianceResult resultTotal = createTotalResultFromList(results, requests.size(), unionModel,
                (ultimate) ? SHACLComplianceResult.Mode.IMDB_ULTIMATE_REQUEST : SHACLComplianceResult.Mode.IMDB_TEST_REQUESTS);
        resultTotal.writeToFile(outputFolder, "inference" +(SPARQL?"_SPARQL":"_Core")+((ultimate?"_ultimate_request":"_test_requests")));
    }

    private static void testConflictDetection(String outputFolder){
        Model unionModel = ModelUtils.loadFullSAVEModel();
        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        unionModel.add(policyModel);
        List<SAVEPolicy> policies = extractPoliciesFromModel(unionModel);
        for (SAVEPolicy policy: policies) {
            SAVEConflictDetector detector = new SAVEConflictDetector(unionModel);
            SHACLComplianceResult result = detector.detectConflictsInPolicy(policy, true, SHACLComplianceResult.Mode.IMDB_TEST_CONFLICT,
                    outputFolder);
            Model infModel = result.getInfModel();
            List<Statement> conflicts = infModel.listStatements(null, infModel.createProperty(
                    SAVEVocabulary.SAVEURI + "conformsTo"), (String) null).toList();
            System.out.println(conflicts.size() + " conflicts were found");
            result.setPolicy("IMDB");
            result.writeToFile(outputFolder, "test_imdb_conflicts_stats");
        }

    }


}