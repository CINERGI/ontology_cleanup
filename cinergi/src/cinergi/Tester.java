package cinergi;

import java.io.File;
import java.io.OutputStream;
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
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
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
	        			String label = getLabel(c,manager, df);
	        			if (hasCinergiFacet(c, extensionsOntology, df))
	        			{	        				
	        				writer.printf("facet class: %-20s ", label);
	        				if (hasParentAnnotation(c, extensionsOntology, df))
	        				{
	        					String parentAnnot = getParentAnnotation(c, extensionsOntology, df);
	        					writer.printf("cinergiParent: %-20s ", label, parentAnnot);
	        				}	        				
		        			if (hasCinergiPreferredLabel(c,manager, df))
		        			{	        				
		        				String cinergiLabel = getCinergiPreferredLabel(c,manager, df);
		        				writer.printf("cinergiLabel: %-20s\n", label, cinergiLabel);
		        			}		        			
		        			else
		        				writer.printf("\n");
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
	

	private static boolean hasParentAnnotation(OWLClass c,
			OWLOntology extensionsOntology, OWLDataFactory df) {
		
		for (OWLAnnotation a : c.getAnnotations(extensionsOntology))
		{
			if (a.getProperty().equals(df.getOWLAnnotationProperty
					(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiParent"))))
			{	
				return true;
			}
		}
		return false;
	}
	
	private static String getParentAnnotation(OWLClass c,
			OWLOntology extensionsOntology, OWLDataFactory df) {
		for (OWLAnnotation a : c.getAnnotations(extensionsOntology))
		{
			if (a.getProperty().equals(df.getOWLAnnotationProperty
					(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiParent"))))
			{
				return (df.getOWLClass((IRI)a.getValue()).toString());
			}
		}
		return null;
	}
	
	private static OWLClass getParentAnnotationClass(OWLClass c,
			OWLOntology extensionsOntology, OWLDataFactory df) {
		for (OWLAnnotation a : c.getAnnotations(extensionsOntology))
		{
			if (a.getProperty().equals(df.getOWLAnnotationProperty
					(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiParent"))))
			{
				return (df.getOWLClass((IRI)(a.getValue())));
			}
		}
		return null;
	}
	
	public static boolean hasCinergiFacet(OWLClass c, OWLOntology o, OWLDataFactory df)
	{
		for (OWLAnnotation a : c.getAnnotations(o))
		{
			if (isCinergiFacet(a))
			{
				if (cinergiFacetTrue(a, df))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isCinergiFacet(OWLAnnotation a)
	{
		return (a.getProperty().getIRI().getShortForm().toString().equals("cinergiFacet"));
	}
	
	// precondition: a is a cinergiFacet annotation
	public static boolean cinergiFacetTrue(OWLAnnotation a, OWLDataFactory df)
	{		
		return a.getValue().equals(df.getOWLLiteral(true));
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

	public static void makeThirdLevelFacetAnnotations(OutputStream os,
			PrintWriter writer, OWLOntologyManager manager, OWLOntology ont,
			OWLDataFactory df) {
		
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
	        			if (hasCinergiFacet(c, ont, df)) // has true cinergiFacet
	        			{
	        				/* if the cinergiParent is Thing, then its a second level facet */
	        				if ((getParentAnnotationClass(c, ont, df).equals(df.getOWLClass
	        						(IRI.create("http://www.w3.org/2002/07/owl#Thing")))))
	        				{	        					 					
	        					// print c
	        					writer.printf("%-20s", getLabel(c,manager, df));	        					
	        					// if it has a preferred label, print that too
	        					if (!getCinergiPreferredLabel(c, manager, df).equals(""))
	        					{	   
	        						writer.printf("\tcinergiPreferredLabel: %s", getCinergiPreferredLabel(c, manager, df));
	        					}
	        					writer.println();	        					
	        					// get the subclasses of c
	        					for (OWLClassExpression cls : c.getSubClasses(manager.getOntologies()))
    							{
	        						OWLClass subcls = (OWLClass) cls;
	        						// if it doesn't have a cinergiParent already then 
	        						// make a cinergiParent annotation in each of these to c	        							        						
	        						if (!hasParentAnnotation(subcls, ont, df))
    								{
	        							// add this subclass to the third_level      						
	        							OWLAnnotation parentAnnot = df.getOWLAnnotation(			
	        								df.getOWLAnnotationProperty(IRI.create
	        										("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiParent")),
	        										c.getIRI() );
	        							OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(subcls.getIRI(), parentAnnot);
	        							AddAxiom addAxiom = new AddAxiom(ont, axiom);
	        							manager.applyChange(addAxiom);	        						
	        							writer.println("\t" + getLabel(subcls,manager, df) + " (added)");	        			
    								}	        						
    							}
	        					writer.println();
	        					
	        				}	        			
	        			}     			
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);
		
	}

	public static void fixEquivalentFacets(OutputStream os, PrintWriter writer,
			OWLOntologyManager manager, OWLOntology ont,OWLDataFactory df) {

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
	        			if (hasCinergiFacet(c, ont, df)) // has true cinergiFacet
	        			{
	        				// get equivalent classes
	        				Set<OWLClassExpression> equivalentClasses = c.getEquivalentClasses(manager.getOntologies());
	        				for (OWLClassExpression oce : equivalentClasses)
	        				{
	        					OWLClass cls = (OWLClass) oce;
	        					// make cls a facet too
	        					// if c has a preferred label, give cls it too
	        					// give cls c's cinergiParent
	        					if (!hasCinergiFacet(cls, ont, df)) // true facet?
    							{    							
	        						OWLAnnotation facetAnnot = df.getOWLAnnotation(			
	        								df.getOWLAnnotationProperty(IRI.create
	        										("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiFacet")),
	        										df.getOWLLiteral(true) );	        						
	        						OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), facetAnnot);
        							AddAxiom addAxiom = new AddAxiom(ont, axiom);
        							manager.applyChange(addAxiom);
    							}
	        					
	        					//if ()
	        				}
	        				
	        			}     			
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);
	}

	public static void printThirdLevelFacets(OutputStream os,PrintWriter writer, OWLOntologyManager manager,
			OWLOntology ont, OWLDataFactory df) {
		
		Set<IRI> iri = new HashSet<IRI>();
		Set<IRI> second_level = new HashSet<IRI>();
		
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalkerVisitor<Object> second_level_visitor = 
        		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c)
	        	{
	        		if (!iri.contains(c.getIRI()))
    				{	
	        			iri.add(c.getIRI());
	        			if (hasCinergiFacet(c, ont, df)) // has true cinergiFacet
	        			{
	        				/* if the cinergiParent is Thing, then its a second level facet */
	        				if ((getParentAnnotationClass(c, ont, df).equals(df.getOWLClass
	        						(IRI.create("http://www.w3.org/2002/07/owl#Thing")))))
	        				{	        	
	        					second_level.add(c.getIRI());        					    						        				
	        				}	        			
	        			}     			
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(second_level_visitor);
		
		for (IRI i : second_level)
		{
			OWLClass second_level_class = df.getOWLClass(i);
			writer.printf("%-20s", getLabel(second_level_class,manager, df));	        					
			// if it has a preferred label, print that too
			if (!getCinergiPreferredLabel(second_level_class, manager, df).equals(""))
			{	   
				writer.printf("\tcinergiPreferredLabel: %s", getCinergiPreferredLabel(second_level_class, manager, df));
			}
			writer.println();	
						
			Set<IRI> iris = new HashSet<IRI>();
			OWLOntologyWalkerVisitor<Object> third_level_visitor = 
	        		
		    		new OWLOntologyWalkerVisitor<Object>(walker)    
		        	{
			        	@Override
			        	public Object visit(OWLClass cls)
			        	{
			        		if (!iris.contains(cls.getIRI()))
		    				{	
			        			iris.add(cls.getIRI());
		        				
			        			/* if has a parent annotation */
			        			if (hasParentAnnotation(cls,ont,df))
			        			{
			        				/* if the cinergiParent is the second level class, then print */			        				
			        				if ((getParentAnnotationClass(cls, ont, df).equals(second_level_class)))
			        				{	        	
			        					writer.printf("\t%s", getLabel(cls,manager, df));	        					
			        					// if it has a preferred label, print that too
			        					if (!getCinergiPreferredLabel(cls, manager, df).equals(""))
			        					{	   
			        						writer.printf("\tcinergiPreferredLabel: %s", getCinergiPreferredLabel(cls, manager, df));
			        					}
			        					writer.println();	        						        					
			        				}
			        			}
		    				}
			        		return null;
			        	}
		        	};
	    	walker.walkStructure(third_level_visitor);
	    	writer.println();
		}
	}
	
	public static void makeThirdLevelFacetAnnotationsFixed(OutputStream os,
			PrintWriter writer, OWLOntologyManager manager, OWLOntology ont,
			OWLDataFactory df) {
		
		Set<IRI> iri = new HashSet<IRI>();
		
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalkerVisitor<Object> visitor = 
        		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass thirdLevel)
	        	{
	        		if (!iri.contains(thirdLevel.getIRI()))
    				{	
	        			iri.add(thirdLevel.getIRI());
	        			// if (!hasCinergiParent)
	        				// if super class has cinergiParent
	        					// if that cinergiparent has cinergiparent which is thing
	        					// then make a cinergiparent relationship from this class to its super class
	        			if (!hasParentAnnotation(thirdLevel,ont,df))
	        			{
	        				for (OWLClassExpression oce : thirdLevel.getSuperClasses(manager.getOntologies()))
	        				{
	        					for (OWLClass secondLevel : oce.getClassesInSignature())
	        					{	        					
		        					if (hasParentAnnotation(secondLevel,ont,df))
		        					{
		        						OWLClass firstLevel = getParentAnnotationClass(secondLevel, ont, df);
		        						if (hasParentAnnotation(firstLevel, ont, df))
		        						{
		        							if ((getParentAnnotationClass(firstLevel, ont, df).equals(df.getOWLClass
		        									(IRI.create("http://www.w3.org/2002/07/owl#Thing")))))
		        							{
		        								OWLAnnotation parentAnnot = df.getOWLAnnotation(			
		    	        								df.getOWLAnnotationProperty(IRI.create
		    	        										("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiParent")),
		    	        										secondLevel.getIRI() );
		    	        							OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(thirdLevel.getIRI(), parentAnnot);
		    	        							AddAxiom addAxiom = new AddAxiom(ont, axiom);
		    	        							manager.applyChange(addAxiom);	        						
		    	        							writer.println(getLabel(firstLevel, manager, df) + " " + firstLevel.getIRI() + " > " + getLabel(secondLevel,manager, df) + " " + secondLevel.getIRI() + " > " + getLabel(thirdLevel,manager, df) + " " + thirdLevel.getIRI());
		    	        							//writer.println(getLabel(firstLevel, manager, df) + " > " + getLabel(secondLevel,manager, df) + " > " + getLabel(thirdLevel,manager, df));
		        							}
		        						}
		        					}
	        					}
	        				}
	        			}
	        			else // if the class has a cinergiParent
	        			{
	        				OWLClass secondLevel = getParentAnnotationClass(thirdLevel, ont, df);
	        				if (hasParentAnnotation(secondLevel,ont,df)) // if this class has a cinergiParent
        					{
        						OWLClass firstLevel = getParentAnnotationClass(secondLevel, ont, df);
        						if (hasParentAnnotation(firstLevel, ont, df))
        						{
        							if ((getParentAnnotationClass(firstLevel, ont, df).equals(df.getOWLClass
        									(IRI.create("http://www.w3.org/2002/07/owl#Thing")))))
        							{	        						
    	        							writer.println(getLabel(firstLevel, manager, df) + " " + firstLevel.getIRI() + " > " + getLabel(secondLevel,manager, df) + " " + secondLevel.getIRI() + " > " + getLabel(thirdLevel,manager, df) + " " + thirdLevel.getIRI());
    	        							//writer.println(getLabel(firstLevel, manager, df) + " > " + getLabel(secondLevel,manager, df) + " > " + getLabel(thirdLevel,manager, df));
        							}
        						}
        					}
	        			}
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);
		
	}
}

