package edu.utah.nanofab.coralapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Security;
import java.util.Properties;
import java.util.Scanner;

import org.opencoral.util.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoralCrypto {
  
  private String configUrl;
  private Properties Props;
  private String CryptoProvider;
  private String Algorithm;
  private String Transformation;
  private Encryption blackBox;
  private ClassLoader classLoader;
  private InputStream key;
  private InputStream conf;
  private byte[] keyAsBytes;
  private Logger logger;

  public CoralCrypto(String configUrl) {
    this.configUrl = configUrl;
    logger = LoggerFactory.getLogger(CoralCrypto.class);
  }  
  
  public void initialize() {
	    try {
			this.loadProperties();
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException");
			e.printStackTrace();
		}
  }
  
  public boolean checkKeyIsValid() {
	InputStream tempKey = null;	  
	try {
		tempKey = this.getKeyStream();
	} catch (MalformedURLException e) {
		logger.error("MalformedURL: " + e.getMessage());
	} catch (Exception e2) {
		logger.error("Error: " + e2.getMessage());
	}
	return tempKey != null;
  }
  
private void loadProperties() throws MalformedURLException {
    this.conf = this.getConfStream();
	key = this.getKeyStream();

	InputStream tempKey = this.getKeyStream();
    this.keyAsBytes = convertStreamToString(tempKey).getBytes(java.nio.charset.Charset.forName("UTF-8"));

    Props = new Properties();
    String s = null;
    try {
      Props.load(conf);
      s = this.Props.getProperty("CRYPTO_PROVIDER");
      if (s != null) {
          this.CryptoProvider = s.trim();
          logger.debug("CryptoProvider: " + CryptoProvider);
      }

      s = this.Props.getProperty("ALGORITHM");
      if (s != null) {
          this.Algorithm = s.trim();
          logger.debug("Algorithm: " + Algorithm);
      }

      s = this.Props.getProperty("TRANSFORMATION");
      if (s != null) {
          this.Transformation = s.trim();
          logger.debug("Transformation: " + Transformation);
      }
      logger.debug("check if key is null? " + (key == null));
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());       
      this.blackBox = new Encryption("encrypt", this.CryptoProvider,
        this.Algorithm, this.Transformation, key);
      logger.debug("check if Encryption class is initialized? " + (this.blackBox.isInitialized()));
      if (!this.blackBox.isInitialized()) {
    	  logger.error("Cannot create JCE Provider. (http://www.bouncycastle.org/wiki/display/JA1/Provider+Installation)");
      }
    } catch (IOException e) {
            System.err.println("Error initializing crypto properties");
      e.printStackTrace();
    }
  }
  
  private InputStream getKeyStream() throws MalformedURLException {
		URLClassLoader loader = new URLClassLoader(new URL[]{new URL(configUrl)});
		return loader.getResourceAsStream("certs/Coral.key");
  }

  private InputStream getConfStream() throws MalformedURLException {
	URLClassLoader loader = new URLClassLoader(new URL[]{new URL(configUrl)});
	return loader.getResourceAsStream("coral.conf");
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
    
    Scanner s = null;
    try {
      s = new Scanner(is).useDelimiter("\\A");
      return s.hasNext() ? s.next() : "";
    } catch (java.lang.NullPointerException npe) {
      return "";
    } finally {
      if (s != null) {
        s.close();
      }
    }
  }

  private void logProperties()
  {
    System.out.println("PROVIDER: " + CryptoProvider);
    System.out.println("ALGORITHM: " + Algorithm);
    System.out.println("TRANSFORMATION: " + Transformation);

    System.out.println("Key loaded: ");
    if (key == null) {
      System.err.println("Unable to load key certs/Coral.key");
    }
    System.out.println(javax.xml.bind.DatatypeConverter.printBase64Binary(keyAsBytes));
  }
  
}
