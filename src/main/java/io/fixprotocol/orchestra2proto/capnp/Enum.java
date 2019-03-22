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
