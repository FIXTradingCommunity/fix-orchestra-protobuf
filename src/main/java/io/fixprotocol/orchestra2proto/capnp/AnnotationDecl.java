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
