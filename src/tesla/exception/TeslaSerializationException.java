package tesla.exception;

public class TeslaSerializationException extends TeslaException {

	private static final long serialVersionUID = -4466131764893874627L;

	public TeslaSerializationException(String message) {
		super(message);
	}

	public TeslaSerializationException(Exception e) {
		super(e);
	}
}