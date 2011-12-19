package st.redline.plugin;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

public class LaunchShortcut2 implements ILaunchShortcut2 {

	@Override
	public void launch(ISelection selection, String mode) {
		try {
			ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = lm.getLaunchConfigurationType("st.redline.plugin.launchConfigurationType1");
			ILaunchConfigurationWorkingCopy wcopy = type.newInstance(null, "blub");
			wcopy.setAttribute("bla", true);
			ILaunchConfiguration configuration = wcopy.doSave();
			DebugUITools.launch(configuration, mode);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		System.out.println();
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		try {
			ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = lm.getLaunchConfigurationType("st.redline.plugin.launchConfigurationType1");
			ILaunchConfigurationWorkingCopy wcopy = type.newInstance(null, "blub");
			wcopy.setAttribute("bla", true);
			ILaunchConfiguration configuration = wcopy.doSave();
			DebugUITools.launch(configuration, mode);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		System.out.println();
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		return null;
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editorpart) {
		return null;
	}

	@Override
	public IResource getLaunchableResource(ISelection selection) {
		return null;
	}

	@Override
	public IResource getLaunchableResource(IEditorPart editorpart) {
		return null;
	}

}
