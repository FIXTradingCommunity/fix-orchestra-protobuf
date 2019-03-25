/*
 * Copyright 2019 FIX Protocol Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

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
