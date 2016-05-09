package jdt;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import api.IProject;
import api.models.IFunction;
import api.models.IType;
import api.models.IVariable;
import api.parser.IProjectParser;
import core.Utils;
import core.models.ArrayVariable;
import core.models.FileInfo;
import core.models.Variable;
import core.models.type.ObjectType;
import jdt.models.JFunction;
import jdt.models.JProjectNode;

public class JProjectParser extends ASTVisitor implements IProjectParser {

	private JProject project;
	private File source;
	private Stack<JProjectNode> stackTreeNode;
	
	@Override
	public void parseSource(File source, IProject project) {
		this.project = (JProject) project;
		this.source = source;
		
		stackTreeNode = new Stack<>();
		JProjectNode root = new JProjectNode();
		stackTreeNode.push(root);
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		try {
			parser.setSource(Utils.getContentFile(source).toCharArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(this);
		this.project.putMapProjectStruct(source, root);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		stackTreeNode.pop();
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		LinkedHashMap<String, IType> schema = new LinkedHashMap<>();
		
//		for (FieldDeclaration f: node.getFields()){
//			
//		}
		ObjectType cls = new ObjectType(node.getName().toString(), schema);
		
		JProjectNode cl = new JProjectNode(cls);
		stackTreeNode.peek().addChild(cl);
		stackTreeNode.push(cl);
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		String s_name = node.getName().getIdentifier();
		Type type = node.getReturnType2();
		
		if (!type.isPrimitiveType()){
			return false;
		}
		String s_type = type.toString();
		
		@SuppressWarnings("unchecked")
		List<SingleVariableDeclaration> paras = node.parameters();
		IVariable[] varPara = new IVariable[paras.size()];
		
		for (int i = 0; i < varPara.length; i++)
			varPara[i] = parseParameter(paras.get(i));
		
		//Thêm một hàm mới được tìm thấy
		IFunction fn = new JFunction(s_name, varPara, project.findType(s_type),
				node.getBody(), project);
		
		fn.setSourceInfo(new FileInfo(node.getStartPosition(), 
				node.getLength(), source));
		project.addFunction(fn);
		stackTreeNode.peek().addChild(new JProjectNode(fn));
		
		return false;
	}
	
	/**
	 * Chuyển từ nút khai báo một tham số hàm sang biến tương ứng
	 * @param para
	 * @return
	 */
	private IVariable parseParameter(SingleVariableDeclaration para){
		return createVariable(para.getType(), para.getName().getIdentifier());
	}
	
	/**
	 * Tạo biến từ kiểu và tên biến
	 */
	@SuppressWarnings("unchecked")
	public IVariable createVariable(Type type, String name){
		IVariable var = null;
		
		//Có một hoặc nhiều cặp [] trong khai báo
		if (type instanceof ArrayType){
			ArrayType arrType = (ArrayType) type;
			IType arr = project.findType(arrType.getElementType().toString());
			List<Dimension> dimen = arrType.dimensions();
			
			for (Dimension d: dimen){
				d.annotations();
				arr = new core.models.type.ArrayType(arr, 0);
			}
			var = new ArrayVariable(name, (core.models.type.ArrayType) arr);
		} 
		
		//Không có khai báo biến mảng, số lượng [] bằng 0
		else {
			var = new Variable(name, project.findType(type.toString()));
		}
		
		return var;
	}

}
