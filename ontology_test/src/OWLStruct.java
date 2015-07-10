import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;


public class OWLStruct implements Iterable<OWLClass> {
	private ArrayList<OWLClass> classes;
	private ArrayList<String> labels;
	private OWLOntologyManager manager;
	private OWLDataFactory df;
	private PrintWriter writer;
	
	public OWLStruct(OWLOntologyManager m, OWLDataFactory dataf, PrintWriter writer)
	{
		df = dataf;
		manager = m;
		classes = new ArrayList<OWLClass>();
		labels = new ArrayList<String>();
		this.writer = writer;
	}
	
	public void addClass(OWLClass c) 
	{
		boolean has_label = false;
		for (OWLOntology ontol : manager.getOntologies())
		{
			if (c.getAnnotations(ontol,df.getRDFSLabel()).isEmpty() == false) // there are labels
			{				
				has_label = true;
				
				OWLAnnotation firstLabel = (OWLAnnotation)(c.getAnnotations(ontol,df.getRDFSLabel()).toArray()[0]);
				labels.add(labelToString(firstLabel));
				//writer.println(labelToString(firstLabel));
				break;
			}
		}
		if (has_label == false)
		{
			labels.add(c.getIRI().getShortForm());
		}
		classes.add(c);
	}
	
	public OWLClass findClass(OWLAnnotation label)
	{
		return findClass(labelToString(label));
	/*	if (labels.indexOf(labelToString(label)) == -1)
		{
			System.out.println("could not find class: " + labelToString(label));
			return null;
		}
		return classes.get(labels.indexOf(labelToString(label)));
	*/
	}
	
	public OWLClass findClass(String str)
	{
		for (int i = 0; i < labels.size(); i++)
		{
			if (labels.get(i).toUpperCase().equals(str.toUpperCase()))
			{
				//System.out.println(str + " is the same as " + labels.get(i));
				//writer.println(labels.get(i));
				return (classes.get(labels.indexOf(labels.get(i))));	
			}
		}
		writer.println("could not find " + str);
		return null;
	}
	
	public boolean contains(OWLClass c)
	{
		return classes.contains(c);
	}
	
	private String labelToString(OWLAnnotation l) // case sensitive
	{
		String label = l.getValue().toString();
		return label.substring(label.indexOf('"')+1,label.lastIndexOf('"'));
	}

	@Override
	public Iterator<OWLClass> iterator() 
	{
		return classes.iterator();
	}
}
