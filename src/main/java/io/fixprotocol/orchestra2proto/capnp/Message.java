package io.fixprotocol.orchestra2proto.capnp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {
	public String name;
	public String homePackage;
	public List<Annotation> annotations;
	public List<MessageField> fields;
	public List<Message> nestedMessages;
	public Map<String, List<MessageField>> nestedUnions; 
	
	public Message() {
		annotations = new ArrayList<Annotation>();
		fields = new ArrayList<MessageField>();
		nestedUnions = new HashMap<String, List<MessageField>>();
	}
	
	public String toString() {
		final String Indent = "    ";
		StringBuilder sb = new StringBuilder();
		sb.append("struct ");
		sb.append(name);
		sb.append(" ");
		for(Annotation ann : annotations) {
			sb.append(ann.toString()).append(" ");
		}
		sb.append("{\n");
		
		List<String> processedUnions = new ArrayList<String>(); // Use to track whether already written.
		for(MessageField field : fields) {
			boolean oneOfMember = false;
			for(Map.Entry<String, List<MessageField>> entry : nestedUnions.entrySet()) {
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
						sb.append(makeUnionNameByConvention(unionName)).append(" :union {\n");
						for(MessageField unionField : unionFieldList) {
							sb.append(Indent).append(Indent).append(unionField.toString()).append("\n");
						}
						sb.append(Indent).append("}\n");
						processedUnions.add(unionName);
					}
					break;
				}
			}
			if(!oneOfMember) {
				sb.append(Indent).append(field.toString()).append("\n");
			}
		}
		sb.append("}\n");
		return sb.toString();
	}
	
	public static String makeUnionNameByConvention(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1) + "Union";
	}
}
