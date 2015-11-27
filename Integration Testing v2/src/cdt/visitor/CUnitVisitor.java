package cdt.visitor;

import cdt.models.CFunction;
import cdt.models.CType;
import core.ProcessInterface;
import core.Utils;
import core.models.ArrayVariable;
import core.models.Function;
import core.models.Type;
import core.models.Variable;
import core.models.type.ArrayType;
import core.models.type.ObjectType;
import core.visitor.UnitVisitor;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.model.AbstractLanguage;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bộ phân tích danh sách hàm và biến toàn cục trong ngôn ngữ C
 * @author ducvu
 *
 */
public class CUnitVisitor implements UnitVisitor {
	private ArrayList<Function> mFunctions = new ArrayList<>();
	private ArrayList<Variable> mVariables = new ArrayList<>();
	private ProcessInterface mProcess;
	private EpUtils mUtils;
	
	@Override
	public UnitVisitor parseSource(File file,  ProcessInterface process) 
			throws IOException {
		mFunctions.clear();
		mVariables.clear();
		mProcess = process;
		mUtils = new EpUtils(process);
		
		IASTTranslationUnit u = getIASTTranslationUnit(file);
		ArrayList<ObjectType> objectType = process.getDeclaredTypes();
		
		u.accept(new ASTVisitor() {
			
{  shouldVisitDeclarations = true; }

/** Duyệt các khai báo trong chương trình*/
@Override
public int visit(IASTDeclaration declaration) {
	
	//Duyệt các khai báo biến đơn giản
	if (declaration instanceof IASTSimpleDeclaration){
		IASTSimpleDeclaration declare = (IASTSimpleDeclaration) declaration;
		IASTDeclSpecifier spec = declare.getDeclSpecifier();
		
		/* 
		 * Duyệt các khai báo đơn giản, bỏ qua khai báo struct, và
		 * không chứa extern, typedef
		 */
		if (spec instanceof IASTSimpleDeclSpecifier &&
				spec.getStorageClass() == IASTDeclSpecifier.sc_unspecified
			){
			mVariables.addAll(parseVariableDeclaration(declare));
		}
		
		/*
		 * Duyệt qua các khai báo struct 
		 */
		else if (spec instanceof IASTCompositeTypeSpecifier){
			IASTCompositeTypeSpecifier comp = (IASTCompositeTypeSpecifier) spec;
			if (comp.getKey() == IASTCompositeTypeSpecifier.k_struct){
				LinkedHashMap<String, Type> schema = new LinkedHashMap<>();
				String name = comp.getName().toString();
				
				//Thêm các khai báo thuộc tính thành phần
				for (IASTDeclaration member: comp.getMembers()){
					IASTSimpleDeclaration sd = (IASTSimpleDeclaration) member;
					Type type = CType.parse(
							sd.getDeclSpecifier().getRawSignature(), mProcess);
					
					for (IASTDeclarator dc: sd.getDeclarators())
						schema.put(dc.getName().toString(), type);
				}
				
				//if: Phòng trường hợp khai báo anonymous
				if (!name.isEmpty())
					objectType.add(new ObjectType(name, schema));
				
				//Có từ khóa typedef, thêm các tên được định nghĩa
				if (comp.getStorageClass() == IASTDeclSpecifier.sc_typedef){
					for (IASTDeclarator dc: declare.getDeclarators())
						objectType.add(new ObjectType(dc.getName().toString(), schema));
				} 
			}
		}
	}
	
	//Duyệt các khai báo hàm
	if (declaration instanceof IASTFunctionDefinition) {
		IASTFunctionDefinition fnDefine = 
				(IASTFunctionDefinition) declaration;
		IASTFunctionDeclarator fnDeclare = fnDefine.getDeclarator();
		
		String type = fnDefine.getDeclSpecifier().getRawSignature();
		String name = fnDeclare.getName().getRawSignature();
		Variable[] para = null;
		IASTStatement fnBody = fnDefine.getBody();
		final Function fn;
		
		//Kiểu con trỏ, tham chiếu, làm sau
		//for (IASTPointerOperator p: fnDeclare.getPointerOperators())
			//type += p.getRawSignature();
		
		//Duyệt các khai báo hàm dạng chuẩn tắc
		if (fnDeclare instanceof IASTStandardFunctionDeclarator){
			IASTStandardFunctionDeclarator fDeclare = 
				(IASTStandardFunctionDeclarator) fnDeclare;
			IASTParameterDeclaration[] fnPara = fDeclare.getParameters();
			para = new Variable[fnPara.length];
			for (int i = 0; i < fnPara.length; i++)
				para[i] = parseParameter(fnPara[i]);
		}
		
		fn = new CFunction(name, para, fnBody, CType.parse(type, mProcess));
		fn.setSourceFile(file);
		mFunctions.add(fn);
	}
	return PROCESS_SKIP;
}
		});
		
		return this;
	}

