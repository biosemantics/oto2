package edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology;

public class CreateSynonymSubmissionException extends Exception {
	public CreateSynonymSubmissionException() { }
	
	public CreateSynonymSubmissionException(String message) {
        super(message);
    }
	
	public CreateSynonymSubmissionException(String message, Throwable cause) {
        super(message);
    }
	
	public CreateSynonymSubmissionException(Throwable cause) {
		super(cause);
	}
}
