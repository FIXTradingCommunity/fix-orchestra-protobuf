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

package io.fixprotocol.orchestra2proto.capnp;

import io.fixprotocol.orchestra2proto.capnp.Annotation;
import io.fixprotocol.orchestra2proto.capnp.EnumField;

import java.util.ArrayList;
import java.util.List;

public class Enum {
	public String name;
	public String homePackage;
	public List<EnumField> fields;
	public List<Annotation> annotationInstances;
	public Enum() {
		fields = new ArrayList<EnumField>();
	}
	
	@Override
	public String toString() {
		final String Indent = "    ";
		StringBuilder sb = new StringBuilder();
		sb.append("enum ");
		sb.append(name);
		sb.append(" {\n");
		
		// for each field, f, call f.toString();
		int n = 0;
		for(EnumField field : fields) {
			field.num = n++;
			sb.append(Indent);
			sb.append(field.toString());
			sb.append("\n");
		}
		
		sb.append("}\n");
		return sb.toString();
	}

}
