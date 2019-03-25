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

public class Extension {
	public String name;
	public String homePackage;
	protected List<ExtensionField> fields;
	
	public Extension(String name) {
		this.name = name;
		fields = null;
	}
	public void addField(ExtensionField ef) {
		if(fields == null)
			fields = new ArrayList<ExtensionField>();
		fields.add(ef);
	}
	
	@Override
	public String toString() { return toString(ProtobufModel.Syntax.PROTO3); }
	
	public String toString(ProtobufModel.Syntax syntax) {
		StringBuilder sb = new StringBuilder();
		sb.append("extend ");
		sb.append(name);
		sb.append(" {\n");
		
		// for each field, f, call f.toString();
		for(ExtensionField field : fields) {
			sb.append("\t");
			sb.append(field.toString(syntax));
			sb.append("\n");
		}
		
		sb.append("}\n");
		return sb.toString();
	}
}
