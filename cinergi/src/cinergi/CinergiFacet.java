package cinergi;

import java.io.OutputStream;
import java.io.PrintWriter;
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

public class CinergiFacet {
	private OutputStream os;
	private PrintWriter writer;
	private OWLOntologyManager manager, facets_manager;
	private OWLOntology ont, ont_facets;
	private OWLDataFactory df;
	
	public CinergiFacet(OutputStream os, PrintWriter writer, OWLOntologyManager manager, OWLOntologyManager facets_manager,
			OWLOntology ont, OWLOntology ont_facets, OWLDataFactory df) 
	{
		this.os = os;
		this.writer = writer;
		this.manager = manager;
		this.facets_manager = facets_manager;
		this.ont = ont;
		this.ont_facets = ont_facets;
		this.df = df;
	}
	
	public void convertFacetsToBoolean()
	{
		Set<OWLClass> cls = new HashSet<OWLClass>();
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalkerVisitor<Object> visitor =
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c) // visit all annotations, if they are a cinergiFacet, change to boolean
	        	{
	        		if (!cls.contains(c))
	        		{
	        			for (OWLOntology o : manager.getOntologies())
	        			{
	        				for (OWLAnnotation a : c.getAnnotations(o))
	        				{
	        					if (a.getProperty().equals(df.getOWLAnnotationProperty(
	        							IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiFacet"))))
	        					{	    
	        						OWLAnnotation facet = a;
	        						OWLAnnotation newFacet;
	        						if (cinergiFacetTrue(a))	        				
	        						{ 	
	        							newFacet = df.getOWLAnnotation(			
	        								df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiFacet")),
	        									df.getOWLLiteral(true) );
	        							
	        						}
	        						else 
	        						{
	        							newFacet = df.getOWLAnnotation(			
	        								df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiFacet")),
		        								df.getOWLLiteral(false) );
	        						}
	        						RemoveAxiom removeAxiom = new RemoveAxiom(ont, df.getOWLAnnotationAssertionAxiom(c.getIRI(), facet));
	        						manager.applyChange(removeAxiom); 
	        						OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), newFacet);
	        						manager.applyChange(new AddAxiom(ont, axiom));
	        						writer.println("cinergiFacet in class: " + c.getIRI().getShortForm()+ " changed to boolean");
	        					}
	        				}
	        			}
	        			cls.add(c);
	        		}	        		
	        		return null;
	        	}
        	};
        	
       walker.walkStructure(visitor);
	}
	
	private boolean cinergiFacetTrue(OWLAnnotation a)
	{
		return a.getValue().equals(df.getOWLLiteral(true));
		//return a.getValue().toString().equals("\"True\"");
	}
	
	private boolean isCinergiFacet(OWLAnnotation a)
	{
		return (a.getProperty().equals(df.getOWLAnnotationProperty(
				IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiFacet"))));
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
		}
		return label;
	}
	
	public void generateList() {
		
		Set<OWLClass> cls = new HashSet<OWLClass>();
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalkerVisitor<Object> visitor =
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c) 
	        	{
	        		if (!cls.contains(c))
	        		{
	        			for (OWLOntology o : manager.getOntologies())
	        			{
	        				for (OWLAnnotation a : c.getAnnotations(o))
	        				{
	        					if (isCinergiFacet(a) && cinergiFacetTrue(a))
	        					{
	        						writer.println(getLabel(c));
	        					}
	        				}
	        			}
	        		}
	        		cls.add(c);
					return null;
	        	}
        	};
		walker.walkStructure(visitor);	        
	}
	
	public void removeFacets()
	{
		Set<OWLClass> cls = new HashSet<OWLClass>();
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalkerVisitor<Object> visitor =
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c)
	        	{
	        		if (!cls.contains(c))
	        		{
	        			for (OWLOntology o : manager.getOntologies())
	        			{
	        				for (OWLAnnotation a : c.getAnnotations(o))
	        				{
	        					if (a.getProperty().equals(df.getOWLAnnotationProperty(
	        							IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiFacet"))))
	        					{	    
	        						// remove a
	        						RemoveAxiom removeAxiom = new RemoveAxiom(ont, df.getOWLAnnotationAssertionAxiom(c.getIRI(), a));
	        						manager.applyChange(removeAxiom);  
	        						writer.println("Facet removed from: " + getLabel(c));
	        					}
	        				}
	        			}
	        		}
	        		cls.add(c);
	        		return null;
	        	}
        	};
        	walker.walkStructure(visitor);
	}
	
	public static boolean isTrueFacet(OWLClass c, OWLOntologyManager m, OWLDataFactory df)
	{
		for (OWLOntology o : m.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(o))
			{
				if (a.getProperty().equals(df.getOWLAnnotationProperty(
						IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiFacet"))))
				{	 
					if (a.getValue().equals(df.getOWLLiteral(true)))
						return true;
				}
			}
		}
		return false;
	}
}
