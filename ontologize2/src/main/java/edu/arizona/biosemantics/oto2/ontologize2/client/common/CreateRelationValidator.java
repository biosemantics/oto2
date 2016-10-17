package edu.arizona.biosemantics.oto2.ontologize2.client.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ShowRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.Images;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.PartsGrid;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class CreateRelationValidator {

	private EventBus eventBus;
	private Images images = GWT.create(Images.class);

	public CreateRelationValidator(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	public void validateAndFire(CreateRelationEvent event) {
		validateClosed(event);
		
	}

	private void validateClosed(CreateRelationEvent event) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		for(final Edge r : event.getRelations()) {
			if(g.isClosedRelations(r.getSrc(), r.getType())) {
				Alerter.showAlert("Create Relation", "Can not create relation for a closed row.");
			}
			
			switch(r.getType()) {
			case PART_OF:
			case SUBCLASS_OF:
				validateSynonymDest(r);
				break;
			case SYNONYM_OF:
				validateType(r);
				break;
			}
		}
	}

	private void validateSynonymDest(final Edge r) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		if(g.isSynonym(r.getDest())) {
			final Vertex preferred = g.getPreferredTerm(r.getDest());
			MessageBox box =  Alerter.showConfirm("Create " + r.getType().getTargetLabel(), "'" + r.getDest() + "' is a synonym of '" + preferred + "'. Do you "
					+ "want to continue replacing it by '" + preferred + "'?");
			box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					Edge newEdge = new Edge(r.getSrc(), preferred, r.getType(), r.getOrigin());
					validateSynonymSrc(newEdge);
				}
			});
		} else
			validateSynonymSrc(r);
	}

	private void validateSynonymSrc(final Edge r) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		if(g.isSynonym(r.getSrc())) {
			final Vertex preferred = g.getPreferredTerm(r.getSrc());
			MessageBox box = Alerter.showConfirm("Create " + r.getType().getTargetLabel(), "'" + r.getSrc() + "' is a synonym of '" + preferred + "'. Do you "
					+ "want to continue replacing it by '" + preferred + "'?");
			box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					Edge newEdge = new Edge(preferred, r.getDest(), r.getType(), r.getOrigin());
					validateType(newEdge);
				}
			});
		} else 
			validateType(r);
	}

	protected void validateType(Edge r) {
		switch(r.getType()) {
		case PART_OF:
			validatePartOfAndFire(r);
			break;
		case SUBCLASS_OF:
			validateSubclassAndFire(r);
			break;
		case SYNONYM_OF:
			validateSynonymAndFire(r);
			break;
		}
	}

	private void validateSynonymAndFire(Edge r) {
		final CompositeModifyEventForSynonymCreator compositeModifyEventForSynonymCreator = new CompositeModifyEventForSynonymCreator();		
		OntologyGraph g = ModelController.getCollection().getGraph();
		try {
			g.isValidSubclass(r);
			if(!r.getSrc().equals(g.getRoot(Type.SYNONYM_OF)) && g.isSubclassOrPart(r)) {
				final Vertex preferred = r.getSrc();
				final Vertex synonym = r.getDest();
				
				List<Edge> partRelations = g.getRelations(synonym, Type.PART_OF);
				List<Edge> subclassRelations = g.getRelations(synonym, Type.SUBCLASS_OF);
				
				List<Edge> relations = new ArrayList<Edge>(partRelations.size() + subclassRelations.size());
				for(Edge partR : partRelations) {
					if(!partR.getSrc().equals(preferred) && !partR.getDest().equals(preferred))
						relations.add(partR);
				}
				for(Edge subclassR : subclassRelations) {
					if(!subclassR.getSrc().equals(preferred) && !subclassR.getDest().equals(preferred))
						relations.add(subclassR);
				}
				if(!relations.isEmpty()) {
					final RelationSelectionDialog relationSelectionDialog = new RelationSelectionDialog(relations, preferred, synonym);
					relationSelectionDialog.show();
					relationSelectionDialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							List<Edge> reattach = relationSelectionDialog.getSelection();
							CompositeModifyEvent compositeModifyEvent = 
									compositeModifyEventForSynonymCreator.create(preferred, synonym, new HashSet<Edge>(reattach));
							eventBus.fireEvent(compositeModifyEvent);
							eventBus.fireEvent(new ShowRelationsEvent(Type.SYNONYM_OF));
						}
					});
				} else {
					CompositeModifyEvent compositeModifyEvent = 
							compositeModifyEventForSynonymCreator.create(preferred, synonym, new HashSet<Edge>(relations));
					eventBus.fireEvent(compositeModifyEvent);
					eventBus.fireEvent(new ShowRelationsEvent(Type.SYNONYM_OF));	
				}
					
				/*MessageBox box = Alerter.showYesNoCancelConfirm("Create synonym", 
						"You want to create the synonym \'" + synonym + "\' to preferred term \'" + preferred + "\'. "
								+ "Do you want to re-attach all subclass and part relations from " + synonym + " to " + preferred + " (YES or NO). "
										+ "Or do you want to cancel the synonym creation (CANCEL)?");
				box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						CompositeModifyEvent compositeModifyEvent = compositeModifyEventForSynonymCreator.create(preferred, synonym, true);
						eventBus.fireEvent(compositeModifyEvent);
						eventBus.fireEvent(new ShowRelationsEvent(Type.SYNONYM_OF));
					}
				});
				box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						CompositeModifyEvent compositeModifyEvent = compositeModifyEventForSynonymCreator.create(preferred, synonym, false);
						eventBus.fireEvent(compositeModifyEvent);
						eventBus.fireEvent(new ShowRelationsEvent(Type.SYNONYM_OF));
					}
				});
				box.getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
					}
				});*/
			} else
				eventBus.fireEvent(new CreateRelationEvent(r));
		} catch(Exception ex) {
			final MessageBox box = Alerter.showAlert("Create synonym", ex.getMessage());
			box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					box.hide();
				}
			});
		}
	}

	private void validateSubclassAndFire(final Edge r) {
		OntologyGraph g = ModelController.getCollection().getGraph();		
		try {
			g.isValidSubclass(r);
			
			Vertex source = r.getSrc();
			Vertex dest = r.getDest();
			List<Edge> existingRelations = g.getInRelations(dest, Type.SUBCLASS_OF);
			if(existingRelations.size() == 1 && existingRelations.get(0).getSrc().equals(g.getRoot(Type.SUBCLASS_OF)))
				eventBus.fireEvent(new CreateRelationEvent(r));
			else if(!existingRelations.isEmpty()) {
				List<Vertex> existSources = new ArrayList<Vertex>(existingRelations.size());
				for(Edge exist : existingRelations) 
					existSources.add(exist.getSrc());
				final MessageBox box = Alerter.showConfirm("Create Subclass", 
						"<i>" + dest + "</i> is already a subclass of " + existingRelations.size() + " superclasses: <i>" +
								Alerter.collapseTermsAsString(existSources) + "</i>.</br></br></br>" +
								"Do you still want to make <i>" + dest + "</i> a subclass of <i>" + source + "</i>?</br></br>" +
								"If NO, please create a new term then make it a subclass of <i>" + source + "</i>.");
				box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						eventBus.fireEvent(new CreateRelationEvent(r));
						box.hide();
					}
				});
				box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						box.hide();
					}
				});
			} else {
				eventBus.fireEvent(new CreateRelationEvent(r));
			}
		} catch(Exception ex) {
			final MessageBox box = Alerter.showAlert("Create subclass", ex.getMessage());
			box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					box.hide();
				}
			});
		}
	}

	private void validatePartOfAndFire(final Edge r) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		try {
			g.isValidPartOf(r);
			
			Vertex source = r.getSrc();
			Vertex dest = r.getDest();
			List<Edge> existingRelations = g.getInRelations(dest, Type.PART_OF);
			if(!existingRelations.isEmpty()) {
				Vertex existSource = existingRelations.get(0).getSrc();				
				/*final MessageBox box = Alerter.showConfirm("Create Part", 
					"\"" + dest + "\" is already a part of \"" + existSource +  "\".</br></br></br>" + 
						"Do you agree to continue as follows: </br>" + 
						"1) Replace \"" + dest + "\" with \"" + existSource + " " + dest + "\" as part of \"" + existSource + "\"</br>" + 
						"2) Create \"" + source + " " + dest + "\" as part of \"" + source + "\"</br>" + 
						"3) Create \"" + existSource + " " + dest + "\" and \"" + source + " " + dest + "\" as subclass of \"" + dest + "\".</br></br>" +
						"If NO, please create a new term to avoid duplication of " + dest + " as a part of \"" + source + "\".");*/
				final Dialog box = new Dialog();
				box.setHeadingText("Create Part");
				box.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO);
				VerticalLayoutContainer vlc = new VerticalLayoutContainer();
				AccordionLayoutContainer alc = new AccordionLayoutContainer();
				HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
				vlc.add(new HTML(SafeHtmlUtils.fromTrustedString("We cannot add the part <i>" + dest + "</i> as is to <i>" + 
						source + "</i>. It is already a part of <i>" +  existSource +  "</i>.")), new VerticalLayoutContainer.VerticalLayoutData(-1, -1, new Margins(0, 0, 10, 0)));
				
				String explanation = "If you apply the non-specific structure pattern we will do the following for you: </br>"
						+ "1) Replace <i>" + dest + "</i> with <i>" + existSource + " " + dest + "</i> as part of <i>" + existSource + "</i></br>" + 
						"2) Make <i>" + source + " " + dest + "</i> a part of <i>" + source + "</i></br>" + 
						"3) Make <i>" + existSource + " " + dest + "</i> and <i>" + source + " " + dest + "</i> a subclass of <i>" + dest + "</i>.";
				hlc.add(new HTML(SafeHtmlUtils.fromTrustedString(
						"Do you want to apply the <b>non-specific structure pattern</b>?")), new HorizontalLayoutData(-1, -1, new Margins(0, 5, 0, 0)));
				Image image = new Image(images.help());
				SimpleContainer sc = new SimpleContainer();
				sc.add(image);
				image.setSize("20px", "20px");
				sc.setToolTip((explanation));
				hlc.add(sc);
				vlc.add(hlc);
				/*vlc.add(alc);
				
				ContentPanel cp = new ContentPanel(appearance);
			    cp.setAnimCollapse(false);
			    cp.setHeadingText("We will do the following for you");
			    cp.add(new HTML("1) Replace <i>" + dest + "</i> with <i>" + existSource + " " + dest + "</i> as part of <i>" + existSource + "</i></br>" + 
						"2) Create <i>" + source + " " + dest + "</i> as part of <i>" + source + "</i></br>" + 
						"3) Create <i>" + existSource + " " + dest + "</i> and <i>" + source + " " + dest + "</i> as subclass of <i>" + dest + "</i>."));
			    alc.add(cp);*/
			    
				box.setWidth(500);
				box.setHeight(150);
				box.setWidget(vlc);
				
				
//						final MessageBox box = Alerter.showConfirm("Create Part", 
//								"We cannot add the part <i>" + dest + "</i> as is to <i>" + source + "</i>. It is already a part of <i>" + 
//										existSource +  "</i>.</br></br>" + 
//									"Do you want to apply the <b>non-specific structure pattern</b>?");
				TextButton yesButton = box.getButton(PredefinedButton.YES);
				yesButton.setText("Apply");
				TextButton noButton = box.getButton(PredefinedButton.NO);
				noButton.setText("Do Nothing");
				yesButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						eventBus.fireEvent(new CreateRelationEvent(r));
						box.hide();
					}
				});
				noButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						box.hide();
					}
				});
				box.show();
			} else {
				eventBus.fireEvent(new CreateRelationEvent(r));
			}
		} catch(Exception ex) {
			final MessageBox box = Alerter.showAlert("Create part", ex.getMessage());
			box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					box.hide();
				}
			});
		}
	}
	
}
