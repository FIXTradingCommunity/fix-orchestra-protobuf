package io.fixprotocol.orchestra2proto;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import io.fixprotocol._2016.fixrepository.CodeSetType;
import io.fixprotocol._2016.fixrepository.CodeSets;
import io.fixprotocol._2016.fixrepository.CodeType;
import io.fixprotocol._2016.fixrepository.ComponentRefType;
import io.fixprotocol._2016.fixrepository.ComponentType;
import io.fixprotocol._2016.fixrepository.Components;
import io.fixprotocol._2016.fixrepository.FieldRefType;
import io.fixprotocol._2016.fixrepository.FieldType;
import io.fixprotocol._2016.fixrepository.GroupRefType;
import io.fixprotocol._2016.fixrepository.GroupType;
import io.fixprotocol._2016.fixrepository.MessageType;
import io.fixprotocol._2016.fixrepository.Messages;
import io.fixprotocol._2016.fixrepository.Repository;
import io.fixprotocol._2016.fixrepository.UnionDataTypeT;
import io.fixprotocol._2016.fixrepository.MessageType.Structure;
import io.fixprotocol.orchestra2proto.capnp.Annotation;
import io.fixprotocol.orchestra2proto.capnp.AnnotationDecl;
import io.fixprotocol.orchestra2proto.capnp.CapnpModel;
import io.fixprotocol.orchestra2proto.capnp.Enum;
import io.fixprotocol.orchestra2proto.capnp.EnumField;
import io.fixprotocol.orchestra2proto.capnp.Message;
import io.fixprotocol.orchestra2proto.capnp.MessageField;
import io.fixprotocol.orchestra2proto.capnp.ScalarType;

public class CapnpModelFactory extends ModelFactory {

	public CapnpModelFactory(Repository repoParam, CodegenSettings appSettingsParam) {
		super(repoParam, appSettingsParam);
	}
	
	protected CapnpModel buildModel() {
		return buildCapnpModel();
	}
	
