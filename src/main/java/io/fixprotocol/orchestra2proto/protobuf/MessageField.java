package io.fixprotocol.orchestra2proto.protobuf;

import java.util.ArrayList;

public class MessageField extends Field {
	
	public enum ScalarOrEnumOrMsg { ProtoScalar, ProtoEnum, ProtoMsg }; // Maybe call this TypeOfType. MetaType?
	public ScalarOrEnumOrMsg scalarOrEnumOrMsg;
	
	public String typeName; // type fieldName whether type is a scalar, an enum or a message
	public ScalarType scalarType; // applicable when is scalar is true
	public boolean isRepeating;
	
	private void init() {
		fieldOptions = new ArrayList<Option>();
		comments = new ArrayList<String>();
		fieldNum = 0;
	}
	public MessageField() {
		init();
	}
	public MessageField(ScalarType scalarType, String fieldName) {
		init();
		this.typeName = scalarType.toString();
		this.scalarType = scalarType;
		this.fieldName = fieldName;
		scalarOrEnumOrMsg = ScalarOrEnumOrMsg.ProtoScalar;
	}
	public MessageField(String typeName, String fieldName) {
		init();
		this.typeName = typeName;
		this.scalarType = null;
		this.fieldName = fieldName;
	}
	
	@Override
	public String toString() { return toString(ProtobufModel.Syntax.PROTO3); }
	
	public String toString(ProtobufModel.Syntax syntax) {
		StringBuilder sb = new StringBuilder();
		if(isRepeating)
			sb.append("repeated ");
		else if(syntax == ProtobufModel.Syntax.PROTO2)
			sb.append("optional ");
		sb.append(typeName).append(" ").append(fieldName).append(" = ").append(fieldNum);
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
