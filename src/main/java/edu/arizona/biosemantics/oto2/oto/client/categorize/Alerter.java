package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.List;
import java.util.Map;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.model.Label.AddResult;

public class Alerter {

	public static void alertNotAddedTerms(List<Term> possibleMainTerms, Map<Term, AddResult> addResults) {
		for(Term possibleMainTerm : possibleMainTerms) {
			AddResult addResult = addResults.get(possibleMainTerm);
			if(!addResult.result) {
				if(addResult.parent != null) {
					AlertMessageBox alert = new AlertMessageBox("Already contained", possibleMainTerm.getTerm() + " " +
							"already contained as synonym of " + addResult.parent.getTerm() + ".");
					alert.show();
				} else {
					AlertMessageBox alert = new AlertMessageBox("Already contained", possibleMainTerm.getTerm() + " " +
							"already contained.");
					alert.show();
				}
			}
		}
	}

}
