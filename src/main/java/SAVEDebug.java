import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.example.*;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

import java.util.*;

import static org.example.SPARQLUtils.extractPoliciesFromModel;


public class SAVEDebug {


    /**
     * Main file for evaluation of SHACL rules based on SAVE model
     */
    public static void main(String[] args) throws Exception {
//        testCreation(true);
//        testConflictDetection();
        testSAVEPolicyToTtl();
//        testComplianceChecking(false);

//        testEvaluationRandomAtomic();
//        testEvaluationIMDBAtomic();
//        testEvaluationIMDBSimple();

//        testValidation();

//        testInference();

//        testListConstraint();

//        testListRule();

//        testDPVTre();
    }

    private static void testDPVTre() {
        Model saveModel = ModelUtils.loadFullSAVEModel();
        SPARQLUtils.getDPVTree(saveModel, "save:LegalBasis");


    }


    private static void testListConstraint() {
//        Model unionModel = ModelUtils.loadFullSAVEModel();
//        // try to load test file and run inference
//        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        Model shapeModel = ModelUtils.loadModelFromResourceFile("ConstraintTest.ttl");
        Model dataModel = ModelUtils.loadModelFromResourceFile("ConstraintTest.ttl");
        Model dpvModel = ModelUtils.loadModelFromResourceFile("dpv.ttl");
//        Model requestsModel = ModelUtils.loadModelFromResourceFile("test10.ttl"); // "testIMDBRequests.ttl"
        dataModel.add(dpvModel);//.add(policyModel).add(requestsModel);

        // Perform the validation of everything, using the data model
        // also as the shapes model - you may have them separated
        Resource report = ValidationUtil.validateModel(shapeModel, shapeModel, true);

        // Print violations
        System.out.println(ModelPrinter.get().print(report.getModel()));

    }

    private static void testListRule() {
//        Model unionModel = ModelUtils.loadFullSAVEModel();
//        // try to load test file and run inference
//        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        Model saveModel = ModelUtils.loadFullSAVEModel();
        Model requestModel = ModelUtils.loadModelFromResourceFile("testIMDBRequests_bckp.ttl");
        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
//        Model requestsModel = ModelUtils.loadModelFromResourceFile("test10.ttl"); // "testIMDBRequests.ttl"
        requestModel.add(policyModel).add(saveModel);//.add(policyModel).add(requestsModel);

        //let's check the query
        String query = """
                PREFIX  save-ex: <http://example.com/privacy-policy-model/examples/>
                 PREFIX  orcp: <http://example.com/odrl:profile:regulatoryCompliance/>
                 PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                 PREFIX  owl:  <http://www.w3.org/2002/07/owl#>
                 PREFIX  dpv-gdpr: <http://www.w3.org/ns/dpv-gdpr#>
                 PREFIX  save: <http://example.com/privacy-policy-model/>
                 PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                 PREFIX  dpv:  <http://www.w3.org/ns/dpv#>
                 
                 SELECT
                 ?dataItem ?dataClass 
                 ?actionItem ?actionsClass
                 
                 WHERE {
                     BIND(BNODE() AS ?bnode)
                     {SELECT *
                        WHERE {
                            {
                                BIND(save-ex:Request_6_test_opt as $this) .
                                $this save:dataList ?dataVal .
                                ?dataVal rdf:rest*/rdf:first ?dataItem .
                                ?dataItem rdf:type/rdfs:subClassOf* ?dataClass .
                                FILTER (?dataClass IN (dpv:Identifying))
                            }
                            UNION{
                                BIND(save-ex:Request_6_test_opt as $this) .
                                FILTER NOT EXISTS {$this save:dataList ?dataVal .}
                                {BIND(save:Blah as ?dataItem) .
                                BIND(dpv:Blah as ?dataClass) .}
                                UNION {BIND(save:Blah1 as ?dataItem) .
                                BIND(dpv:Blah2 as ?dataClass) .}
                                UNION {BIND(save:Blah3 as ?dataItem) .
                                BIND(dpv:Blah3 as ?dataClass) .}
                            }
                            {
                                BIND(save-ex:Request_6_test_opt as $this) .
                                $this save:actionList ?actionVal .
                                ?actionVal rdf:rest*/rdf:first ?actionItem .
                                ?actionItem rdf:type/rdfs:subClassOf* ?actionClass .
                                FILTER (?actionClass IN (dpv:Processing))
                            }
                            UNION{
                                BIND(save-ex:Request_6_test_opt as $this) .
                                FILTER NOT EXISTS {$this save:dataList ?actionVal .}
                                {BIND(save:Blah as ?actionItem) .
                                BIND(dpv:Blah as ?actionClass) .}
                                UNION {BIND(save:Blah1 as ?actionItem) .
                                BIND(dpv:Blah2 as ?actionClass) .}
                                UNION {BIND(save:Blah3 as ?actionItem) .
                                BIND(dpv:Blah3 as ?actionClass) .}
                            }
                         }
                     }
                     
                 }              
                              
                    """;

        QueryExecution qe = QueryExecutionFactory.create(query, requestModel);
        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {
            final QuerySolution qs = rs.next();

            System.out.println(qs);
        }


//        Model result = RuleUtil.executeRules(requestModel, saveModel, null, null);


        // Print violations
//        result.setNsPrefixes(requestModel.getNsPrefixMap());
//        System.out.println(ModelPrinter.get().print(result));

    }


