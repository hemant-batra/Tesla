package tesla.exception;

public class TeslaObjectAlreadyLockedException extends TeslaException {

	private static final long serialVersionUID = -9165616406563235094L;

	public TeslaObjectAlreadyLockedException(String message) {
		super(message);
	}
	
	public TeslaObjectAlreadyLockedException(Exception e) {
		super(e);
	}

}