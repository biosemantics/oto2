package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

public class RelationsView extends SimpleContainer {

	private EventBus eventBus;

	public RelationsView(final EventBus eventBus) {
		this.eventBus = eventBus;
		
		final SubclassesGrid subclassGrid = new SubclassesGrid(eventBus);
		final PartsGrid partsGrid = new PartsGrid(eventBus);
		final SynonymsGrid synonymGrid = new SynonymsGrid(eventBus);
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(subclassGrid.asWidget(), new VerticalLayoutData(1, 0.33));
		vlc.add(partsGrid.asWidget(), new VerticalLayoutData(1, 0.33));
		vlc.add(synonymGrid.asWidget(), new VerticalLayoutData(1, 0.33));
		/*VerticalLayoutContainer superContainer = new VerticalLayoutContainer();
		HorizontalLayoutContainer superHead = new HorizontalLayoutContainer();
		superHead.add(new Label("Is-A relations"));
		TextButton importSuperButton = new TextButton("Import");
		importSuperButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Import Is-A", "");
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						String input = box.getValue();
						String[] lines = input.split("\\n");
						for(String line : lines) {
							String[] terms = line.split(",");
							Row row = new Row(new Term(terms[0]));
							for(int i=1; i<terms.length; i++)
								row.terms.add(new Term(terms[i]));
							superclassGrid.addRow(row);
						}
					}
				});
				box.show();
			}
		});
		superHead.add(importSuperButton);
		superContainer.add(superHead, new VerticalLayoutData(1, 30));
		superContainer.add(superclassGrid);
		vlc.add(superContainer, new VerticalLayoutData(1, 0.33));
		VerticalLayoutContainer partContainer = new VerticalLayoutContainer();
		HorizontalLayoutContainer partHead = new HorizontalLayoutContainer();
		partHead.add(new Label("Part relations"));
		TextButton importPartButton = new TextButton("Import");
		importPartButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Import Parts", "");
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						String input = box.getValue();
						String[] lines = input.split("\\n");
						for(String line : lines) {
							String[] terms = line.split(",");
							Row row = new Row(new Term(terms[0]));
							for(int i=1; i<terms.length; i++)
								row.terms.add(new Term(terms[i]));
							partsGrid.addRow(row);
						}
					}
				});
				box.show();
			}
		});
		partHead.add(importPartButton);
		partContainer.add(partHead, new VerticalLayoutData(1, 30));
		partContainer.add(partsGrid);
		vlc.add(partContainer, new VerticalLayoutData(1, 0.33));
		VerticalLayoutContainer synonymContainer = new VerticalLayoutContainer();
		HorizontalLayoutContainer synonymHead = new HorizontalLayoutContainer();
		synonymHead.add(new Label("Synonym relations"));
		TextButton importSynonymButton = new TextButton("Import");
		importSynonymButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Import Synonyms", "");
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						String input = box.getValue();
						String[] lines = input.split("\\n");
						for(String line : lines) {
							String[] terms = line.split(",");
							Row row = new Row(new Term(terms[0]));
							for(int i=1; i<terms.length; i++)
								row.terms.add(new Term(terms[i]));
							synonymGrid.addRow(row);
						}
					}
				});
				box.show();
			}
		});
		synonymHead.add(importSynonymButton);
		synonymContainer.add(synonymHead, new VerticalLayoutData(1, 30));
		synonymContainer.add(synonymGrid);
		vlc.add(synonymContainer, new VerticalLayoutData(1, 0.33));*/
		this.add(vlc);
	}

}
