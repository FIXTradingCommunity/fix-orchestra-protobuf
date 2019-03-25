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

package io.fixprotocol.orchestra2proto.protobuf;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Message {
	public String name;
	public String homePackage;
	public List<Option> options;
	public List<MessageField> fields;
	public List<Message> nestedMessages;
	public Map<String, List<MessageField>> nestedOneOfs; // elements in this list must refer to fields in the fields list
	
	public Message() {
		options = new ArrayList<Option>();
		fields = new ArrayList<MessageField>();
		nestedOneOfs = new HashMap<String, List<MessageField>>();
	}
	
	@Override
	public String toString() { return toString(ProtobufModel.Syntax.PROTO3); }
	
	public String toString(ProtobufModel.Syntax syntax) {
		final String Indent = "\t";
		StringBuilder sb = new StringBuilder();
		sb.append("message ");
		sb.append(name);
		sb.append(" {\n");
		for(Option option : options) {
			sb.append("\toption ");
			sb.append(option.toString()).append(";");
			sb.append("\n");
		}
		/*
		 * As we iterate through the fields we need to determine whether each field is a member
		 * of a nestedOneOf. If so, we will write all the fields of that oneof together. We must
		 * also keep track of which ones have already been written so we do not duplicate.
		 * Non-members are written as an individual field. There may be better ways to go about
		 * this, but for now it will do.
		 */
		List<String> processedUnions = new ArrayList<String>(); // Use to track whether already written.
		for(MessageField field : fields) {
			boolean oneOfMember = false;
			for(Map.Entry<String, List<MessageField>> entry : nestedOneOfs.entrySet()) {
				String unionName = entry.getKey();
				List<MessageField> unionFieldList = entry.getValue();
				for(Field uf : unionFieldList) {
					if(uf == field) { // reference to the same object
						oneOfMember = true;
						unionName = entry.getKey();
						break;
					}
				}
				if(oneOfMember) {
					if(!processedUnions.contains(unionName)) {
						sb.append(Indent);
						sb.append("oneof ").append(unionName).append(" {\n");
						for(MessageField unionField : unionFieldList) {
							// use PROTO3 syntax to avoid using the "optional" keyword
							sb.append(Indent).append(Indent).append(unionField.toString(ProtobufModel.Syntax.PROTO3)).append("\n");
						}
						sb.append(Indent).append("}\n");
						processedUnions.add(unionName);
					}
					break;
				}
			}
			if(!oneOfMember) {
				sb.append(Indent).append(field.toString(syntax)).append("\n");
			}
		}
		sb.append("}\n");
		return sb.toString();
	}
}
