package cdt.visitor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;

import cdt.models.CFunction;
import cdt.models.CType;
import core.Utils;
import core.models.ArrayVariable;
import core.models.Function;
import core.models.Type;
import core.models.Variable;
import core.models.type.ArrayType;
import core.visitor.UnitVisitor;

/**
 * Bộ phân tích danh sách hàm và biến toàn cục trong ngôn ngữ C
 * @author ducvu
 *
 */
public class CUnitVisitor implements UnitVisitor {
	private ArrayList<Function> mFunctions = new ArrayList<>();
	private ArrayList<Variable> mVariables = new ArrayList<>();
	
	@Override
	public UnitVisitor parseSource(String source, final File file, Object... args) {
		mFunctions.clear();
		mVariables.clear();
		
		IASTTranslationUnit u = getIASTTranslationUnit(file.getAbsolutePath(),
			source.toCharArray());
		
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
					
					fn = new CFunction(name, para, fnBody, CType.parse(type));
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
	public static ArrayList<Variable> parseVariableDeclaration(
			IASTSimpleDeclaration declare){
		IASTDeclSpecifier spec = declare.getDeclSpecifier();
		ArrayList<Variable> gVarList = new ArrayList<Variable>();
		
		for (IASTDeclarator dc: declare.getDeclarators()){
			gVarList.add(parseVariable(dc, spec));
		}
		return gVarList;
	}
	
	/**
	 * Trả về biến ứng với một biến tham số của hàm
	 * @param declare nút AST ứng với khai báo tham số hàm
	 */
	private static Variable parseParameter(IASTParameterDeclaration declare){
		return parseVariable(declare.getDeclarator(), declare.getDeclSpecifier());
	}
	
	/**
	 * Trả về biến tham số từ một khái báo biến
	 * @param declare nội dung khai báo: a = 2, b[] = {1, 2}
	 * @param spec kiểu của biến khai báo: int, float
	 * @return
	 */
	private static Variable parseVariable(IASTDeclarator declare, 
			IASTDeclSpecifier spec){
		Type type = CType.parse(spec.getRawSignature());
		String name = declare.getName().getRawSignature();
		IASTInitializer init = declare.getInitializer();
		Variable var = null;

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
			
			var = new ArrayVariable(name, (ArrayType) type);
			
			//Có biểu thức khởi tạo mảng, gán giá trị này cho biến mảng
			if (init instanceof IASTEqualsInitializer){
				IASTInitializerList initList = (IASTInitializerList) 
						((IASTEqualsInitializer) init)
						.getInitializerClause();
				var.setValue(EpUtils.parseNode(initList));
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
				var.setValue(EpUtils.parseNode(initClause));
				
			}
		}
		return var;
	}
	
	/**
	 * Trả về một cây cú pháp trừu tượng (Abstract Syntax Tree) 
	 * tương ứng với đoạn mã nguồn<br/>
	 * Các thông số được thiết đặt mặc định
	 * @param filePath đường dẫn đến tập tin mã nguồn C
	 * @param code Nội dung của mã nguồn C
	 */
	static IASTTranslationUnit getIASTTranslationUnit(String filePath, char[] code) {
		FileContent reader = FileContent.create(filePath, code);
		Map<String, String> macroDefinitions = new HashMap<String, String>();
		String[] includeSearchPaths = new String[0];
		IScannerInfo scanInfo = new ScannerInfo(macroDefinitions, includeSearchPaths);
		IncludeFileContentProvider fileCreator = 
				IncludeFileContentProvider.getEmptyFilesProvider();
		IIndex index = null;
		int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		IParserLogService log = new DefaultLogService();
		
		try {
			//GCCLanguage
			return GCCLanguage.getDefault().getASTTranslationUnit(
					reader, scanInfo, fileCreator, index, options, log);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/* ------------------TEST------------------- */
	
	public static void main(String[] args) {
		try {
			String filePath = "D:\\Documents\\unit\\delta2.c";
			File f = new File(filePath);
			String source = Utils.getContentFile(f);
			
			printAllNode(source);
			/*for (Function fn : new CUnitVisitor()
				.parseSource(source, f).getFunctionList()){
				System.out.println(fn);
				fn.parseCFG(CBodyVisitor.DEFAULT);
			}*/
			
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
	
	static void printAllNode(String source){
		IASTTranslationUnit u = getIASTTranslationUnit("", source.toCharArray());
		
		//handle(u);
		printTree(u, " | ");
	}
	
	static void handle(IASTTranslationUnit unit){
		unit.accept(new ASTVisitor() {
			{ shouldVisitDeclarations = true; }

			@Override
			public int visit(IASTDeclaration declaration) {
				
				if (declaration instanceof IASTFunctionDefinition) {
					IASTFunctionDefinition fnDefine = 
							(IASTFunctionDefinition) declaration;
					IASTFunctionDeclarator fnDeclare = fnDefine.getDeclarator();
					
					System.out.printf("Function: %s, type: %s, comment: %s\n",
							fnDeclare.getName().getRawSignature(),
							fnDefine.getDeclSpecifier().getRawSignature(),
							"Somehow get the comment above function define???"
							);
				}
				return PROCESS_SKIP;
			}

			@Override
			public int visit(IASTComment comment) {
				System.out.println("Comment: " + comment.getRawSignature());
				return PROCESS_CONTINUE;
			}
			
		});
		
		for (IASTComment cmt: unit.getComments())
			System.out.println("Comment: " + cmt.getRawSignature());
	}
}





