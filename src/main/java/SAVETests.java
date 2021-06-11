
import org.apache.commons.cli.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.example.*;
import org.topbraid.jenax.util.JenaUtil;

import java.awt.*;
import java.util.*;
import java.util.List;
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

        Option optimizedOption = new Option("p", "optimized", true, "whether to use optimized inference (true/false), default = false");
        optimizedOption.setRequired(false);
        options.addOption(optimizedOption);

        Option ultimateOption = new Option("u", "ultimate", true, "whether to use ultimate request (true/false, only works with optimized option), default = false");
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
            boolean optimized = Boolean.parseBoolean(cmd.getOptionValue("optimized", "false"));
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
                System.out.println("Optimized: " + optimized);
                System.out.println("Ultimate Request: " + ultimate);
                if (!optimized && ultimate){
                    System.out.println("Cannot process ultimate request with regular procedure, please choose optimized version by setting -p True");
                    System.exit(1);
                }
                testComplianceChecking(optimized, outputFolder, ultimate, headless);
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
                System.out.println("Checking for conflicts in IMDB policy by using permissions as policy and prohibitions as requests in optimized mode");
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
        List<SHACLComplianceResult> resultsTotalRegular = new ArrayList<>();
        List<SHACLComplianceResult> resultsTotalOptimized = new ArrayList<>();
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
                SHACLPolicyTranslator shaclPolicyTranslatorOptimized = new SHACLPolicyTranslator(unionModel, policy);
                SHACLPolicyTranslator shaclPolicyTranslatorRegular = new SHACLPolicyTranslator(unionModel, policy);
