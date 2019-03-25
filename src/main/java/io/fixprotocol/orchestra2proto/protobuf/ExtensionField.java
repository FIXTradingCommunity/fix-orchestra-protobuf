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
