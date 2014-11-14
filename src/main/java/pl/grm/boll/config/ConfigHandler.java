package pl.grm.boll.config;

import java.awt.Desktop;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextArea;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import pl.grm.boll.Presenter;
import pl.grm.boll.lib.FileOperation;
import pl.grm.boll.math.PasswordHash;
import pl.grm.boll.math.VersionComparator;
import pl.grm.boll.net.rmi.ConnHandler;

public class ConfigHandler {
	public static final String	SERVER_LINK			= "http://grm-dev.pl/";
	public static final String	SERVER_VERSION_LINK	= SERVER_LINK + "bol/version.ini";
	public static final String	APP_DATA			= System.getenv("APPDATA");
	public static final String	BoL_Conf_Loc		= APP_DATA + "\\BOL\\";
	public static final String	logFileName			= "launcher.log";
	public static final String	configFileName		= "config.ini";
	public static final String	LAUNCHER_VERSION	= "0.0.1";
	private Wini				ini;
	private Presenter			presenter;
	private ConnHandler			connHandler;
	private FileHandler			fHandler;
	private BLog				logger;
	
	public ConfigHandler(Presenter presenter) {
		this.presenter = presenter;
		Logger loggerR = null;
		try {
			loggerR = FileOperation.setupLauncherLogger(ConfigHandler.class);
		}
		catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException e) {
			e.printStackTrace();
		}
		logger = new BLog(loggerR);
		connHandler = new ConnHandler(logger);
		presenter.setLogger(logger);
	}
	
	public String getServerConfig(String site, String x, String y) {
		Ini sIni = new Ini();
		URL url;
		try {
			url = new URL(site);
			sIni.load(url);
		}
		catch (MalformedURLException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		catch (InvalidFileFormatException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		return sIni.get(x, y);
	}
	
	public boolean launcherIsUpToDate() {
		Ini sIni = new Ini();
		VersionComparator cmp = new VersionComparator();
		try {
			URL url = new URL(SERVER_VERSION_LINK);
			sIni.load(url);
			String sVersion = sIni.get("Launcher", "last_version");
			String lVersion = ini.get("Launcher", "version");
			
			int result = cmp.compare(sVersion, lVersion);
			if (result <= 0) {
				return true;
			} else if (result > 0) { return false; }
		}
		catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		catch (InvalidFileFormatException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		return false;
	}
	
	public static void openWebpage(String urlString) {
		try {
			Desktop.getDesktop().browse(new URL(urlString).toURI());
		}
		catch (Exception e) {
			
		}
	}
	
	public Boolean login(String login, char[] password) {
		String pass = new String(password);
		String salt = connHandler.getSalt(login);
		try {
			// String hash = Hashing.hash(pass, "SHA-256", salt);
			String hash = PasswordHash.createHash(pass, salt);
			return connHandler.login(login, hash);
			// return connHandler.login(login,
			// Hashing.hash(new String(password), "SHA-256"));
		}
		catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void setConsole(JTextArea console) {
		logger.setConsole(console);
	}
	
	public ConnHandler getConnHandler() {
		return this.connHandler;
	}
	
	public BLog getLogger() {
		return this.logger;
	}
	
	public Wini getIni() {
		return ini;
	}
	
	public void setIni(Wini ini) {
		this.ini = ini;
	}
}
