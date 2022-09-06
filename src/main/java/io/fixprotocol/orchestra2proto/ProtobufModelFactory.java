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

package io.fixprotocol.orchestra2proto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import io.fixprotocol._2020.orchestra.repository.CodeSetType;
import io.fixprotocol._2020.orchestra.repository.CodeSets;
import io.fixprotocol._2020.orchestra.repository.CodeType;
import io.fixprotocol._2020.orchestra.repository.ComponentRefType;
import io.fixprotocol._2020.orchestra.repository.ComponentType;
import io.fixprotocol._2020.orchestra.repository.Components;
import io.fixprotocol._2020.orchestra.repository.Datatype;
import io.fixprotocol._2020.orchestra.repository.Datatypes;
import io.fixprotocol._2020.orchestra.repository.FieldRefType;
import io.fixprotocol._2020.orchestra.repository.FieldType;
import io.fixprotocol._2020.orchestra.repository.GroupRefType;
import io.fixprotocol._2020.orchestra.repository.GroupType;
import io.fixprotocol._2020.orchestra.repository.Groups;
import io.fixprotocol._2020.orchestra.repository.MessageType;
import io.fixprotocol._2020.orchestra.repository.MessageType.Structure;
import io.fixprotocol._2020.orchestra.repository.Messages;
import io.fixprotocol._2020.orchestra.repository.Repository;
import io.fixprotocol._2020.orchestra.repository.UnionDataTypeT;
import io.fixprotocol.orchestra2proto.protobuf.Enum;
import io.fixprotocol.orchestra2proto.protobuf.EnumField;
import io.fixprotocol.orchestra2proto.protobuf.Extension;
import io.fixprotocol.orchestra2proto.protobuf.ExtensionField;
import io.fixprotocol.orchestra2proto.protobuf.Message;
import io.fixprotocol.orchestra2proto.protobuf.MessageField;
import io.fixprotocol.orchestra2proto.protobuf.Option;
import io.fixprotocol.orchestra2proto.protobuf.ProtobufModel;
import io.fixprotocol.orchestra2proto.protobuf.ScalarType;

public class ProtobufModelFactory extends ModelFactory {
	
	public ProtobufModelFactory(Repository repoParam, CodegenSettings appSettingsParam) {
		super(repoParam, appSettingsParam);
	}
	
	protected ProtobufModel buildModel() {
		return buildProtobufModel();
	}
	
