package cdt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.model.AbstractLanguage;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.parser.IMacroDictionary;
import org.eclipse.cdt.internal.core.parser.SavedFilesProvider;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;

import api.IProject;
import api.models.IType;
import api.parser.IProjectParser;
import cdt.models.CFunction;
import cdt.models.CProjectNode;
import core.Utils;
import core.models.ArrayVariable;
import core.models.FileInfo;
import core.models.Variable;
import core.models.type.ArrayType;
import core.models.type.ObjectType;

public class CProjectParser extends ASTVisitor implements IProjectParser{
	
	public CProjectParser(){
		super(true);
	}
	
	private CProject mMain;
	private File mFile;
	private ExpressionConverter mUtils;
	private Stack<CProjectNode> stackTreeNode;
	
	@Override
	public void parseSource(File source, IProject project) {
		mMain = (CProject) project;
		mFile = source;
		mUtils = new ExpressionConverter(project);
		
		stackTreeNode = new Stack<>();
		CProjectNode root = new CProjectNode();
		stackTreeNode.push(root);
		
		try {
			IASTTranslationUnit u = getIASTTranslationUnit(source, mMain.getMarcoMap());
			
			for (IASTPreprocessorMacroDefinition marco: u.getMacroDefinitions()){
				String key = marco.getName().toString();
				String expand = marco.getExpansion();
				mMain.addMarco(key, expand);
			}
			
			u.accept(this);
			mMain.putMapProjectStruct(source, root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
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
				for (Variable v: parseVariableDeclaration(declare))
					mMain.addGlobalVar(v);
			}
			
			if (spec instanceof IASTCompositeTypeSpecifier){
				IASTCompositeTypeSpecifier comp = (IASTCompositeTypeSpecifier) spec;
				int key = comp.getKey();
				String name = comp.getName().toString();
				
				//Khai báo struct
				if (key == IASTCompositeTypeSpecifier.k_struct){
					CProjectNode st = new CProjectNode(
							declare, CProjectNode.TYPE_STRUCT, name);
					
					stackTreeNode.peek().addChild(st);
					stackTreeNode.push(st);
				}
				
				else if (key == ICPPASTCompositeTypeSpecifier.k_class){
					CProjectNode cl = new CProjectNode(
							declare, CProjectNode.TYPE_CLASS, name);
					
					stackTreeNode.peek().addChild(cl);
					stackTreeNode.push(cl);
				}
			
				if (key == IASTCompositeTypeSpecifier.k_struct
						|| key == ICPPASTCompositeTypeSpecifier.k_class){
					LinkedHashMap<String, IType> schema = new LinkedHashMap<>();
					
					//Thêm các khai báo thuộc tính thành phần
					for (IASTDeclaration member: comp.getMembers()){
						if (!(member instanceof IASTSimpleDeclaration)) continue;
						
						IASTSimpleDeclaration sd = (IASTSimpleDeclaration) member;
						IType type = mMain.findType(sd.getDeclSpecifier()
								.getRawSignature());
						
						for (IASTDeclarator dc: sd.getDeclarators())
							schema.put(dc.getName().toString(), type);
					}
					
					//if: Phòng trường hợp khai báo anonymous
					if (!name.isEmpty())
						mMain.addLoadedType(new ObjectType(name, schema));
					
					//Có từ khóa typedef, thêm các tên được định nghĩa
					if (comp.getStorageClass() == IASTDeclSpecifier.sc_typedef){
						for (IASTDeclarator dc: declare.getDeclarators())
							mMain.addLoadedType(new ObjectType(dc.getName().toString(), schema));
					} 
				}

				return PROCESS_CONTINUE;
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
			final CFunction fn;
			
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
			
			fn = new CFunction(name, para, mMain.findType(type), fnBody, mMain)
				.setDeclareStr(type + " " + fnDeclare.getRawSignature());
			
			IASTFileLocation loc = fnDeclare.getFileLocation();
			fn.setSourceInfo(new FileInfo(loc.getNodeOffset(),
					loc.getNodeLength(), mFile));
			
			mMain.addFunction(fn);
			stackTreeNode.peek().addChild(new CProjectNode(fn));
		}
		return PROCESS_SKIP;
	}
	
	@Override
	public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
		CProjectNode ns = new CProjectNode(namespaceDefinition);
		
		stackTreeNode.peek().addChild(ns);
		stackTreeNode.push(ns);
		return PROCESS_CONTINUE;
	}



	@Override
	public int leave(ICPPASTNamespaceDefinition namespaceDefinition) {
		stackTreeNode.pop();
		return PROCESS_CONTINUE;
	}
	
	@Override
	public int leave(IASTDeclaration declaration) {
		//Duyệt các khai báo biến đơn giản
		if (declaration instanceof IASTSimpleDeclaration){
			IASTSimpleDeclaration declare = (IASTSimpleDeclaration) declaration;
			if (declare == stackTreeNode.peek().getValue())
				stackTreeNode.pop();
		}
		return PROCESS_CONTINUE;
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
		IType type = mMain.findType(spec.getRawSignature());
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
					//TODO tìm trong #define
					capacity = 0;
//					throw new RuntimeException("Named length array not support"
//							+ arrMdf.getConstantExpression());
				}
				type = new ArrayType(type, capacity);
			}

			assert type instanceof ArrayType;
			var = new ArrayVariable(name, (ArrayType) type);
			
			//Có biểu thức khởi tạo mảng, gán giá trị này cho biến mảng
			if (init instanceof IASTEqualsInitializer){
				//IASTInitializerList initList = (IASTInitializerList) 
				IASTInitializerClause initClause =
				((IASTEqualsInitializer) init)
						.getInitializerClause();
				
				if (initClause instanceof IASTInitializerList)
					var.setValue(mUtils.parseNode(initClause));
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
	static IASTTranslationUnit getIASTTranslationUnit(File source, 
			Map<String, String> macroList) throws IOException{
		return getIASTTranslationUnit(
				Utils.getContentFile(source).toCharArray(), 
				source.getAbsolutePath(), macroList,
				Utils.getExtension(source).equalsIgnoreCase("c") ? 
						GCCLanguage.getDefault() : GPPLanguage.getDefault());
	}
	
	public static IASTTranslationUnit getIASTTranslationUnit(char[] source, 
			String filePath, Map<String, String> macroList, AbstractLanguage lang) {
		FileContent reader = FileContent.create(filePath, source);
		String[] includeSearchPaths = new String[0];
		IScannerInfo scanInfo = new ScannerInfo(macroList, includeSearchPaths);
		IncludeFileContentProvider fileCreator = new IncludeFileProvider();
		int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		IParserLogService log = new DefaultLogService();
		
		try {
			return lang.getASTTranslationUnit(
					reader, scanInfo, fileCreator, null, options, log);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

class IncludeFileProvider extends SavedFilesProvider{

	@Override
	public InternalFileContent getContentForInclusion(String path,
			IMacroDictionary macroDictionary) {
		if (!getInclusionExists(path))
			return null;
		return (InternalFileContent) FileContent.createForExternalFileLocation(path);
	}
	
}
