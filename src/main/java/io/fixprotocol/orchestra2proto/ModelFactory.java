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

import io.fixprotocol._2016.fixrepository.CodeSetType;
import io.fixprotocol._2016.fixrepository.CodeSets;
import io.fixprotocol._2016.fixrepository.Repository;
import io.fixprotocol._2016.fixrepository.MessageType;
import io.fixprotocol._2016.fixrepository.MessageType.Structure;
import io.fixprotocol._2016.fixrepository.ComponentType;
import io.fixprotocol._2016.fixrepository.Components;
import io.fixprotocol._2016.fixrepository.FieldRefType;
import io.fixprotocol._2016.fixrepository.FieldType;
import io.fixprotocol._2016.fixrepository.GroupType;
import io.fixprotocol._2016.fixrepository.Datatype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;

import org.apache.log4j.Logger;

abstract class ModelFactory {
	
	static Logger logger = Logger.getLogger(ModelFactory.class);
	
	protected Repository repo;
	protected CodegenSettings codegenSettings;
	/*
	 * These maps will come in handy when we build a schema.
	 */
	protected Map<String, Datatype> datatypeMap;
	protected Map<BigInteger, FieldType> fieldMap;
	protected Map<String, CodeSetType> codeSetMap;
	protected Map<String, String> codeSetCategoryMap;
	/*
	 * The component map contain all components whether they are ComponentTypes or GroupTypes.
	 * Recall that GroupType extends ComponentType.
	 */
	protected Map<BigInteger, ComponentType> componentMap;
	
	protected FieldComparator fieldCmp;
	
	public ModelFactory(Repository repoParam, CodegenSettings codegenSettingsParam) {
		/*
		 * If we want to do some validation we can do it here. E.g. check whether
		 * fields have correct datatype spellings.
		 */
		repo = repoParam;
		codegenSettings = codegenSettingsParam;
		
		/*
		 * If we want to make corrections to the repo we can do it here.
		 */
		{
			/*
			 * The field NoSides is problematic. It is a NumInGroup and a CodeSet. Furthermore, when we create an Enum
			 * we cannot assign it to a package. So what to do? Let's remove it from the list of CodeSets.
			 */
			CodeSets codeSets = repo.getCodeSets();
			List<CodeSetType> codeSetTypes = codeSets.getCodeSet();
			CodeSetType noSidesCodeSet = null;
			for(CodeSetType codeSetType : codeSetTypes) {
				if(codeSetType.getName().equals("NoSidesCodeSet")) {
					//logger.info("Removing NoSides from list of CodeSets.");
					noSidesCodeSet = codeSetType;
				}
			}
			if(noSidesCodeSet != null)
				codeSetTypes.remove(noSidesCodeSet);
		}
		
		/*
		 * Let's now set-up our handy maps.
		 */
		datatypeMap = new HashMap<String, Datatype>();
		for(Datatype datatype : repo.getDatatypes().getDatatype()) {
			datatypeMap.put(datatype.getName(), datatype);
		}
		fieldMap = new HashMap<BigInteger, FieldType>();
		for(FieldType field : repo.getFields().getField()) {
			fieldMap.put(field.getId(), field);
		}
		codeSetMap = new HashMap<String, CodeSetType>();
		CodeSets codeSets = repo.getCodeSets();
		List<CodeSetType> codeSetTypes = codeSets.getCodeSet();
		for(CodeSetType codeSetType : codeSetTypes) {
			codeSetMap.put(codeSetType.getName(), codeSetType);
			if(codeSetType.getName().startsWith("NoSide"))
				logger.warn("Field \"NoSides\" is defined as a CodeSet rather than a NumInGroup.");
		}
		componentMap = new HashMap<BigInteger, ComponentType>();
		Components components = repo.getComponents();
		List<ComponentType> componentTypes = components.getComponentOrGroup();
		for(ComponentType componentType : componentTypes) {
			componentMap.put(componentType.getId(), componentType);
			if(componentType instanceof GroupType) {
				GroupType gt = (GroupType)componentType;
				BigInteger idGrp = gt.getId();
				BigInteger idComp = componentType.getId();
				if(!idGrp.equals(idComp))
					logger.warn("GroupType ID (" + idGrp.toString() + ") does not equal ComponentType ID (" + idComp.toString() + ")");
			}
		}
		
		/*
		 * We'll want to know what file to place the CodeSets into. This will be helpful when we are generating the output and need to
		 * know which files must be imported. The rule is: if a CodeSet is referred by more than one field, and each of these fields
		 * are members of messages of different categories, then print the CodeSet in the common.proto file. If each field
		 * is a member of a message of the same category, then print the CodeSet in said category.
		 * It's a bit of work to figure out the proper category for the CodeSets, but here goes...
		 */
		codeSetCategoryMap = new HashMap<String, String>();
		for(ComponentType component : componentTypes) {
			List<Object> compItems = component.getComponentRefOrGroupRefOrFieldRef();
			for(Object obj : compItems){
				if(obj instanceof FieldRefType) {
					FieldRefType fieldRef = (FieldRefType) obj; // we got the field ref, but I think we need to get the actual field and examine it's type.
					FieldType field = fieldMap.get(fieldRef.getId());
					String codeSetName = field.getType(); // CodeSet types will end in "CodeSet".
					CodeSetType cs = codeSetMap.get(codeSetName);
					if(cs != null) {
						String category = component.getCategory();
						if(category != null) {
							if(codeSetCategoryMap.containsKey(codeSetName)) {
								String prevCategory = codeSetCategoryMap.get(codeSetName);
								if(!prevCategory.equals(category)) {
									codeSetCategoryMap.put(codeSetName, "common");
								}
							}
							else {
								codeSetCategoryMap.put(codeSetName,  category);
							}
						}
						else {
							logger.info("category is missing for " + cs.getName());
						}
					}
				}
			}
		}
		for(MessageType message : repo.getMessages().getMessage()) {
			Structure msgStructure = message.getStructure();
			List<Object> msgItems = msgStructure.getComponentOrComponentRefOrGroup();
			for(Object obj : msgItems) {
				if(obj instanceof FieldRefType) {
					FieldRefType fieldRef = (FieldRefType) obj;
					FieldType field = fieldMap.get(fieldRef.getId());
					String codeSetName = field.getType(); // CodeSet types will end in "CodeSet".
					CodeSetType cs = codeSetMap.get(codeSetName);
					if(cs != null) {
						String category = message.getCategory();
						if(category != null) {
							if(codeSetCategoryMap.containsKey(codeSetName)) {
								String prevCategory = codeSetCategoryMap.get(codeSetName);
								if(!prevCategory.equals(category)) {
									codeSetCategoryMap.put(codeSetName, "common");
								}
							}
							else {
								codeSetCategoryMap.put(codeSetName,  category);
							}
						}
						else {
							logger.info("category is missing for " + cs.getName());
						}
					}
				}
			}
		}
		
		if(codegenSettings.maintainRepoFieldOrder)
			fieldCmp = new FieldComparator(FieldComparator.SortOrder.NONE);
		else
			fieldCmp = new FieldComparator(FieldComparator.SortOrder.BY_SPEC);
	}

	protected abstract IModel buildModel();
}
