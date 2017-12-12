package tesla.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import tesla.exception.TeslaDeserializationException;
import tesla.exception.TeslaException;
import tesla.exception.TeslaSerializationException;

public class Persistent implements Serializable {

	private static final long serialVersionUID = 212647586253017848L;
	private boolean locked = false;
	private File file = null;
	
	public final void releaseLock() throws TeslaException {
		Persistence.saveAndReleaseLock(this);
	}
	
	public final Persistent createCopy() throws TeslaSerializationException, TeslaDeserializationException 
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		try {
			 objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			 objectOutputStream.writeObject(this);
	      } catch(Exception e)  {
	         throw new TeslaSerializationException(e);
	      } finally {
	    	 try {
				if(objectOutputStream!=null) objectOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	      }
		
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return (Persistent) objectInputStream.readObject(); 
		} catch(Exception e) {
			throw new TeslaDeserializationException(e);
		} finally {
			try {
				if(objectInputStream!=null) objectInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public final boolean isLocked() {
		return locked;
	}
	
	final void lock() {
		locked = true;
	}
	
	final void unlock() {
		locked = false;
	}
	
	final File getFile() {
		return file;
	}
	
	final void setFile(File file) {
		this.file = file;
	}
}