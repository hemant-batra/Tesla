package tesla.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TeslaException extends Throwable {

	private static final long serialVersionUID = -4712447625499885623L;

	public TeslaException(Exception e) {
		super(e);
	}
	
	public TeslaException(String message) {
		super(message);
	}
	
	public String getStackTraceAsString() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		printStackTrace(printWriter);
		return stringWriter.toString();
	}
}
