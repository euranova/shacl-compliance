package org.example;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.arq.querybuilder.WhereBuilder;
import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.thymeleaf.model.IModel;
import org.topbraid.jenax.util.JenaUtil;

import java.util.*;

import static org.example.ModelUtils.RESOURCE_FOLDER;
import static org.example.ModelUtils.replacePrefixWithURI;

public class SPARQLUtils {

    public static List<SAVEPolicy> extractPoliciesFromModel(Model model){
        Map<String, String> policyMap = getPoliciesFromModel(model);
        List<SAVEPolicy> policies = new ArrayList<>();
        for (Map.Entry<String, String> policyEntry : policyMap.entrySet()) {
            String policyName = policyEntry.getKey();
            policyName = getPrefixedLabel(policyName, model);
            SAVEPolicy policy = new SAVEPolicy(policyName);
            policy = fillSAVEPolicy(policy, model);
            policies.add(policy);
            //now let's try to create a SHACL rule from one SAVE rule
            // start with empty model

        }
        return policies;
    }

    public static List<SAVERule> extractRequestsFromModel(Model model){
        Map<String, String> requestMap = getRequestsFromModel(model);
        List<SAVERule> requests = new ArrayList<>();
        for (Map.Entry<String, String> requestEntry : requestMap.entrySet()) {
            SAVERule rule = getRuleDescription(model, requestEntry.getKey(), requestEntry.getValue());
            requests.add(rule);
        }
        return requests;
    }

    private static SAVEPolicy fillSAVEPolicy(SAVEPolicy policy, Model model) {
        Map<String, String> map = getRulesFromPolicy(model, policy.getName());
        //now, for every rule, find all triples where it is a subject
        for (Map.Entry<String, String> ruleEntry : map.entrySet()) {
            String rule = ruleEntry.getKey();
            String type = ruleEntry.getValue();
            policy.addRule(getRuleDescription(model, rule, type));
        }
        return policy;
    }

    public static Map<String, String> getPoliciesFromModel(Model model){
        SelectBuilder sb = addPrefixesToSelectBuilder(model)
                .addVar("*")
                .addWhere("?policy", "rdf:type", "?type")
                .addWhere("?type", "rdfs:subClassOf", "orcp:Policy");

        Query query = sb.build();
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        Map<String, String> map = new HashMap<String, String>();

        while (rs.hasNext()) {
            final QuerySolution qs = rs.next();
            map.put(qs.get("policy").toString(), qs.get("type").toString());
//            System.out.println(qs.get("rule") +
//                    "\n\t" + qs.get("type"));
        }

        return map;
    }

    public static Map<String, String> getRequestsFromModel(Model model){
        //model has to contain requests and the policy definition
        SelectBuilder sb = addPrefixesToSelectBuilder(model)
                .addVar("*")
                .addWhere("?request", "rdf:type", "?requestType")
                .addWhere("?requestType", "rdfs:subClassOf", "save:RequestBase");

        Query query = sb.build();
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        Map<String, String> map = new HashMap<String, String>();

        while (rs.hasNext()) {
            final QuerySolution qs = rs.next();
            map.put(qs.get("request").toString(), qs.get("requestType").toString());
//            System.out.println(qs.get("rule") +
//                    "\n\t" + qs.get("type"));
        }

        return map;
    }

    public static Map<String, String> getRulesFromPolicy(Model model, String policyName){
        String prefixedPolicyName = policyName;// getPrefixedLabel(policyName, model);
        SelectBuilder sb = addPrefixesToSelectBuilder(model)
                .addVar("*")
                .addWhere("?rule", "rdf:type", "?type")
                .addWhere("?type", "rdfs:subClassOf", "orcp:Rule")
                .addWhere(prefixedPolicyName, "?p", "?rule");

        Query query = sb.build();
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        Map<String, String> map = new HashMap<String, String>();

        while (rs.hasNext()) {
            final QuerySolution qs = rs.next();
            map.put(qs.get("rule").toString(), qs.get("type").toString());
//            System.out.println(qs.get("rule") +
//                    "\n\t" + qs.get("type"));
        }

        return map;
    }