	protected ProtobufModel buildProtobufModel() {
	/*
	 * We are going to add everything from the repo to our proto schema.
	 * It is here where we decide how many files we want to generate and which messages and which enums they contain.
	 */
		ProtobufModel.Syntax syntax = codegenSettings.getSchemaLanguage() == CodegenSettings.SchemaLanguage.PROTO2 ? ProtobufModel.Syntax.PROTO2 : ProtobufModel.Syntax.PROTO3;
			
		ProtobufModel protoSchema = new ProtobufModel(repo.getName(), syntax);
			
		Extension fileExtension = new Extension("google.protobuf.FileOptions");
		fileExtension.addField(new ExtensionField(ScalarType.STRING, "category", 53001));
		fileExtension.homePackage = codegenSettings.useAltOutputPackaging ? "extended-gpb-options" : "fix";
		protoSchema.extensions.add(fileExtension);
			
		Extension msgExtension = new Extension("google.protobuf.MessageOptions");
		msgExtension.addField(new ExtensionField(ScalarType.STRING, "msg_type_value", 52001));
		msgExtension.homePackage = codegenSettings.useAltOutputPackaging ? "extended-gpb-options" : "fix";
		protoSchema.extensions.add(msgExtension);
			
		if(codegenSettings.useAltOutputPackaging) {
			
			Extension ext = new Extension("google.protobuf.FieldOptions");
			ext.addField(new ExtensionField(ScalarType.FIXED32, "tag", 50001));
			ext.addField(new ExtensionField("Version", "field_added", 50002));
			ext.addField(new ExtensionField(ScalarType.SFIXED32, "field_added_ep", 50003));
			ext.addField(new ExtensionField("Version", "field_deprecated", 50004));
			ext.addField(new ExtensionField(ScalarType.FIXED32, "min_len", 50005));
			ext.addField(new ExtensionField(ScalarType.FIXED32, "max_len", 50006));
			ext.addField(new ExtensionField(ScalarType.SFIXED64, "min_value", 50007));
			ext.addField(new ExtensionField(ScalarType.SFIXED64, "max_value", 50008));
			ext.addField(new ExtensionField(ScalarType.FIXED32, "group_tag", 50009));
			ext.addField(new ExtensionField("Datatype", "type", 50010));
			ext.addField(new ExtensionField("TimeUnitFieldOption", "time_unit", 50011));
			ext.addField(new ExtensionField("EpochFieldOption", "epoch", 50012));
			ext.homePackage = "extended-gpb-options";
			protoSchema.extensions.add(ext);
				
			ext = new Extension("google.protobuf.EnumValueOptions");
			ext.addField(new ExtensionField("Version", "enum_added", 51001));
			ext.addField(new ExtensionField(ScalarType.SFIXED32, "enum_added_ep", 51002));
			ext.addField(new ExtensionField("Version", "enum_deprecated", 51003));
			ext.addField(new ExtensionField(ScalarType.STRING, "enum_value", 51004));
			ext.homePackage = "extended-gpb-options";
			protoSchema.extensions.add(ext);
		}
		else {
			Extension ext = new Extension("google.protobuf.FieldOptions");
			ext.addField(new ExtensionField(ScalarType.FIXED32, "tag", 50001));
			ext.addField(new ExtensionField("Version", "field_added", 50002));
			ext.addField(new ExtensionField(ScalarType.SFIXED32, "field_added_ep", 50003));	
			ext.addField(new ExtensionField("Version", "field_deprecated", 50004));
			ext.addField(new ExtensionField(ScalarType.FIXED32, "group_tag", 50009));
			ext.addField(new ExtensionField("Datatype", "type", 50010));
			ext.homePackage = "fix";
			protoSchema.extensions.add(ext);

			ext = new Extension("google.protobuf.FieldOptions");
			ext.addField(new ExtensionField(ScalarType.FIXED32, "min_len", 50005));
			ext.addField(new ExtensionField(ScalarType.FIXED32, "max_len", 50006));
			ext.addField(new ExtensionField(ScalarType.SFIXED64, "min_value", 50007));
			ext.addField(new ExtensionField(ScalarType.SFIXED64, "max_value", 50008));
			ext.addField(new ExtensionField("TimeUnitFieldOption", "time_unit", 50011));
			ext.addField(new ExtensionField("EpochFieldOption", "epoch", 50012));
			ext.homePackage = "meta";
			protoSchema.extensions.add(ext);
			
			ext = new Extension("google.protobuf.EnumValueOptions");
			ext.addField(new ExtensionField("Version", "enum_added", 51001));
			ext.addField(new ExtensionField(ScalarType.SFIXED32, "enum_added_ep", 51002));
			ext.addField(new ExtensionField("Version", "enum_deprecated", 51003));
			ext.addField(new ExtensionField(ScalarType.STRING, "enum_value", 51004));
			ext.homePackage = "fix";
			protoSchema.extensions.add(ext);
		}
			
		/*
		 * Build proto enums from the CodeSets
		 */
		CodeSets codeSets = repo.getCodeSets();
		List<CodeSetType> codeSetTypes = codeSets.getCodeSet();
		for(CodeSetType codeSetType : codeSetTypes) {
			Enum protoEnum = buildEnum(codeSetType);
			protoEnum.homePackage = codegenSettings.useAltOutputPackaging ? "supporting-messages" : "fix";
			protoSchema.enums.add(protoEnum);
		}
		/*
		* Build proto messages from the FIX components.
		*/
		Components components = repo.getComponents();
		List<ComponentType> componentTypes = components.getComponent();
		for(ComponentType componentType : componentTypes) {
			Message protoMsg = buildMessage(componentType);
			protoMsg.homePackage = componentType.getCategory();
			protoSchema.messages.add(protoMsg);
		}
		/*
		 * Build proto messages from the FIX messages.
		 */
		Messages messages = repo.getMessages();
		List<MessageType> messageTypes = messages.getMessage();
		for(MessageType messageType : messageTypes) {
			Message protoMsg = buildMessage(messageType);
			protoMsg.homePackage = messageType.getCategory();
			protoSchema.messages.add(protoMsg);
		}
		/*
		 * Build proto messages from the FIX groups
		 */
		Groups groups = repo.getGroups();
		List<GroupType> groupTypes = groups.getGroup();
		for(GroupType groupType : groupTypes) {
			Message protoMsg = buildMessage(groupType);
			protoMsg.homePackage = groupType.getCategory();
			protoSchema.messages.add(protoMsg);
		}
		/*
		 * Build the supporting messages.
		 */
		protoSchema.messages.add(buildDecimal32());
		protoSchema.messages.add(buildDecimal64());
		protoSchema.messages.add(buildTimestamp());
		protoSchema.messages.add(buildTimeOnly());
		protoSchema.messages.add(buildLocalTimestamp());
		protoSchema.messages.add(buildLocalTimeOnly());
		protoSchema.messages.add(buildTenor());
		/*
		 * Build the supporting enums.
		 */
		protoSchema.enums.add(buildTimeUnit());
		protoSchema.enums.add(buildEpoch());
		protoSchema.enums.add(buildDatatype(repo.getDatatypes()));
		protoSchema.enums.add(buildVersion());
		
		return protoSchema;
	}

	private boolean containsName(final List<EnumField> list, final String name) {
		return list.stream().anyMatch(o -> Objects.equals(o.fieldName, name));
	}