	@Override
	public ArrayList<Function> getFunctionList() {
		return mFunctions;
	}

	@Override
	public ArrayList<Variable> getGlobalVariableList() {
		return mVariables;
	}
	
	/** Duyệt qua một khai báo đơn giản, sau đó trả về danh sách các biến được khai báo
	 * @param declare khai báo biến, thí dụ: <code>int x=1, *y, z[] = {1};</code>
	 * */
	public ArrayList<Variable> parseVariableDeclaration(
			IASTSimpleDeclaration declare){
		IASTDeclSpecifier spec = declare.getDeclSpecifier();
		ArrayList<Variable> gVarList = new ArrayList<>();
		
		for (IASTDeclarator dc: declare.getDeclarators()){
			gVarList.add(parseVariable(dc, spec));
		}
		return gVarList;
	}
	
	/**
	 * Trả về biến ứng với một biến tham số của hàm
	 * @param declare nút AST ứng với khai báo tham số hàm
	 */
	private Variable parseParameter(IASTParameterDeclaration declare){
		return parseVariable(declare.getDeclarator(), declare.getDeclSpecifier());
	}
	
	/**
	 * Trả về biến tham số từ một khái báo biến
	 * @param declare nội dung khai báo: a = 2, b[] = {1, 2}
	 * @param spec kiểu của biến khai báo: int, float
	 */
	private Variable parseVariable(IASTDeclarator declare, 
			IASTDeclSpecifier spec){
		Type type = CType.parse(spec.getRawSignature(), mProcess);
		String name = declare.getName().getRawSignature();
		IASTInitializer init = declare.getInitializer();
		Variable var;

		//Đây là một khai báo biến mảng
		if (declare instanceof IASTArrayDeclarator) {
			
			IASTArrayModifier[] mdfs = ((IASTArrayDeclarator) declare)
					.getArrayModifiers();
			
			//Duyệt qua các thành phần khai báo cỡ mảng ([0], [1])
			for (int i = mdfs.length - 1; i >= 0; i--) {
				IASTArrayModifier arrMdf = mdfs[i];
				int capacity = 0;
				IASTExpression constant = arrMdf.getConstantExpression();
				
				if (constant != null)
				try {
					capacity = Integer.valueOf(constant.getRawSignature());
				} catch (Exception e){
					throw new RuntimeException("Named length array not support"
							+ arrMdf.getConstantExpression());
				}
				type = new ArrayType(type, capacity);
			}

			assert type instanceof ArrayType;
			var = new ArrayVariable(name, (ArrayType) type);
			
			//Có biểu thức khởi tạo mảng, gán giá trị này cho biến mảng
			if (init instanceof IASTEqualsInitializer){
				IASTInitializerList initList = (IASTInitializerList) 
						((IASTEqualsInitializer) init)
						.getInitializerClause();
				var.setValue(mUtils.parseNode(initList));
			}
		} 
		//Biến thường
		else {
			var = new Variable(name, type);
			
			//Duyệt khi thành phần này được gán giá trị khởi tạo
			if (init instanceof IASTEqualsInitializer){
				IASTInitializerClause initClause = 
						((IASTEqualsInitializer) init)
						.getInitializerClause();
				
				//Giá trị gán là một biểu thức cơ bản của chương trình
				var.setValue(mUtils.parseNode(initClause));
				
			}
		}
		return var;
	}
	
