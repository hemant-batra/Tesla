package tesla.persistence;

public class Waiter extends Thread {

	private boolean wait = false;
	private int waitPeriodMS = 0;
	
	static Waiter startWaiting(int waitPeriodMS) {
		return new Waiter(waitPeriodMS);
	}
	
	private Waiter(int waitPeriodMS) {
		this.waitPeriodMS = waitPeriodMS;
		wait = true;
		start();
	}
	
	@Override
	public void run() {
		try {
			sleep(waitPeriodMS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		wait = false;
	}
	
    boolean isWaiting() {
		return wait;
	}
}