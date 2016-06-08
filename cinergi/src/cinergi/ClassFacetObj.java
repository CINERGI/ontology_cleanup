package cinergi;

import org.semanticweb.owlapi.model.OWLClass;

public class ClassFacetObj {
	public OWLClass cls;
	public OWLClass facet;
	public int depth;
	
	public ClassFacetObj(OWLClass cls, OWLClass facet, int depth)
	{
		this.cls = cls;
		this.facet = facet;
		this.depth = depth;
	}
}
