package tesla.core;

import java.io.Serializable;
import java.lang.reflect.Method;

import tesla.exception.TeslaException;
import tesla.persistence.Persistence;
import tesla.persistence.Persistent;
import tesla.store.Request;
import tesla.store.ServerBean;

public abstract class WriteService<ReturnType extends Serializable, PersistentType extends Persistent> extends Service<ReturnType> {

	private Class<PersistentType> classPersistentType = null;
	protected abstract ReturnType execute(Request request, PersistentType persistentType) throws TeslaException;
	public WriteService(ServerBean serverBean) {
		super(serverBean);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final ReturnType execute(Request request) {
		PersistentType persistentType = null;
		try {
			persistentType = (PersistentType) Persistence.acquireLock(getPersistentType());
			return execute(request,persistentType);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		} finally {
			if(persistentType!=null)
				try {
					persistentType.releaseLock();
				} catch (TeslaException e) {
					e.printStackTrace();
				}
		}
	}
	
	@SuppressWarnings("unchecked")
	private Class<PersistentType> getPersistentType() {
		if(classPersistentType==null) {
			Method[] methods = this.getClass().getDeclaredMethods();
			for(Method method : methods) {
				if(method.getName().equals("execute") && method.getParameterTypes().length==2 && method.getParameterTypes()[1]!=Persistent.class) {
					classPersistentType = (Class<PersistentType>) method.getParameterTypes()[1];
					System.out.println("Initializing " + WriteService.class.getSimpleName() + " " + this.getClass().getName()+"<"+method.getReturnType().getName()+", "+classPersistentType.getName()+">");
				}
			}
		}
		return classPersistentType;
	}
}