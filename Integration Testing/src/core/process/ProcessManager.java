package core.process;

import java.util.ArrayList;

public class ProcessManager extends ArrayList<TestProcess> {
	private static final long serialVersionUID = 1L;

	public void runTest(TestProcess process){
		add(process);
		process.start();
	}
	
	public void stopTest(TestProcess process){
		remove(process);
		process.interrupt();
	}
	
	public void stopAll(){
		for (TestProcess p: this){
			p.interrupt();
		}
		clear();
	}
}
