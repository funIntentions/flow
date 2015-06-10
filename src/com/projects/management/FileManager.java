package com.projects.management;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Dan on 5/27/2015.
 */
class FileManager
{
    public FileManager()
    {
    }

    public OntModel readOntology(File file)
    {
        OntModel model = null;

        try
        {
            model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            InputStream in = new FileInputStream(file);
            model.read(in, null);
            in.close();
        }
        catch (Exception exception)
        {
            //TODO: Handle this exception
        }

        return model;
    }
}
