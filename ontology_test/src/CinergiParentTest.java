import java.awt.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
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
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;
import org.semanticweb.owlapi.util.OWLObjectWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;

public class CinergiParentTest {
	public static void test() throws Exception
	{
		OutputStream os = new FileOutputStream("cinergi_new1.owl");
		PrintWriter writer = new PrintWriter("classes.txt", "UTF-8");
	
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(new File("cinergi_new.owl"));
	    OWLDataFactory df = manager.getOWLDataFactory(); 	  
	    Set<OWLOntology> ontologies = manager.getOntologies();
    	Set<OWLClass> cinergiFacetClasses = new HashSet<OWLClass>();
    	Set<OWLClass> cinergiNonFacetClasses = new HashSet<OWLClass>();
    	
	    OWLOntologyWalker walker = new OWLOntologyWalker(ontologies);
        
        OWLOntologyWalkerVisitor<Object> visitor =         
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c)
	        	{
	        		if (c.getIRI().getShortForm().equals("Thing"))
	        			return null;
	        		if (hasTrueCinergiFacet(c, manager))
	        		{
	        			if (!cinergiFacetClasses.contains(c))	        				
	        				cinergiFacetClasses.add(c);
	        		}
	        		else
	        		{
	        			if (!cinergiNonFacetClasses.contains(c))
	        				cinergiNonFacetClasses.add(c);
	        		}
	        		return null;
	        	}
	        	@Override
	        	public Object visit(OWLAnnotation a) // add labels to list
	        	{
	        		return null;
	        	}
        	};      	
        walker.walkStructure(visitor);
        
        for (OWLClass c : cinergiFacetClasses) // c parent
        {
        	for (OWLClassExpression exp : c.getSubClasses(ontologies))
        	{
        		for (OWLClass cl : exp.getClassesInSignature()) // for each subclass cl of facet classes c
    			{
        			if (cinergiFacetClasses.contains(cl)) // if cl is also a facet class
        			{
	    				// add cinergi parent annotation to subclass
	        			OWLAnnotation cinergiParentAnnot = df.getOWLAnnotation(			
	        					df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiParent")),
	    								c.getIRI());
	        			OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(cl.getIRI(), cinergiParentAnnot);
	    				manager.applyChange(new AddAxiom(ont, axiom));
        			}
    			}      	
        	}
        }
//        for (OWLClass c : cinergiNonFacetClasses) // c parent
//        {    	
//			// add cinergi parent annotation to subclass
//			OWLAnnotation cinergiParentAnnot = df.getOWLAnnotation(			
//					df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiParent")),
//							c.getIRI());
//			OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(cl.getIRI(), cinergiParentAnnot);
//			manager.applyChange(new AddAxiom(ont, axiom));	
//        }
	    
        saveAndUpdate(os, manager, ont);
//		manager.saveOntology(ont, os);
//		os.close();
//		writer.close();
	}
	
	public static String shortFormToLabel(String name)
	{ // ThinAndChrispyPizza1 -> T
		String label = "";
		if (name.equals("pH"))
			return name;
		for (int i = 0; i < name.length(); i++)
		{
			char c = name.charAt(i);
			if (i == 0)
				label += c;
			else if (Character.isUpperCase(c))
			{
				if (Character.isUpperCase(name.charAt(i-1))) // if previous character was upper case,
				{
					label += c;
				} 
				else if (name.charAt(i-1) == '_' || name.charAt(i-1) == '-')
				{
					label += c;
				}
				else
					label += " " + c;
			}
			else if (Character.isDigit(c))
			{
				if (Character.isDigit(name.charAt(i-1)) || name.charAt(i-1) == '_' || name.charAt(i-1) == '-')
				{
					label += c;
				}
				else
					label += " " + c;
			}
			else if (c == '_' || c == '-') { 
			//	System.out.println("_ or - encountered");
				label += " "; }
			else if (c == '(' || c == '{' || c == '[')
			{ // (DNS) i = 0 -> (
				boolean flag = false;
				for (int j = i; j < name.length(); j++)
				{
					if (")}]".contains(name.substring(j, j+1)))
					{ // j = 4
						flag = true;
						label += c;
						label += shortFormToLabel(name.substring(i+1, j)); // DNS
						i = j-1;						
					}
				}
				if (!flag)
				{
					label += c;
				}
			}
			else
				label += c;
		}
		return label;
	}
	
	public static String annotToString(OWLAnnotation a)
	{
		String str = a.toString().substring(1 + a.toString().indexOf('"'),a.toString().lastIndexOf('"'));
		return str;
	}
	
	public static boolean isCinergiFacet(OWLAnnotation a)
	{
		return (a.getProperty().getIRI().getShortForm().toString().equals("cinergiFacet"));
	}
	
	public static boolean cinergiFacetTrue(OWLAnnotation a)
	{
		return a.getValue().toString().equals("\"True\"");
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
	
	private static void saveAndUpdate(OutputStream os, OWLOntologyManager manager, OWLOntology ont) throws Exception
	{
		manager.saveOntology(ont, os);
		// df = manager.getOWLDataFactory();
	}
}
