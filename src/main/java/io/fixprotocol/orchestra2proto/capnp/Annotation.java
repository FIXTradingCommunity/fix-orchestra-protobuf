package io.fixprotocol.orchestra2proto.capnp;

public class Annotation {
	// Todo: make this simpler. Make value an Object. Get rid of valueType.
	
	public boolean isStandard;
	public String name;
	public String value;
	public enum ValueType {
		QUOTED_STRING,
		NUMERIC,
		ENUM_LITERAL,
		BOOLEAN,
		STRUCT // I'm not sure how a struct would work but I'll leave it as a placeholder.
	};
	public ValueType valueType;
	public Annotation(String name, String value, ValueType valueType) {
		this.name = name;
		this.value = value;
		this.valueType = valueType;
		isStandard = false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(valueType == ValueType.QUOTED_STRING) {
			sb.append("$").append(name).append("(\"").append(value).append("\")");
		}
		else {
			sb.append("$").append(name).append("(").append(value).append(")");
		}
		return sb.toString();
	}
}
