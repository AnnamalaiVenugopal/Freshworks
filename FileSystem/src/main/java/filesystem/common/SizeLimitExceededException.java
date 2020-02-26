package filesystem.common;

public class SizeLimitExceededException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SizeLimitExceededException(String exception) {
		super(exception);
	}

}
