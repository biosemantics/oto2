package edu.arizona.biosemantics.oto2.steps.shared.model.toontology;

public enum Status {

	ACCEPTED("accepted"), PENDING("pending"), REJECTED("rejected");
	
	private String displayName;

	private Status(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	
	
}
