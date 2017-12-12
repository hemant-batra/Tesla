package tesla.persistence;

import tesla.exception.TeslaDeserializationException;
import tesla.exception.TeslaException;
import tesla.exception.TeslaFileException;
import tesla.exception.TeslaObjectAlreadyLockedException;
import tesla.exception.TeslaReflectionException;
import tesla.exception.TeslaSerializationException;

public class Persistence {
	
	public static Persistent read(Class<? extends Persistent> _class) throws TeslaException {
		Persistent copy = Cache.acquireOriginal(_class).createCopy();
		copy.unlock();
		return copy;
	}
	
	public static synchronized Persistent acquireLock(Class<? extends Persistent> _class) throws TeslaObjectAlreadyLockedException, TeslaDeserializationException, TeslaReflectionException, TeslaSerializationException {
		Persistent original = Cache.acquireOriginal(_class);
		if(original.isLocked()) {
			Waiter waiter = Waiter.startWaiting(1000);
			while(original.isLocked()) {
				if(!waiter.isWaiting())
					throw new TeslaObjectAlreadyLockedException(_class.getName()+" is already locked by some other process");
			}
		}
		original.lock();
		return original.createCopy();
	}
	
	static synchronized void saveAndReleaseLock(Persistent persistent) throws TeslaDeserializationException, TeslaReflectionException, TeslaFileException, TeslaSerializationException {
		if(persistent.isLocked()) {
			Persistent original = Cache.acquireOriginal(persistent.getClass());
			if(original.isLocked())	{
				persistent.unlock();
				Cache.overwriteOriginal(persistent);
			}
		}
	}
}