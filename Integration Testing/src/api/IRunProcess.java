package api;

public interface IRunProcess<E> {

	default void checkStop() throws InterruptedException{
		if (thread().isInterrupted())
			throw new InterruptedException();
	}
	
	default void runStart() {}
	
	void onRun() throws InterruptedException;
	
	default void runEnd(boolean finish, Throwable e) {}
	
	Thread thread();
	
	E getElement();
	
	void stateChange(int state);
	
	final int UPDATE = 0, START = 1, END = 2;
	
	void setStateChange(OnStateChange<E> itemStateChanged);
	
	interface OnStateChange<T>{
		void stateChanged(IRunProcess<T> p, int state);
	}
}
