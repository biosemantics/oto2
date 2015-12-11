package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.util.List;

import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

public class SynonymSubmissionsPagingLoadResult extends PagingLoadResultBean<OntologySynonymSubmission> {
	
	protected SynonymSubmissionsPagingLoadResult() {
	}

	public SynonymSubmissionsPagingLoadResult(List<OntologySynonymSubmission> list, int totalLength, int offset) {
		super(list, totalLength, offset);
	}
}