	private List<EnumField> addEnumVal(final List<EnumField> list, final String name, final String s) {
		// This function adds additional enum values to already existing fields for the case of when a proto can be one of multiple values
		list.stream().filter(o -> Objects.equals(o.fieldName, name)).forEach(
				o -> {
					o.fieldOptions.stream().filter(a -> Objects.equals(a.name, "enum_value")).forEach(
							a -> {
								a.value += "," + s;
							}
					);
				}
		);

		return list;
	}
		
		private Enum buildEnum(CodeSetType codeSet) {
			Enum protoEnum = new Enum();
			protoEnum.name = toProtoEnumName(codeSet.getName());
			List<EnumField> fields = new ArrayList<EnumField>();
			for(CodeType codeType : codeSet.getCode()) {
				String sort = codeType.getSort();
				if(sort == null || sort == "") {
					sort = codeType.getValue();
					logger.error(String.format("sort is blank: CodeType: name='%s', id='%d', value='%s', sort='%s'", codeType.getName(), codeType.getId(), codeType.getValue(), codeType.getSort()));
				}
				if(sort != sort.trim()) {
					logger.error(String.format("unexpected whitespace in attribute: CodeType: name='%s', id='%d', value='%s', sort='%s'", codeType.getName(), codeType.getId(), codeType.getValue(), codeType.getSort()));
					sort = sort.trim();
				}
				EnumField f = new EnumField();
				f.fieldName = toProtoEnumFieldName(codeSet.getName(), codeType.getName());
				f.fieldNum = Integer.parseInt(sort); // for now

				// "UNSPECIFIED" is a reserved default enum value for the generated protos, must make value unique
				if(f.fieldName.contains("_UNSPECIFIED")) {
					f.fieldName = f.fieldName + "_VALUE";
				}

				if(codeType.getAdded() != null) {
					String added = toVersionFieldName(codeType.getAdded());
					f.fieldOptions.add(new Option("enum_added", added, Option.ValueType.ENUM_LITERAL));
				}
				if(codeType.getAddedEP() != null) {
					String added_ep = codeType.getAddedEP().toString();
					f.fieldOptions.add(new Option("enum_added_ep", added_ep, Option.ValueType.NUMERIC));
				}
				if(codeType.getDeprecated() != null) {
					String s = toVersionFieldName(codeType.getDeprecated());
					f.fieldOptions.add(new Option("enum_deprecated", s, Option.ValueType.ENUM_LITERAL));
					// Deprecations may cause enum value duplication for the generated protos, must make values unique
					f.fieldName = f.fieldName + "_DEPRECATED";
				}
				if(codeType.getValue() != null) {
					String s = codeType.getValue();
					// check if already added -- append to enum value if so
					if(containsName(fields, f.fieldName)) {
						fields = addEnumVal(fields, f.fieldName, s);
					} else {
						f.fieldOptions.add(new Option("enum_value", s, Option.ValueType.QUOTED_STRING));
					}
				}
				if (!containsName(fields, f.fieldName)) {
					fields.add(f);
				}
			}
			FieldComparator fieldComparator = new FieldComparator(FieldComparator.SortOrder.NONE);
			Collections.sort(fields, fieldComparator);
			EnumField defaultField = new EnumField(toProtoEnumFieldName(codeSet.getName(), null));
			defaultField.fieldNum = 0;
			protoEnum.fields.add(defaultField);
			for(int i=0; i<fields.size(); i++) {
				fields.get(i).fieldNum = i+1;
				protoEnum.fields.add(fields.get(i));
			}
			return protoEnum;
		}

	private Message buildMessage(GroupType group) {
		Message protoMsg = new Message();
		protoMsg.name = group.getName();

		List<Object> msgItems = group.getComponentRefOrGroupRefOrFieldRef();
		processMessageItems(protoMsg, msgItems);
		Collections.sort(protoMsg.fields, fieldCmp);
		for(int i=0; i<protoMsg.fields.size(); i++)
			protoMsg.fields.get(i).fieldNum = i+1;
		return protoMsg;

	}
		
		private Message buildMessage(ComponentType component) {
			Message protoMsg = new Message();
			protoMsg.name = component.getName();
			List<Object> msgItems = component.getComponentRefOrGroupRefOrFieldRef();
			processMessageItems(protoMsg, msgItems);
			Collections.sort(protoMsg.fields, fieldCmp);
			for(int i=0; i<protoMsg.fields.size(); i++)
				protoMsg.fields.get(i).fieldNum = i+1;
			return protoMsg;
		}
		
		private Message buildMessage(MessageType message) {
			logger.debug(String.format("Message: name='%s'", message.getName()));
			Message protoMsg = new Message();
			protoMsg.name = message.getName();
			if(message.getMsgType() != null)
				protoMsg.options.add(new Option("msg_type_value", message.getMsgType(), Option.ValueType.QUOTED_STRING));
			Structure msgStructure = message.getStructure();
			List<Object> msgItems = msgStructure.getComponentRefOrGroupRefOrFieldRef();
			processMessageItems(protoMsg, msgItems);
			Collections.sort(protoMsg.fields, fieldCmp);
			for(int i=0; i<protoMsg.fields.size(); i++)
				protoMsg.fields.get(i).fieldNum = i+1;
			return protoMsg;
		}