    public static Map<String, String> getDPVTree(Model model, String rootClassName){
        String prefixedRootClassName = rootClassName;// getPrefixedLabel(rootClassName, model);
        SelectBuilder sb = addPrefixesToSelectBuilder(model)
                .addVar("*")
                .addWhere("?individ", "rdf:type", "?class")
                .addWhere("?class", "rdfs:subClassOf*", prefixedRootClassName)
                .addOptional(addPrefixesToSelectBuilder(model).addWhere("?class", "rdfs:subClassOf", "?directParentClass")
                                                .addWhere("?directParentIndivid", "rdf:type", "?directParentClass"));

        Query query = sb.build();
        System.out.println(query.toString());
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        Map<String, String> map = new HashMap<String, String>();

        while (rs.hasNext()) {
            final QuerySolution qs = rs.next();
//            map.put(qs.get("concept").toString(), qs.get("type").toString());
            System.out.println(qs.get("individ") + " - " + qs.get("class")
                    + "\n\tparent:\t" + qs.get("directParentIndivid") + " - " + qs.get("directParentClass"));
        }

        return map;
    }

    public static SAVERule getRuleDescription(Model model, String rule, String type){
        String prefixedLabel = getPrefixedLabel(rule, model);
        String prefixedTypeLabel = getPrefixedLabel(type, model);
        SelectBuilder sb = null;
        try {
            sb = addPrefixesToSelectBuilder(model)
                    .addVar("*")
                    .addWhere(prefixedLabel, "?property", "?value")
                    .addWhere("?value", "rdf:type", "?type")
                    .addFilter("?type != owl:NamedIndividual");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Query query = sb.build();
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        SAVERule saveRule = new SAVERule(prefixedLabel, prefixedTypeLabel);
        while (rs.hasNext()) {
            final QuerySolution qs = rs.next();
            String property = getPrefixedLabel(qs.get("property").toString(), model);
            String value = getPrefixedLabel(qs.get("value").toString(), model);
            String valueType = getPrefixedLabel(qs.get("type").toString(), model);
            if(property.endsWith("constraint")){
                //read the constraint and turn it into normal property
                List<Triple<String, String, String>> constraint = getConstraint(value, valueType, model);
                saveRule.addConstraint(constraint);
            } else {
                saveRule.addProperty(property, value, valueType);
            }
        }
        return saveRule;
    }

    private static List<Triple<String, String, String>> getConstraint(String constraintName, String constraintValueType, Model model) {
        List<Triple<String, String, String>> constraint = new ArrayList<>();
        if (constraintValueType.endsWith("PredicateConstraint")){
            SelectBuilder sb = null;
            try {
                sb = addPrefixesToSelectBuilder(model)
                        .addVar("*")
                        .addWhere(constraintName, "?property", "?value")
                        .addWhere("?value", "rdf:type", "?type")
                        .addFilter("?type != owl:NamedIndividual");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Query query = sb.build();
            QueryExecution exec = QueryExecutionFactory.create(query, model);
            ResultSet rs = exec.execSelect();
            while (rs.hasNext()) {
                final QuerySolution qs = rs.next();
                String property = getPrefixedLabel(qs.get("property").toString(), model);
                String value = getPrefixedLabel(qs.get("value").toString(), model);
                String valueType = getPrefixedLabel(qs.get("type").toString(), model);
                constraint.add(new ImmutableTriple<>(property, value, valueType));
            }
        }
        return constraint;
    }

    public static String getPrefixedLabel(Resource resource, Model model) {
        return model.getNsURIPrefix(resource.getNameSpace()) + ":" + resource.getLocalName();
    }

    public static String getPrefixedLabel(String fullName, Model model) {
//        Map<String, Set<String>> uriToPrefixMap = getUriToPrefixMap(model);
//        String[] ruleSplit = fullName.split("/");
//        String shortName = ruleSplit[ruleSplit.length - 1];
//        if (shortName.contains("#")){
//            String[] ruleLabelSplit = shortName.split("#");
//            shortName = ruleLabelSplit[ruleLabelSplit.length - 1];
//        }
//        String rulePrefixURI = fullName.replace(shortName, "");
//        if (rulePrefixURI.equals("")){
//            //it's already prefixed
//            return fullName;
//        }
//
        Resource resource = model.getResource(fullName);
//        String prefix = model.getNsURIPrefix(resource.getNameSpace());
//        Iterator<String> prefixesIterator = prefix.iterator();
//        String prefix = prefixesIterator.next();
//        if (prefixesIterator.hasNext()){
//            String nextPrefix = prefixesIterator.next();
//            if (prefix.equals("") & !nextPrefix.equals("")){
//                prefix = nextPrefix;
//            }
//        }

        return getPrefixedLabel(resource, model);
    }
    public static boolean getSAVEIndividualFromConceptExists(Model model, String conceptName, String individualName){
        // the concept should be already prefixed
        SelectBuilder sb = null;
        sb = addPrefixesToSelectBuilder(model)
                .addVar("*")
//                .addWhere(individualName, "rdf:type", "owl:NamedIndividual")
                .addWhere("?value", "rdf:type", conceptName);
//                    .addFilter("?type != owl:NamedIndividual");
        Query query = sb.build();

        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        return  rs.hasNext();
    }

    public static String addPrefixesToSparqlString(String originalQuery, Model model){
        SelectBuilder sb = addPrefixesToSelectBuilder(model);
        String prefixes = sb.buildString().split("SELECT")[0];
        return prefixes + originalQuery;
    }

    public static SelectBuilder addPrefixesToSelectBuilder(Model model){
        return addPrefixesToSelectBuilder(null, model);
    }

    public static SelectBuilder addPrefixesToSelectBuilder(SelectBuilder originalQuery, Model model){
        SelectBuilder sb = (originalQuery != null) ? originalQuery : new SelectBuilder();
        sb.addPrefix("save", model.getNsPrefixURI("save"))
            .addPrefix("save-ex", model.getNsPrefixURI("save-ex"))
            .addPrefix("rdf", model.getNsPrefixURI("rdf"))
            .addPrefix("rdfs", model.getNsPrefixURI("rdfs"))
            .addPrefix("dpv", model.getNsPrefixURI("dpv"))
            .addPrefix("dpv-gdpr", model.getNsPrefixURI("dpv-gdpr"))
            .addPrefix("orcp", model.getNsPrefixURI("orcp"))
            .addPrefix("owl", model.getNsPrefixURI("owl"));

        return sb;
    }

    public static Map<String, Boolean> getClassHierarchy(Model model, String topConcept){
        Map<String, Boolean> hierarchyFlat = new HashMap<>();
        SelectBuilder sb = null;
        try {
            sb = addPrefixesToSelectBuilder(model)
                    .addVar("*")
                    .addWhere("?value", "rdfs:subClassOf*", topConcept)
                    .addBind("NOT EXISTS { ?child rdfs:subClassOf ?value }", "leaf");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Query query = sb.build();
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        while (rs.hasNext()) {
            final QuerySolution qs = rs.next();
            String value = getPrefixedLabel(qs.get("value").toString(), model);
            boolean leaf = qs.get("leaf").asLiteral().getBoolean();
            hierarchyFlat.put(value, leaf);
        }
        return hierarchyFlat;

    }

    public static Map<String, String> getIndividuals(Model model, String topConcept){
        Map<String, String> individuals = new HashMap<>();
        SelectBuilder sb = addPrefixesToSelectBuilder(model)
                .addVar("*")
                .addWhere("?value", "rdf:type", "?class")
                .addWhere("?value", "rdf:type/rdfs:subClassOf*", topConcept);
//                    .addBind("NOT EXISTS { ?child rdfs:subClassOf ?value }", "leaf");
        Query query = sb.build();
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        while (rs.hasNext()) {
            final QuerySolution qs = rs.next();
            String value = getPrefixedLabel(qs.get("value").toString(), model);
            String valueClass = getPrefixedLabel(qs.get("class").toString(), model);
            individuals.put(value, valueClass);
        }
        return individuals;

    }

    public static Map<String,String> getSavedRequests(Model model, String requestBaseType) {
        Map<String, String> requestNames = new HashMap<>();
        SelectBuilder sb = addPrefixesToSelectBuilder(model)
                .addVar("*")
                .addWhere("?request", "rdf:type", "?type")
                .addWhere("?type", "rdfs:subClassOf", requestBaseType);
        Query query = sb.build();
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        while (rs.hasNext()) {
            final QuerySolution qs = rs.next();
            String value = getPrefixedLabel(qs.get("request").toString(), model);
            String type = getPrefixedLabel(qs.get("type").toString(), model);
            requestNames.put(value, type);
        }
        return requestNames;
    }


    public static List<SAVERule> getRequestsByName(List<String> requestNames, Model model) {
        List<SAVERule> requests = new ArrayList<>();
        for(String requestName: requestNames) {
            SelectBuilder sb = null;
            try {
                sb = addPrefixesToSelectBuilder(model)
                        .addVar("*")
                        .addWhere(requestName, "?prop", "?value")
                        .addWhere("?value", "rdf:type", "?type")
                        .addFilter("?type != owl:NamedIndividual");
                Query query = sb.build();
                QueryExecution exec = QueryExecutionFactory.create(query, model);
                ResultSet rs = exec.execSelect();
                if(rs.hasNext()) {
                    SAVERule request = new SAVERule(requestName, "");
                    while (rs.hasNext()) {
                        final QuerySolution qs = rs.next();
                        String prop = getPrefixedLabel(qs.get("prop").toString(), model);
                        String value = getPrefixedLabel(qs.get("value").toString(), model);
                        String type = getPrefixedLabel(qs.get("type").toString(), model);
                        if(prop.equals("rdf:type") && value.contains("save")){
                            request.setType(value);
                        } else {
                            request.addProperty(prop, value, type);
                        }
                    }
                    requests.add(request);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return requests;
    }

    public static List<String> getPolicyAndRuleNamesForPolicyFile(Model model, String policyFileName) {
        List<String> policAndRuleNames = new ArrayList<>();
        Model policyModel = JenaUtil.createDefaultModel();
        policyModel.read(ModelUtils.class.getResourceAsStream(RESOURCE_FOLDER + policyFileName), "urn:dummy",
                FileUtils.langTurtle);
//        model.add(policyModel);
        SelectBuilder sb = null;
        try {
            sb = addPrefixesToSelectBuilder(policyModel)
                    .addVar("*")
                    .addWhere("?policy", "rdf:type", "orcp:Set")
                    .addWhere("?policy", "?prop", "?rule")
                    .addFilter("?prop IN (save:permission, save:prohibition, save:obligation, save:dispensation)");
            Query query = sb.build();
            QueryExecution exec = QueryExecutionFactory.create(query, policyModel);
            ResultSet rs = exec.execSelect();
            if(rs.hasNext()) {
                while (rs.hasNext()) {
                    final QuerySolution qs = rs.next();
                    String policy = getPrefixedLabel(qs.get("policy").toString(), policyModel);
                    if(policAndRuleNames.isEmpty()){
                        //first, add policy name
                        policAndRuleNames.add(policy);
                    }
                    String rule = getPrefixedLabel(qs.get("rule").toString(), policyModel);
                    policAndRuleNames.add(rule);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return policAndRuleNames;
    }


    public static List<String> getFinalResult(Model model) {
        List<String> answerTuple = new ArrayList<>();
        SelectBuilder sb = null;
        sb = addPrefixesToSelectBuilder(model)
                .addVar("*")
                .addWhere("?request", "save:result", "?obj")
                .addWhere("?obj", "save:answerPermitted", "?ansPermitted")
                .addWhere("?obj", "save:answerProhibited", "?ansProhibited");
        Query query = sb.build();
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        if(rs.hasNext()) {
            while (rs.hasNext()) {
                final QuerySolution qs = rs.next();
                String ansPermitted = qs.get("ansPermitted").toString();
                String ansProhibited = qs.get("ansProhibited").toString();
                answerTuple.add(ansPermitted);
                answerTuple.add(ansProhibited);
            }
        }
        return answerTuple;
    }

    public static List<String> getFinalResultRegular(Model model, String parentRequestName) {
        List<String> answerTuple = new ArrayList<>();
        SelectBuilder sb = null;
        sb = addPrefixesToSelectBuilder(model)
                .addVar("*")
                .addWhere(parentRequestName, "save:answerPermitted", "?ansPermitted")
                .addWhere(parentRequestName, "save:answerProhibited", "?ansProhibited");
        Query query = sb.build();
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        if(rs.hasNext()) {
            while (rs.hasNext()) {
                final QuerySolution qs = rs.next();
                String ansPermitted = qs.get("ansPermitted").toString();
                String ansProhibited = qs.get("ansProhibited").toString();
                answerTuple.add(ansPermitted);
                answerTuple.add(ansProhibited);
            }
        }
        return answerTuple;
    }

    public static Map<String, SAVERule> getResultIntersection(Model model, String property) {
        Map<String, SAVERule> conformsToRules = new HashMap<>();
        SelectBuilder sb = null;
        sb = addPrefixesToSelectBuilder(model)
                .addVar("*")
                .addWhere("?request", property, "?obj")
                .addWhere("?obj", "save:rule", "?rule")
                .addWhere("?obj", "?prop", "?value")
                .addWhere("?value", "rdf:type", "?type");
        Query query = sb.build();
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        if(rs.hasNext()) {
            while (rs.hasNext()) {
                final QuerySolution qs = rs.next();
                String rule = getPrefixedLabel(qs.get("rule").toString(), model);
                String prop = getPrefixedLabel(qs.get("prop").toString(), model);
                String value = getPrefixedLabel(qs.get("value").toString(), model);
                String type = getPrefixedLabel(qs.get("type").toString(), model);
                if(!conformsToRules.containsKey(rule)){
                    conformsToRules.put(rule, new SAVERule(rule, null));
                }
                SAVERule currentRule = conformsToRules.get(rule);
                currentRule.addProperty(prop, value, type);
            }
        }
        return conformsToRules;
    }

    public static Map<String, SAVERule> getSubrequestsResults(Model model, String property, String parentRequestName) {
        Map<String, SAVERule> conformsToRules = new HashMap<>();
        SelectBuilder sb = null;
        sb = addPrefixesToSelectBuilder(model)
                .addVar("*")
                .addWhere("?request", "save:parent", parentRequestName)
                .addWhere("?request", "save:answer", property.equals("save:conformsTo") ? "permitted" : "prohibited")
                .addWhere("?request", property, "?rule")
                .addWhere("?request", "?prop", "?value")
                .addWhere("?value", "rdf:type", "?type");
        Query query = sb.build();
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet rs = exec.execSelect();
        if(rs.hasNext()) {
            while (rs.hasNext()) {
                final QuerySolution qs = rs.next();
                String rule = getPrefixedLabel(qs.get("rule").toString(), model);
                String prop = getPrefixedLabel(qs.get("prop").toString(), model);
                String value = getPrefixedLabel(qs.get("value").toString(), model);
                String type = getPrefixedLabel(qs.get("type").toString(), model);
                if(!conformsToRules.containsKey(rule)){
                    conformsToRules.put(rule, new SAVERule(rule, null));
                }
                SAVERule currentRule = conformsToRules.get(rule);
                currentRule.addProperty(prop, value, type);
            }
        }
        return conformsToRules;
    }

    public static Map<String, SAVERule> getSubrequestsPermitted(Model model, String parentRequestName) {
        return getSubrequestsResults(model, "save:conformsTo", parentRequestName);
    }

    public static Map<String, SAVERule> getSubrequestsProhibited(Model model, String parentRequestName) {
        return getSubrequestsResults(model, "save:prohibitedBy", parentRequestName);
    }

    public static Map<String, SAVERule> getProhibitedBy(Model model) {
        return getResultIntersection(model, "save:prohibitedBy");
    }

    public static Map<String, SAVERule> getConformsTo(Model model) {
        return getResultIntersection(model, "save:conformsTo");
    }
}
