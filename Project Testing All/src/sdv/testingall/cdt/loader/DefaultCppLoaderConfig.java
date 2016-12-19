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

import sdv.testingall.core.logger.ILogger;

/**
 * Configuration during load C/C++ project
 * 
 * @author VuSD
 *
 * @date 2016-10-27 VuSD created
 */
public class DefaultCppLoaderConfig implements ICppLoaderConfig {

	private Map<String, String>	marcoMap;
	private List<String>		includeDirs;

	private List<String>	listCExt;
	private List<String>	listCppExt;

	private boolean	logErrorDrt;
	private ILogger	logger;

	/**
	 * Construct default C/C++ loader configuration
	 */
	@SuppressWarnings("nls")
	public DefaultCppLoaderConfig()
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

	@Override
	public Map<String, String> getMarcoMap()
	{
		return marcoMap;
	}

	public void setMarcoMap(Map<String, String> marcoMap)
	{
		this.marcoMap = marcoMap;
	}

	@Override
	public String[] getIncludeDirs()
	{
		return includeDirs == null ? null : includeDirs.toArray(new String[includeDirs.size()]);
	}

	public void setIncludeDirs(List<String> includeDirs)
	{
		this.includeDirs = includeDirs;
	}

	@Override
	public List<String> getListCExt()
	{
		return listCExt;
	}

	public void setListCExt(List<String> listCExt)
	{
		this.listCExt = listCExt;
	}

	@Override
	public List<String> getListCppExt()
	{
		return listCppExt;
	}

	public void setListCppExt(List<String> listCppExt)
	{
		this.listCppExt = listCppExt;
	}

	@Override
	public ILogger getLogger()
	{
		return logger;
	}

	public void setLogger(ILogger logger)
	{
		this.logger = logger;
	}

	@Override
	public boolean shouldLogErrorDirective()
	{
		return logErrorDrt;
	}

	public void setLogErrorDirective(boolean logErrorDrt)
	{
		this.logErrorDrt = logErrorDrt;
	}
}