		private Message processMessageItems(Message protoMsg, List<Object> msgItems) {
			for(Object msgItem : msgItems) {
				MessageField protoField = null;
				if(msgItem instanceof GroupRefType) {
					protoField = buildField((GroupRefType) msgItem);
				}
				else if(msgItem instanceof ComponentRefType) {
					protoField = buildField((ComponentRefType) msgItem);
				}
				else if(msgItem instanceof FieldRefType) {
					protoField = buildField((FieldRefType) msgItem);
					if(hasUnionType((FieldRefType) msgItem)) {
						MessageField altField = buildAltUnionField((FieldRefType) msgItem);
						if(altField != null) {
							protoMsg.fields.add(altField);
							List<MessageField> unionList = new ArrayList<MessageField>(Arrays.asList(protoField, altField));
							protoMsg.nestedOneOfs.put(protoField.fieldName + "_union", unionList);
						}
					}
				}
				else {
					logger.error("unknown type: " + msgItem);
				}
				if(protoField != null) {
					protoMsg.fields.add(protoField);
				}
			}
			return protoMsg;
		}
		
		private MessageField buildField(GroupRefType groupRef) {
			GroupType group = groupMap.get(groupRef.getId());
			MessageField protoField = new MessageField(group.getName(), toProtoFieldName(group.getName()));
			protoField.isRepeating = true;
			protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
			if(groupRef.getAdded() != null) {
				String s = toVersionFieldName(groupRef.getAdded());
				protoField.fieldOptions.add(new Option("field_added", s, Option.ValueType.ENUM_LITERAL));
			}
			if(groupRef.getAddedEP() != null) {
				String s = groupRef.getAddedEP().toString();
				protoField.fieldOptions.add(new Option("field_added_ep", s, Option.ValueType.NUMERIC));
			}
			if(groupRef.getDeprecated() != null) {
				String s = toVersionFieldName(groupRef.getDeprecated());
				protoField.fieldOptions.add(new Option("field_deprecated", s, Option.ValueType.ENUM_LITERAL));
			}
			FieldRefType numInGroup = group.getNumInGroup();
			if(numInGroup != null) {
				String s = numInGroup.getId().toString();
				protoField.fieldOptions.add(new Option("group_tag", s, Option.ValueType.NUMERIC));
			}
			return protoField;
		}
		
		private MessageField buildField(ComponentRefType componentRef) {
			ComponentType component = componentMap.get(componentRef.getId());
			if(component == null) {
				logger.error(String.format("ComponentType is null: ComponentRefType: id='%d'", componentRef.getId()));
			}
			else {
				logger.debug(String.format("Component: name='%s', id='%d'", component.getName(), component.getId()));
			}
			MessageField protoField = new MessageField(component.getName(), toProtoFieldName(component.getName()));
			protoField.isRepeating = false;
			protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
			if(componentRef.getAdded() != null) {
				String s = toVersionFieldName(componentRef.getAdded());
				protoField.fieldOptions.add(new Option("field_added", s, Option.ValueType.ENUM_LITERAL));
			}
			if(componentRef.getAddedEP() != null) {
				String s = componentRef.getAddedEP().toString();
				protoField.fieldOptions.add(new Option("field_added_ep", s, Option.ValueType.NUMERIC));
			}
			if(componentRef.getDeprecated() != null) {
				String s = toVersionFieldName(componentRef.getDeprecated());
				protoField.fieldOptions.add(new Option("field_deprecated", s, Option.ValueType.ENUM_LITERAL));
			}
			return protoField;
		}
		
