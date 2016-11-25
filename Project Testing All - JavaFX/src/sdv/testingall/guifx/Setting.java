/**
 * Application configuration
 * @file Setting.java
 * @author (SDV)[VuSD]
 * Copyright (C) 2016 SDV, All Rights Reserved.
 */
package sdv.testingall.guifx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jdt.annotation.Nullable;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import sdv.testingall.cdt.loader.ICppLoaderConfig;
import sdv.testingall.core.logger.ILogger;

/**
 * Application configuration
 * 
 * @author VuSD
 *
 * @date 2016-11-25 VuSD created
 */
public class Setting implements Serializable, ICppLoaderConfig {

	private static final long	serialVersionUID	= 9092250455045821743L;
	private static final File	PATH				= new File("configuration.dat");
	private static final int	SETTING_VERSION		= 1;

	public transient SimpleObjectProperty<Charset>		APP_CHARSET				= new SimpleObjectProperty<>();
	public transient SimpleObjectProperty<Locale>		APP_LOCALE				= new SimpleObjectProperty<>();
	public transient SimpleBooleanProperty				CPP_LOG_ERROR_DIRECTIVE	= new SimpleBooleanProperty();
	public transient SimpleListProperty<String>			CPP_CEXTENSION			= new SimpleListProperty<>();
	public transient SimpleListProperty<String>			CPP_CPPEXTENSION		= new SimpleListProperty<>();
	public transient SimpleListProperty<String>			CPP_INCLUDE_DIR			= new SimpleListProperty<>();
	public transient SimpleMapProperty<String, String>	CPP_MARCO_MAP			= new SimpleMapProperty<>();

	private transient ResourceBundle	appRes;
	private transient ILogger			logger;

	private int setting_version = SETTING_VERSION;

	/**
	 * Initialize default value if no configuration file found
	 */
	private Setting()
	{
		APP_CHARSET.set(Charset.defaultCharset());
		APP_LOCALE.set(Locale.ENGLISH);
		CPP_LOG_ERROR_DIRECTIVE.set(true);
		CPP_CEXTENSION.set(FXCollections.observableArrayList(".c"));
		CPP_CPPEXTENSION.set(FXCollections.observableArrayList(".cpp", ".cc", ".cxx", ".c++", ".cp"));
		CPP_INCLUDE_DIR.set(FXCollections.observableArrayList());
		CPP_MARCO_MAP.set(FXCollections.observableHashMap());
	}

	@Override
	public @Nullable Charset getFileCharset()
	{
		return APP_CHARSET.get();
	}

	@Override
	public ILogger getLogger()
	{
		return logger;
	}

	@Override
	public void setLogger(ILogger logger)
	{
		this.logger = logger;
	}

	@Override
	public String resString(String key)
	{
		return appRes.getString(key);
	}

	@Override
	public Map<String, String> getMarcoMap()
	{
		return CPP_MARCO_MAP;
	}

	@Override
	public void setMarcoMap(Map<String, String> marcoMap)
	{
		CPP_MARCO_MAP.clear();
		CPP_MARCO_MAP.putAll(marcoMap);
	}

	@Override
	public String[] getIncludeDirs()
	{
		return CPP_INCLUDE_DIR.toArray(new String[CPP_INCLUDE_DIR.size()]);
	}

	@Override
	public void setIncludeDirs(List<String> includeDirs)
	{
		CPP_INCLUDE_DIR.clear();
		CPP_INCLUDE_DIR.addAll(includeDirs);
	}

	@Override
	public List<String> getListCExt()
	{
		return CPP_CEXTENSION;
	}

	@Override
	public void setListCExt(List<String> listCExt)
	{
		CPP_CEXTENSION.clear();
		CPP_CEXTENSION.addAll(listCExt);
	}

	@Override
	public List<String> getListCppExt()
	{
		return CPP_CPPEXTENSION;
	}

	@Override
	public void setListCppExt(List<String> listCppExt)
	{
		CPP_CPPEXTENSION.clear();
		CPP_CPPEXTENSION.addAll(listCppExt);
	}

	@Override
	public boolean shouldLogErrorDirective()
	{
		return CPP_LOG_ERROR_DIRECTIVE.get();
	}

	@Override
	public void setLogErrorDirective(boolean logErrorDrt)
	{
		CPP_LOG_ERROR_DIRECTIVE.set(logErrorDrt);
	}

	/**
	 * Save setting data to file
	 * 
	 * @throws IOException
	 *             exception during saving data
	 */
	public void save() throws IOException
	{
		try (ObjectOutputStream settingWriter = new ObjectOutputStream(new FileOutputStream(PATH))) {
			settingWriter.writeObject(this);
		}
	}

	/**
	 * Load setting from file or create a new one
	 * 
	 * @return Setting object
	 */
	public static Setting loadSetting()
	{
		Setting setting = null;

		try (ObjectInputStream settingReader = new ObjectInputStream(new FileInputStream(Setting.PATH))) {
			setting = (Setting) settingReader.readObject();
			// System.out.println("Load from file");
		} catch (Exception e) {
			setting = new Setting();
			// System.out.println("Load from default");
		}

		setting.appRes = ResourceBundle.getBundle("sdv.testingall.guifx.ProjectTestingAll", setting.APP_LOCALE.get());
		return setting;
	}

	private void writeObject(ObjectOutputStream s) throws IOException
	{
		s.defaultWriteObject();
		s.writeObject(APP_CHARSET.get().name());
		s.writeObject(APP_LOCALE.get());
		s.writeBoolean(CPP_LOG_ERROR_DIRECTIVE.get());
		s.writeObject(new ArrayList<>(CPP_CEXTENSION));
		s.writeObject(new ArrayList<>(CPP_CPPEXTENSION));
		s.writeObject(new ArrayList<>(CPP_INCLUDE_DIR));
		s.writeObject(new HashMap<>(CPP_MARCO_MAP));
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		if (setting_version != SETTING_VERSION) {
			throw new IOException("Version mismatch");
		}

		APP_CHARSET = new SimpleObjectProperty<>(Charset.forName((String) s.readObject()));
		APP_LOCALE = new SimpleObjectProperty<>((Locale) s.readObject());
		CPP_LOG_ERROR_DIRECTIVE = new SimpleBooleanProperty(s.readBoolean());
		CPP_CEXTENSION = new SimpleListProperty<>(FXCollections.observableArrayList((List<String>) s.readObject()));
		CPP_CPPEXTENSION = new SimpleListProperty<>(FXCollections.observableArrayList((List<String>) s.readObject()));
		CPP_INCLUDE_DIR = new SimpleListProperty<>(FXCollections.observableArrayList((List<String>) s.readObject()));
		CPP_MARCO_MAP = new SimpleMapProperty<>(FXCollections.observableMap((Map<String, String>) s.readObject()));

	}
}
