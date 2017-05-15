package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.Iterator;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

public class FieldValidator {

	public boolean validate(Iterator<Widget> iterator) {
		while(iterator.hasNext()) {
			Widget widget = iterator.next();
			if(widget instanceof FieldLabel) {
				FieldLabel fieldLabel = (FieldLabel)widget;
				Widget fieldWidget = fieldLabel.getWidget();
				boolean result = validate(fieldWidget);
				if(!result)
					return false;
			} else if(widget instanceof HasWidgets.ForIsWidget) {
				boolean result = validate(((HasWidgets.ForIsWidget) widget).iterator());
				if(!result)
					return false;
			}
		}
		return true;
	}
	
	private boolean validate(Widget widget) {
		if(widget instanceof Field) {
			Field field = (Field)widget;
			boolean result = field.validate();
			if(!result)
				return false;
		}
		if(widget instanceof HasWidgets) {
			HasWidgets hasWidgets = (HasWidgets)widget;
			Iterator<Widget> it = hasWidgets.iterator();
			boolean result = true;
			while(it.hasNext()) {
				result &= validate(it.next());
				if(!result)
					return false;
			}
		}
		return true;
	}
	
}
