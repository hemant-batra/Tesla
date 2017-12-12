package tesla.exception;

public class TeslaReflectionException extends TeslaException {

	private static final long serialVersionUID = -5602471294664480136L;

	public TeslaReflectionException(String message) {
		super(message);
	}

	public TeslaReflectionException(Exception e) {
		super(e);
	}
}