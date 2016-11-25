/**
 * Interface for C/C++ loader config
 * @file ICppLoaderConfig.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.loader;

import java.util.List;
import java.util.Map;

import sdv.testingall.core.loader.ILoaderConfig;

/**
 * Interface for C/C++ loader config
 * 
 * @author VuSD
 *
 * @date 2016-11-25 VuSD created
 */
public interface ICppLoaderConfig extends ILoaderConfig {

	/**
	 * @return a map from marco name and its expansion
	 */
	Map<String, String> getMarcoMap();

	/**
	 * @param marcoMap
	 *            a map from marco name and its expansion
	 */
	void setMarcoMap(Map<String, String> marcoMap);

	/**
	 * Get additional include directories
	 * 
	 * @return the list of directory to use "#include"
	 */
	String[] getIncludeDirs();

	/**
	 * Set additional include directories
	 * 
	 * @param includeDirs
	 *            the list of directory to use "#include"
	 */
	void setIncludeDirs(List<String> includeDirs);

	/**
	 * @return the list of extension for C source file
	 */
	List<String> getListCExt();

	/**
	 * @param listCExt
	 *            the list of extension for C source file
	 */
	void setListCExt(List<String> listCExt);

	/**
	 * @return the list of extension for C++ source file
	 */
	List<String> getListCppExt();

	/**
	 * @param listCppExt
	 *            the list of extension for C++ source file
	 */
	void setListCppExt(List<String> listCppExt);

	/**
	 * Get whether to log error when C/C++ <code>#error</code> directive is active
	 * 
	 * @return log state
	 */
	boolean shouldLogErrorDirective();

	/**
	 * Set whether to log error when C/C++ <code>#error</code> directive is active
	 * 
	 * @param logErrorDrt
	 *            log state
	 */
	void setLogErrorDirective(boolean logErrorDrt);

}