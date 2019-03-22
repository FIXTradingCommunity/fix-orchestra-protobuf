package io.fixprotocol.orchestra2proto;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;

class JAXBValidator extends ValidationEventCollector {
	 @Override 
	 public boolean handleEvent(ValidationEvent event) {
	    if (event.getSeverity() == ValidationEvent.ERROR || event.getSeverity() == ValidationEvent.FATAL_ERROR) {
	        ValidationEventLocator locator = event.getLocator();
	        String msg = "XML Validation Exception: " + event.getMessage() + " at row: " +
	            	locator.getLineNumber() +
	            	" column: " + locator.getColumnNumber();
	        throw new RuntimeException(msg); 
	    } 
	    return true; 
	 } 
}
