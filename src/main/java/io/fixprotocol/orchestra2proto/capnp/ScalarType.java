package io.fixprotocol.orchestra2proto.capnp;

public enum ScalarType {
	VOID,
	BOOL,
	INT8,
	INT16,
	INT32,
	INT64,
	UINT8,
	UINT16,
	UINT32,
	UINT64,
	FLOAT32,
	FLOAT64,
	TEXT,
	DATA;
	public String toString() {
		return name().substring(0, 1) + name().substring(1).toLowerCase();
	}
}
