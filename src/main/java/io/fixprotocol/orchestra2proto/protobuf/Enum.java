package io.fixprotocol.orchestra2proto.protobuf;

import java.util.List;
import java.util.ArrayList;

public class Enum {
	public String name;
	public String homePackage;
	public List<EnumField> fields;
	public List<Option> options;
	public Enum() {
		fields = new ArrayList<EnumField>();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("enum ");
		sb.append(name);
		sb.append(" {\n");
		
		// for each field, f, call f.toString();
		for(EnumField field : fields) {
			sb.append("\t");
			sb.append(field.toString());
			sb.append("\n");
		}
		
		sb.append("}\n");
		return sb.toString();
	}
}
