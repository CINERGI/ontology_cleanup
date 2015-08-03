//temporary class to fix incorrect locations of labels and annotations

package cinergi;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;

public class CinergiClassFixer {
	private OutputStream os;
	private PrintWriter writer;
	private OWLOntologyManager manager;
	private	OWLOntologyManager new_manager;
	private OWLOntology cinergiOntology;
	private OWLOntology extensionsOntology;
	private OWLOntology new_ont;
	private OWLDataFactory df;
	
	public CinergiClassFixer(OutputStream os, PrintWriter writer, OWLOntologyManager manager, OWLOntologyManager new_manager,
			OWLOntology cinergiOntology, OWLOntology extensionsOntology,OWLOntology new_ont, OWLDataFactory df)
	{
		this.os = os;
		this.writer = writer;
		this.manager = manager;
		this.new_manager = new_manager;
		this.cinergiOntology = cinergiOntology;
		this.extensionsOntology = extensionsOntology;
		this.new_ont = new_ont;
		this.df = df;
	}
	
	public void fixClasses() 
	{
		writer.println("classes \n\n");
		
		Set<OWLOntology> ontologies = cinergiOntology.getImports();
		ontologies.add(cinergiOntology);
		
		for (OWLClass c : extensionsOntology.getClassesInSignature())
		{
			
			boolean contains = false;
			for (OWLOntology o : ontologies)
			{				
				if (cinergiOntology.containsClassInSignature(c.getIRI()) || o.containsClassInSignature(c.getIRI()))
					contains = true;
			}
			if (!contains) // not in cinergi or its imports
			{
				writer.println(getLabel(c));
				
				addClassToOntology(c); //  add to new extensions //// SHOULD ADD TO CINERGI.OWL LATER
				Set<OWLAnnotationAssertionAxiom> axioms = c.getAnnotationAssertionAxioms(extensionsOntology);
				for (OWLAnnotationAssertionAxiom a : axioms)
				{
					if (a.getProperty().equals(df.getRDFSLabel())) // add labels, not any other annotations yet
					{
						new_manager.applyChange(new AddAxiom(new_ont, a));
					}
				}
			}
		}
		
		// declare facet, parent, and preferredlabel annotations here
		
		
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies()); // check every class to get all annotations, otherwise would miss Thing etc.
        OWLOntologyWalkerVisitor<Object> visitor =
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c) 
	        	{
	        		// for each annotation in class c
	        		// if its a cinergiFacet or cinergiParent,
	        		// add to new_ont
	        		for (OWLAnnotation a : c.getAnnotations(extensionsOntology)) // add all cinergiFacets
	        		{
	        			if (a.getProperty().equals(df.getOWLAnnotationProperty
	        					(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiFacet"))))
	        			{
	        				OWLAnnotation facet = df.getOWLAnnotation(			
    								df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiFacet")), // cinergiExtensions base
    									((OWLLiteral)(a.getValue())).equals(df.getOWLLiteral(true)) ? df.getOWLLiteral(true) : df.getOWLLiteral(false));
	        				
	        				OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), facet);
    						manager.applyChange(new AddAxiom(new_ont, axiom));
	        			}
	        			
	        			else if (a.getProperty().equals(df.getOWLAnnotationProperty
	        					(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiParent"))))
	        			{
	        				OWLAnnotation cinergi_parent = df.getOWLAnnotation(			
    								df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiParent")), // cinergiExtensions base
    									a.getValue());	 
	        				
	        				OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), cinergi_parent);
    						manager.applyChange(new AddAxiom(new_ont, axiom));
	        			}
	        			
	        			else if (a.getProperty().equals(df.getOWLAnnotationProperty
	        					(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiPreferredLabel"))))
	        			{
	        				OWLAnnotation cinergi_preferredLabel = df.getOWLAnnotation(			
    								df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiPreferredLabel")), // cinergiExtensions base
    									a.getValue());	 
	        				
	        				OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), cinergi_preferredLabel);
    						manager.applyChange(new AddAxiom(new_ont, axiom));
	        			}
	        		}
	        		return null;
	        	}
        	};
        	walker.walkStructure(visitor);
	}	
	
	public void fixLabels() // to do  /
	{
		// go through all classes in cinergi.owl and all of its imports. If a class does not have a label add
		writer.println("\n\nlabels to be added to cinergi.owl \n\n");
		
		Set<OWLOntology> ontologies = cinergiOntology.getImports();
		ontologies.add(cinergiOntology);
		
		for (OWLClass c : extensionsOntology.getClassesInSignature())
		{
			boolean hasLabel = false;
			for (OWLOntology o : ontologies)
			{				
				// if there is not a label
				if (getLabelAnnotation(c, o) != null)
				{
					hasLabel = true;
				}
			}
			if (!hasLabel) // not in cinergi or its imports -> probably in cinergiExtensions
			{
				OWLAnnotation label = getLabelAnnotation(c, extensionsOntology);
				if (label == null)
				{
					writer.println("could not find label for " + c.getIRI() + "in cinergiExtensions");
				}
				else
				{
					writer.println(((OWLLiteral)(label.getValue())).getLiteral());
				}
				// need to add these labels to cinergi.owl
				 
				OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), getLabelAnnotation(c, extensionsOntology));
				new_manager.applyChange(new AddAxiom(new_ont, axiom)); // add label to extensions
				
			}
		}
	}
	
	public void printSubClassRelationships()
	{
		OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(extensionsOntology));
        OWLOntologyWalkerVisitor<Object> visitor =
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c) 
	        	{
	        		return null;
	        	}
        	};
    	walker.walkStructure(visitor);
	}
	
	private void addClassToOntology(OWLClass c) // to do
	{
		Set<OWLClassExpression> superClassExpressions = c.getSuperClasses(manager.getOntologies());
		for (OWLClassExpression oce : superClassExpressions)
		{
			OWLAxiom axiom = df.getOWLSubClassOfAxiom(c, oce);
			AddAxiom addAxiom = new AddAxiom(new_ont, axiom);
			new_manager.applyChange(new AddAxiom(new_ont,df.getOWLDeclarationAxiom(c)));
			new_manager.applyChange(addAxiom);
		}
	}
	
	private OWLAnnotation getLabelAnnotation(OWLClass c, OWLOntology o) // to do
	{
		OWLAnnotation label = null;
		for (OWLAnnotation a : c.getAnnotations(o, df.getRDFSLabel()))
		{
			if (((OWLLiteral)a.getValue()).hasLang("en"))
			{
				label = a;
				break; // if there 
			}
			label = a;
		}
		return label;		
	}
	
	private String getLabel(OWLClass c)
	{	
		String label = "";
		for (OWLOntology o : manager.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(o, df.getRDFSLabel()))
			{
				if (((OWLLiteral)a.getValue()).hasLang("en"))
				{
					label = (((OWLLiteral)a.getValue()).getLiteral());
					break;
				}
				label = (((OWLLiteral)a.getValue()).getLiteral());	
			}
		}
		if (label == "")
		{
			label = c.getIRI().getShortForm();
			return "couldn't find a label, using " + label + " instead";
		}
		return label;
	}
}
