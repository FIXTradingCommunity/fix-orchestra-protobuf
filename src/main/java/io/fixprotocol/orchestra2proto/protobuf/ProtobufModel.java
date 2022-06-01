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

import io.fixprotocol.orchestra2proto.IModel;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/*
import io.fixprotocol.orchestra2proto.capnp.AnnotationDecl;
import io.fixprotocol.orchestra2proto.capnp.Enum;
import io.fixprotocol.orchestra2proto.capnp.Message;
*/
//import io.fixprotocol.orchestra2proto.capnp.CapnpModel;


public class ProtobufModel implements IModel {
	
	public enum Syntax {
		PROTO2,
		PROTO3
	};
	
	public Syntax syntax;
	public String repoName;
	public List<Enum> enums;
	public List<Message> messages;
	public List<Extension> extensions;	// Extension defined at the global scope. Primary use is to facilitate custom fieldOptions.
	
	//public String packageName; // we should set this equal to repoName
	
	public ProtobufModel(String repoName, Syntax syntax) {
		this.syntax = syntax;
		this.repoName = repoName;
		enums = new ArrayList<Enum>();
		messages = new ArrayList<Message>();
		extensions = new ArrayList<Extension>();
	}
	
	private StringBuilder makeFileHeader(String fileName, String pkgName, List<String> imports) {
		StringBuilder sb = new StringBuilder();
		String dateTimeString = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
		sb.append("//").append("\n");
		sb.append("//\t").append("FIX 2016 Repository mapping to Google Protocol Buffers").append("\n");
		sb.append("//").append("\n");
		sb.append("//\t").append("Copyright (c) FIX Trading Community. All Rights Reserved.").append("\n");
		sb.append("//").append("\n");
		sb.append("//\t").append("File:\t\t\t").append(fileName).append("\n");
		sb.append("//\t").append("Source repository:\t" + repoName).append("\n");
		sb.append("//\t").append("Created:\t\t" + dateTimeString).append("\n");
		sb.append("//").append("\n\n");
		if(syntax == Syntax.PROTO2)
			sb.append("syntax = \"proto2\";\n\n");
		else
			sb.append("syntax = \"proto3\";\n\n");
		
		sb.append("package " + pkgName + ";\n\n");
		
		for(String importName : imports) {
			if(!importName.equals(fileName))
				sb.append("import \"" + importName + "\";\n");
		}
		sb.append("\n");
		
		return sb;
	}
	
