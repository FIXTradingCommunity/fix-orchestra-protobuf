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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.fixprotocol._2020.orchestra.repository.CodeSetType;
import io.fixprotocol._2020.orchestra.repository.CodeSets;
import io.fixprotocol._2020.orchestra.repository.ComponentType;
import io.fixprotocol._2020.orchestra.repository.Components;
import io.fixprotocol._2020.orchestra.repository.Datatype;
import io.fixprotocol._2020.orchestra.repository.FieldRefType;
import io.fixprotocol._2020.orchestra.repository.FieldType;
import io.fixprotocol._2020.orchestra.repository.GroupType;
import io.fixprotocol._2020.orchestra.repository.Groups;
import io.fixprotocol._2020.orchestra.repository.MessageType;
import io.fixprotocol._2020.orchestra.repository.MessageType.Structure;
import io.fixprotocol._2020.orchestra.repository.Repository;

abstract class ModelFactory {
	
	static Logger logger = LogManager.getLogger(ModelFactory.class);
	
	protected Repository repo;
	protected CodegenSettings codegenSettings;
	/*
	 * These maps will come in handy when we build a schema.
	 */
	protected Map<String, Datatype> datatypeMap;
	protected Map<BigInteger, FieldType> fieldMap;
	protected Map<String, CodeSetType> codeSetMap;
	protected Map<String, String> codeSetCategoryMap;
	protected Map<BigInteger, ComponentType> componentMap;
	protected Map<BigInteger, GroupType> groupMap;
	
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
		List<ComponentType> componentTypes = components.getComponent();
		for(ComponentType componentType : componentTypes) {
			componentMap.put(componentType.getId(), componentType);
		}
		groupMap = new HashMap<BigInteger, GroupType>();
        Groups groups = repo.getGroups();
        List<GroupType> groupTypes = groups.getGroup();
        for(GroupType groupType : groupTypes) {
            groupMap.put(groupType.getId(), groupType);
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
			List<Object> msgItems = msgStructure.getComponentRefOrGroupRefOrFieldRef();
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
	
    protected String getFieldName(FieldRefType fieldRef) {
      FieldType field = fieldMap.get(fieldRef.getId());
      if (field != null) {
        return field.getName();
      } else {
        return "";
      }
    }
}
