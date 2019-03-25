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

import io.fixprotocol.orchestra2proto.IModel;
import io.fixprotocol.orchestra2proto.capnp.Annotation;
import io.fixprotocol.orchestra2proto.capnp.AnnotationDecl;
import io.fixprotocol.orchestra2proto.capnp.Enum;
import io.fixprotocol.orchestra2proto.capnp.EnumField;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CapnpModel implements IModel {

	public String repoName;
	public List<Enum> enums;
	public List<Message> messages;
	public List<AnnotationDecl> annotationDecls;
	private final String fileSfx = ".capnp";
	
	public CapnpModel(String repoName) {
		this.repoName = repoName;
		enums = new ArrayList<Enum>();
		messages = new ArrayList<Message>();
		annotationDecls = new ArrayList<AnnotationDecl>();
	}
	
	//public Map<String, StringBuilder> toFileSet(boolean singlePackage) { return new HashMap<String, StringBuilder>(); };
	
	public Map<String, StringBuilder> toFileSet() {
		
		class ProtoFile {
			String pkgName;
			StringBuilder body;
			List<String> requiredImports;
			ProtoFile() {
				pkgName = "undefined-pkg";
				body = new StringBuilder();
				requiredImports = new ArrayList<String>();
			}
		}
		Map<String, ProtoFile> fileMap = new HashMap<String, ProtoFile>();
		
		for(AnnotationDecl annDecl : annotationDecls) {
			ProtoFile f;
			String fileName = annDecl.homePackage == null ? "undefined-pkg.capnp" : annDecl.homePackage.toLowerCase() + fileSfx;
			String pkgName = "fix";
			if(!fileMap.containsKey(fileName)) {
				f = new ProtoFile();
				f.pkgName = pkgName;
				f.body.append("using Cxx = import \"/capnp/c++.capnp\";\n");
				f.body.append("$Cxx.namespace(\"fix::").append(repoName).append("\");\n");
				fileMap.put(fileName, f);
			}
			else {
				f = fileMap.get(fileName);
			}
			f.body.append(annDecl.toString());
			f.body.append("\n");
		}

		for(Message msg : messages) {
			ProtoFile f;
			String fileName = msg.homePackage == null ? "undefined-pkg.capnp" : msg.homePackage.toLowerCase() + fileSfx;
			String pkgName = "fix";
			if(!fileMap.containsKey(fileName)) {
				f = new ProtoFile();
				f.pkgName = pkgName;
				fileMap.put(fileName, f);
			}
			else {
				f = fileMap.get(fileName);
			}
			f.body.append(msg.toString());
			f.body.append("\n");
			for(MessageField field : msg.fields) {
				if(field.scalarOrEnumOrMsg == MessageField.ScalarOrEnumOrMsg.ProtoMsg) {
					String msgRefName = field.typeName;
					for(Message item : messages) {
						if(item.name.equals(msgRefName)) {
							String itemPkg = item.homePackage;
							if(!itemPkg.equals(msg.homePackage)) {
								String itemFileName = itemPkg.toLowerCase() + fileSfx;
								String itemName = "\"" + itemFileName + "\"" + "." + msgRefName;
								if(!f.requiredImports.contains(itemName))
									f.requiredImports.add(itemName);
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
								String itemFileName = itemPkg.toLowerCase() + fileSfx;
								String itemName = "\"" + itemFileName + "\"" + "." + enumRefName;						
								if(!f.requiredImports.contains(itemName))
									f.requiredImports.add(itemName);		
							}	
						}
					}
				}
				//
				// Now we need to know if there are annotations and what the annotationDecl homePackages are.
				//
				for(Annotation ann : field.annotations) {
					if(!ann.isStandard) {
						for(AnnotationDecl annDecl : annotationDecls) {
							if(annDecl.name.equals(ann.name)) {
								if(!annDecl.homePackage.equals(msg.homePackage)) {
									String itemFileName = annDecl.homePackage.toLowerCase() + fileSfx;
									String itemName = "\"" + itemFileName + "\"" + "." + ann.name;						
									if(!f.requiredImports.contains(itemName))
										f.requiredImports.add(itemName);
								}
							}
						}
					}
				}
			}
			//
			// Now we need to know if there are annotations and what the annotationDecl homePackages are.
			//
			for(Annotation ann : msg.annotations) {
				if(!ann.isStandard) {
					for(AnnotationDecl annDecl : annotationDecls) {
						if(annDecl.name.equals(ann.name)) {
							if(!annDecl.homePackage.equals(msg.homePackage)) {
								String itemFileName = annDecl.homePackage.toLowerCase() + fileSfx;
								String itemName = "\"" + itemFileName + "\"" + "." + ann.name;						
								if(!f.requiredImports.contains(itemName))
									f.requiredImports.add(itemName);
							}
						}
					}
				}
			}
		}
		
		for(Enum e : enums) {
			ProtoFile f;
			String fileName = e.homePackage == null ? "undefined-pkg.proto" : e.homePackage.toLowerCase() + fileSfx;
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
				//
				// Now we need to know if there are annotations and what the annotationDecl homePackages are.
				//
				for(Annotation ann : field.annotations) {
					if(!ann.isStandard) {
						for(AnnotationDecl annDecl : annotationDecls) {
							if(annDecl.name.equals(ann.name)) {
								if(!annDecl.homePackage.equals(e.homePackage)) {
									String itemFileName = annDecl.homePackage.toLowerCase() + fileSfx;
									String itemName = "\"" + itemFileName + "\"" + "." + ann.name;						
									if(!f.requiredImports.contains(itemName))
										f.requiredImports.add(itemName);
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
	
	private StringBuilder makeFileHeader(String fileName, String pkgName, List<String> imports) {
		StringBuilder sb = new StringBuilder();
		String dateTimeString = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
		sb.append("#").append("\n");
		sb.append("#\t").append("FIX 2016 Repository mapping to Cap'n Proto").append("\n");
		sb.append("#").append("\n");
		sb.append("#\t").append("Copyright (c) FIX Trading Community. All Rights Reserved.").append("\n");
		sb.append("#").append("\n");
		sb.append("#\t").append("File:\t\t\t").append(fileName).append("\n");
		sb.append("#\t").append("Source repository:\t" + repoName).append("\n");
		sb.append("#\t").append("Created:\t\t" + dateTimeString).append("\n");
		sb.append("#").append("\n\n");
		
		String fid = "0x" + Long.toHexString(UUID.randomUUID().getLeastSignificantBits()); 
		sb.append("@").append(fid).append(";\n\n");
		
		//sb.append("package " + pkgName + ";\n\n");
		
		String quotedFileName = "\"" + fileName + "\"";
		for(String importName : imports) {
			if(!importName.startsWith(quotedFileName))
				sb.append("using import " + importName + ";\n");
		}
		sb.append("\n");
		
		return sb;
	}
	
}



