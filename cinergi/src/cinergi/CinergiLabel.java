package cinergi;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;


public class CinergiLabel {
	
	private OutputStream os;
	private PrintWriter writer;
	private OWLOntologyManager manager;
	private OWLOntology ont;
	private OWLDataFactory df;
	
	
	public CinergiLabel(OutputStream os, PrintWriter writer,
			OWLOntologyManager manager, OWLOntology ont, OWLDataFactory df) throws Exception {
	
		this.os = os;
		this.writer = writer;
		this.manager = manager;
		this.ont = ont;
		this.df = df;
	}
	
	//public void 
}
