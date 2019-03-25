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

import java.util.List;
import java.util.ArrayList;

public class AnnotationDecl {
	
	public enum Target {
		FILE, STRUCT, FIELD, UNION, GROUP, ENUM, ENUMERANT, INTERFACE, PARAMETER, ANNOTATION, CONST
	}
	
	private final String dfltHomePkg = "annotation-decls";
	
	public String name;
	public Object type; // reference to a struct, enum or built-in type (which includes Void).
	public List<Target> targets;
	public String homePackage;
	
	public AnnotationDecl(String name) {
		this.name = name;
		targets = new ArrayList<Target>();
		homePackage = dfltHomePkg;
	}
	
	public AnnotationDecl(String name, Object type) {
		this.name = name;
		this.type = type;
		targets = new ArrayList<Target>();
		homePackage = dfltHomePkg;
	}
	
	public void addTarget(Target target) {
		targets.add(target);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("annotation ").append(name).append("(");
		int remain = targets.size();
		for(Target t : targets) {
			sb.append(t.toString().toLowerCase());
			if(--remain > 0)
				sb.append(", ");
		}
		sb.append(")").append(" :").append(type).append(";");
		return sb.toString();
	}
}
