package io.fixprotocol.orchestra2proto.protobuf;

public class ExtensionField extends Field {
	boolean isScalar;
	public String typeName; // type fieldName whether type is a scalar, an enum or a message
	public ScalarType scalarType;
	public ExtensionField(String typeName, String fieldName, int fieldNum) {
		this.typeName = typeName;
		this.fieldName = fieldName;
		this.fieldNum = fieldNum;
	}
	public ExtensionField(ScalarType scalarType, String fieldName, int fieldNum) {
		this.typeName = scalarType.toString();
		this.fieldName = fieldName;
		this.fieldNum = fieldNum;
	}
	@Override
	public String toString() { return toString(ProtobufModel.Syntax.PROTO3); }
	
	public String toString(ProtobufModel.Syntax syntax) {
		StringBuilder sb = new StringBuilder();
		if(syntax == ProtobufModel.Syntax.PROTO2)
			sb.append("optional ");
		sb.append(typeName).append(" ").append(fieldName).append(" = ").append(fieldNum);
		sb.append(";");
		return sb.toString();
	}
}
