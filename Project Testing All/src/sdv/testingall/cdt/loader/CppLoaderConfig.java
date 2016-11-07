/**
 * Configuration during load C/C++ project
 * @file CppLoaderConfig.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.cdt.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sdv.testingall.core.loader.ILoaderConfig;
import sdv.testingall.core.logger.ILogger;

/**
 * Configuration during load C/C++ project
 * 
 * @author VuSD
 *
 * @date 2016-10-27 VuSD created
 */
public class CppLoaderConfig implements ILoaderConfig {

	private Map<String, String>	marcoMap;
	private List<String>		includeDirs;

	private List<String>	listCExt;
	private List<String>	listCppExt;

	private ILogger logger;

	/**
	 * Construct default C/C++ loader configuration
	 */
	public CppLoaderConfig()
	{
		listCExt = new ArrayList<>();
		listCppExt = new ArrayList<>();

		listCExt.add(".c");
		listCppExt.add(".cpp");
		listCppExt.add(".cc");
		listCppExt.add(".cxx");
		listCppExt.add(".c++");
		listCppExt.add(".cp");
	}

	/**
	 * @return a map from marco name and its expansion
	 */
	public Map<String, String> getMarcoMap()
	{
		return marcoMap;
	}

	/**
	 * @param marcoMap
	 *            a map from marco name and its expansion
	 */
	public void setMarcoMap(Map<String, String> marcoMap)
	{
		this.marcoMap = marcoMap;
	}

	/**
	 * Get additional include directories
	 * 
	 * @return the list of directory to use "#include"
	 */
	public String[] getIncludeDirs()
	{
		return includeDirs == null ? null : includeDirs.toArray(new String[includeDirs.size()]);
	}

	/**
	 * Set additional include directories
	 * 
	 * @param includeDirs
	 *            the list of directory to use "#include"
	 */
	public void setIncludeDirs(List<String> includeDirs)
	{
		this.includeDirs = includeDirs;
	}

	/**
	 * @return the list of extension for C source file
	 */
	public List<String> getListCExt()
	{
		return listCExt;
	}

	/**
	 * @param listCExt
	 *            the list of extension for C source file
	 */
	public void setListCExt(List<String> listCExt)
	{
		this.listCExt = listCExt;
	}

	/**
	 * @return the list of extension for C++ source file
	 */
	public List<String> getListCppExt()
	{
		return listCppExt;
	}

	/**
	 * @param listCppExt
	 *            the list of extension for C++ source file
	 */
	public void setListCppExt(List<String> listCppExt)
	{
		this.listCppExt = listCppExt;
	}

	@Override
	public ILogger getLogger()
	{
		return logger;
	}

	/**
	 * Set the logger
	 * 
	 * @param logger
	 *            the logger object to set
	 */
	public void setLogger(ILogger logger)
	{
		this.logger = logger;
	}
}
