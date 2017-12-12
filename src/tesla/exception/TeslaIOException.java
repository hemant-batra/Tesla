package tesla.exception;

public class TeslaIOException extends TeslaException {

	private static final long serialVersionUID = 1132828943843077836L;

	public TeslaIOException(String message) {
		super(message);
	}

	public TeslaIOException(Exception e) {
		super(e);
	}
}