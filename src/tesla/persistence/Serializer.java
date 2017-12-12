package tesla.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import tesla.exception.TeslaDeserializationException;
import tesla.exception.TeslaSerializationException;

public class Serializer {
	
	static void serialize(File file, Object object) throws TeslaSerializationException
	{
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			 fileOutputStream = new FileOutputStream(file);
			 objectOutputStream = new ObjectOutputStream(fileOutputStream);
			 objectOutputStream.writeObject(object);
	      } catch(Exception e)  {
	          throw new TeslaSerializationException(e);
	      } finally {
	    	 try {
				if(objectOutputStream!=null) objectOutputStream.close();
				 if(fileOutputStream!=null) fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	      }
	}
	
	static Object deserialize(File file) throws TeslaDeserializationException
	{
		FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
		try {
	         fileInputStream = new FileInputStream(file);
	         objectInputStream = new ObjectInputStream(fileInputStream);
	         return objectInputStream.readObject();
		} catch(Exception e)  {
		          throw new TeslaDeserializationException(e);
		} finally {
		    try {
				  if(objectInputStream!=null) objectInputStream.close();
				  if(fileInputStream!=null) fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
}