	public Map<String, StringBuilder> toFileSet() {
	/*
	* Files are organized by homePackage. There will be one protobuf file for each homePackage. Where an enum, extension or message
	* lands depends on its homePackage. Files are named according to <homePackage>.proto. if singlePackage then all protobuf files
	* will specify a package of "fix", otherwise package will be set to homePackage.
	*/
		class ProtoFile {
			String pkgName;
			StringBuilder body;
			List<String> requiredImports;
			ProtoFile() {
				body = new StringBuilder();
				requiredImports = new ArrayList<String>();
			}
		}
		Map<String, ProtoFile> fileMap = new HashMap<String, ProtoFile>();
		
		for(Extension ext : extensions) {
			ProtoFile f;
			String fileName = ext.homePackage == null ? "undefined-pkg.proto" : ext.homePackage.toLowerCase() + ".proto";
			String pkgName = "fix";
			if(!fileMap.containsKey(fileName)) {
				f = new ProtoFile();
				f.pkgName = pkgName;
				fileMap.put(fileName, f);
			}
			else {
				f = fileMap.get(fileName);
			}
			f.body.append(ext.toString(syntax));
			f.body.append("\n");
			String importName = "google/protobuf/descriptor.proto";
			if(!f.requiredImports.contains(importName))
				f.requiredImports.add(importName);
		}
		for(Message msg : messages) {
			ProtoFile f;
			String fileName = msg.homePackage == null ? "undefined-pkg.proto" : msg.homePackage.toLowerCase() + ".proto";
			String pkgName = "fix";
			if(!fileMap.containsKey(fileName)) {
				f = new ProtoFile();
				f.pkgName = pkgName;
				fileMap.put(fileName, f);
			}
			else {
				f = fileMap.get(fileName);
			}
			f.body.append(msg.toString(syntax));
			f.body.append("\n");
			for(MessageField field : msg.fields) {
				if(field.scalarOrEnumOrMsg == MessageField.ScalarOrEnumOrMsg.ProtoMsg) {
					String msgRefName = field.typeName;
					for(Message item : messages) {
						if(item.name.equals(msgRefName)) {
							String itemPkg = item.homePackage;
							if(!itemPkg.equals(msg.homePackage)) {
								String itemFileName = itemPkg.toLowerCase() + ".proto";
								if(!f.requiredImports.contains("fix/" + itemFileName))
									f.requiredImports.add("fix/" + itemFileName);
							}	
						}
					}
				}
				else if(field.scalarOrEnumOrMsg == MessageField.ScalarOrEnumOrMsg.ProtoEnum) {
					String enumRefName = field.typeName;
					for(Enum item : enums) {
						if(item.name.equals(enumRefName)) {
							String itemPkg = item.homePackage;
							if(!itemPkg.equals(msg.homePackage)) {
								String itemFileName = itemPkg.toLowerCase() + ".proto";
								if(!f.requiredImports.contains("fix/" + itemFileName))
									f.requiredImports.add("fix/" + itemFileName);
							}	
						}
					}
				}
				//
				// now we need to know if there are extensions and what the extension homePackages are.
				//
				for(Option opt : field.fieldOptions) {
					if(!opt.isStandard) {
						String optRefName = opt.name;
						for(Extension ext : extensions) {
							for(ExtensionField ef : ext.fields) {
								if(ef.fieldName.equals(optRefName)) {
									String efPkg = ext.homePackage;
									if(!efPkg.equals(msg.homePackage)) {
										String itemFileName = efPkg.toLowerCase() + ".proto";
										if(!f.requiredImports.contains("fix/" + itemFileName))
											f.requiredImports.add("fix/" + itemFileName);
									}	
								}
							}
						}
					}
				}
			}
		}
		for(Enum e : enums) {
			ProtoFile f;
			String fileName = e.homePackage == null ? "undefined-pkg.proto" : e.homePackage.toLowerCase() + ".proto";
			String pkgName = "fix";
			if(!fileMap.containsKey(fileName)) {
				f = new ProtoFile();
				f.pkgName = pkgName;
				fileMap.put(fileName, f);
			}
			else {
				f = fileMap.get(fileName);
			}
			f.body.append(e.toString());
			f.body.append("\n");
			for(EnumField field : e.fields) {
				for(Option opt : field.fieldOptions) {
					if(!opt.isStandard) {
						String optRefName = opt.name;
						for(Extension ext : extensions) {
							for(ExtensionField ef : ext.fields) {
								if(ef.fieldName.equals(optRefName)) {
									String efPkg = ext.homePackage;
									if(!efPkg.equals(e.homePackage)) {
										String itemFileName = efPkg.toLowerCase() + ".proto";
										if(!f.requiredImports.contains("fix/" + itemFileName))
											f.requiredImports.add("fix/" + itemFileName);
									}	
								}
							}
						}
					}
				}
			}
		}
		
		Map<String, StringBuilder> sbMap = new HashMap<String, StringBuilder>();
		
		for(Map.Entry<String, ProtoFile> entry : fileMap.entrySet()) {
			String fileName = entry.getKey();
			ProtoFile protoFile = entry.getValue();
			StringBuilder sb = new StringBuilder();
			sb.append(makeFileHeader(fileName, protoFile.pkgName, protoFile.requiredImports));
			sb.append(protoFile.body);
			sbMap.put(fileName, sb);
		}
			
		return sbMap;
	}
}
