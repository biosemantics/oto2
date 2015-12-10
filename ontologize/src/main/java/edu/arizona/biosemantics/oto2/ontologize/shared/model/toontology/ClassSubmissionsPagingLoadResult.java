package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.util.List;

import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

public class ClassSubmissionsPagingLoadResult extends PagingLoadResultBean<OntologyClassSubmission> {
	
	protected ClassSubmissionsPagingLoadResult() {
	}

	public ClassSubmissionsPagingLoadResult(List<OntologyClassSubmission> list, int totalLength, int offset) {
		super(list, totalLength, offset);
	}
}