package edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell;

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

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class AttachedTermCell extends MenuExtendedCell<String> {

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
	
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private TermsGrid termsGrid;
	protected static Templates templates = GWT.create(Templates.class);
	
	public AttachedTermCell(TermsGrid termsGrid) {
		this.termsGrid = termsGrid;
	}

	/**
	 * create context menu items for cells such as Remove items
	 */
	@Override
	protected Menu createContextMenu(int columnIndex, int rowIndex) {
		final Row row = termsGrid.getRow(rowIndex);
		final Term term = row.getAttachedTerms().get(columnIndex - 1);
		Menu menu = new Menu();
		MenuItem removeItem = new MenuItem("Remove this term");
		removeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				List<Term> remove = new LinkedList<Term>();
				remove.add(term);
				termsGrid.removeAttachedTermsFromRow(row, remove);
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
