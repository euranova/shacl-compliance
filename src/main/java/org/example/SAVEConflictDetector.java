package org.example;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.topbraid.jenax.util.JenaUtil;

import java.util.*;

import static org.example.SHACLComplianceResult.createTotalResultFromList;

/**
 * First version of the functionality of conflict detection
 */
public class SAVEConflictDetector {

    private SAVEVocabulary vocab;
    private Model unionModel;
    private Model infModel;


    public SAVEConflictDetector(Model unionModel) {
        this.unionModel = unionModel;
        vocab = new SAVEVocabulary(unionModel);
        infModel = JenaUtil.createMemoryModel();
        infModel.setNsPrefixes(unionModel.getNsPrefixMap());
    }


    /**
     * Given a policy, detects "intersections" between permissions and prohibitions
     * using optimized compliance checking method
     * @param policy input policy
     * @param normalizeProhibitions whether to normalize prohibitions as "reqeusts"
     *                              and use permissions as a "policy" or the other way around
     * @param mode test mode for the result object
     * @param outputFolder the folder to write the result to
     * @return result containing test stats
     */
    public SHACLComplianceResult detectConflictsInPolicy(SAVEPolicy policy, boolean normalizeProhibitions,
                                                         SHACLComplianceResult.Mode mode, String outputFolder){
        //create new proxy policy
        SAVEPolicy proxy = null;
        if(normalizeProhibitions && (!policy.getPermissions().isEmpty() || !policy.getDispensations().isEmpty())){
            //permissions go into the policy
            proxy = new SAVEPolicy(policy.getName() + "PreconflictPermissions");
            proxy.setPermissions(policy.getPermissions());
            proxy.setDispensations(policy.getDispensations());
        }else if(!policy.getProhibitions().isEmpty()) {
            //prohibitions go into the policy
            proxy = new SAVEPolicy(policy.getName() + "PreconflictProhibtions");
            proxy.setProhibitions(policy.getProhibitions());
        }
        if(proxy == null){
            throw new IllegalArgumentException("Either permissions or prohibitons are empty -> no conflicts!");
        }
        SHACLPolicyTranslator translator = new SHACLPolicyTranslator(unionModel, proxy);
        translator.translateSAVEPolicyToSHACLOptimized();
        translator.writeSHACLPolicyToFile("src/main/resources/", true);
        if(outputFolder != null){
            translator.writeSHACLPolicyToFile(outputFolder, true);
        }
        Model shaclPolicy = translator.getInfModel();
        //now let us take all other rules and run them against the policy
        List<SAVERule> requests = null;
        if(normalizeProhibitions){
            requests = policy.getProhibitions();
        }else {
            requests = policy.getPermissions();
            requests.addAll(policy.getDispensations());
        }
        SAVENormalizer normalizer = new SAVENormalizer(unionModel);
        SHACLInferenceRunner runner = new SHACLInferenceRunner(unionModel, unionModel, shaclPolicy);
        List<SHACLComplianceResult> results = new ArrayList<>();
        for(SAVERule rule: requests){
            System.out.println("Checking " + rule.getName());
            rule.setType(proxy.getName().split(":")[0] +":" + "Request" + proxy.getName().split(":")[1]);
            SAVERuleNormalized ruleNormalized = normalizer.normalizeSAVERule(rule, false, true);
            SHACLComplianceResult ruleResult = runner.checkNormalizedSAVERuleOptimized(ruleNormalized,
                    mode, true);
            List<Statement> conflicts = ruleResult.getInfModel().listStatements(null, infModel.createProperty(
                    SAVEVocabulary.SAVEURI + "conformsTo"), (String) null).toList();
            System.out.println(conflicts.size() + " conflicts were found");
            results.add(ruleResult);
        }
        SHACLComplianceResult resultTotal = createTotalResultFromList(results, 0, unionModel, SHACLComplianceResult.Mode.IMDB_TEST_CONFLICT);
        return resultTotal;
    }
}
