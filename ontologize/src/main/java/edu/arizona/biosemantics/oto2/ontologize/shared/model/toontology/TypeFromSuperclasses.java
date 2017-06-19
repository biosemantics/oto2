package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.util.List;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;

public class TypeFromSuperclasses {

	public static Type getType(List<Superclass> superclasses) {
		for(Superclass superclass : superclasses) {
			if(superclass.hasIri() && superclass.getIri().equals(Type.ENTITY.getIRI()))
				return Type.ENTITY;
			if(superclass.hasIri() && superclass.getIri().equals(Type.QUALITY.getIRI()))
				return Type.QUALITY;
		}
		return null;
	}
	
}
