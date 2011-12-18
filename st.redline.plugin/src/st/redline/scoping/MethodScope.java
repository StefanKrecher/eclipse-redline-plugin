package st.redline.scoping;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.impl.AbstractScope;

import st.redline.smalltalk.impl.TemporaryImpl;

public class MethodScope extends AbstractScope {

	private final EObject context;

	public MethodScope(EObject context) {
		super(NULLSCOPE, false);
		this.context = context;
	}

	@Override
	protected Iterable<IEObjectDescription> getAllLocalElements() {
		ArrayList<IEObjectDescription> objects = new ArrayList<IEObjectDescription>();
		
		EList<EObject> elist = context.eContents();
		for(EObject eobj : elist) {
			if(eobj instanceof TemporaryImpl) {
				String name = ((TemporaryImpl) eobj).getName();
				QualifiedName qname = QualifiedName.create(name.split("\\."));
				objects.add(EObjectDescription.create(qname, eobj));				
			}
		}
		
		return objects;
	}
	
}
