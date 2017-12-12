package tesla.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tesla.exception.TeslaDeserializationException;
import tesla.exception.TeslaFileException;
import tesla.exception.TeslaReflectionException;
import tesla.exception.TeslaSerializationException;

public class Cache {
	
	private static volatile Map<Class<? extends Persistent>,Persistent> cacheMap = new ConcurrentHashMap<Class<? extends Persistent>, Persistent>();
	
	static Persistent acquireOriginal(Class<? extends Persistent> _class) throws TeslaDeserializationException, TeslaReflectionException {
		if(cacheMap.containsKey(_class))
			return cacheMap.get(_class);
		
		Persistent persistent = Converter.classToObject(_class);
		cacheMap.put(_class, persistent);
		
		return persistent;
	}
	
	static void overwriteOriginal(Persistent persistent) throws TeslaFileException, TeslaSerializationException {
		cacheMap.put(persistent.getClass(), persistent);
		Converter.objectToClass(persistent);
	}
}