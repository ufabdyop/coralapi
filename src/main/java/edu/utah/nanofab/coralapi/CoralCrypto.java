package edu.utah.nanofab.coralapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.opencoral.gui.LabFrame;
import org.opencoral.util.Encryption;

public class CoralCrypto {
	private String configUrl;
	private Properties Props;
	private String CryptoProvider;
	private String Algorithm;
	private String Transformation;
	private Encryption blackBox;
	private ClassLoader classLoader;

	public CoralCrypto(String configUrl) {
		this.configUrl = configUrl;
		this.loadProperties();
	}
	
	private void loadProperties() {
		ClassLoader cl = this.getClassLoader();
		this.classLoader = cl;
		InputStream key = cl.getResourceAsStream("certs/Coral.key");
		InputStream conf = cl.getResourceAsStream("coral.conf");
		Props = new Properties();
		String s = null;
		try {
			Props.load(conf);
			s = this.Props.getProperty("CRYPTO_PROVIDER");
			if (s != null) {
			    this.CryptoProvider = s.trim();
			}

			s = this.Props.getProperty("ALGORITHM");
			if (s != null) {
			    this.Algorithm = s.trim();
			}

			s = this.Props.getProperty("TRANSFORMATION");
			if (s != null) {
			    this.Transformation = s.trim();
			}
			this.blackBox = new Encryption("encrypt", this.CryptoProvider,
				this.Algorithm, this.Transformation, key);
			this.logProperties();
		} catch (IOException e) {
		        System.err.println("Error initializing crypto properties");
			e.printStackTrace();
		}
	}
	
	public ClassLoader getClassLoader() {
		ClassLoader result = null;
		try {
			//see if config.jar is already in classpath
			ClassLoader cl = this.getClass().getClassLoader();
			InputStream conf = cl.getResourceAsStream("coral.conf");
			if (conf == null) {
				result = new URLClassLoader(new URL[]{new URL(configUrl)});
			} else {
				conf.close();
				result = this.getClass().getClassLoader();
			}
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		return result;
	}
	
	public byte[] encrypt(String source) {
		try {
			return this.blackBox.encrypt(source);
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[]{};
		}
	}
	
	/**
	 * From sTackOverflow: http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
	 * @param InputStream is to convert to a string
	 * @return the string
	 */
	private static String convertStreamToString(java.io.InputStream is) {
		try {
			java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		} catch (java.lang.NullPointerException npe) {
			return "";
		}
	}

	private void logProperties()
	{
              System.out.println("PROVIDER: " + CryptoProvider);
              System.out.println("ALGORITHM: " + Algorithm);
              System.out.println("TRANSFORMATION: " + Transformation);

	      System.out.println("Key loaded: ");
		InputStream key = classLoader.getResourceAsStream("certs/Coral.key");
		byte[] keyAsBytes = convertStreamToString(key).getBytes(java.nio.charset.Charset.forName("UTF-8"));
		System.out.println(javax.xml.bind.DatatypeConverter.printBase64Binary(keyAsBytes));
	      try {
		key.close();
	      } catch (IOException e) {
			System.err.println("Cannot close input stream for key");
	      }
	}
	
}
