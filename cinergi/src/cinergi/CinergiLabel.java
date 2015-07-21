package cinergi;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;


public class CinergiLabel {
	
	private OutputStream os;
	private PrintWriter writer;
	private OWLOntologyManager manager;
	private OWLOntology cinergiOntology, ont;
	private OWLDataFactory df;
	private Set<OWLOntology> ontologies;
	
	public CinergiLabel(OutputStream os, PrintWriter writer,
			OWLOntologyManager manager, OWLOntology cinergiOntology, OWLOntology ont, OWLDataFactory df) throws Exception {
	
		this.os = os;
		this.writer = writer;
		this.manager = manager;
		this.cinergiOntology = cinergiOntology;
		this.ont = ont;
		this.df = df;
		
		ontologies = cinergiOntology.getImports();
		ontologies.add(cinergiOntology);
		ontologies.add(ont);
		
	}
	
	public void fixFacetLabels()
	{
		Set<IRI> iri = new HashSet<IRI>();
		OWLOntologyWalker walker = new OWLOntologyWalker(ontologies);	
        OWLOntologyWalkerVisitor<Object> visitor =
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c) 
	        	{
	        		if (!iri.contains(c.getIRI()))
	        		{ 
		        		iri.add(c.getIRI());
	        			if (CinergiFacet.isTrueFacet(c, manager, df)) // if c has a cinergiFacet = true
	        			{
	        				OWLAnnotation oldLabelAnnotation = getLabelAnnotation(c);
	        				if (oldLabelAnnotation == null) // test
	        				{
	        					System.out.println("no label found for: " + c.getIRI().getShortForm());
	        				}
	        				String oldLabel = ((OWLLiteral) (oldLabelAnnotation.getValue())).getLiteral(); 
	        				
	        				String newLabel = stringToLabel(oldLabel); // get new label	        				
	        				if (newLabel.equals(oldLabel))
	        					return null;
	        				
	        				OWLAnnotation cinergi_preferredLabel = df.getOWLAnnotation(			
    								df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiPreferredLabel")), // cinergiExtensions base
    									df.getOWLLiteral(newLabel));	 
	        				
	        				OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), cinergi_preferredLabel);
    						manager.applyChange(new AddAxiom(ont, axiom));
    						writer.println(oldLabel + " added cinergiPreferredLabel: " + newLabel);
	        				/*
	        				RemoveAxiom removeAxiom = new RemoveAxiom(ont, df.getOWLAnnotationAssertionAxiom(c.getIRI(), oldLabelAnnotation));
							manager.applyChange(removeAxiom);  
							
							OWLAnnotation newLabelAnnotation = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral(newLabel));
							OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), newLabelAnnotation);
							manager.applyChange(new AddAxiom(ont, axiom));
							writer.println(c.getIRI() + " already visited. Skipping..");
							//writer.printf("%-20s\t%-10s\t%-20s\n",oldLabel,"changed to",newLabel);	
							 *         			
							 */
	        			}	        			
	        		}
	        		return null;
	        	}
        	};
        	
       walker.walkStructure(visitor);
	}
	
	private OWLAnnotation getLabelAnnotation(OWLClass c)
	{	
		OWLAnnotation anot = null;
		for (OWLOntology o : ontologies)
		{
			for (OWLAnnotation a : c.getAnnotations(o, df.getRDFSLabel()))
			{
				if (((OWLLiteral)a.getValue()).hasLang("en"))
				{
					return a;
				}
				anot = a;
			}
		}
		return anot;
	}
	
	private String stringToLabel(String str)
	{
		String label = "";
  		for (int i = 0; i < str.length(); i++)
		{
			if (i == 0)
				label += Character.toUpperCase(str.charAt(i));
			else if (" _-".contains("" + str.charAt(i-1)))
			{
				label += Character.toUpperCase(str.charAt(i));
			}
			else if (Character.isUpperCase(str.charAt(i)))
			{
				if (str.charAt(i-1) == '(')
				{
					label += str.charAt(i);
				}
				else
				{
					label += " " + str.charAt(i);
				}
			}
			else
				label += str.charAt(i);
		}
		return label;
	}
}