	protected CapnpModel buildCapnpModel() {
		
		CapnpModel schema = new CapnpModel(repo.getName());
		
		/*
		 * Build the AnnotationDecls
		 *
		 */
		AnnotationDecl annDecl = new AnnotationDecl("category", ScalarType.TEXT);
		annDecl.addTarget(AnnotationDecl.Target.FILE);
		schema.annotationDecls.add(annDecl);
		
		annDecl = new AnnotationDecl("msgTypeValue", ScalarType.TEXT);
		annDecl.addTarget(AnnotationDecl.Target.STRUCT);
		schema.annotationDecls.add(annDecl);
		
		annDecl = new AnnotationDecl("tag", ScalarType.INT32);
		annDecl.addTarget(AnnotationDecl.Target.FIELD);
		schema.annotationDecls.add(annDecl);
		
		annDecl = new AnnotationDecl("groupTag", ScalarType.INT32);
		annDecl.addTarget(AnnotationDecl.Target.FIELD);
		schema.annotationDecls.add(annDecl);
		
		// Todo: investigate whether we can use "added" instead of "enumAdded". Use "added" for Structs Fields, Enums and Enumeratnts.
		annDecl = new AnnotationDecl("enumAdded", ScalarType.TEXT);
		annDecl.addTarget(AnnotationDecl.Target.ENUMERANT);
		schema.annotationDecls.add(annDecl);
		
		annDecl = new AnnotationDecl("enumAddedEP", ScalarType.INT32);
		annDecl.addTarget(AnnotationDecl.Target.ENUMERANT);
		schema.annotationDecls.add(annDecl);
		
		annDecl = new AnnotationDecl("enumDeprecated", ScalarType.TEXT);
		annDecl.addTarget(AnnotationDecl.Target.ENUMERANT);
		schema.annotationDecls.add(annDecl);
		
		annDecl = new AnnotationDecl("enumValue", ScalarType.TEXT);
		annDecl.addTarget(AnnotationDecl.Target.ENUMERANT);
		schema.annotationDecls.add(annDecl);
		
		annDecl = new AnnotationDecl("type", ScalarType.TEXT);
		annDecl.addTarget(AnnotationDecl.Target.FIELD);
		schema.annotationDecls.add(annDecl);
		
		annDecl = new AnnotationDecl("fieldAdded", ScalarType.TEXT);
		annDecl.addTarget(AnnotationDecl.Target.FIELD);
		schema.annotationDecls.add(annDecl);
		
		annDecl = new AnnotationDecl("fieldAddedEP", ScalarType.INT32);
		annDecl.addTarget(AnnotationDecl.Target.FIELD);
		schema.annotationDecls.add(annDecl);
		
		annDecl = new AnnotationDecl("fieldDeprecated", ScalarType.TEXT);
		annDecl.addTarget(AnnotationDecl.Target.FIELD);
		schema.annotationDecls.add(annDecl);
		
		/*
		 * Build enums from the CodeSets
		 */
		CodeSets codeSets = repo.getCodeSets();
		List<CodeSetType> codeSetTypes = codeSets.getCodeSet();
		for(CodeSetType codeSetType : codeSetTypes) {
			Enum protoEnum = buildEnum(codeSetType);
			if(codeSetCategoryMap.containsKey(codeSetType.getName())) {
				String pkgName = codeSetCategoryMap.get(codeSetType.getName());
				protoEnum.homePackage = pkgName;
			}
			else {
				protoEnum.homePackage = null;
			}
			schema.enums.add(protoEnum);
		}
		/*
		 * Build proto messages from the FIX components.
		 */
		Components components = repo.getComponents();
		List<ComponentType> componentTypes = components.getComponentOrGroup();
		for(ComponentType componentType : componentTypes) {
			Message protoMsg = buildMessage(componentType);
			protoMsg.homePackage = componentType.getCategory();
			schema.messages.add(protoMsg);
		}
		/*
		 * Build proto messages from the FIX messages.
		 */
		Messages messages = repo.getMessages();
		List<MessageType> messageTypes = messages.getMessage();
		for(MessageType messageType : messageTypes) {
			Message protoMsg = buildMessage(messageType);
			protoMsg.homePackage = messageType.getCategory();
			schema.messages.add(protoMsg);
		}
		/*
		 * Build the supporting messages.
		 */
		schema.messages.add(buildDecimal32());
		schema.messages.add(buildDecimal64());
		schema.messages.add(buildTimestamp());
		schema.messages.add(buildTimeOnly());
		schema.messages.add(buildLocalTimestamp());
		schema.messages.add(buildLocalTimeOnly());
		schema.messages.add(buildTenor());
		
		return schema;
	}
	
	private Enum buildEnum(CodeSetType codeSet) {
		Enum protoEnum = new Enum();
		protoEnum.name = codeSet.getName();
		
		EnumField dfltField = new EnumField();
		dfltField.name = "unspecified";
		dfltField.num = 0;
		protoEnum.fields.add(dfltField);
		
		for(CodeType codeType : codeSet.getCode()) {
			EnumField f = new EnumField();
			f.name = codeType.getName();
			f.num = Integer.parseInt(codeType.getSort()); // for now
			if(codeType.getAdded() != null) {
				String added = codeType.getAdded();
				f.annotations.add(new Annotation("enumAdded", added, Annotation.ValueType.QUOTED_STRING));
			}
			if(codeType.getAddedEP() != null) {
				String added_ep = codeType.getAddedEP().toString();
				f.annotations.add(new Annotation("enumAddedEP", added_ep, Annotation.ValueType.NUMERIC));
			}
			if(codeType.getDeprecated() != null) {
				String s = codeType.getDeprecated();
				f.annotations.add(new Annotation("enumDeprecated", s, Annotation.ValueType.QUOTED_STRING));
			}
			if(codeType.getValue() != null) {
				String s = codeType.getValue();
				f.annotations.add(new Annotation("enumValue", s, Annotation.ValueType.QUOTED_STRING));
			}
			protoEnum.fields.add(f);
		}
		
		/*
		FieldComparator fieldComparator = new FieldComparator(FieldComparator.SortOrder.NONE);
		Collections.sort(fields, fieldComparator);
		EnumField defaultField = new EnumField(toProtoEnumFieldName(codeSet.getName(), null));
		defaultField.fieldNum = 0;
		protoEnum.fields.add(defaultField);
		for(int i=0; i<fields.size(); i++) {
			fields.get(i).fieldNum = i+1;
			protoEnum.fields.add(fields.get(i));
		}
		*/
		
		return protoEnum;
	}
	
