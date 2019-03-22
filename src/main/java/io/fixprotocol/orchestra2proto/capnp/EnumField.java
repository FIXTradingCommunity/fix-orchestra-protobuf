package io.fixprotocol.orchestra2proto.capnp;

import io.fixprotocol.orchestra2proto.capnp.Annotation;

import java.util.ArrayList;

public class EnumField extends Field {
	public EnumField() {
		annotations = new ArrayList<Annotation>();
	}
	public EnumField(String name) {
		annotations = new ArrayList<Annotation>();
		this.name = name;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getNameByConvention()).append(" @").append(num);
		int optCount = annotations.size();
		if(optCount > 0) {
			sb.append(" ");
			int optRemain = optCount;
			for(Annotation opt : annotations) {
				sb.append(opt.toString());
				if(--optRemain > 0)
					sb.append(" ");
			}
		}
		sb.append(";");
		return sb.toString();
	}
}
