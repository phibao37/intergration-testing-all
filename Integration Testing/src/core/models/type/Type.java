package core.models.type;

import java.util.ArrayList;
import java.util.List;

import core.models.Element;
import api.models.IType;

public abstract class Type extends Element implements IType {

	private int mSize;
	private List<ITypeModifier> listMdf;
	
	protected Type(String content, int size){
		super(content);
		mSize = size;
	}

	@Override
	public int getSize() {
		return mSize;
	}
	
	protected void addModifier(ITypeModifier mdf){
		if (listMdf == null)
			listMdf = new ArrayList<>();
		listMdf.add(mdf);
	}

	@Override
	public List<ITypeModifier> getModifiers() {
		return listMdf;
	}
	
	public static class TypeModifier extends Element implements ITypeModifier {
		protected TypeModifier(String content){
			super(content);
		}
	}

}
