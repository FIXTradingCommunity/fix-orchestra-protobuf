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

public class EnumField extends Field {
	public EnumField() {
		fieldOptions = new ArrayList<Option>();
	}
	public EnumField(String fieldName) {
		fieldOptions = new ArrayList<Option>();
		this.fieldName = fieldName;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(fieldName).append(" = ").append(fieldNum);
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
