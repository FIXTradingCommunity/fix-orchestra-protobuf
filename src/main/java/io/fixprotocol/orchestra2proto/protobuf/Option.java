package io.fixprotocol.orchestra2proto.protobuf;

public final class Option {
	public boolean isStandard;
	public String name;
	/*
	 * We use a string representation for the option value even though the value of an option can be
	 * any protobuf scalar or some enumeration constant. There may be others but we'll keep it simple
	 * by categorizing scalar types as numeric, booleans, or strings. We'll also support enum constant
	 * literals.
	 */
	public String value;
	public enum ValueType {
		QUOTED_STRING,
		NUMERIC,
		ENUM_LITERAL,
		BOOLEAN
	};
	public ValueType valueType;
	public Option(String name, String value, ValueType valueType) {
		this.name = name;
		this.value = value;
		this.valueType = valueType;
		isStandard = false;
	}
	public Option() {
		isStandard = false;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String v = value;
		if(valueType == ValueType.QUOTED_STRING) {
			v = "\"" + value + "\"";
		}
		if(isStandard)
			sb.append(name).append("=").append(v);
		else
			sb.append("(").append(name).append(")").append("=").append(v);
		return sb.toString();
	}
}
