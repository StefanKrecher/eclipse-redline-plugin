
package st.redline;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class SmalltalkStandaloneSetup extends SmalltalkStandaloneSetupGenerated{

	public static void doSetup() {
		new SmalltalkStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

