package edu.arizona.biosemantics.oto2.oto.client.categorize;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;

public class HelpView implements IsWidget {

	@Override
	public Widget asWidget() {
		TabPanel panel = new TabPanel();
		panel.setBorders(false);

		Label label1 = new Label("Help1");
		label1.addStyleName("pad-text");

		Label label2 = new Label("Help2");
		label2.addStyleName("pad-text");

		panel.add(label1, new TabItemConfig("Help1"));
		panel.add(label2, new TabItemConfig("Help2"));
		return panel;
	}

}
