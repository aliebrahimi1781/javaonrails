package me.jor.roa.parser;

import me.jor.common.GlobalObject;
import me.jor.roa.core.AccessData;
import me.jor.roa.core.AccessDataParser;
import me.jor.roa.exception.AccessDataParseException;

public class JsonParser implements AccessDataParser{

	@Override
	public AccessData parse(String accessData) {
		try {
			return GlobalObject.getJsonMapper().readValue(accessData, AccessData.class);
		} catch (Exception e) {
			throw new AccessDataParseException(accessData, e);
		}
	}

}
