package cinergi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class Tester {
	
	public static void printPreferredLabels(final PrintWriter writer,	OWLOntology extensionsOntology, 
			final OWLOntologyManager manager, final OWLDataFactory df) 
	{
		final Set<IRI> iri = new HashSet<IRI>();
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
				
	        			if (hasCinergiPreferredLabel(c,manager, df))
	        			{	        				
	        				String cinergiLabel = getCinergiPreferredLabel(c,manager, df);
	        				writer.printf("label: %-25s ", label);
	        				writer.printf("cinergiPreferredLabel: %-25s\n", cinergiLabel);
	        			}		
    				}
	        		return null;
	        	}		
        	};
		walker.walkStructure(visitor);
	}
	
	public static void test(OWLOntologyManager manager, OWLOntology ont, OWLDataFactory df, final PrintWriter writer) throws Exception
	{
		
		final Set<OWLAnnotation> anots = new HashSet<OWLAnnotation>();
		
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
	
	private static List<OWLClass> getParentAnnotationClasses(OWLClass c,
			OWLOntology extensionsOntology, OWLDataFactory df) {
		
		List<OWLClass> parents = new ArrayList<OWLClass>();
		
		for (OWLAnnotation a : c.getAnnotations(extensionsOntology))
		{
			if (a.getProperty().equals(df.getOWLAnnotationProperty
					(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiParent"))))
			{
				parents.add(df.getOWLClass((IRI)(a.getValue())));
			}
		}
		return parents;
	}
	
	/*public static boolean hasCinergiFacet(OWLClass c, OWLOntologyManager m, OWLDataFactory df)
	{
		for (OWLOntology o : m.getOntologies())
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
		}
		return false;
	}*/
	
	public static boolean hasCinergiFacet(OWLClass c, OWLOntologyManager manager, OWLDataFactory df)
	{
		/*boolean hasFacet = true;
		for (OWLOntology o : manager.getOntologies())
		{
			Set<OWLAnnotation> cinergiFacet = c.getAnnotations(o,df.getOWLAnnotationProperty
					(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiFacet")));
			
			if (!cinergiFacet.isEmpty() == false) // current ontology is cinergiExtensions
			{
				hasFacet = true;
				System.err.println(c.getIRI());
				if (cinergiFacet.iterator().hasNext())
				{
					OWLAnnotation facet = cinergiFacet.iterator().next();
					if (facet.getValue().equals(df.getOWLLiteral(true)))
					{
						return true;
					}
				}
				else
				{
					System.err.println(c.getIRI()+" nonempty cinergiFacet annotations but no next");
				}
				return false;
			}			
		}
		if (!hasFacet)
		{
			System.out.println("no facet annotation for " + c.getIRI());
		}
		
		return false; */
		boolean hasFacet = false;
		for (OWLOntology o : manager.getOntologies())
		{
			if (!c.getAnnotations(o,df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiFacet"))).isEmpty())
			{
				hasFacet = true;
				for (OWLAnnotation facet : c.getAnnotations(o,df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiFacet"))))				
				{
					if (facet.getValue().equals(df.getOWLLiteral(true)))
					{
						return true;
					}
					if (facet.getValue().toString().equals("true"))
					{
						System.err.println("wrong cinergiFacet format for: " + c.getIRI());
						return true;
					}
					return false;
				}
			}
		}
		if (!hasFacet)
		{
			System.err.println("no cinergiFacet annotation in class: " + c.getIRI());
		}
		return false;
	}
	
	public static boolean hasCinergiFacetAnnotation(OWLClass c, OWLOntologyManager m, OWLDataFactory df)
	{
		for (OWLOntology o : m.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(o))
			{
				if (isCinergiFacet(a))
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
	
	public static String getLabel(OWLNamedIndividual c, OWLOntologyManager m, OWLDataFactory df)
	{
		String label = "";
		for (OWLOntology o : m.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(o, df.getRDFSLabel()))
			{				
				if (((OWLLiteral)a.getValue()).getLang().toString().equals("en"))
				{					
					label = ((OWLLiteral)a.getValue()).getLiteral();
					break;  
				}
				label = ((OWLLiteral)a.getValue()).getLiteral();
			}
		}
		for (OWLOntology o : m.getOntologies())			
		{
			for (OWLAnnotation a : c.getAnnotations(o, df.getOWLAnnotationProperty
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
				if (((OWLLiteral)a.getValue()).getLang().toString().equals("en"))
				{					
					label = ((OWLLiteral)a.getValue()).getLiteral();
					break;  
				}
				label = ((OWLLiteral)a.getValue()).getLiteral();
			}
		}
		for (OWLOntology o : m.getOntologies())			
		{
			for (OWLAnnotation a : c.getAnnotations(o, df.getOWLAnnotationProperty
						(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiPreferredLabel"))))
			{
				label = ((OWLLiteral)a.getValue()).getLiteral();
			}
		}
		return label;		
	}

	public static String getLabelOld(OWLClass c, OWLOntologyManager m, OWLDataFactory df)
	{
		String label = "";
		for (OWLOntology o : m.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(o, df.getRDFSLabel()))
			{				
				if (((OWLLiteral)a.getValue()).getLang().toString().equals("en"))
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
			final PrintWriter writer, final OWLOntologyManager manager, final OWLOntology ont,
			final OWLDataFactory df) {
		
		final Set<IRI> iri = new HashSet<IRI>();
		
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
	        			if (hasCinergiFacet(c, manager, df)) // has true cinergiFacet
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
			final OWLOntologyManager manager, final OWLOntology ont,final OWLDataFactory df) {

		final Set<IRI> iri = new HashSet<IRI>();
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
	        			if (hasCinergiFacet(c, manager, df)) // has true cinergiFacet
	        			{
	        				// get equivalent classes
	        				Set<OWLClassExpression> equivalentClasses = c.getEquivalentClasses(manager.getOntologies());
	        				for (OWLClassExpression oce : equivalentClasses)
	        				{
	        					OWLClass cls = (OWLClass) oce;
	        					// make cls a facet too
	        					// if c has a preferred label, give cls it too
	        					// give cls c's cinergiParent
	        					if (!hasCinergiFacet(cls, manager, df)) // true facet?
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

	public static void printThirdLevelFacets(OutputStream os,final PrintWriter writer, final OWLOntologyManager manager,
			final OWLOntology ont, final OWLDataFactory df) {
		
		final Set<IRI> iri = new HashSet<IRI>();
		final Set<IRI> second_level = new HashSet<IRI>();
		
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
	        			if (hasCinergiFacet(c, manager, df)) // has true cinergiFacet
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
			final OWLClass second_level_class = df.getOWLClass(i);
			writer.printf("%-20s", getLabel(second_level_class,manager, df));	        					
			// if it has a preferred label, print that too
			if (!getCinergiPreferredLabel(second_level_class, manager, df).equals(""))
			{	   
				writer.printf("\tcinergiPreferredLabel: %s", getCinergiPreferredLabel(second_level_class, manager, df));
			}
			writer.println();	
						
			final Set<IRI> iris = new HashSet<IRI>();
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
			final PrintWriter writer, final PrintWriter writer4, final OWLOntologyManager manager, final OWLOntology ont,
			final OWLDataFactory df) {
		
		final Set<IRI> iri = new HashSet<IRI>();
		
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
	        			
	        			String label = getLabel(thirdLevel, manager, df);
	        			
	        			// hard coded exclusions
	        			if (label.equals("Software Service Resource") || label.equals("Data Service Resource"))
	        			{
	        				System.out.println("found : " + label);
	        				/* make sure Software Service Resource and Web Service Resource aren't being mapped
	        				 * Data Service Resource excluded too
	        				 */
	        				return null;
	        			}
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
		        					if (hasParentAnnotation(secondLevel,ont,df) && hasCinergiFacet(secondLevel, manager, df))	
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
	    	        							writer4.println(getLabel(firstLevel, manager, df) + " > " + getLabel(secondLevel,manager, df) + " > " + getLabel(thirdLevel,manager, df));
		        							}
		        						}
		        					}
	        					}
	        				}
	        			}
	        			else // if the class has a cinergiParent
	        			{
	        				System.out.println(getLabel(thirdLevel, manager, df));
	        				OWLClass secondLevel = getParentAnnotationClass(thirdLevel, ont, df);
	        				if (hasParentAnnotation(secondLevel,ont,df) && hasCinergiFacet(secondLevel, manager, df)) // if this class has a cinergiParent
        					{
        						OWLClass firstLevel = getParentAnnotationClass(secondLevel, ont, df);
        						if (hasParentAnnotation(firstLevel, ont, df))
        						{
        							if ((getParentAnnotationClass(firstLevel, ont, df).equals(df.getOWLClass
        									(IRI.create("http://www.w3.org/2002/07/owl#Thing")))))
        							{	        						
    	        							writer.println(getLabel(firstLevel, manager, df) + " " + firstLevel.getIRI() + " > " + getLabel(secondLevel,manager, df) + " " + secondLevel.getIRI() + " > " + getLabel(thirdLevel,manager, df) + " " + thirdLevel.getIRI());
    	        							writer4.println(getLabel(firstLevel, manager, df) + " > " + getLabel(secondLevel,manager, df) + " > " + getLabel(thirdLevel,manager, df));
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

	public static void printFacets(final PrintWriter writer, final OWLOntology ont,
			final OWLOntologyManager manager, final OWLDataFactory df) {
		
		final Set<IRI> iri = new HashSet<IRI>();
		
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
	        			if (hasCinergiFacet(c, manager, df))
	        			{
	        				String label = getLabel(c,manager, df);	        			
    						
	        				writer.printf("facet: %-25s ", label);
	        				for (OWLClass parent : getParentAnnotationClasses(c, ont, df))
	        				{
	        					writer.printf("cinergiParent: %-25s ", getLabel(parent, manager, df));
	        				}
	        				writer.println();
	        			}	        			
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);
	}

	public static List<String> getFirstLevelFacets(final OWLOntologyManager manager, final OWLDataFactory df) {
		
		final Set<IRI> iri = new HashSet<IRI>();
		
		final List<String> firstLevel = new ArrayList<String>();
		
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
	        			for (OWLOntology o : manager.getOntologies())
	        			{	
		        			if (hasParentAnnotation(c, o, df))
		        			{
		        				if ((getParentAnnotationClass(c, o, df).equals(df.getOWLClass
		        						(IRI.create("http://www.w3.org/2002/07/owl#Thing")))))
		        				{	  
		        					firstLevel.add(getLabel(c, manager, df));
		        				}
		        			}
	        			}
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);
		
		return firstLevel;
	}
	
	public static void correctElements(final PrintWriter writer, final OWLOntology ont,
			final OWLOntologyManager manager, final OWLDataFactory df, OutputStream os) {
		
		final Set<IRI> iri = new HashSet<IRI>();
		
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
	        			for (OWLOntology o : manager.getOntologies())
	        			{
	        				for (OWLAnnotation a : c.getAnnotations(o, df.getRDFSLabel()))
	        				{
	        					if (((OWLLiteral)a.getValue()).getLiteral().contains("Elemental"))
	        					{									
									OWLAnnotation parentAnnot = df.getOWLAnnotation(			
	        								df.getOWLAnnotationProperty(IRI.create
	        										("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiParent")),
	        										IRI.create("http://purl.obolibrary.org/obo/CHEBI_24431") );
        							OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), parentAnnot);
        							AddAxiom addAxiom = new AddAxiom(ont, axiom);
        							manager.applyChange(addAxiom);	
        							
        							writer.println("Chemicals parent added to : " + ((OWLLiteral)a.getValue()).getLiteral());
	        					}
	        				}
	        			}
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);
		
	}

	public static void printAllCaps(final PrintWriter writer, OWLOntology ont,
			final OWLOntologyManager manager, final OWLDataFactory df) {
		final Set<IRI> iri = new HashSet<IRI>();
		
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalkerVisitor<Object> visitor = 
        		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c)
	        	{
	        		if (!iri.contains(c.getIRI()))
    				{	
	        			String label = getLabel(c, manager, df);
	        			if (label.toUpperCase().equals(label)) // all upper case
	        			{
	        				writer.println(label);
	        			}
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);
		
	}

	public static void printTop3Levels(PrintWriter writer, OWLOntology ont,
			final OWLOntologyManager manager, OWLDataFactory df) {
		final Set<IRI> iri = new HashSet<IRI>();

		final Set<OWLClass> first_level = new HashSet<OWLClass>();
		
		OWLClass testClass = df.getOWLClass(IRI.create("http://purl.org/obo/owl/CMO#CMO_0000000"));
		
		
		
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
	        			for (OWLClassExpression oce : c.getSuperClasses(manager.getOntologies()))
	        			{
        					if (oce.getClassExpressionType().toString().equals("Class"))
        					{
        						return null;
        					}
	        			} 	 
	        			first_level.add(c);
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);
		
		for (OWLClass first_level_class : first_level)
		{
			// if has no subclasses, then print			
			if (first_level_class.getSubClasses(manager.getOntologies()).isEmpty())
			{
				String firstLevel_label = getLabel(first_level_class, manager, df);
				// no label
				if (firstLevel_label.equals(""))
				{
					firstLevel_label = first_level_class.getIRI().getShortForm();
				}
				writer.printf("%-15s\n",firstLevel_label);
			}
			else // has children
			{
				String label = getLabel(first_level_class, manager, df);
				// no label
				if (label.equals(""))
				{
					label = first_level_class.getIRI().getShortForm();
				}
				writer.printf("%-15s\n",label);
				
				// same thing for each child
				for (OWLClassExpression child_oce : first_level_class.getSubClasses(manager.getOntologies()))
				{
					OWLClass second_level_class = (OWLClass)child_oce;
					if (second_level_class.getSubClasses(manager.getOntologies()).isEmpty())
					{
						String secondLevel_label = getLabel(second_level_class, manager, df);
						// no label
						if (secondLevel_label.equals(""))
						{
							secondLevel_label = second_level_class.getIRI().getShortForm();
						}
						writer.printf("%-15s%-15s\n","",secondLevel_label);
					}
					else // second level has children
					{
						String secondLevel_label = getLabel(second_level_class, manager, df);
						// no label
						if (secondLevel_label.equals(""))
						{
							secondLevel_label = second_level_class.getIRI().getShortForm();
						}
						writer.printf("%-15s%-15s\n","",secondLevel_label);

						for (OWLClassExpression third_oce : second_level_class.getSubClasses(manager.getOntologies()))
						{
							OWLClass third_level_class = (OWLClass)third_oce;
							String thirdLevel_label = getLabel(third_level_class, manager, df);
							// no label
							if (thirdLevel_label.equals(""))
							{
								thirdLevel_label = third_level_class.getIRI().getShortForm();
							}
							writer.printf("%-15s%-15s%-15s\n","","",thirdLevel_label);
						}
					}
				}
			}
		}
	}

	public static void printFacetsNotMappedTo(PrintWriter writer,
			final OWLOntology ont, final OWLOntologyManager manager, final OWLDataFactory df,
			OutputStream os) {
		
		final Set<IRI> facets = new HashSet<IRI>();
		final Set<IRI> iri = new HashSet<IRI>();
		
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
	        			if (hasCinergiFacet(c, manager, df))
	        			{
	        				facets.add(c.getIRI());
	        			}	        			
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);
		iri.clear(); // clear iris
		
		OWLOntologyWalkerVisitor<Object> visitor2 = 
        		
	    		new OWLOntologyWalkerVisitor<Object>(walker)    
	        	{
		        	@Override
		        	public Object visit(OWLClass c)
		        	{
		        		if (!iri.contains(c.getIRI()))
	    				{	
		        			iri.add(c.getIRI());
		        			// if cinergiParent or parent is one of the facets, remove the facet from the HashSet
		        			if  (hasParentAnnotation(c, ont, df))
		        			{
		        				IRI facetIRI = getParentAnnotationClass(c, ont, df).getIRI();
		        				if (facets.contains(facetIRI))		        			
        						{
		        					facets.remove(facetIRI);
        						}
		        				for (OWLClassExpression oce : c.getSuperClasses(manager.getOntologies()))
		        				{
		        					if (oce.getClassExpressionType().toString().equals("Class"))
		        					{
			        					IRI parentIRI = ((OWLClass) oce ).getIRI();
			        					if (facets.contains(parentIRI))
	        							{
			        						facets.remove(parentIRI);
	        							}
		        					}
		        				}		        			
		        			}
	        			}
		        		return null;
		        	}
	        	};
    	walker.walkStructure(visitor2);
    	
    	for (IRI i : facets)
    	{
    		writer.println("facet: " + getLabel(df.getOWLClass(i), manager, df));	
    	}
	}

	// prints any equivalent classes
	public static void printEquivalentClasses(final PrintWriter writer, OWLOntology ont, final OWLOntologyManager manager,
			final OWLDataFactory df) {
		
		final Set<IRI> iris = new HashSet<IRI>();

		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalkerVisitor<Object> visitor = 
        		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c)
	        	{
	        		ArrayList<OWLClass> classes = new ArrayList<OWLClass>();
	        		boolean hasClassEquivalent = false;
	        		classes.add(c);
	        		if (!iris.contains(c.getIRI()))
    				{	
	        			iris.add(c.getIRI());
	        			// check if there are any equivalency axioms
	        			if (!c.getEquivalentClasses(manager.getOntologies()).isEmpty())
	        			{
	        				for (OWLClassExpression oce : c.getEquivalentClasses(manager.getOntologies()))
	        				{		     
	        					// set hasClassEquivalent flag if there is a class that is equal	        
	        					if (oce.getClassExpressionType().toString().equals("Class"))
	        					{	        						
	        						hasClassEquivalent = true;
	        						classes.add((OWLClass) oce);
		        					// do nothing if this class has already been dealt with
	        						if (iris.contains(((OWLClass) oce).getIRI()))
	        						{
	        							return null;
	        						}
	        					}
	        				}
	        				if (hasClassEquivalent)
	        				{
	        					for (OWLClass cl : classes)
		        				{
	        						writer.printf("%-40s", getLabel(cl, manager, df));
		        				}
	        					// get URI of the class with the deepest subtree
	        					writer.printf("%-40s\n", IRIOfDeepest(classes, manager, df) );
	        				}
	        			}        			
    				}
	        		return null;
	        	}

				private String IRIOfDeepest(ArrayList<OWLClass> classes, OWLOntologyManager manager,
						OWLDataFactory df) {
					// i is index of the class with the deepest subtree
					int index = 0, max = 0, i = 0;				
					for (i = 0; i < classes.size(); i++)
					{
						int depth = getDepth(classes.get(i), manager, df);
						if (hasCinergiFacet(classes.get(i), manager, df))
						{
							// increase depth by 1000 if the class is a cinergi facet
							depth+=1000;
						}
						if (depth > max)
						{
							max = depth;
							index = i;
						}
						System.out.println(" depth of "
								+ getLabel(classes.get(i), manager, df) +
								" is " + depth);
					}				
					return (classes.get(index)).getIRI().toString();
				}		
        	};
		walker.walkStructure(visitor);
		
	}

	public static int getDepth(OWLClass c, OWLOntologyManager manager, OWLDataFactory df) {
		// TODO Auto-generated method stub
		if (c.getSubClasses(manager.getOntologies()).isEmpty())
		{
			return 0;
		}
		int numSubClasses = 0;
		for (OWLClassExpression oce : c.getSubClasses(manager.getOntologies()))
		{		     
			if (oce.getClassExpressionType().toString().equals("Class"))
			{
				numSubClasses = numSubClasses + 1 + getDepth(((OWLClass)oce), manager, df);
			}
		}
		return numSubClasses;		
	}

	public static void geologicTimeImpure(OutputStream os, final PrintWriter writer,
			final OWLOntologyManager manager, final OWLOntologyManager manager2, final OWLOntology ont, 
			final OWLOntology ont2, final OWLDataFactory df, final OWLDataFactory df2) {
		
		final Set<IRI> iri = new HashSet<IRI>();
		final ArrayList<OWLAnnotation> annots = new ArrayList<OWLAnnotation>();
		
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        
		final String geochronologyImpure = "http://hydro10.sdsc.edu/cinergi_ontology/geochronologyImpure.owl#";
		
		OWLOntologyWalkerVisitor<Object> visitor =         		
		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLNamedIndividual indiv)
	        	{
	        		boolean addedToOntology = false;
	        		// ensure that we dont process the same individual more than once
	        		if (iri.contains(indiv.getIRI()))
	        			return null;
	        		iri.add(indiv.getIRI());
	        		
	        		// wipe the annotations
	        		annots.clear();	
	        		
	        		for (OWLOntology ont : manager.getOntologies())
	        		{
	        			for (OWLAnnotation a : indiv.getAnnotations(ont))
	        			{
	        				// get all geologic times that have a ranks
	        				if (a.getProperty().equals(df.getOWLAnnotationProperty
	        						(IRI.create("http://resource.geosciml.org/ontology/timescale/gts#rank"))))
	        				{
	        					OWLAnnotationValue rank = a.getValue();
	        					writer.println(getLabel(indiv, manager, df) + " == " + ((IRI)rank).getShortForm());
	        					
	        					// add the individual as a subclass of the correct rank
	        					OWLAxiom subclassAxiom = df.getOWLSubClassOfAxiom(df.getOWLClass(
	        							IRI.create(geochronologyImpure + indiv.getIRI().getShortForm())), 
	        							df2.getOWLClass(IRI.create(geochronologyImpure + ((IRI)rank).getShortForm())));

    							AddAxiom addAxiom = new AddAxiom(ont2, subclassAxiom);
    							manager2.applyChange(addAxiom);	 
    							addedToOntology = true;
    							
    							// manually add this annotation to geochronologyImpure
    							OWLAnnotation rankAnnotation = df.getOWLAnnotation(a.getProperty(), 
    									IRI.create(geochronologyImpure + ((IRI)rank).getShortForm()));
    							OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(
    									IRI.create(geochronologyImpure + indiv.getIRI().getShortForm()), rankAnnotation);
								AddAxiom addAxiom2 = new AddAxiom(ont2, axiom);
								manager2.applyChange(addAxiom2);	
    							continue;      								
	        				}
	        				// add the annotations of that individual to the class
	        				annots.add(a);
        				}
	        		}
	        		if (addedToOntology)
	        		{
	        			for (OWLOntology ont : manager.getOntologies())
		        		{
		        			for (OWLAnnotation a : annots)
		        			{
		        				OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(
		        						IRI.create(geochronologyImpure + indiv.getIRI().getShortForm()), a);
								AddAxiom addAxiom = new AddAxiom(ont2, axiom);
								manager2.applyChange(addAxiom);	
		        			}
		        		}
	        		}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);

	}

	// adds a cinergiFacet = false annotation to each class that does not already have a cinergiFacet annotation
	public static void addMissingFacets(final PrintWriter writer, final OWLOntology extensions, final OWLOntologyManager manager,
			final OWLDataFactory df) {
		
		final Set<IRI> iri = new HashSet<IRI>();
		
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
	        			// the class is missing a facet annotation
	        			if (!hasCinergiFacetAnnotation(c, manager, df))
	        			{
	        				OWLAnnotation facetAnnot = df.getOWLAnnotation(			
    								df.getOWLAnnotationProperty(IRI.create
    										("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiFacet")),
    										df.getOWLLiteral(false) );	        						
    						OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), facetAnnot);
	        				AddAxiom addAxiom = new AddAxiom(extensions, axiom);
	        				manager.applyChange(addAxiom);	        						
	        				writer.println("facet added for " + getLabel(c,manager, df) + " (false)");	 
	        			}	
	        			else if (hasCinergiFacet(c, manager, df) && !hasParentAnnotation(c, extensions, df)) // cinergiFacet true && no cinergiParent
	        			{
	        				writer.println(getLabel(c,manager,df) + " does not have a cinergiParent. <<< correct this.");
	        			}
    				}
	        		return null;
	        	}
        	};
		walker.walkStructure(visitor);
		
		
	}

	public static void addSynonymsToGeologicTimes(final PrintWriter writer, OutputStream os, final OWLOntology ont, 
			final OWLOntologyManager manager, final OWLDataFactory df) {
		
		final Set<IRI> iri = new HashSet<IRI>();
		
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
	        			
	        			// if the class is a subclass of Age, Eon, Epoch, Era, Period, Sub-Period, or Super-Eon, 
	        			// then add a synonym of its fragment
	        			
	        			if (c.equals(df.getOWLClass(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#GeologicTime"))))
	        			{
	        				for (OWLClassExpression cl : c.getSubClasses(manager.getOntologies()))
	        				{
	        					if (cl.getClassExpressionType().toString().equals("Class"))
	        					{
	        						for (OWLClassExpression cl_2 : ((OWLClass) cl).getSubClasses(manager.getOntologies()))
	        						{	        						
	        							if (cl_2.getClassExpressionType().toString().equals("Class"))
	    	        					{
			        						OWLClass cl_class = (OWLClass) cl_2;
			        						OWLAnnotation labelAnnotation = df.getOWLAnnotation(			
			        								df.getRDFSLabel(), df.getOWLLiteral(cl_class.getIRI().getShortForm()));
			        	        			
			        	        			OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(cl_class.getIRI(), labelAnnotation);
			                				AddAxiom addAxiom = new AddAxiom(ont, axiom);
			                				manager.applyChange(addAxiom);	        						
			                				writer.println(cl_class.getIRI().getShortForm() + " label added in class: " + getLabel(cl_class,manager, df));
	    	        					}
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

	public static void synonymsToLabels(final PrintWriter writer, final OutputStream os, final OWLOntology cinergi_ont,
			final OWLOntologyManager manager, final OWLDataFactory df) {
		
		final Set<IRI> iri = new HashSet<IRI>();
		
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
	        			for (OWLOntology ont : manager.getOntologies())
	        			{
	        				for (OWLAnnotation a : c.getAnnotations(ont))
	        				{
	        					if (a.getProperty().equals(df.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo#Synonym")))
	        						|| a.getProperty().equals(df.getOWLAnnotationProperty(IRI.create("http://ontology.neuinfo.org/NIF/Backend/OBO_annotation_properties.owl#synonym")))
	        						|| a.getProperty().equals(df.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo#Synonym"))))
	        					{
	        						OWLLiteral synonym = (OWLLiteral) a.getValue();
	        						
	        						if (getLabel(c, manager, df).equals(synonym.getLiteral()))
	        						{
	        							System.out.println("already a label for " + synonym.getLiteral());
	        							return null;
	        						}
	        						
	        						OWLAnnotation labelAnnotation = df.getOWLAnnotation(			
	        								df.getRDFSLabel(), df.getOWLLiteral(synonym.getLiteral()));
	        	        			OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), labelAnnotation);
	                				AddAxiom addAxiom = new AddAxiom(cinergi_ont, axiom);
	                				manager.applyChange(addAxiom);	        						
	                				writer.println(synonym.getLiteral() + " label added to class:" + c.getIRI().getShortForm());
	        					}
	        				}
	        			}
    				}
	        		return null;
	        	}
        	};
    	walker.walkStructure(visitor);
	}

	public static void printCinergiParentEdges(final OWLOntology extensions_ont, final PrintWriter writer5,
			final OWLOntologyManager manager, final OWLDataFactory df) {

		final Set<IRI> IRI = new HashSet<IRI>();
		
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalkerVisitor<Object> visitor = 
        		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass cls)
	        	{
	        		if (!IRI.contains(cls.getIRI()))
    				{		        			
	        			IRI.add(cls.getIRI());
	        			
	        			if (hasParentAnnotation(cls,extensions_ont,df))
	        			{
		        			for (OWLClass parents : getParentAnnotationClasses(cls, extensions_ont, df))
		        			{
		        				writer5.println(getLabel(parents, manager, df) + " " + parents.getIRI() + " > " + getLabel(cls,manager, df) + " " + cls.getIRI());
		        			}
	        			}	
	        			
    				}
	        		return null;
	        	}
        	};
        walker.walkStructure(visitor);
		
	}

	public static void printSameLabels(final PrintWriter writer, final PrintWriter filter, final PrintWriter nullPaths, OWLOntology cinergi_ont, final OWLOntologyManager manager,
		final OWLDataFactory df) throws Exception {
		final Set<IRI> iri = new HashSet<IRI>();
		final Set<IRI> nullPathClassIRI = new HashSet<IRI>();
		final Gson gson = new Gson();
		final List<String> firstLevelFacets = getFirstLevelFacets(manager, df);
		
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalkerVisitor<Object> visitor = 
    		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass cls)
	        	{
	        		if (!iri.contains(cls.getIRI()))
    				{		        			
	        			iri.add(cls.getIRI());
	        			String label = getLabelOld(cls, manager, df);
	        			//Concept[] concepts;
	        			List<Concept> concepts;
						try {
							concepts = vocabTerm(gson , label);
						} catch (UnsupportedEncodingException e) {
							return null;
						}

	        			if (concepts == null)
	        				return null;
	        			if (concepts.size() > 1)
	        			{
	        				List<Concept> filtered = new ArrayList<Concept>();	        				
	        				List<OWLClass> classes = new ArrayList<OWLClass>();
	        				for (Concept conc : concepts)
	        				{
	        					if (childOfSameLabel(conc, manager, df))
	        					{	
	        						filter.println(conc.uri + " is a child of itself. ");	        						
	        						continue;
	        					}
	        					String classPath = printSubClassPath(conc.uri, manager, df); 
	        					if (classPath != null)
	        					{
	        						boolean contains = false;
	        						for (String facet : firstLevelFacets)
        							{
	        							if (classPath.contains(facet + " >"))
	        							{
	        								filter.println(facet + " > " + conc.labels.get(0));
	        								contains = true;
	        							}
	        							if (classPath.contains("Thing >"))
	        							{
	        								filter.println("Thing > " + conc.labels.get(0));
	        								contains = true;
	        							}	        							
        							}
	        						if (!contains)
	        							filtered.add(conc); // add only the ones that get past
	        					}
	        					else // filter out classes with null class path
	        					{
	        						filter.println(conc.uri + " has a null class path.");
	        						nullPaths.println(conc.uri);
	        						nullPathClassIRI.add(IRI.create(conc.uri)); // do something with this set after execution
	        					}
	        				}	        				
	        				if (filtered.size() > 1)
	        				{	
		        				for (Concept conc : filtered)
		        				{
		        					/*	iri.add(IRI.create(conc.uri));
		        					String superClsPath = printSubClassPath(conc.uri, manager, df);
		        					if (superClsPath != null)
		        					{
		        						superClsPath += label;
		        					}
		        					writer.printf("%-30s%-100s%-100s\n", label, conc.uri, superClsPath);
		        					*/		        					
		        					classes.add(df.getOWLClass(IRI.create(conc.uri)));
		        				}	
		        				//writer.println(); 
		        				List<ClassFacetObj> classesWithFacets = new ArrayList<ClassFacetObj>();		        						        					        				
		        				for (OWLClass c : classes) // for each class in the result
		        				{
		        					int depth = 0;	
		        					ClassFacetObj temp = new ClassFacetObj(c, null, 0);
		        					if (getFacet(temp, c, manager, df, depth, firstLevelFacets) == false) // if there isn't a facet
		        					{
		        						// do something with temp; these are the classes that have no mapping essentially
		        					
		        						continue;
		        					}
		        					System.out.println(getLabelOld(c, manager, df) + "\t" + "depth = " + temp.depth);
		        					// go through facets list and check if there are any of the same ones
		        					
		        					if (classesWithFacets.isEmpty())
		        					{
        								classesWithFacets.add(new ClassFacetObj(c, temp.facet, temp.depth));
        								continue;
		        					}
		        					
		        					boolean added = false;
		        					for (ClassFacetObj cfo : classesWithFacets) // put it in the List accordingly
		        					{
		        						if (temp.facet.getIRI().equals(cfo.facet.getIRI())) // same facet
        								{
		        							if (temp.depth > cfo.depth) // greater depth
		        							{
		        								classesWithFacets.remove(cfo);	// replace the old one
		        								classesWithFacets.add(new ClassFacetObj(c, temp.facet, temp.depth));
		        								added = true;
		        								break;
		        							}
		        							else // dont add
		        							{
		        								added = false;
		        								filter.println(getLabelOld(c, manager, df) + " -> " + getLabel(temp.facet,manager, df) + " exluded");
		        							}
        								}		        					
		        					}
		        					if (!added)
		        						classesWithFacets.add(new ClassFacetObj(c, temp.facet, temp.depth));
		        					
		        				}
		        //				OWLClass facet = classesWithFacets.get(0).facet; // first facet of first
		        				for (int i = 1; i < classesWithFacets.size(); i++)
		        				{
		        					if (classesWithFacets.get(i).facet.getIRI().equals(classesWithFacets.get(0).facet.getIRI())) // same facet as first
		        					{
		        						
		        						int count_i = 0;
		        						int count_0 = 0;
		        						switch (classesWithFacets.get(i).facet.getIRI().toString().substring(0,  20)) {
		        							case "http://purl.obolibra" : count_i += 3;
		        								break;
		        							case "http://sweet.jpl.nas" : count_i += 2;
		        								break;
		        							case "http://yago-knowledg" : count_i += 1;
		        								break;       									        								
		        						}
		        						switch (classesWithFacets.get(0).facet.getIRI().toString().substring(0,  20)) {
		        							case "http://purl.obolibra" : count_i += 3;
		        								break;
		        							case "http://sweet.jpl.nas" : count_i += 2;
		        								break;
		        							case "http://yago-knowledg" : count_i += 1;
		        								break;       									        								
		        						}
		        						if (count_i >= count_0)
		        							classesWithFacets.remove(0);
		        						else
		        							classesWithFacets.remove(i);
		        						i--;
		        					}
		        				}
		        				
		        				if (classesWithFacets.size() > 1)
		        				{
		        					for (ClassFacetObj cfo : classesWithFacets)		        				
			        				{
			        					String lbl = getLabelOld(cfo.cls, manager, df);
		        						String facetLabel = getLabel(cfo.facet, manager, df);
			        					writer.printf("%-30s%-100s%-25s%-100s\n", lbl, cfo.cls.getIRI(), facetLabel, 
			        							printSubClassPath(cfo.cls.getIRI().toString(), manager, df) + 
			        							" " + getLabelOld(cfo.cls, manager, df));
			        				}
		        					writer.println();
		        				}		        				
	        				}
	        			} 
	        			else
	        			{
	        				iri.add(IRI.create(concepts.get(0).uri)); // concepts to be used
	        			//	iri.add(IRI.create(concepts[0].uri));
	        				System.err.println("only one term, adding iri: " + concepts.get(0).uri);
	        			//	System.err.println("only one term, adding iri: " + concepts[0].uri);
	        			}
    				}
	        		return null;
	        	}

				private boolean childOfSameLabel(Concept conc, OWLOntologyManager manager, OWLDataFactory df) {
					
					OWLClass cls = df.getOWLClass(IRI.create(conc.uri));
					if (cls.equals(null)) return false;
					for (OWLClassExpression superClass : cls.getSuperClasses(manager.getOntologies()))
					{
						for (OWLClass w : superClass.getClassesInSignature())
						{
							if (getLabel(w, manager, df).equals(getLabel(cls, manager, df)))
							{
								return true;
							}
						}
					}
					return false;
				}

        	};
        walker.walkStructure(visitor);
	}
	
	// gets the top level facet encountered through subClassOf traversal or cinergiParent traversal
	public static boolean getFacet(ClassFacetObj cfo, OWLClass cls, OWLOntologyManager manager, OWLDataFactory df, int depth, List<String> topLevel)
	{
		System.err.println(cls.getIRI());
		System.err.println(cfo.cls.getIRI());
		depth = depth + 1;
		if (cls.getIRI().equals("http://www.w3.org/2002/07/owl#Thing"))
			return false;
	
		/*
		if (hasCinergiFacet(cls, manager, df))
		{
			cfo.facet = cls;
			cfo.depth = depth;
			System.out.println(getLabel(cfo.cls, manager, df) + "\t" + getLabel(cfo.facet, manager, df) + "\tdepth = " + cfo.depth);
			return true;
		}
		*/
		String label = getLabel(cls, manager, df);
		if (topLevel.contains(label)) // check if the class is one of the top level facets
		{
			cfo.facet = cls;
			cfo.depth = depth;
			return true;
		}
		
		
		
		for (OWLOntology ont : manager.getOntologies()) // check if the class has as cinergiParent
		{
			if (hasParentAnnotation(cls,ont,df))
			{		        
				
				cfo.facet = getParentAnnotationClass(cls, ont, df);
				cfo.depth = depth;
				if (!topLevel.contains(label)) // the facet is not a top level one
				{
					return getFacet( cfo, cfo.facet, manager, df, depth, topLevel);
				}
				
				System.out.println(getLabel(cfo.cls, manager, df) + "\t" + getLabel(cfo.facet, manager, df) + "\tdepth = " + cfo.depth);
				return true;
			}	
		}
			
		if (cls.getSuperClasses(manager.getOntologies()).isEmpty())
		{
			return false;
		}
		
		for (OWLClassExpression oce : cls.getSuperClasses(manager.getOntologies())) // subClassOf
		{
			if (oce.getClassExpressionType().toString().equals("Class"))
			{	
				OWLClass cl = oce.getClassesInSignature().iterator().next();
				{
				if (getLabelOld(cl, manager, df).equals(getLabelOld(cls, manager, df)))
					continue; // skip if child of the same class
				}
				System.err.println("calling method");
				return getFacet( cfo, oce.getClassesInSignature().iterator().next(), manager, df, depth, topLevel);
			}
		}
		
	/*	for (OWLClassExpression oce : cls.getEquivalentClasses(manager.getOntologies())) // equivalencies
		{
			if (oce.getClassExpressionType().toString().equals("Class"))
			{
				getFacet( cfo, (OWLClass)oce, manager, df, depth);
			}	 
		}
		*/
		return false;
	}
	
	public static String printSubClassPath(String uri, OWLOntologyManager manager, OWLDataFactory df) {
		OWLClass cls = df.getOWLClass(IRI.create(uri));
		if (cls == null)
			return null;
		for (OWLClassExpression superClass : cls.getSuperClasses(manager.getOntologies()))
		{
			for (OWLClass w : superClass.getClassesInSignature())
			{
				return getLabel(w, manager, df) + " > ";
			}
		}
		return null;
	}
	
	public static List<Concept> vocabTerm(Gson gson, String input) throws UnsupportedEncodingException
	{
		String prefix = "http://tikki.neuinfo.org:9000/scigraph/vocabulary/term/";
		String suffix = "?limit=20&searchSynonyms=true&searchAbbreviations=false&searchAcronyms=false";
		String urlInput = URLEncoder.encode(input, StandardCharsets.UTF_8.name()).replace("+", "%20");
	
		String urlOut;
		System.out.println(prefix+urlInput+suffix);
		try {
			urlOut = readUrl(prefix+urlInput+suffix);	
		} catch (Exception e) {
			return null;
		}
/*		Vocab voc = gson.fromJson(urlOut, Vocab.class);
		List<Concept> concepts = voc.concepts;
		//Concept[] concepts = gson.fromJson(urlOut, Concept[].class);
		
		return concepts;
*/		
		Concept[] concepts = gson.fromJson(urlOut, Concept[].class);
		//Vocab vocab = gson.fromJson(urlOut, Vocab.class);
		ArrayList<Concept> conceptList = new ArrayList<Concept>(Arrays.asList(concepts));
		Vocab vocab = new Vocab(conceptList);
		// preliminary check
		return vocab.concepts;
	}
	
	public static String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 
	        System.out.println(buffer.toString());
	        return buffer.toString();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}
	
	public static void printUnassignedTerms(final PrintWriter writer, final OWLOntology ont,
			final OWLOntologyManager manager, final OWLDataFactory df) {
		
		//OWLClass Feature = df.getOWLClass(IRI.create("http://sweet.jpl.nasa.gov/2.3/realm.owl#Realm"));
		
		List<OWLClass> topLevelFacets = getTopLevelFacets(manager, df);
		
		for (OWLClass topLevelClass : topLevelFacets)
		{
			writer.println(getLabel(topLevelClass, manager, df));
			System.out.println(getLabel(topLevelClass,manager,df));
			for (OWLClassExpression subClassExpression : topLevelClass.getSubClasses(manager.getOntologies()))
			{
				boolean hasFacetFlag = false; // flag if the class needs to be classified.
				if (subClassExpression.getClassExpressionType().toString().equals("Class"))
				{
				
					OWLClass subClass = subClassExpression.getClassesInSignature().iterator().next();
				
					if (hasCinergiFacet(subClass,  manager,  df))
					{		
						hasFacetFlag = true;
						continue;
					}
					if (!subClass.getEquivalentClasses(manager.getOntologies()).isEmpty()) // if it has equivalent classes
					{
						System.err.println(getLabel(subClass, manager, df));
						for (OWLClassExpression oce : subClass.getEquivalentClasses(manager.getOntologies()))
						{
							if (oce.getClassExpressionType().toString().equals("Class"))
							{
								OWLClass equivalentClass = oce.getClassesInSignature().iterator().next();
								System.err.println("\t\t" + getLabel(equivalentClass, manager, df));
								if (hasCinergiFacet(equivalentClass, manager, df))
								{
									hasFacetFlag = true;
								}
							}				
						}						
					}	
					if (!hasFacetFlag)
					{
						writer.println("\t" + getLabel(subClass, manager, df));
					}
				}
			}
			writer.println("\n");
		}		
	}
      public static List<OWLClass> getTopLevelFacets(final OWLOntologyManager manager, final OWLDataFactory df) {
		
		final ArrayList<OWLClass> topLevelClasses = new ArrayList<OWLClass>();
		final Set<IRI> iri = new HashSet<IRI>();
		
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
	        			if (isTopLevelFacet(c, manager, df))
	        			{
	        				topLevelClasses.add(c);
	        			}       					        		
		        	}
	        		return null;
	        	}
        	};
			walker.walkStructure(visitor);
					
			return topLevelClasses;	
		
	}

	public static void printEquivalentFacets(final PrintWriter writer, final OWLOntology ont,
			final OWLOntologyManager manager, final OWLDataFactory df) {
		
		final Set<IRI> iri = new HashSet<IRI>();
		
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
	        			if (!hasCinergiFacet(c, manager, df))
        				{
		        			for (OWLClassExpression oce : c.getEquivalentClasses(manager.getOntologies()))
		        			{
		        				if (oce.getClassExpressionType().toString().equals("Class"))
		        				{	
		        					OWLClass equiCl = oce.getClassesInSignature().iterator().next();
		        					
		        					if (hasCinergiFacet(equiCl, manager, df))
		        					{
		        						writer.println(getLabel(c, manager, df) + " " + c.getIRI());
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
	
	public static boolean isTopLevelFacet(final OWLClass c, final OWLOntologyManager m, final OWLDataFactory df)
	{
		for (OWLOntology o : m.getOntologies())
		if (hasParentAnnotation( c, o, df))
		{
			for (OWLClass cl : getParentAnnotationClasses(c, o, df) )
			{
				if (cl.getIRI().equals(IRI.create("http://www.w3.org/2002/07/owl#Thing")))
				{	
					if (hasCinergiFacet(c, m, df))						
						return true;					
				}
			}
		}
		return false;
	}

	public static void printTermsNotInTheOntology(PrintWriter writer, PrintWriter writer2) throws FileNotFoundException, IOException {
		
		final Gson gson = new Gson();
		
		try(BufferedReader br = new BufferedReader(new FileReader("earthchem.txt"))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	String[] splices = line.split(",");
		    	for (String splice : splices)
		    	{
		    		System.out.print(splice.trim());
		    		if	(vocabTerm(gson, splice.trim()) != null)
		    		{
		    			writer.println(splice.trim());
		    		}
		    		else
		    		{
		    			writer2.println(splice.trim());
		    		}
		    	}
		    	System.out.println();
	    	}
		}
	}

	public static void addToOntology(final PrintWriter writer, final OWLOntology ont, final OWLOntologyManager manager,
			final OWLDataFactory df) {
		
		final Gson gson = new Gson();

		try {
			
			JsonReader reader = new JsonReader(new FileReader("ontology.json"));
			ELSSTClass[] classes = gson.fromJson(reader, ELSSTClass[].class);
			
			OWLClass Thing = df.getOWLClass(IRI.create("http://www.w3.org/2002/07/owl#Thing"));
			for (ELSSTClass cls : classes)
			{
				OWLClass owlcls = df.getOWLClass(IRI.create(cls.getLink()));
				
				if (cls.getParents()[0].equals("none")) // no parent; should be under Thing
				{
					OWLAxiom axiom = df.getOWLSubClassOfAxiom(owlcls, Thing); 
					AddAxiom addAxiom = new AddAxiom(ont, axiom);
					manager.applyChange(addAxiom);
				}
				else 
				{
					for (String parent : cls.getParents()) // has parent(s)
					{
						OWLAxiom axiom = df.getOWLSubClassOfAxiom(owlcls, 
								df.getOWLClass(IRI.create(parent)));
						AddAxiom addAxiom = new AddAxiom(ont, axiom);
						manager.applyChange(addAxiom);
					}
				}				
				// add annotations to the class
				OWLAnnotation labelAnnotation = df.getOWLAnnotation(df.getRDFSLabel(), 
						df.getOWLLiteral(cls.getKeyword()));
				OWLAxiom labelAxiom = df.getOWLAnnotationAssertionAxiom(
						owlcls.getIRI(), labelAnnotation);			
				manager.applyChange(new AddAxiom(ont, labelAxiom));
				writer.println(cls.getKeyword());
				
				if (!cls.getKeyword().equals(cls.getPreferredTerm())) // if the preferredTerm is different than the term
				{
					// add a preferredTerm annotation
					OWLAnnotation preferredTermAnnotation = df.getOWLAnnotation(df.getOWLAnnotationProperty(IRI.create("#preferredTerm")), 
							df.getOWLLiteral(cls.getPreferredTerm()));
					OWLAxiom preferredTermAxiom = df.getOWLAnnotationAssertionAxiom(
							owlcls.getIRI(), preferredTermAnnotation);			
					manager.applyChange(new AddAxiom(ont, preferredTermAxiom));
					writer.println(cls.getPreferredTerm());
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}		
	}

public static void secondLevelFacetPrint(final OWLOntologyManager manager, final OWLDataFactory df, final PrintWriter writer) {
	
	final List<OWLClass> topLevelClasses = Tester.getTopLevelFacets(manager, df);

	final Set<IRI> iri = new HashSet<IRI>();
	
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
        			if (topLevelClasses.contains(c))
        			{
        				System.out.println(c.getIRI()+ " is a 1st level");
        			}
        			else if (hasCinergiFacet(c, manager, df)){
        				writer.println(getLabel(c, manager, df) + "\t" + c.getIRI().getNamespace());
        			}
	        	}
        		return null;
        	}
    	};
		walker.walkStructure(visitor);
				
		
	}
}
	

