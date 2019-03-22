package io.fixprotocol.orchestra2proto.protobuf;

import java.util.List;

/*
 * When we are ready we can make this the base class for EnumField, MessageField and ExtensionField.
 * It may help that the Comparator class can compare Field types instead of the concrete classes.
 */
public abstract class Field {
	public String fieldName;
	public Integer fieldNum;
	public List<Option> fieldOptions;
	public List<String> comments;
}
