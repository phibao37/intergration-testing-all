package api.error;

public class ProcessErrorException extends Exception {
	private static final long serialVersionUID = 1L;

	private int exitCode;
	private String message;
	
	public ProcessErrorException(int exitCode, String message){
		this.exitCode = exitCode;
		this.message = message;
	}
	
	public int getExitCode(){
		return exitCode;
	}
	
	public String getMessage(){
		return message;
	}
}
