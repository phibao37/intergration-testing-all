package jdt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

import core.Utils;
import core.models.ArrayVariable;
import core.models.Function;
import core.models.Variable;
import core.models.type.ArrayType;
import core.visitor.UnitVisitor;

public class JUnitVisitor implements UnitVisitor {
	
	private ArrayList<Function> mFunctions = new ArrayList<Function>();
	
	@Override
	public UnitVisitor parseSource(String source, File file, Object... args) {
		mFunctions.clear();
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(MethodDeclaration node) {
				String s_name = node.getName().getIdentifier();
				Type type = node.getReturnType2();
				
				if (!type.isPrimitiveType()){
					//throw new RuntimeException("CHua ho tro");
					//System.out.println("Chua ho tro kieu: " + type);
					return false;
				}
				String s_type = type.toString();
				
				@SuppressWarnings("unchecked")
				List<SingleVariableDeclaration> paras = node.parameters();
				Variable[] varPara = new Variable[paras.size()];
				
				for (int i = 0; i < varPara.length; i++)
					varPara[i] = parseParameter(paras.get(i));
				
				//Thêm một hàm mới được tìm thấy
				mFunctions.add(new Function(s_name, varPara, node.getBody(), 
						JType.parse(s_type)));
				
				return false;
			}
			
		});
		return this;
	}
	
	
	/**
	 * Chuyển từ nút khai báo một tham số hàm sang biến tương ứng
	 * @param para
	 * @return
	 */
	private static Variable parseParameter(SingleVariableDeclaration para){
		String name = para.getName().getIdentifier();
		Type type = para.getType();
		int dimen = para.getExtraDimensions();
		Variable var = null;
		
		//Không có khai báo biến mảng, số lượng [] bằng 0
		if (dimen == 0){
			var = new Variable(name, JType.parse(type.toString()));
		} 
		
		//Có một hoặc nhiều cặp [] trong khai báo
		else {
			core.models.Type arr = JType.parse(type.toString());
			for (int i = 0; i < dimen; i++)
				arr = new ArrayType(arr, 0);
			//TODO chưa lấy được chỉ số phần tử mảng khi khai báo
			var = new ArrayVariable(name, (ArrayType) arr);
		}
		
		return var;
	}

	@Override
	public ArrayList<Function> getFunctionList() {
		return mFunctions;
	}

	@Override
	public ArrayList<Variable> getGlobalVariableList() {
		return new ArrayList<Variable>();
	}
	
	public static void main(String[] args){
		try{
		String path = "D:\\Documents\\java\\TestUnit1.java";
		File f = new File(path);
		String source = Utils.getContentFile(f);
		
		for (Function fn: new JUnitVisitor().parseSource(source, f).getFunctionList()){
			System.out.println(fn);
		}

		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
