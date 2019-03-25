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

import io.fixprotocol.orchestra2proto.protobuf.EnumField;
import io.fixprotocol.orchestra2proto.protobuf.Field;
import io.fixprotocol.orchestra2proto.protobuf.MessageField;
import io.fixprotocol.orchestra2proto.protobuf.Option;

import java.util.Comparator;

import org.apache.log4j.Logger;

class FieldComparator implements Comparator<Field> /*, Comparator<capnp_model.FieldField> */ {
	
	static Logger logger = Logger.getLogger(FieldComparator.class);
	
	public enum SortOrder {
		BY_SPEC,
		NONE
	}
	private SortOrder sortOrder;
	
	FieldComparator(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public int compare(Field f1, Field f2) {
		if(sortOrder == SortOrder.NONE)
			return 0;
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		
		if(f1 instanceof MessageField && f2 instanceof MessageField) {
			String fieldAdded = "";
			String epAdded = "";
			for(Option opt : f1.fieldOptions) {
				if(opt.name.equals("field_added")) {
					sb1.append(opt.value);
				}
				else if(opt.name.equals("field_added_ep")) {
					sb1.append(opt.value);
				}
			}
			sb1.append(rightPadding(fieldAdded,12));
			sb1.append(rightPadding(epAdded, 6));
			sb1.append(f1.fieldName);
			fieldAdded = "";
			epAdded = "";
			for(Option opt : f2.fieldOptions) {
				if(opt.name.equals("field_added")) {
					sb2.append(opt.value);
				}
				else if(opt.name.equals("field_added_ep")) {
					sb2.append(opt.value);
				}
			}
			sb2.append(rightPadding(fieldAdded,12));
			sb2.append(rightPadding(epAdded, 6));
			sb2.append(f2.fieldName);
		}
		else if(f1 instanceof EnumField && f2 instanceof EnumField) {
			String enumAdded = "";
			String epAdded = "";
			for(Option opt : f1.fieldOptions) {
				if(opt.name.equals("enum_added")) {
					sb1.append(opt.value);
				}
				else if(opt.name.equals("enum_added_ep")) {
					sb1.append(opt.value);
				}
			}
			sb1.append(rightPadding(enumAdded,12));
			sb1.append(rightPadding(epAdded, 6));
			sb1.append(f1.fieldName);
			enumAdded = "";
			epAdded = "";
			for(Option opt : f2.fieldOptions) {
				if(opt.name.equals("field_added")) {
					sb2.append(opt.value);
				}
				else if(opt.name.equals("field_added_ep")) {
					sb2.append(opt.value);
				}
			}
			sb2.append(rightPadding(enumAdded,12));
			sb2.append(rightPadding(epAdded, 6));
			sb2.append(f2.fieldName);
		}
		else {
			logger.error("Cannot compare fields: "+ f1.toString() + " " + f2.toString());
		}
		
		return sb1.toString().compareTo(sb2.toString());
	}
	
	public static String rightPadding(String str, int num) {
	    return String.format("%1$-" + num + "s", str);
	}
		
	/*
	public int compare(capnp_model.Field f1, capnp_model.Field f2) {
		return 0;
	}
	 */
	
}
