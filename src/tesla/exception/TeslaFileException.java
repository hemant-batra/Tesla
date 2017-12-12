package tesla.exception;

public class TeslaFileException extends TeslaException {

	private static final long serialVersionUID = -16360379958424080L;

	public TeslaFileException(String message) {
		super(message);
	}

	public TeslaFileException(Exception e) {
		super(e);
	}
}