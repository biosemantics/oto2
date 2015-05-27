package edu.arizona.biosemantics.oto2.ontologize.client.toontology;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ImageResource;

public interface TermStatusImages extends ClientBundle {
	
	@Source("green.png")
	ImageResource green();
	
	@Source("yellow.png")
	ImageResource yellow();
	
	@Source("orange.png")
	ImageResource orange();
	
}