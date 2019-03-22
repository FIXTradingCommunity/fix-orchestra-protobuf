package io.fixprotocol.orchestra2proto.capnp;

import java.util.List;

public abstract class Field {
	public String name;
	public Integer num;
	public List<Annotation> annotations;
	public List<String> comments;
	
	public String getNameByConvention() {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}
}
