package edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.AttachedTermCell.Templates;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class LeadTermCell extends MenuExtendedCell<String> {
	
	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div class=\"{0}\" qtip=\"{4}\">" +
				"<div class=\"{1}\" " +
				"style=\"" +
				"width: calc(100% - 9px); " +
				"height:14px; " +
				"background: no-repeat 0 0;" +
				"background-image:{6};" +
				"background-color:{5};" +
				"\">{3}<a class=\"{2}\" style=\"height: 22px;\"></a>" +
				"</div>" +
				"</div>")
		SafeHtml cell(String grandParentStyleClass, String parentStyleClass,
				String aStyleClass, String value, String quickTipText, String colorHex, String backgroundImage);
	}
	
	private TermsGrid termsGrid;
	protected static Templates templates = GWT.create(Templates.class);
	
	public LeadTermCell(TermsGrid termsGrid) {
		this.termsGrid = termsGrid;
	}
	
	@Override
	protected Menu createContextMenu(int column, int rowIndex) {
		final Row row = termsGrid.getRow(rowIndex);
		Menu menu = new Menu();
		MenuItem removeItem = new MenuItem("Remove this row");
		removeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				Collection<Row> rowList = new LinkedList<Row>();
				rowList.add(row);
				termsGrid.removeRows(rowList);
			}
		});
		menu.add(removeItem);
		
		return menu;
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		SafeHtml rendered = templates.cell("", columnHeaderStyles.headInner(),
				columnHeaderStyles.headButton(), value, "", "", "");
		sb.append(rendered);
	}

}
