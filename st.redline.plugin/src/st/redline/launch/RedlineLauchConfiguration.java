package st.redline.launch;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class RedlineLauchConfiguration implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		
			    Process process = null;
			    String commandLine = "notepad.exe ";    
			    // enable debugging if needed
			    if(mode == ILaunchManager.DEBUG_MODE) {
			      commandLine += "--debug ";
			    }    
			    // append script path to launch
//			    commandLine += configuration.getAttribute(ATTR_BATCH_PATH, "");    
			    try {
			      process = Runtime.getRuntime().exec(commandLine);
			    } catch (IOException e) {
			      e.printStackTrace();
			    }
			    DebugPlugin.newProcess(launch, process, "Script process");
		
		
		System.out.println(configuration);
	}

}