		private MessageField buildField(FieldRefType fieldRef) {
			FieldType field = fieldMap.get(fieldRef.getId());
			MessageField protoField = new MessageField();
			/*
			 * Determine whether a CodeSet. If so, handle accordingly, otherwise it's just a regular FIX type.
			 */
			if(codeSetMap.containsKey(field.getType())) {
				CodeSetType codeSet = codeSetMap.get(field.getType());
				String typeName = toProtoEnumName(codeSet.getName());
				protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
				protoField.typeName = typeName;
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoEnum;
				protoField.isRepeating = codeSet.getType().equals("MultipleCharValue") || codeSet.getType().equals("MultipleStringValue");
				
				if(codeSet.getType() != null) {
					String s = toProtoEnumFieldName("Datatype", codeSet.getType());
					protoField.fieldOptions.add(new Option("type", s, Option.ValueType.ENUM_LITERAL));
				}
			}
			/*
			 * Just a regular FIX type.
			 */
			else {
				if(field.getType().equals("int")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.FIXED32;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("TagNum")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.FIXED32;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("NumInGroup")) {
					return null;
				}
				else if(field.getType().equals("SeqNum")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.FIXED32;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("Length")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.FIXED32;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("DayOfMonth")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.FIXED32;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("float")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.typeName = "Decimal64"; // this will depend on enc attrs
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("Qty")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.typeName = "Decimal64"; // this will depend on enc attrs
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("Price")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.typeName = "Decimal64"; // this will depend on enc attrs
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("PriceOffset")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.typeName = "Decimal64"; // this will depend on enc attrs
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("Amt")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.typeName = "Decimal64"; // this will depend on enc attrs
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("Percentage")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.typeName = "Decimal64"; // this will depend on enc attrs
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("char")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.BYTES;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("Boolean")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.BOOL;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("String")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.STRING;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("MultipleCharValue")) {
					// This case never occurs.
					logger.info(getFieldName(fieldRef) + " is a MultipleCharValue");
				}
				else if(field.getType().equals("MultipleStringValue")) {
					// This case never occurs.
					logger.info(getFieldName(fieldRef) + " is a MultipleStringValue");
				}
				else if(field.getType().equals("Country")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.STRING;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("Currency")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.STRING;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("Exchange")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.STRING;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("MonthYear")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.SFIXED32;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("UTCTimestamp")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.typeName = "Timestamp";
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("UTCTimeOnly")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.typeName = "TimeOnly";
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("UTCDateOnly")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.SFIXED32;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("LocalMktDate")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.SFIXED32;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("TZTimeOnly")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.typeName = "LocalTimeOnly";
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("TZTimestamp")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.typeName = "LocalTimestamp";
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("data")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.STRING;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("Pattern")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.STRING;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("Tenor")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.typeName = "Tenor"; // this will depend on enc attrs
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("XMLData")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.STRING;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("Language")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.STRING;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("XID")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.STRING;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(field.getType().equals("XIDRef")) {
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef));
					protoField.scalarType = ScalarType.STRING;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else {
					logger.info(field.getName() + ": unrecognized type: " + field.getType());
					return null;
				}
				if(field.getType() != null) {
					String s = toProtoEnumFieldName("Datatype", field.getType());
					protoField.fieldOptions.add(new Option("type", s, Option.ValueType.ENUM_LITERAL));
				}
			}
			if(fieldRef.getAdded() != null) {
				String added = toVersionFieldName(fieldRef.getAdded());
				protoField.fieldOptions.add(new Option("field_added", added, Option.ValueType.ENUM_LITERAL));
			}
			if(fieldRef.getAddedEP() != null) {
				String added_ep = fieldRef.getAddedEP().toString();
				protoField.fieldOptions.add(new Option("field_added_ep", added_ep, Option.ValueType.NUMERIC));
			}
			if(fieldRef.getDeprecated() != null) {
				String s = toVersionFieldName(fieldRef.getDeprecated());
				protoField.fieldOptions.add(new Option("field_deprecated", s, Option.ValueType.ENUM_LITERAL));
			}
			if(fieldRef.getId() != null) {
				String s = fieldRef.getId().toString();
				protoField.fieldOptions.add(new Option("tag", s, Option.ValueType.NUMERIC));
			}
			return protoField;
		}


		
		private boolean hasUnionType(FieldRefType fieldRef) {
			FieldType field = fieldMap.get(fieldRef.getId());
			return field.getUnionDataType() != null;
		}
		
