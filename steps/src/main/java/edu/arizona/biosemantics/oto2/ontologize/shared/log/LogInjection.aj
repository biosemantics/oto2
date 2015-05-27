package edu.arizona.biosemantics.oto2.ontologize.shared.log;

import edu.arizona.biosemantics.common.log.AbstractLogInjection;
import edu.arizona.biosemantics.common.log.ILoggable;

public aspect LogInjection extends AbstractLogInjection {
	
	declare parents : edu.arizona.biosemantics.oto2.ontologize.server..* implements ILoggable;
}
