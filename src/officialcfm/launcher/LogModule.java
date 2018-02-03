package officialcfm.launcher;

/**
 * It logs info and debugging information
 * TODO: Export to file
 * 
 * @author KP
 */
public class LogModule implements IModule {
	public enum Case {
		INFO, WARNING, ERROR
	}
	
	@Override
	public void init() {
		log(Case.INFO, "Logger initialized.");
	}
	
	public void log(Case severity, String string) {
		System.out.println("[" + severity.toString() + "] " + string);
	}
}