    private static void testCreation(boolean optimized) {
        Model unionModel = ModelUtils.loadFullSAVEModel();
        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        unionModel.add(policyModel);
        Model finalModel = JenaUtil.createMemoryModel();
        List<SAVEPolicy> policies = extractPoliciesFromModel(unionModel);
        for (SAVEPolicy policy: policies) {
            SHACLPolicyTranslator shaclPolicyTranslator = new SHACLPolicyTranslator(unionModel, policy);
            if (optimized) {
                shaclPolicyTranslator.translateSAVEPolicyToSHACLOptimized();
            } else {
                shaclPolicyTranslator.translateSAVEPolicyToSHACL();
            }
            shaclPolicyTranslator.writeSHACLPolicyToFile("src/main/resources/", optimized);
            finalModel.add(shaclPolicyTranslator.getInfModel());
        }
    }

    private static void testSAVEPolicyToTtl() {
        Model unionModel = ModelUtils.loadFullSAVEModel();
        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        unionModel.add(policyModel);
        List<SAVEPolicy> policies = extractPoliciesFromModel(unionModel);
        for (SAVEPolicy policy: policies) {
            SAVEPolicyTranslator savePolicyTranslator = new SAVEPolicyTranslator(unionModel, policy);
            savePolicyTranslator.translateSAVEPolicy();
            savePolicyTranslator.writeSAVEPolicyToFile("src/main/resources/");
        }
    }

    private static void testConflictDetection(){
        Model unionModel = ModelUtils.loadFullSAVEModel();
        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        unionModel.add(policyModel);
        List<SAVEPolicy> policies = extractPoliciesFromModel(unionModel);
        for (SAVEPolicy policy: policies) {
            SAVEConflictDetector detector = new SAVEConflictDetector(unionModel);
            detector.detectConflictsInPolicy(policy, true, SHACLComplianceResult.Mode.IMDB_TEST_CONFLICT, null);
        }
    }


    private static void testValidation() {
        Model unionModel = ModelUtils.loadFullSAVEModel();
        // try to load test file and run inference
        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        Model shapeModel = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.ttl");
        Model requestsModel = ModelUtils.loadModelFromResourceFile("test10.ttl"); // "testIMDBRequests.ttl"
        unionModel.add(shapeModel).add(policyModel).add(requestsModel);

        // Perform the validation of everything, using the data model
        // also as the shapes model - you may have them separated
        Resource report = ValidationUtil.validateModel(unionModel, unionModel, true);

        // Print violations
        System.out.println(ModelPrinter.get().print(report.getModel()));
    }

    private static void testInference() {
        Model unionModel = ModelUtils.loadFullSAVEModel();
        // Perform the rule calculation, using the data model
        // also as the rule model - you may have them separated
        Model policyModel = ModelUtils.loadModelFromResourceFile("save.imdb.policy.ttl");
        Model testModel = ModelUtils.loadModelFromResourceFile("IMDBPolicy.shapes.ttl");
        Model requestsModel = ModelUtils.loadModelFromResourceFile("test10.ttl"); // "testIMDBRequests.ttl"
        unionModel.add(policyModel).add(requestsModel);
        Model dataModel = unionModel;
        Model shapeModel = testModel;

        Model result = RuleUtil.executeRules(dataModel, shapeModel, null, null);
        dataModel.add(testModel);
        Model result1 = RuleUtil.executeRules(dataModel, dataModel, null, null);


        // you may want to add the original data, to make sense of the rule results
//        result.add(dataModel);

        final List<Statement> triples = result.listStatements().toList();
        final List<Statement> triples1 = result1.listStatements().toList();
        for (Statement triple :
                triples) {
            dataModel.add(triple);
        }

        System.out.println(ModelPrinter.get().print(dataModel));
    }

}