		private MessageField buildAltUnionField(FieldRefType fieldRef) {
			FieldType field = fieldMap.get(fieldRef.getId());
			MessageField protoField = null;
			if(field.getUnionDataType() != null) {
				UnionDataTypeT unionType = field.getUnionDataType();
				if(unionType == UnionDataTypeT.QTY) {
					protoField = new MessageField();
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef) + unionType.value());
					protoField.typeName = "Decimal64";
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				else if(unionType == UnionDataTypeT.RESERVED_1000_PLUS ||
						unionType == UnionDataTypeT.RESERVED_100_PLUS ||
						unionType == UnionDataTypeT.RESERVED_4000_PLUS) {
					protoField = new MessageField();
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef) + unionType.value());
					protoField.scalarType = ScalarType.SFIXED32;
					protoField.typeName = protoField.scalarType.toString();
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
					protoField.isRepeating = false;
				}
				else if(unionType == UnionDataTypeT.TENOR) {
					protoField = new MessageField();
					protoField.fieldName = toProtoFieldName(getFieldName(fieldRef) + unionType.value());
					protoField.typeName = "Tenor"; // this will depend on enc attrs
					protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
					protoField.isRepeating = false;
				}
				if(protoField != null) {
					if(field.getType() != null) {
						String s = toProtoEnumFieldName("Datatype", unionType.value());
						protoField.fieldOptions.add(new Option("type", s, Option.ValueType.ENUM_LITERAL));
					}
					if(fieldRef.getAdded() != null) {
						String added = toVersionFieldName(fieldRef.getAdded());
						protoField.fieldOptions.add(new Option("field_added", added, Option.ValueType.ENUM_LITERAL));
					}
					if(fieldRef.getAddedEP() != null) {
						String added_ep = fieldRef.getAddedEP().toString();
						protoField.fieldOptions.add(new Option("field_added_ep", added_ep, Option.ValueType.NUMERIC));
					}
					if(fieldRef.getDeprecated() != null) {
						String s = toVersionFieldName(fieldRef.getDeprecated());
						protoField.fieldOptions.add(new Option("field_deprecated", s, Option.ValueType.ENUM_LITERAL));
					}
					if(fieldRef.getId() != null) {
						String s = fieldRef.getId().toString();
						protoField.fieldOptions.add(new Option("tag", s, Option.ValueType.NUMERIC));
					}
				}
				else {
					logger.info("Alternate union data type " + unionType.name() + " is not supported.");
				}
			}
			else {
				logger.error(fieldRef.toString() + ": attempt to build alternate union field failed.");
			}
			return protoField;
		}
		
		/*
		 * Factory routines to build supporting message types.
		 */
		private Message buildDecimal32() {
			Message protoMsg = new Message();
			protoMsg.name = "Decimal32";
			protoMsg.homePackage = codegenSettings.useAltOutputPackaging ? "supporting-messages" : "fix";
			protoMsg.fields.add(new MessageField(ScalarType.SFIXED32, "mantissa"));
			protoMsg.fields.add(new MessageField(ScalarType.SFIXED32, "exponent"));
			for(int i=0; i<protoMsg.fields.size(); i++)
				protoMsg.fields.get(i).fieldNum = i+1;
			return protoMsg;
		}
		
		private Message buildDecimal64() {
			Message protoMsg = new Message();
			protoMsg.name = "Decimal64";
			protoMsg.homePackage = codegenSettings.useAltOutputPackaging ? "supporting-messages" : "fix";
			protoMsg.fields.add(new MessageField(ScalarType.SFIXED64, "mantissa"));
			protoMsg.fields.add(new MessageField(ScalarType.SFIXED32, "exponent"));
			for(int i=0; i<protoMsg.fields.size(); i++)
				protoMsg.fields.get(i).fieldNum = i+1;
			return protoMsg;
		}
		
		private Message buildTimestamp() {
			Message protoMsg = new Message();
			protoMsg.name = "Timestamp";
			protoMsg.homePackage = codegenSettings.useAltOutputPackaging ? "supporting-messages" : "fix";
			protoMsg.fields.add(new MessageField(ScalarType.INT64, "seconds"));
			protoMsg.fields.add(new MessageField(ScalarType.INT32, "nanos"));
			for(int i=0; i<protoMsg.fields.size(); i++)
				protoMsg.fields.get(i).fieldNum = i+1;
			return protoMsg;
		}
		
		private Message buildTimeOnly() {
			Message protoMsg = new Message();
			protoMsg.name = "TimeOnly";
			protoMsg.homePackage = codegenSettings.useAltOutputPackaging ? "supporting-messages" : "fix";
			protoMsg.fields.add(new MessageField(ScalarType.INT64, "seconds"));
			protoMsg.fields.add(new MessageField(ScalarType.INT32, "nanos"));
			for(int i=0; i<protoMsg.fields.size(); i++)
				protoMsg.fields.get(i).fieldNum = i+1;
			return protoMsg;
		}
		
		private Message buildLocalTimestamp() {
			Message protoMsg = new Message();
			protoMsg.name = "LocalTimestamp";
			protoMsg.homePackage = codegenSettings.useAltOutputPackaging ? "supporting-messages" : "fix";
			protoMsg.fields.add(new MessageField(ScalarType.INT32, "date"));
			protoMsg.fields.add(new MessageField(ScalarType.INT32, "hours"));
			protoMsg.fields.add(new MessageField(ScalarType.INT32, "minutes"));
			protoMsg.fields.add(new MessageField(ScalarType.INT64, "seconds"));
			protoMsg.fields.add(new MessageField(ScalarType.INT32, "nanos"));
			protoMsg.fields.add(new MessageField(ScalarType.SINT32, "utc_hour_offset"));
			protoMsg.fields.add(new MessageField(ScalarType.SINT32, "utc_minutes_offset"));
			for(int i=0; i<protoMsg.fields.size(); i++)
				protoMsg.fields.get(i).fieldNum = i+1;
			return protoMsg;
		}
		
		private Message buildLocalTimeOnly() {
			Message protoMsg = new Message();
			protoMsg.name = "LocalTimeOnly";
			protoMsg.homePackage = codegenSettings.useAltOutputPackaging ? "supporting-messages" : "fix";
			protoMsg.fields.add(new MessageField(ScalarType.INT32, "hours"));
			protoMsg.fields.add(new MessageField(ScalarType.INT32, "minutes"));
			protoMsg.fields.add(new MessageField(ScalarType.INT64, "seconds"));
			protoMsg.fields.add(new MessageField(ScalarType.INT32, "nanos"));
			protoMsg.fields.add(new MessageField(ScalarType.SINT32, "utc_hour_offset"));
			protoMsg.fields.add(new MessageField(ScalarType.SINT32, "utc_minute_offset"));
			for(int i=0; i<protoMsg.fields.size(); i++)
				protoMsg.fields.get(i).fieldNum = i+1;
			return protoMsg;
		}
		
		private Message buildTenor() {
			Message protoMsg = new Message();
			protoMsg.name = "Tenor";
			protoMsg.homePackage = codegenSettings.useAltOutputPackaging ? "supporting-messages" : "fix";
			protoMsg.fields.add(new MessageField(ScalarType.FIXED32, "days"));
			protoMsg.fields.add(new MessageField(ScalarType.FIXED32, "weeks"));
			protoMsg.fields.add(new MessageField(ScalarType.FIXED32, "months"));
			protoMsg.fields.add(new MessageField(ScalarType.FIXED32, "years"));
			for(int i=0; i<protoMsg.fields.size(); i++) {
				protoMsg.fields.get(i).fieldNum = i+1;
			}
			List<MessageField> unionFields = new ArrayList<MessageField>();
			for(int i=0; i<protoMsg.fields.size(); i++) {
				unionFields.add(protoMsg.fields.get(i));
			}
			protoMsg.nestedOneOfs.put("tenor_union", unionFields);
			return protoMsg;
		}
		
		private Enum buildDatatype(Datatypes datatypes) {
			/*
			 * We'll use the datatype records provided by the repository to build our enum.
			 */
			Enum protoEnum = new Enum();
			String codeSetName = "Datatype"; // mock-up a code set name so we can build like the other enums.
			protoEnum.name = toProtoEnumName(codeSetName);
			protoEnum.homePackage = codegenSettings.useAltOutputPackaging ? "extended-gpb-options" : "fix";
			List<EnumField> fields = new ArrayList<EnumField>();
			for(Datatype datatype : datatypes.getDatatype()) {
				EnumField f = new EnumField();
				f.fieldName = toProtoEnumFieldName(codeSetName, datatype.getName());
				if(datatype.getAdded() != null) {
					String added = toVersionFieldName(datatype.getAdded());
					f.fieldOptions.add(new Option("enum_added", added, Option.ValueType.ENUM_LITERAL));
				}
				if(datatype.getAddedEP() != null) {
					String added_ep = datatype.getAddedEP().toString();
					f.fieldOptions.add(new Option("enum_added_ep", added_ep, Option.ValueType.NUMERIC));
				}
				if(datatype.getDeprecated() != null) {
					String s = datatype.getDeprecated();
					f.fieldOptions.add(new Option("enum_deprecated", s, Option.ValueType.QUOTED_STRING));
				}
				fields.add(f);
			}
			Collections.sort(fields, fieldCmp);
			/* For supporting Enums we will not include the default field. This is because
			 * these enums are intended for custom option values and are never transmitted
			 * over the wire.
			 * WHAT HAPPENS IN OUT CPP CODE WHEN WE ACCESS AN OPTION AND IT'S NOT THERE???
			 */
			// testing with it now.
			EnumField defaultField = new EnumField(toProtoEnumFieldName(codeSetName, null));
			defaultField.fieldNum = 0;
			protoEnum.fields.add(defaultField);

			for(int i=0; i<fields.size(); i++) {
				fields.get(i).fieldNum = i+1;
				protoEnum.fields.add(fields.get(i));
			}
			return protoEnum;
		}
		
		private Enum buildVersion() {
			List<String> values = Arrays.asList(
					"FIX_2_7",
					"FIX_3_0",
					"FIX_4_0",
					"FIX_4_1",
					"FIX_4_2",
					"FIX_4_3",
					"FIX_4_4",
					"FIX_5_0",
					"FIXT_1_1",
					"FIX_5_0SP1",
					"FIX_5_0SP2",
					"FIX_LATEST"
			);
			Enum protoEnum = new Enum();
			String codeSetName = "Version"; // mock-up a code set name so we can build like the other enums.
			protoEnum.name = toProtoEnumName(codeSetName);
			protoEnum.homePackage = codegenSettings.useAltOutputPackaging ? "extended-gpb-options" : "fix";
			List<EnumField> fields = new ArrayList<EnumField>();
			for(String v : values) {
				fields.add(new EnumField(toProtoEnumFieldName(codeSetName, v)));
			}
			EnumField defaultField = new EnumField(toProtoEnumFieldName(codeSetName, null));
			defaultField.fieldNum = 0;
			protoEnum.fields.add(defaultField);
			for(int i=0; i<fields.size(); i++) {
				fields.get(i).fieldNum = i+1;
				protoEnum.fields.add(fields.get(i));
			}
			return protoEnum;
		}
		
		private Enum buildEpoch() {
			List<String> values = Arrays.asList(
					"MIDNIGHT",
					"UNIX",
					"1900",
					"2000"
			);
			Enum protoEnum = new Enum();
			String codeSetName = "EpochFieldOption"; // mock-up a code set name so we can build like the other enums.
			protoEnum.name = toProtoEnumName(codeSetName);
			protoEnum.homePackage = codegenSettings.useAltOutputPackaging ? "extended-gpb-options" : "meta";
			List<EnumField> fields = new ArrayList<EnumField>();
			for(String v : values) {
				fields.add(new EnumField(toProtoEnumFieldName(codeSetName, v)));
			}
			EnumField defaultField = new EnumField(toProtoEnumFieldName(codeSetName, null));
			defaultField.fieldNum = 0;
			protoEnum.fields.add(defaultField);
			for(int i=0; i<fields.size(); i++) {
				fields.get(i).fieldNum = i+1;
				protoEnum.fields.add(fields.get(i));
			}
			return protoEnum;
		}
		
		private Enum buildTimeUnit() {
			List<String> values = Arrays.asList(
					"DAYS",
					"SECONDS",
					"MILLISECONDS",
					"MICROSECONDS",
					"NANOSECONDS",
					"PICOSECONDS"
			);
			Enum protoEnum = new Enum();
			String codeSetName = "TimeUnitFieldOption"; // mock-up a code set name so we can build like the other enums.
			protoEnum.name = toProtoEnumName(codeSetName);
			protoEnum.homePackage = codegenSettings.useAltOutputPackaging ? "extended-gpb-options" : "meta";
			List<EnumField> fields = new ArrayList<EnumField>();
			for(String v : values) {
				fields.add(new EnumField(toProtoEnumFieldName(codeSetName, v)));
			}
			
			EnumField defaultField = new EnumField(toProtoEnumFieldName(codeSetName, null));
			defaultField.fieldNum = 0;
			protoEnum.fields.add(defaultField);

			for(int i=0; i<fields.size(); i++) {
				fields.get(i).fieldNum = i+1;
				protoEnum.fields.add(fields.get(i));
			}
			return protoEnum;
		}
		
		/*
		 * Naming convention routines
		 */
		
		private String toProtoFieldName(String fixFieldName) {
			String t = handleAcronyms(fixFieldName);
			//t = t.substring(0, 1).toLowerCase() + t.substring(1, t.length());
			t = camelToUnderscore(t).toLowerCase();
			return t;
		}
		
		private String toProtoEnumName(String fixCodeSetName) {
			return fixCodeSetName.replaceFirst("CodeSet", "Enum");
		}
		
		private String toProtoEnumFieldName(String fixCodeSetName, String fixCodeTypeName) {
			String s0 = fixCodeSetName.replaceFirst("CodeSet", "");
			String s1 = handleAcronyms(s0);
			String s2 = camelToUnderscore(s1);
			String s3 = fixCodeTypeName == null ? "Unspecified" : fixCodeTypeName;
			String s4 = handleAcronyms(s3);
			String s5 = camelToUnderscore(s4);
			return s2.toUpperCase() + "_" + s5.toUpperCase();
		}
		
		private String handleAcronyms(String s)
		{
			// Note the order of precedence. It is important.
			String[] acronyms = {
					"ISDAFpML", "CUSIP", "ISIN", "RIC", "USD", "US", "UK", "NERC",
					"CDS", "IOI", "MD", "EFP", "GT", "RFQ", "CFI", "ID",
					"XML", "ISO", "CP", "NT", "FX", "UTC", "TZ", "PKCS", "CSD", "ISITC"
			};
			String[] replacements = {
					"IsdaFpml", "Cusip", "Isin", "Ric", "Usd", "Us", "Uk", "Nerc",
					"Cds", "Ioi", "Md", "Efp", "Gt", "Rfq", "Cfi", "Id",
					"Xml", "Iso", "Cp", "Nt", "Fx", "Utc", "Tz", "Pkcs", "Csd", "Isitc"
			};
			String t = new String(s);
			for(int i =0; i<acronyms.length; ++i) {
				if(t.contains(acronyms[i])) {
					// go through all cases and replace but also append a '_' if the next char is lower-case
					//int n = t.indexOf(acronyms[i]);
					t = t.replace(acronyms[i], replacements[i]);
				}
			}
			return t;
		}
		
		private String camelToUnderscore(String v)
		{
			StringBuilder sb = new StringBuilder();
			String regex = "(?<=[a-z,0-9])(?=[A-Z])";
			int i = 0;
			for(String w : v.split(regex)) {
				if(++i > 1)
					sb.append("_");
				sb.append(w);
			}
			String u = sb.toString();
			StringBuilder sb2 = new StringBuilder();
			regex = "(?<=[a-z,A-Z])(?=[0-9])";
			i = 0;
			for(String w : u.split(regex)) {
				if(++i > 1)
					sb2.append("_");
				sb2.append(w);
			}
			return sb2.toString();
		}
		
		private String toVersionFieldName(String fixVersion) {
			/*
			 * The FIX repository represents a FIX version as String with a "." delimiter to separate its various
			 * components. Our naming convention requires us to use underscore delimiters. And we also have to
			 * account for the enum name "Version". So, for example, "FIX.4.4" will become "VERSION_FIX_4_4".
			 */
			return toProtoEnumFieldName("Version", fixVersion.replace('.', '_'));
		}

}