//                shaclPolicyTranslatorOptimized.writeSHACLPolicyToFile("src/main/resources/", optimized);
//                finalModel.add(shaclPolicyTranslatorOptimized.getInfModel());
                shaclPolicyTranslatorRegular.translateSAVEPolicyToSHACL();
                shaclPolicyTranslatorOptimized.translateSAVEPolicyToSHACLOptimized();
                Model shapeModelOptimized = shaclPolicyTranslatorOptimized.getInfModel();
                Model shapeModelRegular = shaclPolicyTranslatorRegular.getInfModel();
                shaclPolicyTranslatorRegular.writeSHACLPolicyToFile(outputFolder+"generated_policies/", false);
                shaclPolicyTranslatorOptimized.writeSHACLPolicyToFile(outputFolder+"generated_policies/", true);
                SAVENormalizer normalizer = new SAVENormalizer(unionModel);
                SHACLInferenceRunner runnerRegular = new SHACLInferenceRunner(unionModel, saveModel, shapeModelRegular);
                SHACLInferenceRunner runnerOptimized = new SHACLInferenceRunner(unionModel, saveModel, shapeModelOptimized);
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
                //do regular with a whole batch and separately, then do "optimized" one by one (since it's not one request)
                // actually, optimized is not needed, since for atomic requests almost always the regular way will be faster,
                // just need to prove it empirically
                SHACLComplianceResult resultBatchRegular = runnerRegular.checkNormalizedSAVERulesBatchAtomicRegular(saveRules, SHACLComplianceResult.Mode.RANDOM_SIMPLE_POLICIES, false);
                resultBatchRegular.setPolicy(policyName);
                resultBatchRegular.addnRulesPerPolicy(policySize);
                resultBatchRegular.writeToFile(outputFolder + "logs/", "test_random_atomic_regular_" + policySize + "_" + seed);
                resultsTotalRegular.add(resultBatchRegular);

                SHACLComplianceResult resultBatchOptimized = runnerOptimized.checkNormalizedSAVERulesBatchAtomicOptimized(saveRules, SHACLComplianceResult.Mode.RANDOM_SIMPLE_POLICIES, false);
                resultBatchOptimized.setPolicy(policyName);
                resultBatchOptimized.addnRulesPerPolicy(policySize);
                resultBatchOptimized.writeToFile(outputFolder + "logs/", "test_random_atomic_optimized_" + policySize + "_" + seed);
                resultsTotalOptimized.add(resultBatchOptimized);
            }
        }
        //consolidate results for all seeds
        SHACLComplianceResult resultRegular = createTotalResultFromList(resultsTotalRegular,
                nRules.get(0)*policySizes.size()*seeds.size(), unionModel, SHACLComplianceResult.Mode.RANDOM_SIMPLE_POLICIES);
        resultRegular.setPolicy("Random");
        resultRegular.writeToFile(outputFolder, "test_random_atomic_regular_total_stats");
        SHACLComplianceResult resultOptimized = createTotalResultFromList(resultsTotalOptimized,
                nRules.get(0)*policySizes.size()*seeds.size(), unionModel, SHACLComplianceResult.Mode.RANDOM_SIMPLE_POLICIES);
        resultOptimized.setPolicy("Random");
        resultOptimized.writeToFile(outputFolder, "test_random_atomic_optimized_total_stats");
        //chart
        if(!headless) {
            EvaluationChart chart = new EvaluationChart("Batch of 100 atomic requests against random policies",
                    "# of simple rules per policy", "Avg time per request (ms)");
            chart.addSeries(resultRegular.getAvgTimePerNumberOfRules(true), "Regular");
            chart.addSeries(resultOptimized.getAvgTimePerNumberOfRules(true), "Optimized");
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
        Model shapeModelOptimized = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.optimized.ttl");
        Model shapeModelRegular = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.ttl");
        List<SHACLComplianceResult> resultsTotalRegular = new ArrayList<>();
        List<SHACLComplianceResult> resultsTotalOptimized = new ArrayList<>();
//        int[] seeds = {1, 5, 10, 100, 200, 500, 1000};
//        int[] nRules = new int[]{10, 50, 100, 1000, 5000};
        for (int seed : seeds) {
            System.out.println("Seed: " + seed);
            for (int nRulesj : nRules) {
                System.out.println("# of requests: " + nRulesj);
                SAVENormalizer normalizer = new SAVENormalizer(unionModel);
                SHACLInferenceRunner runnerRegular = new SHACLInferenceRunner(unionModel, saveModel, shapeModelRegular);
                SHACLInferenceRunner runnerOptimized = new SHACLInferenceRunner(unionModel, saveModel, shapeModelOptimized);
                List<SAVERuleNormalized> saveRules = new ArrayList<>();
                SAVERuleGenerator generator = new SAVERuleGenerator(unionModel, seed);
                for (int i = 1; i <= nRulesj; i++) {
                    SAVERule rule = generator.generateRandomRule("save-ex:test_" + i + "_" + seed, "save-ex:RequestIMDBPolicy",
                            true, null);
                    SAVERuleNormalized ruleNormalized = normalizer.normalizeSAVERule(rule, true, false);
                    saveRules.add(ruleNormalized);
//            System.out.println(ruleNormalized);
                }
                //do regular with a whole batch and separately, then do "optimized" one by one (since it's not one request)
                // actually, optimized is not needed, since for atomic requests almost always the regular way will be faster,
                // just need to prove it empirically
                SHACLComplianceResult resultBatchRegular = runnerRegular.checkNormalizedSAVERulesBatchAtomicRegular(saveRules, SHACLComplianceResult.Mode.IMDB_ATOMIC_REQUESTS, false);
                resultBatchRegular.setPolicy("IMDB");
                resultBatchRegular.writeToFile(outputFolder + "logs/", "test_imdb_atomic_regular_" + nRulesj + "_" + seed);
                resultsTotalRegular.add(resultBatchRegular);

                SHACLComplianceResult resultBatchOptimized = runnerOptimized.checkNormalizedSAVERulesBatchAtomicOptimized(saveRules, SHACLComplianceResult.Mode.IMDB_ATOMIC_REQUESTS, false);
                resultBatchOptimized.setPolicy("IMDB");
                resultBatchOptimized.writeToFile(outputFolder + "logs/", "test_imdb_atomic_optimized_" + nRulesj + "_" + seed);
                resultsTotalOptimized.add(resultBatchOptimized);
            }
        }
        //consolidate results for all seeds
        SHACLComplianceResult resultRegular = createTotalResultFromList(resultsTotalRegular,
                nRules.stream().mapToInt(Integer::intValue).sum() *seeds.size(), unionModel, SHACLComplianceResult.Mode.IMDB_ATOMIC_REQUESTS);
        resultRegular.setPolicy("IMDB");
        resultRegular.writeToFile(outputFolder, "test_imdb_atomic_regular_total_stats");
        SHACLComplianceResult resultOptimized = createTotalResultFromList(resultsTotalOptimized,
                nRules.stream().mapToInt(Integer::intValue).sum()*seeds.size(), unionModel, SHACLComplianceResult.Mode.IMDB_ATOMIC_REQUESTS);
        resultOptimized.setPolicy("IMDB");
        resultOptimized.writeToFile(outputFolder, "test_imdb_atomic_optimized_total_stats");
        //chart
        if(!headless) {
            EvaluationChart chart = new EvaluationChart("Batch of atomic requests",
                    "# of atomic requests in batch", "Avg time per batch (ms)");
            chart.addSeries(resultRegular.getAvgTimePerNumberOfBatchRequests(), "Regular");
            chart.addSeries(resultOptimized.getAvgTimePerNumberOfBatchRequests(), "Optimized");
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
        Model shapeModelOptimized = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.optimized.ttl");
        Model shapeModelRegular = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.ttl");
        List<SHACLComplianceResult> resultsTotalRegular = new ArrayList<>();
        List<SHACLComplianceResult> resultsTotalOptimized = new ArrayList<>();
//        List<Integer> seeds = seeds.//{1, 5, 10, 100, 200, 500, 1000};
//        int[] nRules = new int[seeds.size()];
//        Arrays.fill(nRules, 100);
        for (int j = 0; j < seeds.size(); j++) {
            int seed = seeds.get(j);
            int nRulesj;
            nRulesj = nRules.get(0);
            System.out.println("Seed: " + seed);
            SAVENormalizer normalizer = new SAVENormalizer(unionModel);
            SHACLInferenceRunner runnerRegular = new SHACLInferenceRunner(unionModel, saveModel, shapeModelRegular);
            SHACLInferenceRunner runnerOptimized = new SHACLInferenceRunner(unionModel, saveModel, shapeModelOptimized);
            List<SAVERuleNormalized> saveRules = new ArrayList<>();
            SAVERuleGenerator generator = new SAVERuleGenerator(unionModel, seed);
            for (int i = 1; i <= nRulesj; i++) {
                SAVERule rule = generator.generateRandomRule("save-ex:test_" + i + "_" + seed, "save-ex:RequestIMDBPolicy",
                        false, null);
                SAVERuleNormalized ruleNormalized = normalizer.normalizeSAVERule(rule, false, false);
                saveRules.add(ruleNormalized);
//            System.out.println(ruleNormalized);
            }
            //do each request one by one regularly and optimized, compare time
            List<SHACLComplianceResult> resultsRegular = new ArrayList<>();
            List<SHACLComplianceResult> resultsOptimized = new ArrayList<>();
            for (SAVERuleNormalized rule : saveRules) {
//                System.out.println("Rule " + rule.getName());
                try {
                    resultsRegular.add(runnerRegular.checkNormalizedSAVERuleRegular(rule, true, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS, false));
                } catch (Exception e) {
                    System.out.println("Request " + rule.getName() + " could not be processed, too many subrequests!");
                }
                resultsOptimized.add(runnerOptimized.checkNormalizedSAVERuleOptimized(rule, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS, false));
            }
            SHACLComplianceResult resultBatchRegular = createTotalResultFromList(resultsRegular, saveRules.size(), unionModel, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS);
            resultBatchRegular.setPolicy("IMDB");
            resultBatchRegular.writeToFile(outputFolder + "logs/", "test_imdb_simple_regular_" + nRulesj + "_" + seed);
            resultsTotalRegular.add(resultBatchRegular);

            SHACLComplianceResult resultBatchOptimized = createTotalResultFromList(resultsOptimized, saveRules.size(), unionModel, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS);
            resultBatchOptimized.setPolicy("IMDB");
            resultBatchOptimized.writeToFile(outputFolder + "logs/", "test_imdb_simple_optimized_" + nRulesj + "_" + seed);
            resultsTotalOptimized.add(resultBatchOptimized);
        }
        //consolidate results for all seeds
        SHACLComplianceResult resultRegular = createTotalResultFromList(resultsTotalRegular, nRules.get(0)*seeds.size(), unionModel, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS);
        resultRegular.setPolicy("IMDB");
        resultRegular.writeToFile(outputFolder, "test_imdb_simple_regular_total_stats");
        SHACLComplianceResult resultOptimized = createTotalResultFromList(resultsTotalOptimized, nRules.get(0)*seeds.size(), unionModel, SHACLComplianceResult.Mode.IMDB_SIMPLE_REQUESTS);
        resultOptimized.setPolicy("IMDB");
        resultOptimized.writeToFile(outputFolder, "test_imdb_simple_optimized_total_stats");
        //chart
        if(!headless) {
//            EvaluationChart chart = new EvaluationChart("Batch of simple requests",
//                    "# of subrequests", "Avg time per request (ms)");
//            chart.addSeries(resultRegular.getAvgTimePerNumberOfSubrequests(), "Regular");
//            chart.addSeries(resultOptimized.getAvgTimePerNumberOfSubrequests(), "Optimized");
//            chart.initChart();


            //bucketed chart
            EvaluationChart chartBucketed = new EvaluationChart("Batch of simple requests",
                    "# of subrequests (bins)", "Avg time per request (ms)");
            chartBucketed.addSeries(resultRegular.getAvgTimePerNumberOfSubrequestsBucketed(), "Regular");
            chartBucketed.addSeries(resultOptimized.getAvgTimePerNumberOfSubrequestsBucketed(), "Optimized");
            chartBucketed.initChart();
            try {
//                chart.saveAsPng(outputFolder, "simple");
                chartBucketed.saveAsPng(outputFolder, "simple_bin");
            } catch (IllegalAccessException e) {
                System.out.println("Initialize chart first!");
            }
        }
    }



    private static void testComplianceChecking(boolean optimized, String outputFolder, boolean ultimate, boolean headless) {
        Model unionModel = ModelUtils.loadFullSAVEModel();
        Model saveModel = ModelUtils.loadSAVEModel();
        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        Model shapeModel;
        if (optimized) {
            shapeModel = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.optimized.ttl");
        } else {
            shapeModel = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.ttl");
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
            if (optimized) {
                result = runner.checkNormalizedSAVERuleOptimized(requestNormalized,
                        (ultimate) ? SHACLComplianceResult.Mode.IMDB_ULTIMATE_REQUEST : SHACLComplianceResult.Mode.IMDB_TEST_REQUESTS, true);
            } else {
                try {
                    result = runner.checkNormalizedSAVERuleRegular(requestNormalized, false,
                            (ultimate) ? SHACLComplianceResult.Mode.IMDB_ULTIMATE_REQUEST : SHACLComplianceResult.Mode.IMDB_TEST_REQUESTS, true);
                } catch (Exception e) {
                    System.out.println("The regular method should not have stopped but it did!");
                }
            }
            if(result != null) {
                result.setPolicy("IMDB");
                results.add(result);

            }

        }
        SHACLComplianceResult resultTotal = createTotalResultFromList(results, requests.size(), unionModel,
                (ultimate) ? SHACLComplianceResult.Mode.IMDB_ULTIMATE_REQUEST : SHACLComplianceResult.Mode.IMDB_TEST_REQUESTS);
        resultTotal.writeToFile(outputFolder, "inference" +(optimized?"_optimized":"_regular")+((ultimate?"_ultimate_request":"_test_requests")));
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