	/**
	 * Trả về một cây cú pháp trừu tượng (Abstract Syntax Tree) 
	 * tương ứng với đoạn mã nguồn<br/>
	 * Các thông số được thiết đặt mặc định
	 * @param source tập tin mã nguồn cần phân tích
	 */
	static IASTTranslationUnit getIASTTranslationUnit(File source) throws IOException {
		return getIASTTranslationUnit(
				Utils.getContentFile(source).toCharArray(), 
				source.getAbsolutePath(), 
				Utils.getExtension(source).equalsIgnoreCase("c") ? 
						GCCLanguage.getDefault() : GPPLanguage.getDefault());
	}
	
	/**
	 * Trả về cây cú pháp trừu tượng với mã nguồn C đơn giản
	 */
	static IASTTranslationUnit getIASTranslationUnit(String source) throws IOException{
		return getIASTTranslationUnit(source.toCharArray(), "", 
				GCCLanguage.getDefault());
	}
	
	private static IASTTranslationUnit getIASTTranslationUnit(char[] source, 
			String filePath, AbstractLanguage lang) throws IOException {
		FileContent reader = FileContent.create(filePath, source);
		Map<String, String> macroDefinitions = new HashMap<>();
		String[] includeSearchPaths = new String[0];
		IScannerInfo scanInfo = new ScannerInfo(macroDefinitions, includeSearchPaths);
		IncludeFileContentProvider fileCreator = 
				IncludeFileContentProvider.getEmptyFilesProvider();
		int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		IParserLogService log = new DefaultLogService();
		
		try {
			return lang.getASTTranslationUnit(
					reader, scanInfo, fileCreator, null, options, log);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/* ------------------TEST------------------- */
	
	public static void main(String[] args) {
		try {
			String filePath = "D:\\Documents\\unit\\delta2.cpp";
			File f = new File(filePath);
			
			IASTTranslationUnit u = getIASTTranslationUnit(f);
			printTree(u, " | ");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * In cây cấu trúc ra màn hình
	 */
	static void printTree(IASTNode n, String s){
		String content = n.getRawSignature().replaceAll("\n", "");
		IASTNode[] child = n.getChildren();
		
		System.out.println(s + content + ", type = " + n.getClass().getSimpleName());
		if (n instanceof IASTCompoundStatement){
			ASTNodeProperty p = n.getPropertyInParent();
			System.out.println(" ** " + p.getName());
		}
		for (IASTNode c: child){
			printTree(c, s + "   ");
		}
		
	}
	
	
//	static void handle(IASTTranslationUnit unit){
//		unit.accept(new ASTVisitor() {
//			{ shouldVisitDeclarations = true; }
//
//			@Override
//			public int visit(IASTDeclaration declaration) {
//
//				if (declaration instanceof IASTFunctionDefinition) {
//					IASTFunctionDefinition fnDefine =
//							(IASTFunctionDefinition) declaration;
//					IASTFunctionDeclarator fnDeclare = fnDefine.getDeclarator();
//
//					System.out.printf("Function: %s, type: %s, comment: %s\n",
//							fnDeclare.getName().getRawSignature(),
//							fnDefine.getDeclSpecifier().getRawSignature(),
//							"Somehow get the comment above function define???"
//							);
//				}
//				return PROCESS_SKIP;
//			}
//
//			@Override
//			public int visit(IASTComment comment) {
//				System.out.println("Comment: " + comment.getRawSignature());
//				return PROCESS_CONTINUE;
//			}
//
//		});
//
//		for (IASTComment cmt: unit.getComments())
//			System.out.println("Comment: " + cmt.getRawSignature());
//	}
}





