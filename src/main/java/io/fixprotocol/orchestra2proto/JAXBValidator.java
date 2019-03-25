/*
 * Copyright 2019 FIX Protocol Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

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
