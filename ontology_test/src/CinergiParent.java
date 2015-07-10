import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;


public class CinergiParent {

	public static void addParents(OutputStream os, PrintWriter writer,
			OWLOntologyManager manager, OWLOntology ont, OWLDataFactory df) {
		
	    OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
	    HashSet<OWLClass> classes = new HashSet<OWLClass>();
       
	    
        OWLOntologyWalkerVisitor<Object> visitor = 
        
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c)//(OWLObjectSomeValuesFrom desc)
	        	{
	        		if (c.getIRI().getShortForm().equals("Thing"))
	        		{
	        			return null;
	        		}
	        		if (!classes.contains(c))	        
	        			classes.add(c);
	        		return null;	
	        	}
        	};
        walker.walkStructure(visitor);
        
        for (OWLClass c : classes)
        {
        	// if c is a cinergifacet true, if it does not have a cinergiParent, check its superclasses. if none of them are 
        	// a cinergiFacet, then make cinergiparent Thing
        	if (hasTrueCinergiFacet(c, manager))
        	{
        		if (!hasCinergiParentAnnot(c, manager))
        		{
        			boolean superClassIsFacet = false;
        			for (OWLClassExpression superExpression : c.getSuperClasses(manager.getOntologies()))
        			{
        				for (OWLClass cl : superExpression.getClassesInSignature())
        				{
        					if (hasTrueCinergiFacet(cl, manager))
        					{
        						superClassIsFacet = true;
        						addCinergiParent(c, cl.getIRI(), df, manager, ont);
        						writer.println("cinergiParent " + cl + "   added to Class " + c);
        					}
        				}   
        			}
        			if (superClassIsFacet == false) // if none of c's super classes is a facet, create one for thing
        			{
    					addCinergiParent(c, IRI.create("http://www.w3.org/2002/07/owl#Thing"), df, manager, ont);
    					writer.println("cinergiParent Thing    added to Class " + c);
    				}
        		}
        	}
        }	
	}
	
	public static void makeCinergiParentsBasedOnFacetFile(OutputStream os, PrintWriter writer, OWLOntologyManager manager,
			OWLOntologyManager manager2, OWLOntology ont, OWLOntology ont_facets, OWLDataFactory df)
	{	
        OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalker walker2 = new OWLOntologyWalker(manager2.getOntologies());
        
        OWLStruct ostruct = new OWLStruct(manager, df, writer);
        OWLStruct ostruct_nf = new OWLStruct(manager, df, writer);
        OWLStruct ostruct_facets = new OWLStruct(manager2, df, writer);
        
        
        OWLOntologyWalkerVisitor<Object> visitor2 = 
        		
        		new OWLOntologyWalkerVisitor<Object>(walker2)    
            	{
    	        	@Override
    	        	public Object visit(OWLClass c) // visit all classes, give each of them the cinergiFacet property and set to false
    	        	{
    	        		if (!ostruct_facets.contains(c)) 
    	        			ostruct_facets.addClass(c);
    	        		return null;
    	        	}
            	}; 
            	
            System.out.println("walking facets");
        	walker2.walkStructure(visitor2);
        
        OWLOntologyWalkerVisitor<Object> visitor = 
        		
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c) // visit all classes, give each of them the cinergiFacet property and set to false
	        	{
	        		if (hasTrueCinergiFacet(c, manager))
    				{
	        			if (!ostruct.contains(c))
	        			{
	        				ostruct.addClass(c);
	        			}
    				}
	        		else
	        		{
	        			ostruct_nf.addClass(c);
	        		}
	        		return null;
	        	}
        	}; 
    	System.out.println("walking cinergi");
    	walker.walkStructure(visitor);
    	
    	
    	// if c has super class find relative class 
		// make parent relationship for that relative class
    	for (OWLClass c : ostruct_facets) // fix incorrect facets according to facets file
    	{ // .getClassesInSignature()
    		
			OWLClass relative;    // = ostruct.findClass(label); // gets class with that label			
			OWLAnnotation label = (OWLAnnotation) c.getAnnotations(ont_facets,df.getRDFSLabel()).toArray()[0]; //get first label			
			relative = ostruct.findClass(label) != null ? ostruct.findClass(label) : ostruct_nf.findClass(label);			
			
			if (relative == null)
			{
				writer.println(label + "not in either");
			}
			else
			{
				if (hasCinergiFacet(relative,manager))
				{
					OWLAnnotation facet = getCinergiFacetAnnotation(relative, manager);
					writer.println("removing incorrect cinergiFacet for: " + label);
					RemoveAxiom removeAxiom = new RemoveAxiom(ont, df.getOWLAnnotationAssertionAxiom(relative.getIRI(), facet));
					manager.applyChange(removeAxiom); 
				}
				// add facet
				CinergiFacet.addFacet(writer, ont, manager, relative, df);
			}
	// ---------- add cinergiParent  ---------
			if (c.getIRI().toString().equals("http://www.w3.org/2002/07/owl#Thing"))
				continue;
			
			OWLClass superClass = null; 
			OWLClass superRelative;
			OWLAnnotation superLabel;
			
			for (OWLClassExpression superExpression : c.getSuperClasses(ont_facets))
			{
				for (OWLClass cl : superExpression.getClassesInSignature())
					superClass = cl;
			}
			
			if (superClass == null)
			{
				//System.out.println(label + " doesnt have a super class"); this is working
				superClass = df.getOWLClass(IRI.create("http://www.w3.org/2002/07/owl#Thing")); // if c doesnt have a super class, make it thing
			}
			superLabel = (OWLAnnotation) superClass.getAnnotations(ont_facets,df.getRDFSLabel()).toArray()[0]; //get first label
			writer.println(superLabel);
			superRelative = ostruct.findClass(superLabel) != null ? ostruct.findClass(superLabel) : ostruct_nf.findClass(superLabel);
		//	superRelative = ostruct.findClass(superLabel) == null ? ostruct_nf.findClass(superLabel) : null;
			
			if (superRelative == null) 
			{
				System.out.println("not found ~~ " + label);
				addCinergiParent(relative, IRI.create("http://www.w3.org/2002/07/owl#Thing"), df, manager, ont);
			}
			else // found class in cinergi
			{
				if (hasCinergiFacet(superRelative, manager))
				{
					OWLAnnotation parentAnnot = getCinergiParentAnnotation(relative, manager);
					if (parentAnnot != null)
					{
						// remove
						RemoveAxiom removeAxiom = new RemoveAxiom(ont, df.getOWLAnnotationAssertionAxiom(relative.getIRI(), parentAnnot));
						manager.applyChange(removeAxiom); // remove false cinergi facet
					}
					addCinergiParent(relative, superRelative.getIRI(), df, manager, ont);
					// make cinergi parent of superRelative
				}
				else
				{
					addCinergiParent(relative, IRI.create("http://www.w3.org/2002/07/owl#Thing"), df, manager, ont);
					// make cinergi parent of thing
				}
	    	}
    	}
	}   		
	
	public static boolean hasCinergiFacet(OWLClass c, OWLOntologyManager m)
	{
		boolean has_facet = false;
		for (OWLOntology o : m.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(o))
			{
				if (a.getProperty().getIRI().toString().equals("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiFacet"))
				{
					has_facet = true;
					break;
				}
			}
		}
		return has_facet;
	}
	
	public static OWLAnnotation getCinergiFacetAnnotation(OWLClass c, OWLOntologyManager m)
	{
		for (OWLOntology o : m.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(o))
			{
				if (a.getProperty().getIRI().toString().equals("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiFacet"))
				{
					return a;
				}
			}
		}
		return null;
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
	
	public static void addCinergiParent(OWLClass c, IRI clIRI, OWLDataFactory df, OWLOntologyManager manager, OWLOntology ont)
	{ // creates a cinergiParent annotation in class c of IRI clIRI
		OWLAnnotation cinergiParentAnnot = df.getOWLAnnotation(			
				df.getOWLAnnotationProperty(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiParent")),
						clIRI);
		OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), cinergiParentAnnot);
		manager.applyChange(new AddAxiom(ont, axiom));
	}
	
	public static boolean hasCinergiParentAnnot(OWLClass c, OWLOntologyManager manager)
	{
		boolean has = false;
		for (OWLOntology ont : manager.getOntologies())
		{
			for (OWLAnnotation a : c.getAnnotations(ont)) // only checking in ont because thats only where they are defined
			{	
				if (a.getProperty().getIRI().toString().equals("http://hydro10.sdsc.edu/cinergi_ontology/cinergi.owl#cinergiParent"))
				{
					has = true;
				}
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

}
