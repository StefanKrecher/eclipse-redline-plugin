package redlineplugin.editors;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parseTreeConstruction.XtextParsetreeConstructor;
import org.eclipse.xtext.parsetree.reconstr.Serializer;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.inject.Guice;
import com.google.inject.Injector;

import st.redline.SmalltalkRuntimeModule;
import st.redline.smalltalk.Method;
import st.redline.smalltalk.impl.MethodImpl;

import static org.eclipse.xtext.xtend2.lib.EObjectExtensions.*;

/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class MultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener {

	/** The text editor used in page 0. */
	private XtextEditor _editor;

	/** The font chosen in page 1. */
	private Font _font;

	/** The text widget used in page 2. */
//	private StyledText _methodSource;
	private StyledText _methodSource;
	private List _methodList;

	private java.util.List<Method> _methods;

	/**
	 * Creates a multi-page editor example.
	 */
	public MultiPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates page 0 of the multi-page editor, which contains a text editor.
	 */
	void createSourceView() {
		_editor = getXtextEditor();
		int index = 0;
		try {
			index = addPage(_editor, getEditorInput());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
			e.printStackTrace();
		}
		setPageText(index, _editor.getTitle());
	}

	private XtextEditor getXtextEditor() {
		XtextEditor xtextEditor = null;

		IExtensionPoint ep = RegistryFactory.getRegistry().getExtensionPoint("org.eclipse.ui.editors");
		IExtension[] extensions = ep.getExtensions();
		IExtension iex = ep.getExtension("st.redline.Smalltalk");
		IConfigurationElement confElem = null;
		for (IExtension ex : extensions) {
			for (IConfigurationElement ce : ex.getConfigurationElements()) {
				if (ce.getAttribute("id").equals("st.redline.Smalltalk")) {
					System.out.println("-- " + ce.getName() + " id=" + ce.getAttribute("id") + " class="
							+ ce.getAttribute("class"));
					confElem = ce;
					break;
				}
			}
			if (confElem != null) {
				break;
			}
		}
		try {
			// create the xtext editor
			xtextEditor = (XtextEditor) confElem.createExecutableExtension("class");
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		return xtextEditor;
	}

	/**
	 * Creates page 2 of the multi-page editor, which shows the sorted text.
	 */
	void createClassView() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		composite.setLayout(layout);

		_methodList = new List(composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);

		_methodSource = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.RESIZE);


		

		_methodList.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int idx = _methodList.getSelectionIndex();
				Method method = _methods.get(idx);

				String editorText = _editor.getDocumentProvider().getDocument(_editor.getEditorInput()).get();

				String[] methods = editorText.split("\n- ");

				DefaultEObjectLabelProvider labelProvider = new DefaultEObjectLabelProvider();
				Object source = labelProvider.text(method);
				_methodSource.setText(methods[idx + 1]);
			}

		});
		int index = addPage(composite);
		setPageText(index, getEditorInput().getName() + " Classview");
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createClassView();
		createSourceView();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(1).doSave(monitor);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(1);
		editor.doSaveAs();
		setPageText(1, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Calculates the contents of page 0 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 0) {
			analyseSource();
		}
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) _editor.getEditorInput()).getFile().getProject()
								.equals(event.getResource())) {
							IEditorPart editorPart = pages[i].findEditor(_editor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	/**
	 * Sets the font related data to be applied to the text in page 2.
	 */
	void setFont() {
//		FontDialog fontDialog = new FontDialog(getSite().getShell());
//		fontDialog.setFontList(_methodSource.getFont().getFontData());
//		FontData fontData = fontDialog.open();
//		if (fontData != null) {
//			if (_font != null)
//				_font.dispose();
//			_font = new Font(_methodSource.getDisplay(), fontData);
//			_methodSource.setFont(_font);
//		}
	}

	void analyseSource() {

		IXtextDocument myDocument = _editor.getDocument();

		_methods = myDocument.readOnly(new IUnitOfWork<java.util.List<Method>, XtextResource>() {
			public java.util.List<Method> exec(XtextResource resource) {
				java.util.List<Method> mymethods = new ArrayList<Method>();
				EList<EObject> contents = resource.getContents();
				for (EObject eo : contents.get(0).eContents().get(0).eContents()) {
					if (eo instanceof MethodImpl) {
						mymethods.add((Method) eo);
					}
				}
				return mymethods;
			}
		});

		String editorText = _editor.getDocumentProvider().getDocument(_editor.getEditorInput()).get();

		_methodList.removeAll();
		for (Method m : _methods) {
			_methodList.add(m.getMethodName());
		}
	}
}