	private Message buildMessage(ComponentType component) {
		Message protoMsg = new Message();
		protoMsg.name = component.getName();
		List<Object> msgItems = component.getComponentRefOrGroupRefOrFieldRef();
		for(Object msgItem : msgItems) {
			MessageField protoField = null;
			if(msgItem instanceof ComponentRefType) {
				if(msgItem instanceof GroupRefType)
					protoField = buildField((GroupRefType) msgItem);
				else
					protoField = buildField((ComponentRefType) msgItem);
			}
			else if(msgItem instanceof FieldRefType) {
				protoField = buildField((FieldRefType) msgItem);
				
				if(hasUnionType((FieldRefType) msgItem)) {
					MessageField altField = buildAltUnionField((FieldRefType) msgItem);
					if(altField != null) {
						protoMsg.fields.add(altField);
						List<MessageField> unionList = new ArrayList<MessageField>(Arrays.asList(altField, protoField));
						protoMsg.nestedUnions.put(protoField.name, unionList);
					}
				}
				
			}
			if(protoField != null) {
				protoMsg.fields.add(protoField);
			}
				
		}
		/*
		Collections.sort(protoMsg.fields, fieldCmp);
		*/
		for(int i=0; i<protoMsg.fields.size(); i++)
			protoMsg.fields.get(i).num = i;
		return protoMsg;
	}
	
	private Message buildMessage(MessageType message) {
		Message protoMsg = new Message();
		protoMsg.name = message.getName();
		if(message.getMsgType() != null)
			protoMsg.annotations.add(new Annotation("msgTypeValue", message.getMsgType(), Annotation.ValueType.QUOTED_STRING));
		Structure msgStructure = message.getStructure();
		List<Object> msgItems = msgStructure.getComponentOrComponentRefOrGroup();
		for(Object msgItem : msgItems) {
			MessageField protoField = null;
			if(msgItem instanceof ComponentRefType) {
				if(msgItem instanceof GroupRefType)
					protoField = buildField((GroupRefType) msgItem);
				else
					protoField = buildField((ComponentRefType) msgItem);
			}
			else if(msgItem instanceof FieldRefType) {
				protoField = buildField((FieldRefType) msgItem);
				if(hasUnionType((FieldRefType) msgItem)) {
					MessageField altField = buildAltUnionField((FieldRefType) msgItem);
					if(altField != null) {
						protoMsg.fields.add(altField);
						List<MessageField> unionList = new ArrayList<MessageField>(Arrays.asList(altField, protoField));
						protoMsg.nestedUnions.put(protoField.name, unionList);
					}
				}
			}
			if(protoField != null)
				protoMsg.fields.add(protoField);
		}
		/*
		Collections.sort(protoMsg.fields, fieldCmp);
		*/
		for(int i=0; i<protoMsg.fields.size(); i++)
			protoMsg.fields.get(i).num = i;
		return protoMsg;
	}
	
