package io.fixprotocol.orchestra2proto;

import java.util.Map;
//import io.fixprotocol._2016.fixrepository.*;

public interface IModel {
	//public boolean buildFromRepo(Repository repo);
	public Map<String, StringBuilder> toFileSet();
}
