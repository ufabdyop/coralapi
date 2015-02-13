package edu.utah.nanofab.coralapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import edu.utah.nanofab.coralapi.exceptions.ConfigLoaderException;

public class ConfigLoader {
	private InputStream conf;
	private String configUrl;
	private Properties props;

	public ConfigLoader(String configUrl) {
		this.configUrl = configUrl;
	}
	public Properties load() throws ConfigLoaderException {
		boolean success = false;
		try {
			this.conf = this.getConfStream();
			props = new Properties();
			props.load(conf);
			success = true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (success) {
			return props;
		} else {
			throw new ConfigLoaderException("Error loading config from " + this.configUrl);
		}
	}
	
	  private InputStream getConfStream() throws MalformedURLException {
		URLClassLoader loader = new URLClassLoader(new URL[]{new URL(configUrl)});
		return loader.getResourceAsStream("coral.conf");
	  }

}
