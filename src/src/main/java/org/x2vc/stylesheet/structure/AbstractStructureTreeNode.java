package org.x2vc.stylesheet.structure;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for all derivations of {@link IStructureTreeNode}.
 */
public abstract class AbstractStructureTreeNode implements IStructureTreeNode {

	private static final long serialVersionUID = -4581883325565055335L;
	private IStylesheetStructure parentStructure;

	/**
	 * Constructor for a tree node.
	 *
	 * @param parentStructure the {@link IStylesheetStructure} the node belongs to
	 */
	protected AbstractStructureTreeNode(IStylesheetStructure parentStructure) {
		checkNotNull(parentStructure);
		this.parentStructure = parentStructure;
	}

	@Override
	public boolean isXSLTDirective() {
		return false;
	}

	@Override
	public IXSLTDirectiveNode asDirective() throws IllegalStateException {
		throw new IllegalStateException("This structure node can not be cast to IXSLTDirectiveNode");
	}

	@Override
	public boolean isXSLTParameter() {
		return false;
	}

	@Override
	public IXSLTParameterNode asParameter() throws IllegalStateException {
		throw new IllegalStateException("This structure node can not be cast to IXSLTParameterNode");
	}

	@Override
	public boolean isXSLTSort() {
		return false;
	}

	@Override
	public IXSLTSortNode asSort() throws IllegalStateException {
		throw new IllegalStateException("This structure node can not be cast to IXSLTSortNode");
	}

	@Override
	public boolean isXML() {
		return false;
	}

	@Override
	public IXMLNode asXML() throws IllegalStateException {
		throw new IllegalStateException("This structure node can not be cast to IXMLNode");
	}

	@Override
	public boolean isText() {
		return false;
	}

	@Override
	public ITextNode asText() throws IllegalStateException {
		throw new IllegalStateException("This structure node can not be cast to ITextNode");
	}

	@Override
	public IStylesheetStructure getParentStructure() {
		return this.parentStructure;
	}

}