package org.example;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.util.*;

import static org.example.ModelUtils.replacePrefixWithURI;
import static org.example.SPARQLUtils.getPrefixedLabel;


/**
 * Helper class to work with Jena
 */
public class JenaUtils {

    /**
     * Method needed for normalization.
     * @param startClass the SAVE concept to start with
     * @param model the model graph
     * @return the list of leaves originating from the startClass
     */
    public static List<String> findLeaves(String startClass, Model model){
        List<String> leaves = new ArrayList<>();
        StmtIterator stmts = model.listStatements( null, RDFS.subClassOf, model.getResource( replacePrefixWithURI(startClass, model)));
        if(!stmts.hasNext()){
            //start class is already prefixed
            leaves.add(startClass);
        }
        while ( stmts.hasNext() ) {
            rdfDFS( stmts.next().getSubject(), new HashSet<RDFNode>(), leaves, model);
        }
//        if(byValue){
//            List<String> leavesValues = new ArrayList<>();
//
//            //now that we have all the children, let's find all instances defined
//            for (String leafClass: leaves) {
//                StmtIterator valuesStmts = model.listStatements(null, RDF.type, model.getResource(ModelUtils.replacePrefixWithURI(leafClass, model)));
//                while (stmts.hasNext()){
//                    //start class is already prefixed
//                    leavesValues.add(getPrefixedLabel(stmts.next().getSubject().toString(), model));
//                }
//            }
//            return leavesValues;
//        }
        return leaves;

    }

    /**
     * Recursive method for finding leaves
     * @param node node to start with
     * @param visited set of visited nodes
     * @param leaves list of leaves to add to
     * @param model the model graph
     */
    private static void rdfDFS(RDFNode node, Set<RDFNode> visited, List<String> leaves, Model model) {
        if ( visited.contains( node )) {
            return;
        }
        else {
            visited.add( node );
            if ( node.isResource() ) {
                StmtIterator stmts = model.listStatements( null, RDFS.subClassOf, node);
                if(!stmts.hasNext()){
                    leaves.add(SPARQLUtils.getPrefixedLabel(node.toString(), model));
                }
                while ( stmts.hasNext() ) {
                    Statement stmt = stmts.next();
                    rdfDFS( stmt.getSubject(), visited, leaves, model );
                }
            }
        }
    }

    /**
     * Given SAVE concept, get a default individual for it
     * @param concept the SAVE concept
     * @param defaultPrefix default namespace prefix for the individual
     * @param model model graph
     * @return the individual resource
     */
    public static Resource getAnyIndividualFromConcept(String concept, String defaultPrefix, Model model) {
        //check in union model
        String individualName = concept.split(":")[1];
        individualName = defaultPrefix + ":" + individualName;
        Resource individual;
        //TODO replace with model.contains? should be faster
        if(model.contains(model.createResource(replacePrefixWithURI(individualName, model)),
                RDF.type, model.createResource(replacePrefixWithURI(concept, model)))){
            individual = model.getResource(replacePrefixWithURI(individualName, model));
        } else {
            Iterator<Statement> possibleIndividuals = model.listStatements(null,
                    RDF.type, model.createResource(replacePrefixWithURI(concept, model)));
            if (possibleIndividuals.hasNext()) {
                individual = possibleIndividuals.next().getSubject();
            }else {

                //TODO there is no individual, let us throw an eception for now
                throw new NoSuchElementException(String.format("Individual of class %s does not exist in SAVE", concept));
            }
        }
//        if (SPARQLUtils.getSAVEIndividualFromConceptExists(saveModel, concept, individualName)){
//            individual = saveModel.getResource(replacePrefixWithURI(individualName));
//        } else {
//            individual = saveModel.createResource(replacePrefixWithURI(individualName));
////            Statement stmt = saveModel.createStatement(individual,
////                    RDF.type,
////                    saveModel.createResource(replacePrefixWithURI("owl:NamedIndividual")));
////            saveModel.add(stmt);
////            unionModel.add(stmt);
//            Statement stmt = saveModel.createStatement(individual,
//                    RDF.type,
//                    saveModel.createResource(replacePrefixWithURI(concept)));
//            saveModel.add(stmt);
//            unionModel.add(stmt);
//        }

        return individual;
    }



}