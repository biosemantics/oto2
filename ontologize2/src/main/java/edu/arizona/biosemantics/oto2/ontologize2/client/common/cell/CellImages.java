package edu.arizona.biosemantics.oto2.ontologize2.client.common.cell;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ImageResource;

public interface CellImages extends ClientBundle {

	@Source("blue.gif")
	ImageResource blue();

	@Source("red.gif")
	ImageResource red();
	
	@Source("green.gif")
	ImageResource green();
	

	@Source("black.gif")
	ImageResource black();
}