package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

public enum StatusEnum {

	ACCEPTED("accepted"), PENDING("pending"), REJECTED("rejected"), NEW("new");
	
	private String displayName;

	private StatusEnum(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	
	
}
