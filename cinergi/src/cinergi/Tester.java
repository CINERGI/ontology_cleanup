package cinergi;

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
	
	public static void printPreferredLabels(PrintWriter writer,	OWLOntology extensionsOntology, 
			OWLOntologyManager manager, OWLDataFactory df) 
	{
		Set<IRI> iri = new HashSet<IRI>();
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalkerVisitor<Object> visitor = 
        		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c)
	        	{
	        		if (!iri.contains(c.getIRI()))
    				{		
	        			iri.add(c.getIRI());
	        			if (hasCinergiPreferredLabel(c,manager, df))
	        			{
	        				String label = getLabel(c,manager, df);
	        				String cinergiLabel = getCinergiPreferredLabel(c,manager, df);
	        				writer.printf("class: %-20s cinergiLabel: %-20s\n", label, cinergiLabel);
	        			}
	        			
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);
	}
	
	public static void test(OWLOntologyManager manager, OWLOntology ont, OWLDataFactory df, PrintWriter writer) throws Exception
	{
		
		Set<OWLAnnotation> anots = new HashSet<OWLAnnotation>();
		
        OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        
        OWLOntologyWalkerVisitor<Object> visitor = 
        		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLAnnotation a)
	        	{
	        		if (!anots.contains(a));
	        		writer.println("property = " + a.getProperty());
	        		writer.println("value = " + a.getValue());
	        		anots.add(a);
	        		return null;
	        	}
        	};
        	
       walker.walkStructure(visitor);
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
	

	public static boolean hasCinergiPreferredLabel(OWLClass c, OWLOntologyManager m, OWLDataFactory df)
	{
		boolean has = false;
		for (OWLOntology o : m.getOntologies())
		{
			if (!c.getAnnotations(o, df.getOWLAnnotationProperty
					(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiPreferredLabel"))).isEmpty())
			{
				has = true;
			}
		}
		return has;
	}
	
	public static String getCinergiPreferredLabel(OWLClass c, OWLOntologyManager m, OWLDataFactory df)
	{
		String label = "";
		for (OWLOntology o : m.getOntologies())
		{
			for (OWLAnnotation a: c.getAnnotations(o, df.getOWLAnnotationProperty
					(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiPreferredLabel"))))
			{
				label = ((OWLLiteral)a.getValue()).getLiteral();
			}
		}
		return label;
	}
	
	public static String getLabel(OWLClass c, OWLOntologyManager m, OWLDataFactory df)
	{
		String label = "";
		for (OWLOntology o : m.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(o, df.getRDFSLabel()))
			{
				if (((OWLLiteral)a.getValue()).hasLang("en"))
				{
					label = ((OWLLiteral)a.getValue()).getLiteral();
					break;  
				}
				label = ((OWLLiteral)a.getValue()).getLiteral();
			}
		}
		return label;		
	}
}
