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
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;
import org.semanticweb.owlapi.util.OWLObjectWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;

public class LabelTestNew {
	
	public static void correctLabels(OutputStream os, PrintWriter writer,
			OWLOntologyManager manager, OWLOntology ont, OWLDataFactory df) throws Exception
	{
		/*
		File owlFile = new File("cinergi.owl");
		OutputStream os = new FileOutputStream("cinergi_new.owl");
		PrintWriter writer = new PrintWriter("classes.txt", "UTF-8");
	
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl"));
	    OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
	    OWLDataFactory df = manager.getOWLDataFactory();
	    writer.println(manager.getOntologies() + "\n");
	    */
		
	    Set<OWLOntology> ontologies = manager.getOntologies();
	    
	    OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
	    HashSet<OWLClass> classes = new HashSet<OWLClass>();
	    HashSet<OWLAnnotation> labels = new HashSet<OWLAnnotation>();
        
        OWLOntologyWalkerVisitor<Object> visitor = 
        		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c)//(OWLObjectSomeValuesFrom desc)
	        	{
	        		
	        		if (!classes.contains(c))//hasCinergiFacet(c))	        
	        			classes.add(c);//addFacet(c); // creates cinergiFacet = false annotation
	        		return null;	
	        	}
	        	@Override
	        	public Object visit(OWLAnnotation a) // add labels to list
	        	{
	        		//writer.println(a.getClass());
	        		
	        	   /*if (labels.contains(a) && a.getProperty().isLabel())
	        		{
	        			labels.add(a);
	        		}*/
	        		return null;
	        	}
        	};
        walker.walkStructure(visitor);
        
        for (OWLClass c : classes)
        {
        	boolean has_label = false;
        	
    		for (OWLOntology ontol : manager.getOntologies())
    		{
    			if (c.getAnnotations(ontol,df.getRDFSLabel()).isEmpty() == false)
    			{
    				has_label = true;
    			}
			}
    		if (!has_label)
    		{
    			String name = c.getIRI().getShortForm();
    			writer.printf("%-73s %-73s\n" , "Class without label: ".concat(name), "Label added: ".concat(shortFormToLabel(name)));
    			OWLAnnotation label = df.getOWLAnnotation(df.getRDFSLabel(),df.getOWLLiteral(shortFormToLabel(name), "en"));
				OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(c.getIRI(), label);
				manager.applyChange(new AddAxiom(ont, ax));
    		}
        }
        
        //saveAndUpdate(os, manager, ont);
        /*
		manager.saveOntology(ont, os);
		os.close();
		writer.close();
		*/
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
//				if ((name.charAt(i-1) != '_' && name.charAt(i-1) != '-')) // if previous character was not _ nor -
//				{
//					label += " " + c; // HeatFlow -> Heat Flow
//				} // Heat_Flow
			}
			else if (Character.isDigit(c))
			{
				if (Character.isDigit(name.charAt(i-1)) || name.charAt(i-1) == '_' || name.charAt(i-1) == '-') // if previous character was a num
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
	
	private static void saveAndUpdate(OutputStream os, OWLOntologyManager manager, OWLOntology ont) throws Exception
	{
		manager.saveOntology(ont, os);
		// df = manager.getOWLDataFactory();
	}
}
