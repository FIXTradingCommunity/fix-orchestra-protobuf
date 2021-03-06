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

package io.fixprotocol.orchestra2proto.capnp;

import java.util.ArrayList;

public class MessageField extends Field {

	/*
	Consider not implementing toString(). Put a writeMe(MessageField) function into the specific ModelFactory instead.
	*/
	
	public enum ScalarOrEnumOrMsg { ProtoScalar, ProtoEnum, ProtoMsg }; // Maybe call this TypeOfType. MetaType?
	public ScalarOrEnumOrMsg scalarOrEnumOrMsg;
	
	public String typeName; // type fieldName whether type is a scalar, an enum or a message
	public ScalarType scalarType; // applicable when is scalar is true
	public boolean isRepeating;
	
	// Let's replace the previous three with just one Object.
	public Object typeRef; // will be a Scalar, Enum or Message. Consider create an interface called IFieldTypeCandidate.
	
	private void init() {
		annotations = new ArrayList<Annotation>();
		comments = new ArrayList<String>();
		num = 0;
	}
	
	public MessageField() {
		init();
	}
	
	public MessageField(String name) {
		init();
		this.name = name;
	}
	
	public MessageField(String name, ScalarType scalarType) {
		init();
		this.name = name;
		this.scalarType = scalarType;
		typeName = scalarType.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if(isRepeating) {
			sb.append(getNameByConvention()).append(" @").append(num).append(" :List(").append(typeName).append(")");
		}
		else {
			sb.append(getNameByConvention()).append(" @").append(num).append(" :").append(typeName);
		}
		
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
