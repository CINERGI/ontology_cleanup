import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;


public class EquivalencyTest {
	
	public static void equivalencies() throws Exception {

		File owlFile = new File("pizza.owl");
		OutputStream os = new FileOutputStream("pizza_new.owl");
		//PrintWriter writer = new PrintWriter("classes.txt", "UTF-8");
		
		// StreamDocumentTarget stream = new StreamDocumentTarget(os);
		System.out.println("File loaded \n\n");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
	    OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
	    OWLDataFactory df = manager.getOWLDataFactory();
		

        OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());

        ArrayList<OWLAnnotation> anots = new ArrayList<OWLAnnotation>();
        
//        for (OWLClass oc : ont.getClassesInSignature())
//        {
//        	
//        	anots.addAll(oc.getAnnotations(ont, df.getRDFSLabel()));
//        	
//        }
        
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(walker)		
    		{

	        @Override
	        public Object visit(OWLClass desc) {
	        	for(OWLAnnotation a : desc.getAnnotations(ont, df.get))
	        	{
	        		if (anots.contains(a))
	        		{
	        			if (anots.get(anots.indexOf(a)).equals(a) == false)
	        			{
	        				
	        			}
	        		}
	        	}
	            // Print out the restriction
	            //System.out.println(desc);
	            // Print out the axiom where the restriction is used
	            //System.out.println("         " + getCurrentAxiom().getAxiomType());
	            //System.out.println();
	            // We don't need to return anything here.
	       // 	writer.println(shortFormToLabel(desc.getIRI().getShortForm()) + " was visited");
	            return null;
            }
        };
        // Now ask the walker to walk over the ontology structure using our
        // visitor instance.
        walker.walkStructure(visitor);
        
        os.close();
    }
	
	public static String shortFormToLabel(String name)
	{ // ThinAndChrispyPizza1 -> T
		String label = "";
		for (int i = 0; i < name.length(); i++)
		{
			char c = name.charAt(i);
			if (i == 0)
				label += c;
			else if (Character.isUpperCase(c))
				label += " " + c;
			else if (Character.isDigit(c))
				label += " " + c;
			else if (c == '_' || c == '-') { 
			//	System.out.println("_ or - encountered");
				label += " "; }
			else
				label += c;
		}
		return label;
	}

}
