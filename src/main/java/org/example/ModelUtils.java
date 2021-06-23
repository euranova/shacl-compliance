package org.example;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.topbraid.jenax.util.JenaUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper class to deal with loading/saving models
 */
public class ModelUtils {

    public static final String RESOURCE_FOLDER = "/";
    public static final String SAVE_ONTOLOGY_NAME = "save.ontology.ttl";
    public static final String SAVE_SHAPES_NAME = "save.shapes.ttl";
    public static final String DPV_NAME = "dpv.ttl";
    public static final String ORCP_NAME = "odrl_regulatory_profile_prefixed.ttl";

    /**
     * Full model contains:
     *  - SAVE ontology
     *  - SAVE shapes (base request shape)
     *  - DPV ontology
     *  - ORCP ontology
     * The policies and policy shapes come separately - need to be added separately with loadModelFromResourceFile method
     * @return full model representing SAVE
     */
    public static Model loadFullSAVEModel(){
        Model dataModel = loadSAVEModel();

        Model shapesModel = loadModelFromResourceFile(SAVE_SHAPES_NAME);

        Model dataModelDPV = loadModelFromResourceFile(DPV_NAME);

        Model dataModelORCP = loadModelFromResourceFile(ORCP_NAME);

        Model unionModel = JenaUtil.createMemoryModel();

        unionModel.add(dataModelORCP).add(dataModelDPV).add(dataModel).add(shapesModel);

//        writeModelToResourceFile(unionModel, "save.union.ttl");
        return unionModel;
    }

    /**
     * Loads just the SAVE model (no imported files)
     * @return SAVE model graph
     */
    public static Model loadSAVEModel(){
        return loadModelFromResourceFile(SAVE_ONTOLOGY_NAME);
    }

    /**
     * Loads any model from the "resources" folder
     * @param filename the name of the file to load from
     * @return model graph
     */
    public static Model loadModelFromResourceFile(String filename){
        Model model = JenaUtil.createMemoryModel();
        model.read(ModelUtils.class.getResourceAsStream(RESOURCE_FOLDER + filename), "urn:dummy",
                FileUtils.langTurtle);
        return model;
    }

    /**
     * Save the model into SAVE main file
     * @param model graph
     */
    public static void writeSAVEOntologyModel(Model model) {
        writeModelToResourceFile(model, RESOURCE_FOLDER + SAVE_ONTOLOGY_NAME);
    }


    /**
     * Write any model into "resources" folder
     * @param model model graph
     * @param fileName the desired name
     */
    public static void writeModelToResourceFile(Model model, String fileName) {
        try {
            FileWriter out = new FileWriter( fileName);
            model.write(out, "Turtle");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Replace the prefixed name with full URI
     * @param prefixedName the name
     * @param infModel the model containing the prefixes mapping
     * @return the full URI
     */
    public static String replacePrefixWithURI(String prefixedName, Model infModel){
        String prefix = prefixedName.split(":")[0];
        return prefixedName.replace(prefix + ":", infModel.getNsPrefixURI(prefix));
    }

    public static List<String> getSHACLPolicyNames(){
        String folder = "./resources";
        Set<String> policies = new HashSet<>();
        List<String> files;

//        InputStream dirIs = ModelUtils.class.getResourceAsStream(RESOURCE_FOLDER);//.toURI());
//        InputStreamReader isr = new InputStreamReader(dirIs, StandardCharsets.UTF_8);
//        BufferedReader br = new BufferedReader(isr);
//        System.out.println("MAIN RESOURCES");
//        for(String line: br.lines().collect(Collectors.toList())){
//            System.out.println("LINE:" + line);
//        }
//        files = br.lines().filter( name -> name.endsWith(".shapes.core.ttl") || name.endsWith(".shapes.sparql.ttl")).collect(Collectors.toList());
//
//
//        for (String filename : files) {
//            System.out.println("SHACL file:" + filename);
//            policies.add(filename);
//        }

        ClassLoader cl = ModelUtils.class.getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
//        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;
        try {
            resources = resolver.getResources("classpath:*");
            for (Resource resource : resources) {
                System.out.println("IS FILE " + resource.isFile());
                System.out.println("FILE " + resource.getFilename());
                if(resource.getFilename() != null) {
                    if (resource.getFilename().contains(".shapes.core.ttl") || resource.getFilename().contains(".shapes.sparql.ttl")) {
                        System.out.println("SHACL file " + resource.getFilename());
                        policies.add(resource.getFilename());
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        return new ArrayList<>(policies);
    }

}
