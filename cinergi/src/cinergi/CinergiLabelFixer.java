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

public class CinergiLabelFixer {

	private OutputStream os;
	private PrintWriter writer;
	private OWLOntologyManager manager;
	private	OWLOntologyManager manager2;
	private OWLOntology cinergiOntology;
	private OWLOntology oldOntology;
	private OWLDataFactory df;
	
	public CinergiLabelFixer(OutputStream os, PrintWriter writer,OWLOntologyManager manager, 
			OWLOntologyManager manager2, OWLDataFactory df, OWLOntology cinergiOntology, OWLOntology oldOntology) 
	{
		this.os = os;
		this.writer = writer;
		this.manager = manager;
		this.manager2 = manager2;
		this.cinergiOntology = cinergiOntology;
		this.oldOntology = oldOntology;
		this.df = df;
	}

	public CinergiLabelFixer(OutputStream os, PrintWriter writer, OWLOntologyManager manager,
			OWLDataFactory df, OWLOntology cinergiOntology) {
		this(os, writer, manager, null, df, cinergiOntology, null);	
	}

	public void fixLabels() 
	{
		Set<IRI> iri = new HashSet<IRI>();
		OWLOntologyWalker walker = new OWLOntologyWalker(manager.getOntologies());
        OWLOntologyWalkerVisitor<Object> visitor =
    		new OWLOntologyWalkerVisitor<Object>(walker)    
        	{
	        	@Override
	        	public Object visit(OWLClass c) // if no label, add one 
	        	{
	        		if (!iri.contains(c.getIRI()))
	        		{
	        			iri.add(c.getIRI());
	        			if (!hasLabel(c))
	        			{	        				
	        				OWLAnnotation label = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral(makeLabel(c.getIRI().getShortForm())));	    									        			
	        				OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), label);
	        				
    						manager.applyChange(new AddAxiom(cinergiOntology, axiom));
    						writer.println(((OWLLiteral)(label.getValue())).getLiteral() + " added to class: " + c.getIRI().getShortForm());
	        			}
	        			else // has a label
	        			{	
	        				boolean isInCinergi = true;
	        				OWLAnnotation label = getLabelAnnotation(c, Collections.singleton(cinergiOntology));
	        				if (label == null) // no label in cinergiOntology
	        				{
	        					isInCinergi = false;
	        					label = getLabelAnnotation(c, manager.getOntologies());
	        				}
	        				
	        				String label_str = ((OWLLiteral)label.getValue()).getLiteral();
	        				
	        				if (hasPreferredLabel(c)) // if there is a cinergiPreferredLabel
	        				{
	        					return null; // don't do anything
	        				}
	        				
	        	
	        				String new_label = processLabel(label);
	        				if (!label_str.equals(new_label))
	        				{
	        					writer.printf("%-70s\t%12s\t%-70s\n",label_str," changed to ",new_label);
	        					if (isInCinergi) // bad label in cinergi, can remove from cinergi.owl
	        					{
		        					RemoveAxiom removeAxiom = new RemoveAxiom(cinergiOntology, df.getOWLAnnotationAssertionAxiom(c.getIRI(), label));
	        						manager.applyChange(removeAxiom);  
	        					}
	        					
        						OWLAnnotation newLabelAnnotation = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral(new_label, "en"));	    									        			
    	        				OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(c.getIRI(), newLabelAnnotation);
    	        				
        						manager.applyChange(new AddAxiom(cinergiOntology, axiom));
	        					
	        					// delete label
	        					// make new label with new_label, add to ontology
	        					// writer.print
	        				}	        			
	        			}
	        		}
	        		return null;
	        	}

        	};
    	walker.walkStructure(visitor);
	}
	
	private String processLabel(OWLAnnotation a)
	{
		String str = ((OWLLiteral)a.getValue()).getLiteral();
		String newString = "";
		for (int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i);
			if (i == 0)
			{
				newString += Character.toUpperCase(c);
			}
			else if (str.charAt(i-1) == ' ')
			{
				newString += Character.toUpperCase(c);
			}
			else 
			{
				newString += c;
			}
		}
		return newString;
	}
	
	private OWLAnnotation getLabelAnnotation(OWLClass c, Set<OWLOntology> onts)
	{
		OWLAnnotation label = null;
		for (OWLOntology o : onts)
		{
			for (OWLAnnotation a : c.getAnnotations(o, df.getRDFSLabel()))
			{
				if (((OWLLiteral)a.getValue()).hasLang("en"))
				{
					label = a;
					break;  
				}
				label = a;
			}
		}
		return label;		
	}
	
	private boolean hasPreferredLabel(OWLClass c)
	{
		for (OWLOntology o : manager.getOntologies())
		{
			if (!c.getAnnotations(o, df.getOWLAnnotationProperty
						(IRI.create("http://hydro10.sdsc.edu/cinergi_ontology/cinergiExtensions.owl#cinergiPreferredLabel"))).isEmpty())
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean hasLabel(OWLClass c)
	{
		boolean has = false;
		for (OWLOntology o : manager.getOntologies())
		{
			if (c.getAnnotations(o, df.getRDFSLabel()).isEmpty() == false)
			{
				has = true;
			}				
		}
		return has;
	}
	
	private String makeLabel(String name)
	{ 
		String label = "";
		if (name.equals("pH"))
			return name;
		for (int i = 0; i < name.length(); i++)
		{
			char c = name.charAt(i);
			if (i == 0)
				label += c;
			else if (Character.isUpperCase(c))
			{
				if (Character.isUpperCase(name.charAt(i-1)))
				{
					label += c;
				} 
				else if (name.charAt(i-1) == '_' || name.charAt(i-1) == '-')
				{
					label += c;
				}
				else
					label += " " + c;
			}
			else if (Character.isDigit(c))
			{
				if (Character.isDigit(name.charAt(i-1)) || name.charAt(i-1) == '_' || name.charAt(i-1) == '-')
				{
					label += c;
				}
				else
					label += " " + c;
			}
			else if (c == '_' || c == '-') { 
				label += " "; }
			else if (c == '(' || c == '{' || c == '[')
			{ 
				boolean flag = false;
				for (int j = i; j < name.length(); j++)
				{
					if (")}]".contains(name.substring(j, j+1)))
					{
						flag = true;
						label += c;
						label += makeLabel(name.substring(i+1, j));
						i = j-1;						
					}
				}
				if (!flag)
				{
					label += c;
				}
			}
			else
				label += c;
		}
		return label;
	}
}