	private MessageField buildField(GroupRefType groupRef) {
		ComponentType component = componentMap.get(groupRef.getId()); // note that component Map contains groups as components
		MessageField protoField = new MessageField(component.getName());
		protoField.isRepeating = true;
		protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
		
		protoField.typeName = component.getName();
				
		if(groupRef.getAdded() != null) {
			String s = groupRef.getAdded();
			protoField.annotations.add(new Annotation("fieldAdded", s, Annotation.ValueType.QUOTED_STRING)); //Annotation.ValueType.ENUM_LITERAL));
		}
		if(groupRef.getAddedEP() != null) {
			String s = groupRef.getAddedEP().toString();
			protoField.annotations.add(new Annotation("fieldAddedEP", s, Annotation.ValueType.NUMERIC));
		}
		if(groupRef.getDeprecated() != null) {
			String s = groupRef.getDeprecated();
			protoField.annotations.add(new Annotation("fieldDeprecated", s, Annotation.ValueType.QUOTED_STRING)); //Annotation.ValueType.ENUM_LITERAL));
		}
		GroupType group = (GroupType) component;
		if(group.getNumInGroupId() != null) {
			String s = group.getNumInGroupId().toString();
			protoField.annotations.add(new Annotation("groupTag", s, Annotation.ValueType.NUMERIC));
		}
		return protoField;
	}
	
