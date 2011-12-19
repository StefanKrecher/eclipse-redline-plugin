package st.redline.plugin;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class AbstractLaunchConfigurationTabGroup1 extends AbstractLaunchConfigurationTabGroup {

	public AbstractLaunchConfigurationTabGroup1() {
		System.out.println();
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new EnvironmentTab(),
				new CommonTab()
				};
		setTabs(tabs); 
		
		System.out.println();
	}

}
