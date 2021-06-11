import org.apache.jena.atlas.lib.SetUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ComputeArea {


    /**
     * Loads an example SHACL-AF (rules) file and execute it against the data.
     */
    public static void main(String[] args) throws Exception {

        // Load the main data model that contains rule(s)
        Model dataModel = JenaUtil.createMemoryModel();
        dataModel.read(ComputeArea.class.getResourceAsStream("rectangle.test.ttl"), "urn:dummy",
                FileUtils.langTurtle);


//        testRules(dataModel);


        // Perform the validation of everything, using the data model
        // also as the shapes model - you may have them separated
        Resource report = ValidationUtil.validateModel(dataModel, dataModel, true);

        // Print violations
        System.out.println(ModelPrinter.get().print(report.getModel()));

        // print model before inference
        System.out.println(ModelPrinter.get().print(dataModel));

        testRules(dataModel);
    }

    private static void testRules(Model dataModel) {
        // Perform the rule calculation, using the data model
        // also as the rule model - you may have them separated
        Model result = RuleUtil.executeRules(dataModel, dataModel, null, null);


        // you may want to add the original data, to make sense of the rule results
//        result.add(dataModel);

        final List<Statement> triples = result.listStatements().toList();
        for (Statement triple:
             triples) {
            dataModel.add(triple);
        }


        System.out.println(ModelPrinter.get().print(dataModel));


        // Print rule calculation results
//        System.out.println(result);
//        System.out.println(ModelPrinter.get().print(result));
    }
}