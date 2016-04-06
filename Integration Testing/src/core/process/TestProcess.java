package core.process;

public abstract class TestProcess extends Thread {

	protected final void checkStop() throws InterruptedException{
		if (isInterrupted())
			throw new InterruptedException();
	}
	
	@Override
	public final void run() {
		try {
			testStart();
			checkStop();
			test();
			checkStop();
			testEnd(true);
		} 
		
		catch (InterruptedException e) {
			testEnd(false);
		}
		
		catch (Exception e){
			e.printStackTrace();
			testEnd(false);
		}
	}
	
	public void testStart() {}
	public abstract void test() throws InterruptedException;
	public void testEnd(boolean finish) {}
	
}
