package edu.arizona.biosemantics.oto2.oto.client.categorize.single;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent.BeforeSelectHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet.MainTermTreeNode;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.common.UncategorizeDialog;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;

public abstract class TermMenu extends Menu implements BeforeShowHandler {
	
	private EventBus eventBus;
	private Collection collection;
	private Label label;
	
	public TermMenu(EventBus eventBus, Collection collection, Label label) {
		this.eventBus = eventBus;
		this.collection = collection;
		this.label = label;
		this.addBeforeShowHandler(this);
		this.setWidth(140);
	}

	@Override
	public void onBeforeShow(BeforeShowEvent event) {
		this.clear();
		
		final List<Term> terms = getTerms();
		if(terms == null || terms.isEmpty()) {
			event.setCancelled(true);
			this.hide();
		} else {
			builtMenu(terms);
		}
			
		if(this.getWidgetCount() == 0)
			event.setCancelled(true);
	}

	public void builtMenu(List<Term> terms) {
		createMoveTo(terms);
		createCopy(terms);
		createRename(terms);
		createRemove(terms);
		createAddSynonom(terms);
		createRemoveSynonym(terms);
		createRemoveAllSynonyms(terms);
	}

	protected void createRemoveAllSynonyms(final List<Term> terms) {
		boolean showRemoveAllSynonyms = false;
		for(Term term : terms) {
			if(!label.getSynonyms(term).isEmpty()) {
				showRemoveAllSynonyms = true;
			}
		}
		if(showRemoveAllSynonyms) {
			MenuItem removeAllSynonyms = new MenuItem("Remove all Synonyms");
			this.add(removeAllSynonyms);
			removeAllSynonyms.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					
					for(Term term : terms) {
						if(label.isMainTerm(term)) {
							List<Term> oldSynonyms = label.getSynonyms(term);
							label.removeSynonymy(term, oldSynonyms);
							eventBus.fireEvent(new SynonymRemovalEvent(label, term, oldSynonyms));
						}
					}
				}
			});
		}
	}

	protected void createRemoveSynonym(final List<Term> terms) {
		if(terms.size() == 1 && label.isMainTerm(terms.get(0))) {
			final Term term = terms.iterator().next();
			if(!label.getSynonyms(term).isEmpty()) {
				Menu synonymMenu = new Menu();
				VerticalPanel verticalPanel = new VerticalPanel();
				final List<Term> toRemove = new LinkedList<Term>();
				final TextButton synonymRemoveButton = new TextButton("Remove");
				synonymRemoveButton.setEnabled(false);
				for(final Term synonymTerm : label.getSynonyms(term)) {
					CheckBox checkBox = new CheckBox();
					checkBox.setBoxLabel(synonymTerm.getTerm());
					checkBox.setValue(false);
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if(event.getValue())
								toRemove.add(synonymTerm);
							else
								toRemove.remove(synonymTerm);
							synonymRemoveButton.setEnabled(!toRemove.isEmpty());
						}
					});
					verticalPanel.add(checkBox);
				}
				
				if(verticalPanel.getWidgetCount() > 0) {
					synonymRemoveButton.addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							label.removeSynonymy(term, toRemove);
							eventBus.fireEvent(new SynonymRemovalEvent(label, term, toRemove));
							TermMenu.this.hide();
						}
					});
					verticalPanel.add(synonymRemoveButton);
					synonymMenu.add(verticalPanel);
					MenuItem removeSynonym = new MenuItem("Remove Synonym");
					removeSynonym.setSubMenu(synonymMenu);
					this.add(removeSynonym);
				}	
			}
		}
	}

	protected void createAddSynonom(final List<Term> terms) {
		if(terms.size() == 1 && label.getMainTerms().size() > 1 && 
				label.isMainTerm(terms.get(0))) {
			final Term term = terms.iterator().next();
			Menu synonymMenu = new Menu();
			
			VerticalPanel verticalPanel = new VerticalPanel();
			final List<Term> synonymTerms = new LinkedList<Term>();
			final TextButton synonymButton = new TextButton("Synonomize");
			synonymButton.setEnabled(false);
			for(final Term synonymTerm : label.getMainTerms()) {
				if(!synonymTerm.equals(term)) {
					CheckBox checkBox = new CheckBox();
					checkBox.setBoxLabel(synonymTerm.getTerm());
					checkBox.setValue(false);
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if(event.getValue())
								synonymTerms.add(synonymTerm);
							else
								synonymTerms.remove(synonymTerm);
							synonymButton.setEnabled(!synonymTerms.isEmpty());
						}
					});
					verticalPanel.add(checkBox);
				}
			}
			
			if(verticalPanel.getWidgetCount() > 0) {
				synonymButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						label.addSynonymy(term, synonymTerms);
						eventBus.fireEvent(new SynonymCreationEvent(label, term, synonymTerms));
						TermMenu.this.hide();
					}
				});
				verticalPanel.add(synonymButton);
				synonymMenu.add(verticalPanel);
				MenuItem addSynonym = new MenuItem("Add Synonym");
				addSynonym.setSubMenu(synonymMenu);
				this.add(addSynonym);
			}					
		}
	}

	protected void createRemove(final List<Term> terms) {
		MenuItem remove = new MenuItem("Remove");
		remove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for(Term term : terms) {
					List<Label> labels = collection.getLabels(term);
					if(labels.size() > 1) {
						UncategorizeDialog dialog = new UncategorizeDialog(eventBus, label, 
								term, labels);
					} else {
						label.uncategorizeTerm(term);
						eventBus.fireEvent(new TermUncategorizeEvent(term, label));
					}
				}
			}
		});
		this.add(remove);
	}

	protected void createRename(final List<Term> terms) {
		if(terms.size() == 1) {
			MenuItem rename = new MenuItem("Rename");
			final Term term = terms.iterator().next();
			rename.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					final PromptMessageBox box = new PromptMessageBox(
							"Term rename", "Please input new spelling");
					box.getButton(PredefinedButton.OK).addBeforeSelectHandler(new BeforeSelectHandler() {
						@Override
						public void onBeforeSelect(BeforeSelectEvent event) {
							if(box.getTextField().getValue().trim().isEmpty()) {
								event.setCancelled(true);
								AlertMessageBox alert = new AlertMessageBox("Empty", "Empty not allowed");
								alert.show();
							}
						}
					});
					box.getTextField().setValue(term.getTerm());
					box.addHideHandler(new HideHandler() {
						@Override
						public void onHide(HideEvent event) {
							String newName = box.getValue();
							term.setTerm(newName);
							eventBus.fireEvent(new TermRenameEvent(term));
						}
					});
					box.show();
				}
			});
			this.add(rename);
		}
	}

	protected void createCopy(final List<Term> terms) {
		if(collection.getLabels().size() > 1) {
			Menu copyMenu = new Menu();
			VerticalPanel verticalPanel = new VerticalPanel();
			final List<Label> copyLabels = new LinkedList<Label>();
			final TextButton copyButton = new TextButton("Copy");
			copyButton.setEnabled(false);
			for(final Label collectionLabel : collection.getLabels()) {
				if(!label.equals(collectionLabel) && !collectionLabel.getMainTerms().containsAll(terms)) {
					CheckBox checkBox = new CheckBox();
					checkBox.setBoxLabel(collectionLabel.getName());
					checkBox.setValue(false);
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if(event.getValue())
								copyLabels.add(collectionLabel);
							else
								copyLabels.remove(collectionLabel);
							copyButton.setEnabled(!copyLabels.isEmpty());
						}
					});
					verticalPanel.add(checkBox);
				}
			}
			if(verticalPanel.getWidgetCount() > 0) {
				copyButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						Map<Term, AddResult> addResults = new HashMap<Term, AddResult>();
						for(Label copyLabel : copyLabels) {
							Map<Term, AddResult> addResult = copyLabel.addMainTerms(terms);
							Alerter.alertNotAddedTerms(terms, addResult);
							addResults.putAll(addResult);
						}
						eventBus.fireEvent(new CategorizeCopyTermEvent(terms, label, copyLabels, addResults));
						TermMenu.this.hide();
					}
				});
				verticalPanel.add(copyButton);
				copyMenu.add(verticalPanel);
				MenuItem copy = new MenuItem("Copy to");
				copy.setSubMenu(copyMenu);
				this.add(copy);
			}
		}
	}

	protected void createMoveTo(final List<Term> terms) {
		if(collection.getLabels().size() > 1) {
			Menu moveMenu = new Menu();
			for(final Label collectionLabel : collection.getLabels())
				if(!label.equals(collectionLabel) && !collectionLabel.getMainTerms().containsAll(terms)) {
					moveMenu.add(new MenuItem(collectionLabel.getName(), new SelectionHandler<MenuItem>() {
						@Override
						public void onSelection(SelectionEvent<MenuItem> event) {
							Map<Term, AddResult> addResult = collectionLabel.addMainTerms(terms);
							Alerter.alertNotAddedTerms(terms, addResult);
							label.uncategorizeMainTerms(terms);
							eventBus.fireEvent(new CategorizeMoveTermEvent(terms, label, collectionLabel, addResult));
							TermMenu.this.hide();
						}
					}));
				}
			if(moveMenu.getWidgetCount() > 0) {
				MenuItem move = new MenuItem("Move to");
				move.setSubMenu(moveMenu);
				this.add(move);
			}
		}
	}

	public abstract List<Term> getTerms();
}