	private MessageField buildField(ComponentRefType componentRef) {
		ComponentType component = componentMap.get(componentRef.getId());
		MessageField protoField = new MessageField(component.getName());
		protoField.isRepeating = false;
		protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
		
		protoField.typeName = component.getName();
		
		if(componentRef.getAdded() != null) {
			String s = componentRef.getAdded();
			protoField.annotations.add(new Annotation("fieldAdded", s, Annotation.ValueType.QUOTED_STRING)); //Annotation.ValueType.ENUM_LITERAL));
		}
		if(componentRef.getAddedEP() != null) {
			String s = componentRef.getAddedEP().toString();
			protoField.annotations.add(new Annotation("fieldAddedEP", s, Annotation.ValueType.NUMERIC));
		}
		if(componentRef.getDeprecated() != null) {
			String s = componentRef.getDeprecated();
			protoField.annotations.add(new Annotation("fieldDeprecated", s, Annotation.ValueType.QUOTED_STRING)); //Annotation.ValueType.ENUM_LITERAL));
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
			protoField.name = fieldRef.getName();
			protoField.typeName = codeSet.getName();
			protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoEnum;
			protoField.isRepeating = codeSet.getType().equals("MultipleCharValue") || codeSet.getType().equals("MultipleStringValue");
			if(codeSet.getType() != null) {
				String s = codeSet.getType();
				protoField.annotations.add(new Annotation("type", s, Annotation.ValueType.QUOTED_STRING)); //Annotation.ValueType.ENUM_LITERAL));
			}
		}
		/*
		 * Just a regular FIX type.
		 */
		else {
			if(field.getType().equals("int")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.INT32;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("TagNum")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.INT32;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("NumInGroup")) {
				return null;
			}
			else if(field.getType().equals("SeqNum")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.INT32;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("Length")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.INT32;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("DayOfMonth")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.INT32;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("float")) {
				protoField.name = fieldRef.getName();
				protoField.typeName = "Decimal32"; // this will depend on enc attrs
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("Qty")) {
				protoField.name = fieldRef.getName();
				protoField.typeName = "Decimal32"; // this will depend on enc attrs
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("Price")) {
				protoField.name = fieldRef.getName();
				protoField.typeName = "Decimal32"; // this will depend on enc attrs
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("PriceOffset")) {
				protoField.name = fieldRef.getName();
				protoField.typeName = "Decimal32"; // this will depend on enc attrs
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("Amt")) {
				protoField.name = fieldRef.getName();
				protoField.typeName = "Decimal32"; // this will depend on enc attrs
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("Percentage")) {
				protoField.name = fieldRef.getName();
				protoField.typeName = "Decimal32"; // this will depend on enc attrs
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("char")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.DATA;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("Boolean")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.BOOL;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("String")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.TEXT;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("MultipleCharValue")) {
				// This case never occurs.
				logger.info(fieldRef.getName() + " is a MultipleCharValue");
			}
			else if(field.getType().equals("MultipleStringValue")) {
				// This case never occurs.
				logger.info(fieldRef.getName() + " is a MultipleStringValue");
			}
			else if(field.getType().equals("Country")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.TEXT;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("Currency")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.TEXT;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("Exchange")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.TEXT;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("MonthYear")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.INT32;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("UTCTimestamp")) {
				protoField.name = fieldRef.getName();
				protoField.typeName = "Timestamp";
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("UTCTimeOnly")) {
				protoField.name = fieldRef.getName();
				protoField.typeName = "TimeOnly";
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("UTCDateOnly")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.INT32;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("LocalMktDate")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.INT32;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("TZTimeOnly")) {
				protoField.name = fieldRef.getName();
				protoField.typeName = "LocalTimeOnly";
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("TZTimestamp")) {
				protoField.name = fieldRef.getName();
				protoField.typeName = "LocalTimestamp";
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("data")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.TEXT;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("Pattern")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.TEXT;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("Tenor")) {
				protoField.name = fieldRef.getName();
				protoField.typeName = "Tenor"; // this will depend on enc attrs
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("XMLData")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.TEXT;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("Language")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.TEXT;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("XID")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.TEXT;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(field.getType().equals("XIDRef")) {
				protoField.name = fieldRef.getName();
				protoField.scalarType = ScalarType.TEXT;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else {
				logger.info(field.getName() + ": unrecognized type: " + field.getType());
				return null;
			}
			if(field.getType() != null) {
				String s = field.getType();
				protoField.annotations.add(new Annotation("type", s, Annotation.ValueType.QUOTED_STRING)); //Annotation.ValueType.ENUM_LITERAL));
			}
		}
		if(fieldRef.getAdded() != null) {
			String added = fieldRef.getAdded();
			protoField.annotations.add(new Annotation("fieldAdded", added, Annotation.ValueType.QUOTED_STRING)); //Annotation.ValueType.ENUM_LITERAL));
		}
		if(fieldRef.getAddedEP() != null) {
			String added_ep = fieldRef.getAddedEP().toString();
			protoField.annotations.add(new Annotation("fieldAddedEP", added_ep, Annotation.ValueType.NUMERIC));
		}
		if(fieldRef.getDeprecated() != null) {
			String s = fieldRef.getDeprecated();
			protoField.annotations.add(new Annotation("fieldDeprecated", s, Annotation.ValueType.QUOTED_STRING)); //Annotation.ValueType.ENUM_LITERAL));
		}
		if(fieldRef.getId() != null) {
			String s = fieldRef.getId().toString();
			protoField.annotations.add(new Annotation("tag", s, Annotation.ValueType.NUMERIC));
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
				protoField.name = fieldRef.getName() + unionType.value();
				protoField.typeName = "Decimal32";
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			else if(unionType == UnionDataTypeT.RESERVED_1000_PLUS ||
					unionType == UnionDataTypeT.RESERVED_100_PLUS ||
					unionType == UnionDataTypeT.RESERVED_4000_PLUS) {
				protoField = new MessageField();
				protoField.name = fieldRef.getName() + unionType.value();
				protoField.scalarType = ScalarType.INT32;
				protoField.typeName = protoField.scalarType.toString();
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoScalar;
				protoField.isRepeating = false;
			}
			else if(unionType == UnionDataTypeT.TENOR) {
				protoField = new MessageField();
				protoField.name = fieldRef.getName() + unionType.value();
				protoField.typeName = "Tenor"; // this will depend on enc attrs
				protoField.scalarOrEnumOrMsg = MessageField.ScalarOrEnumOrMsg.ProtoMsg;
				protoField.isRepeating = false;
			}
			if(protoField != null) {
				if(field.getType() != null) {
					String s = unionType.value();
					protoField.annotations.add(new Annotation("type", s, Annotation.ValueType.QUOTED_STRING));
				}
				if(fieldRef.getAdded() != null) {
					String added = fieldRef.getAdded();
					protoField.annotations.add(new Annotation("fieldAdded", added, Annotation.ValueType.QUOTED_STRING));
				}
				if(fieldRef.getAddedEP() != null) {
					String added_ep = fieldRef.getAddedEP().toString();
					protoField.annotations.add(new Annotation("fieldAddedEP", added_ep, Annotation.ValueType.NUMERIC));
				}
				if(fieldRef.getDeprecated() != null) {
					String s = fieldRef.getDeprecated();
					protoField.annotations.add(new Annotation("fieldDeprecated", s, Annotation.ValueType.QUOTED_STRING));
				}
				if(fieldRef.getId() != null) {
					String s = fieldRef.getId().toString();
					protoField.annotations.add(new Annotation("tag", s, Annotation.ValueType.NUMERIC));
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
		protoMsg.homePackage = "supporting-messages";
		protoMsg.fields.add(new MessageField("mantissa", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("exponent", ScalarType.INT32));
		for(int i=0; i<protoMsg.fields.size(); i++)
			protoMsg.fields.get(i).num = i;
		return protoMsg;
	}
	
	private Message buildDecimal64() {
		Message protoMsg = new Message();
		protoMsg.name = "Decimal64";
		protoMsg.homePackage = "supporting-messages";
		protoMsg.fields.add(new MessageField("mantissa", ScalarType.INT64));
		protoMsg.fields.add(new MessageField("exponent", ScalarType.INT32));
		for(int i=0; i<protoMsg.fields.size(); i++)
			protoMsg.fields.get(i).num = i;
		return protoMsg;
	}
	
	private Message buildTimestamp() {
		Message protoMsg = new Message();
		protoMsg.name = "Timestamp";
		protoMsg.homePackage = "supporting-messages";
		protoMsg.fields.add(new MessageField("seconds", ScalarType.INT64));
		protoMsg.fields.add(new MessageField("nanos", ScalarType.INT32));
		for(int i=0; i<protoMsg.fields.size(); i++)
			protoMsg.fields.get(i).num = i;
		return protoMsg;
	}
	
	private Message buildTimeOnly() {
		Message protoMsg = new Message();
		protoMsg.name = "TimeOnly";
		protoMsg.homePackage = "supporting-messages";
		protoMsg.fields.add(new MessageField("seconds", ScalarType.INT64));
		protoMsg.fields.add(new MessageField("nanos", ScalarType.INT32));
		for(int i=0; i<protoMsg.fields.size(); i++)
			protoMsg.fields.get(i).num = i;
		return protoMsg;
	}
	
	private Message buildLocalTimestamp() {
		Message protoMsg = new Message();
		protoMsg.name = "LocalTimestamp";
		protoMsg.homePackage = "supporting-messages";
		protoMsg.fields.add(new MessageField("date", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("hours", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("minutes", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("seconds", ScalarType.INT64));
		protoMsg.fields.add(new MessageField("nanos", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("utcHourOffset", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("utcMinuteOffset", ScalarType.INT32));
		for(int i=0; i<protoMsg.fields.size(); i++)
			protoMsg.fields.get(i).num = i;
		return protoMsg;
	}
	
	private Message buildLocalTimeOnly() {
		Message protoMsg = new Message();
		protoMsg.name = "LocalTimeOnly";
		protoMsg.homePackage = "supporting-messages";
		protoMsg.fields.add(new MessageField("hours", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("minutes", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("seconds", ScalarType.INT64));
		protoMsg.fields.add(new MessageField("nanos", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("utcHourOffset", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("utcMinuteOffset", ScalarType.INT32));
		for(int i=0; i<protoMsg.fields.size(); i++)
			protoMsg.fields.get(i).num = i;
		return protoMsg;
	}
	
	private Message buildTenor() {
		Message protoMsg = new Message();
		protoMsg.name = "Tenor";
		protoMsg.homePackage = "supporting-messages";
		protoMsg.fields.add(new MessageField("days", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("weeks", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("months", ScalarType.INT32));
		protoMsg.fields.add(new MessageField("years", ScalarType.INT32));
		for(int i=0; i<protoMsg.fields.size(); i++) {
			protoMsg.fields.get(i).num = i;
		}
		List<MessageField> unionFields = new ArrayList<MessageField>();
		for(int i=0; i<protoMsg.fields.size(); i++) {
			unionFields.add(protoMsg.fields.get(i));
		}
		protoMsg.nestedUnions.put("tenorUnion", unionFields);
		return protoMsg;
	}
}
