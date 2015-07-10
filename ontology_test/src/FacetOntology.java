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
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;


	// walk ontology and make new ontology of just cinergiFacet True classes

public class FacetOntology {
	public static void createFacetOntology(OutputStream os2, OWLOntology ont, OWLDataFactory df, OWLOntologyManager manager, PrintWriter writer) throws Exception
	{
		
		OWLOntology facetOntology = manager.createOntology();
		Set<OWLClass> added = new HashSet<OWLClass>();
		
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
		OWLOntologyWalkerVisitor<Object> visitor = 
   		
			new OWLOntologyWalkerVisitor<Object>(walker)    
			{
				@Override
				public Object visit(OWLClass c) // visit all classes, if is a facet and not already visited, add it and parents to ontology
				{
					if (hasTrueCinergiFacet(c, manager) && !added.contains(c))
					{
						if (!c.getIRI().getShortForm().equals("Thing"))
						{
							OWLClass cls = df.getOWLClass(c.getIRI());
							OWLAnnotation parentAnnot = getCinergiParentAnnotation(c, manager);
							OWLClass parent = df.getOWLClass((IRI) parentAnnot.getValue());
							//writer.println(parentAnnot);
							
							OWLAnnotation clsLabel = getLabel(cls, manager, df);
							OWLAxiom clsLabelAxiom = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), clsLabel);
							AddAxiom addclsAxiom = new AddAxiom(facetOntology, clsLabelAxiom);
																		
							OWLAnnotation parentLabel = getLabel(parent, manager, df);
							OWLAxiom parentLabelAxiom = df.getOWLAnnotationAssertionAxiom(parent.getIRI(), parentLabel);
							AddAxiom addparentAxiom = new AddAxiom(facetOntology, parentLabelAxiom);		
							
							OWLAxiom subClassAxiom = df.getOWLSubClassOfAxiom(cls, parent);	
							
							manager.applyChange(addclsAxiom);
							manager.applyChange(addparentAxiom);
							manager.applyChange(new AddAxiom(facetOntology, subClassAxiom));
							
//							added.add(cls);
//							added.add(parent);
						}
					}
					return null;
				}
			};
		walker.walkStructure(visitor);
		
		manager.saveOntology(facetOntology, os2);
		
	}
	
	public static OWLAnnotation getLabel(OWLClass c, OWLOntologyManager m, OWLDataFactory df)
	{
		for (OWLOntology o : m.getOntologies())
		{
			if (c.getAnnotations(o,df.getRDFSLabel()).isEmpty() != true)
			{
				return (OWLAnnotation) (c.getAnnotations(o,df.getRDFSLabel()).toArray()[0]);
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