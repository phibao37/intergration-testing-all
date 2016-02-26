package cdt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
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
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
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
import api.parser.UnitParser;
import cdt.models.CFunction;
import core.Utils;
import core.models.ArrayVariable;
import core.models.Variable;
import core.models.type.ArrayType;

public class CUnitParser extends ASTVisitor implements UnitParser{
	
	{  
		shouldVisitDeclarations = true; 	
	}
	
	private CProject mMain;
	private File mFile;
	private ExpressionUtils mUtils;
	
	@Override
	public void parseUnit(File source, IProject project) {
		mMain = (CProject) project;
		mFile = source;
		mUtils = new ExpressionUtils(project);
		
		try {
			IASTTranslationUnit u = getIASTTranslationUnit(source, mMain.getMarcoMap());
			
			for (IASTPreprocessorMacroDefinition marco: u.getMacroDefinitions()){
				String key = marco.getName().toString();
				String expand = marco.getExpansion();
				mMain.addMarco(key, expand);
			}
			
			u.accept(this);
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
			
			/*
			 * Duyệt qua các khai báo struct 
			 */
			/*else if (spec instanceof IASTCompositeTypeSpecifier){
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
			}*/
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
			
			fn = new CFunction(name, para, mMain.findType(type), fnBody)
				.setDeclareStr(type + " " + fnDeclare.getRawSignature());
			fn.setSourceFile(mFile);
			mMain.addFunction(fn);
		}
		return PROCESS_SKIP;
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
	static IASTTranslationUnit getIASTTranslationUnit(File source, 
			Map<String, String> macroList) throws IOException{
		return getIASTTranslationUnit(
				Utils.getContentFile(source).toCharArray(), 
				source.getAbsolutePath(), macroList,
				Utils.getExtension(source).equalsIgnoreCase("c") ? 
						GCCLanguage.getDefault() : GPPLanguage.getDefault());
	}
	
	/**
	 * Trả về cây cú pháp trừu tượng với mã nguồn C đơn giản
	 */
	static IASTTranslationUnit getIASTTranslationUnit(String source) throws IOException{
		return getIASTTranslationUnit(source.toCharArray(), "", 
				new HashMap<>(), GCCLanguage.getDefault());
	}
	
	private static IASTTranslationUnit getIASTTranslationUnit(char[] source, 
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
