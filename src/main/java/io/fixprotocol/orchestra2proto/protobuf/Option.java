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
