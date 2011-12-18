
package st.redline;

import com.google.inject.Injector;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class SmalltalkStandaloneSetup extends SmalltalkStandaloneSetupGenerated{

	public static void doSetup() {
		Injector injector = new SmalltalkStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

