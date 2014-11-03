package pl.grm.boll;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import pl.grm.boll.config.ConfigHandler;
import pl.grm.boll.panels.AdvPanel;
import pl.grm.boll.panels.GamePanel;
import pl.grm.boll.panels.LoginPanel;

/**
 * Presenter from MVP model.
 * <p>
 * Contains all launcher logic and send all data to main model -> Game
 * <p>
 * This presenter has also some kind of temp model.
 */
public class Presenter {
	private MainWindow		mainWindow;
	private ConfigHandler	configHandler;
	private JTextArea		console;
	private LoginPanel		loginPanel;
	private AdvPanel		advPanel;
	private GamePanel		gamePanel;
	private String			login;
	private char[]			password;
	
	/**
	 * Presenter Constructor
	 */
	public Presenter() {
		configHandler = new ConfigHandler(this);
		configHandler.readConfigFile();
	}
	
	/**
	 * Adds reference to mainWindow and its components.
	 * 
	 * @param mainWindow
	 */
	public void addWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		saveComponentsRefs();
	}
	
	private void saveComponentsRefs() {
		this.console = this.mainWindow.getLeftPanel().getConsole();
		this.loginPanel = this.mainWindow.getRightPanel().getLoginPanel();
		this.advPanel = this.mainWindow.getRightPanel().getAdvPanel();
		this.gamePanel = this.mainWindow.getRightPanel().getGamePanel();
	}
	
	public synchronized void pressedLoginButton(ActionEvent e) {
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				return null;
			}
			
			@Override
			protected void done() {
				console.append("Login\n");
			}
		};
		worker.execute();
	}
	
	public synchronized void pressedRegisterButton(String loginT, char[] passwordT) {
		this.login = loginT;
		this.password = passwordT;
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				gamePanel.getProgressBar().setIndeterminate(true);
				return configHandler.register(login, password);
			}
			
			@Override
			protected void done() {
				try {
					console.append(super.get().toString());
				}
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		worker.execute();
	}
	
	public synchronized void pressedSettingsButton() {
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				return null;
			}
			
			@Override
			protected void done() {
				console.append("Opcje\n");
			}
		};
		worker.execute();
	}
	
	public synchronized void pressedStartButton() {
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				return null;
			}
			
			@Override
			protected void done() {
				console.append("Start\n");
			}
		};
		worker.execute();
	}
	
	public MainWindow getMainWindow() {
		return mainWindow;
	}
}
