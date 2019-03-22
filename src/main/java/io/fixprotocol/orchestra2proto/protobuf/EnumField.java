package io.fixprotocol.orchestra2proto.protobuf;

import java.util.ArrayList;

public class EnumField extends Field {
	public EnumField() {
		fieldOptions = new ArrayList<Option>();
	}
	public EnumField(String fieldName) {
		fieldOptions = new ArrayList<Option>();
		this.fieldName = fieldName;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(fieldName).append(" = ").append(fieldNum);
		int optCount = fieldOptions.size();
		if(optCount > 0) {
			sb.append(" [");
			int optRemain = optCount;
			for(Option opt : fieldOptions) {
				sb.append(opt.toString());
				if(--optRemain > 0)
					sb.append(", ");
			}
			sb.append("]");
		}
		sb.append(";");
		return sb.toString();
	}
}
