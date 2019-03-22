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
