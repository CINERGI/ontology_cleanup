import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

public class MergeTest{
	
	public static void shouldMergeOntologies() throws OWLOntologyCreationException, OWLOntologyStorageException {
        // Just load two arbitrary ontologies for the purposes of this example
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        man.loadOntologyFromOntologyDocument(IRI.create(PIZZA_IRI));
        man.loadOntologyFromOntologyDocument(IRI.create("http://www.co-ode.org/ontologies/amino-acid/2006/05/18/amino-acid.owl"));
        // Create our ontology merger
        OWLOntologyMerger merger = new OWLOntologyMerger(man);
        // We merge all of the loaded ontologies. Since an OWLOntologyManager is
        // an OWLOntologySetProvider we just pass this in. We also need to
        // specify the URI of the new ontology that will be created.
        IRI mergedOntologyIRI = IRI.create("http://www.semanticweb.com/mymergedont");
        OWLOntology merged = merger
                .createMergedOntology(man, mergedOntologyIRI);
        // Print out the axioms in the merged ontology.
        for (OWLAxiom ax : merged.getAxioms()) {
            System.out.println(ax);
        }
        // Save to RDF/XML
        man.saveOntology(merged, new RDFXMLOntologyFormat(),
                IRI.create("file:/tmp/mergedont.owlapi"));
    }
}