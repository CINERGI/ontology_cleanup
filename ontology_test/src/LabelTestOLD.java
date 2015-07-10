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

public class LabelTestOLD {
	
	public static void test() throws Exception
	{
		File owlFile = new File("cinergi.owl");
		OutputStream os = new FileOutputStream("cinergi_new.owl");
		PrintWriter writer = new PrintWriter("classes.txt", "UTF-8");
		
		// StreamDocumentTarget stream = new StreamDocumentTarget(os);
		System.out.println("File loaded \n\n");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
	    OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
	    OWLDataFactory df = manager.getOWLDataFactory();
	    writer.println(manager.getOntologies());
	    writer.println();
	    Set<OWLOntology> ontologies = manager.getOntologies();
	    for (OWLOntology o : ontologies)
	    {
	    	
			for (OWLClass cls : o.getClassesInSignature()) 
			{
				String name = cls.getIRI().getShortForm();
					System.out.println(cls);
				//if (cls.getAnnotations(pizza).isEmpty()) // if no annotation
				if (cls.getAnnotations(o,df.getRDFSLabel()).isEmpty()) // no labels
				{
					// create annotation based on short form, then add it
					OWLAnnotation label = df.getOWLAnnotation(df.getRDFSLabel(),df.getOWLLiteral(shortFormToLabel(name), "en"));
							// Specify that the pizza class has an annotation
							OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), label);
							// OWLAxiom ax1 = df.getOWLEquivalentClassesAxiom(cls, cls);
							// Add the axiom to the ontology
							manager.applyChange(new AddAxiom(ont, ax));
	
			//		System.out.println(cls.getIRI().getShortForm()); // print the name of the class
						 
				}
				else // there are labels
				{	
					for (OWLAnnotation a : cls.getAnnotations(o,df.getRDFSLabel()))
					{
						//System.out.println(annotToString(a));
						if (annotToString(a).equals(shortFormToLabel(name)) == false) // not the same label as intended
						{
							//System.out.println(annotToString(a));
							
							// remove the axiom from the ontology
							RemoveAxiom removeAxiom = new RemoveAxiom(o, df.getOWLAnnotationAssertionAxiom(cls.getIRI(),a));
							manager.applyChange(removeAxiom);
	
							
							OWLAnnotation label = df.getOWLAnnotation(df.getRDFSLabel(),df.getOWLLiteral(shortFormToLabel(name), "en"));
							OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), label);
							manager.applyChange(new AddAxiom(o, ax));
						}
					}
			 	}
			}
				
// 			System.out.println(cls.getIRI().getShortForm()); // this is the fragment in IRI or name of class
			// Get the annotations on the class that use the label property
//			for (OWLAnnotation annotation : cls.getAnnotations(pizza, df.getRDFSLabel())) 
//			{ 
//				annotation.getClass();
//				//System.out.println(annotation.getSignature());
//				//System.out.println(cls + " labelled " + (OWLLiteral)(annotation.getValue()));
//			}
//				if (annotation.getValue() instanceof OWLLiteral) 
//				{
//					OWLLiteral val = (OWLLiteral) annotation.getValue();
//					// look for portuguese labels
//					val.
//					if (val.hasLang("pt"))
//					System.out.println(cls +
//					" labelled " + val.getLiteral());
//				}
//			}
		}
//				
			
		//File output = File.createTempFile("saved_pizza", "owl");
		//IRI documentIRI2 = IRI.create(output);
		// save in OWL/XML format
		//manager.saveOntology(pizza, new OWLXMLOntologyFormat(), documentIRI2);
		
		//equivalencies(manager, ont, df, writer);
		
		manager.saveOntology(ont, os);
		os.close();
		writer.close();
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
	
	public static String annotToString(OWLAnnotation a)
	{
		String str = a.toString().substring(1 + a.toString().indexOf('"'),a.toString().lastIndexOf('"'));
		return str;
	}

}

/*
if (!(has_cinergiFacet))
{
	if (!c.getIRI().getShortForm().toString().equals("Thing")) // not facet nor is thing
	{
		OWLAnnotation cinergiAnnot = df.getOWLAnnotation(			
				df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiFacet")),
					df.getOWLLiteral("False",
							df.getOWLDatatype(IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral"))) );
		OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), cinergiAnnot);
		manager.applyChange(new AddAxiom(ont, axiom));
	}
}
else // has cinergi_facet already
{
	Set<OWLClass> superClasses = new HashSet<OWLClass>();
	
	for (OWLClassExpression a : c.getSuperClasses(manager.getOntologies()))
	{
		for (OWLClass b : a.getClassesInSignature())
		{
			if (!superClasses.contains(b))
			{
				superClasses.add(b); //adds all super classes to set
			}
		}      			
	}
	
	for (OWLClassExpression a : c.getEquivalentClasses(manager.getOntologies()))
	{
		for (OWLClass b : a.getClassesInSignature())
		{
			if (!superClasses.contains(b))
			{
				superClasses.add(b); //adds shared super classes
			}
		}
	}
	
	for (OWLClass cl : superClasses)
	{
		// give them a cinergiParent 
	}
	Set<OWLClassExpression> cinergiParents = c.getSuperClasses(manager.getOntologies());
	//System.out.println(cinergiParents);
	// add cinergy parent 
	
}
*/
/*        		if (classes.contains(c))
{
	classes.add(c);
}
*/        	
