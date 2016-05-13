package core;

import api.IRunProcess;

public abstract class RunProcess<E> extends Thread implements IRunProcess<E> {

	private E element;
	private OnStateChange<E> stateChange;
	
	public RunProcess(E element) {
		this.element = element;
	}
	
	@Override
	public final void run() {
		runStart();
		stateChange(START);
		try {
			checkStop();
			onRun();
			checkStop();
			runEnd(true, null);
		} 
		
		catch (InterruptedException e) {
			runEnd(false, null);
		}
		
		catch (Throwable e){
			runEnd(false, e);
		}
		stateChange(END);
	}
	
	@Override
	public void stateChange(int state){
		if (stateChange != null)
			stateChange.stateChanged(this, state);
	}

	@Override
	public E getElement(){
		return element;
	}
	
	@Override
	public Thread thread() {
		return this;
	}

	@Override
	public void setStateChange(OnStateChange<E> itemStateChanged) {
		this.stateChange = itemStateChanged;
	}
	
}
