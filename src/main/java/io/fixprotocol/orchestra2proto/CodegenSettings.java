package io.fixprotocol.orchestra2proto;

public class CodegenSettings {

	public enum SchemaLanguage {
		PROTO2,
		PROTO3,
		CAPNPROTO,
		FLATBUFFERS
	}
	
	protected SchemaLanguage schemaLanguage;
	
	public SchemaLanguage getSchemaLanguage() {
		return schemaLanguage;
	}
	
	public void setSchemaLanguage(SchemaLanguage lang) {
		schemaLanguage = lang;
	}
	
	public boolean maintainRepoFieldOrder;
	public boolean useAltOutputPackaging;
}
