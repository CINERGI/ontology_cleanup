import java.io.File;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;


public class Tester {
	public Tester()
	{
	}
	
	public static void test() throws Exception
	{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(new File("cinergi_7_6_2015.owl"));
        System.out.print("file loaded");
        OWLDataFactory df = manager.getOWLDataFactory();
        PrintWriter writer = new PrintWriter("description.txt", "UTF-8");
      //  writer.println(df.getRDFSLabel());
        // manager.saveOntology(pizza, new SystemOutDocumentTarget());
//        for (OWLOntology o : manager.getOntologies())
//        {
//        	writer.println(o.getOntologyID().getOntologyIRI().getShortForm());
//        }
        OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        
        
        OWLOntologyWalkerVisitor<Object> visitor = 
        		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c) // visit all classes, give each of them the cinergiFacet property and set to false
	        	{
	        		if (hasTrueCinergiFacet(c, manager))
	        		{
		        		System.out.println(getCinergiParentAnnotation(c, manager));
		        		System.out.println(getCinergiParentAnnotation(c, manager).getValue());
	        		}
	        		return null;
	        	}
        	};
       walker.walkStructure(visitor);
       
       writer.close();
       
      
	}	
	
	public static OWLAnnotation getCinergiParentAnnotation(OWLClass c, OWLOntologyManager m)
	{
		for (OWLOntology o : m.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(o))
			{
				if (a.getProperty().getIRI().toString().equals("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiParent"))
				{
					return a;
				}
			}
		}
		return null;
	}
	
	public static boolean hasTrueCinergiFacet(OWLClass c, OWLOntologyManager m)
	{
		boolean has = false;
		for (OWLOntology o : m.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(o))
			{
				if (isCinergiFacet(a))
					if (cinergiFacetTrue(a))
						has = true;
			}
		}
		return has;
	}
	
	public static boolean isCinergiFacet(OWLAnnotation a)
	{
		return (a.getProperty().getIRI().getShortForm().toString().equals("cinergiFacet"));
	}
	
	public static boolean cinergiFacetTrue(OWLAnnotation a)
	{
		return a.getValue().toString().equals("\"True\"");
	}
}
