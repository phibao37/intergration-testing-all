package core;

public abstract class RunProcess<E> extends Thread {

	private E element;
	private OnStateChange<E> stateChange;
	
	public RunProcess(E element) {
		this.element = element;
	}
	
	protected final void checkStop() throws InterruptedException{
		if (isInterrupted())
			throw new InterruptedException();
	}
	
	@Override
	public final void run() {
		runStart();
		if (stateChange != null)
			stateChange.stateChanged(this, START);
		try {
			checkStop();
			onRun();
			checkStop();
			runEnd(true, null);
		} 
		
		catch (InterruptedException e) {
			runEnd(false, null);
		}
		
		catch (Exception e){
			runEnd(false, e);
		}
		if (stateChange != null)
			stateChange.stateChanged(this, END);
	}
	
	public E getElement(){
		return element;
	}
	
	
	public void runStart() {}
	public abstract void onRun() throws InterruptedException;
	public void runEnd(boolean finish, Exception e) {}

	public void setStateChange(OnStateChange<E> itemStateChanged) {
		this.stateChange = itemStateChanged;
	}

	public interface OnStateChange<T>{
		void stateChanged(RunProcess<T> p, int state);
	}
	
	public static final int START = 1, END = 2;
}
