package cinergi;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
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
	  /*OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(ont));	
        OWLOntologyWalkerVisitor<Object> visitor =
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c) 
	        	{
	        		return null;
	        	}
        	};
    	*/		
		for (OWLClass c : extensionsOntology.getClassesInSignature())
		{
			if (cinergiOntology.containsClassInSignature(c.getIRI()))
			{
				writer.println(c.getIRI());
			}
			
			else 
			{
				addClassToOntology(c);
				Set<OWLAnnotationAssertionAxiom> axioms = c.getAnnotationAssertionAxioms(extensionsOntology);
				for (OWLAnnotationAssertionAxiom a : axioms)
				{
					new_manager.applyChange(new AddAxiom(new_ont, a));
				}
			}
		}
	}	
	
	private void addClassToOntology(OWLClass c)
	{
		if (new_ont.containsClassInSignature(c.getIRI()))
			return;
		
	}
}
