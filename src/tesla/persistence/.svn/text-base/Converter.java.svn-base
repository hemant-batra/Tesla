package tesla.persistence;

import java.io.File;

import tesla.exception.TeslaDeserializationException;
import tesla.exception.TeslaFileException;
import tesla.exception.TeslaReflectionException;
import tesla.exception.TeslaSerializationException;

public class Converter {
	
	static Persistent classToObject(Class<? extends Persistent> _class) throws TeslaDeserializationException, TeslaReflectionException
	{
		File file = new File("tesla", _class.getName().toLowerCase()+".data");
		if(file.exists()) 
			return (Persistent) Serializer.deserialize(file);
		
		if(!file.getParentFile().exists())
			file.getParentFile().mkdir();
		
		try {
			Persistent persistent = _class.getConstructor().newInstance();
			persistent.setFile(file);
			return persistent;
		} catch(Exception e) {
			throw new TeslaReflectionException(e);
		}
	}
	
	static void objectToClass(Persistent persistent) throws TeslaFileException, TeslaSerializationException 
	{
		File file = persistent.getFile();
		
		if(file.exists())
			if(!file.delete())
				throw new TeslaFileException("Unable to delete existing persistent file "+file.getAbsolutePath());
		
		Serializer.serialize(persistent.getFile(),persistent);
	}
}