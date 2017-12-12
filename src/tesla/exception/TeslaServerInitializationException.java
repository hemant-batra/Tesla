package tesla.exception;

public class TeslaServerInitializationException extends TeslaException {

	private static final long serialVersionUID = -7035451403543411504L;

	public TeslaServerInitializationException(String message) {
		super(message);
	}

	public TeslaServerInitializationException(Exception e) {
		super(e);
	}
}