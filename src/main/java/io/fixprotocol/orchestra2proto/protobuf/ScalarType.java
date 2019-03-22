package io.fixprotocol.orchestra2proto.protobuf;

public enum ScalarType {
	DOUBLE,
	FLOAT,
	INT32,
	INT64,
	UINT32,
	UINT64,
	SINT32,
	SINT64,
	FIXED32,
	FIXED64,
	SFIXED32,
	SFIXED64,
	BOOL,
	STRING,
	BYTES;
	public String toString() {
		return name().toLowerCase();
	}
}
