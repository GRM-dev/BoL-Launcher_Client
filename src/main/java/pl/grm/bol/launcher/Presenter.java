package pl.grm.bol.launcher;

import java.awt.Color;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import pl.grm.bol.launcher.boxes.SettingsDialog;
import pl.grm.bol.launcher.core.ConfigHandler;
import pl.grm.bol.launcher.core.GameStarter;
import pl.grm.bol.launcher.core.SwingWorkersImpl;
import pl.grm.bol.launcher.panels.AdvPanel;
import pl.grm.bol.launcher.panels.GamePanel;
import pl.grm.bol.launcher.panels.LoggedPanel;
import pl.grm.bol.launcher.panels.LoginPanel;
import pl.grm.bol.lib.BLog;

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
	private LoginPanel		loginPanel;
	private LoggedPanel		loggedPanel;
	private AdvPanel		advPanel;
	private GamePanel		gamePanel;
	private String			login;
	private Color			bgColor	= new Color(0, 139, 139);
	private BLog			logger;
	private JTextArea		console;
	
	public Presenter() {
		configHandler = new ConfigHandler(this);
	}
	
	/**
	 * Adds reference to mainWindow.
	 * 
	 * @param mainWindow
	 */
	public void addWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		saveComponentsRefs();
		logger.setConsole(console);
		logger.info("\u4eca\u65e5\u306f.");
		if (configHandler.getConnHandler().isConnected()) {
			logger.info("You are online");
		} else {
			logger.info("You are offline");
		}
		SwingWorkersImpl.setPresenter(this);
	}
	
	/**
	 * Saves references to objects in panels.
	 */
	private void saveComponentsRefs() {
		this.console = mainWindow.getLeftPanel().getConsole();
		this.loginPanel = mainWindow.getRightPanel().getLoginPanel();
		this.loggedPanel = mainWindow.getRightPanel().getLoggedPanel();
		this.advPanel = mainWindow.getRightPanel().getAdvPanel();
		this.gamePanel = mainWindow.getRightPanel().getGamePanel();
	}
	
	public synchronized void pressedLoginButton(String loginT, char[] password) {
		this.login = loginT;
		if (!configHandler.getConnHandler().isConnected()) {
			if (!configHandler.getConnHandler().reconnect(mainWindow)) {
				logger.info("No connection to server!");
				return;
			}
		}
		logger.info("Loging in ...\n");
		SwingWorker<Boolean, Void> worker = SwingWorkersImpl.Login(password);
		worker.execute();
	}
	
	public synchronized void pressedRegisterButton() {
		SwingWorker<Void, Void> worker = SwingWorkersImpl.Register();
		worker.execute();
	}
	
	public synchronized void pressedSettingsButton() {
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				SettingsDialog setDBox = new SettingsDialog(Presenter.this);
				setDBox.setVisible(true);
				setDBox.setModal(true);
				return null;
			}
			
			@Override
			protected void done() {
				logger.info("Settings");
			}
		};
		worker.execute();
	}
	
	public synchronized void pressedStartButton() {
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				gamePanel.getLaunchButton().setEnabled(false);
				JProgressBar progressBar = gamePanel.getProgressBar();
				progressBar.setValue(5);
				if (!configHandler.isUpToDate("Game")) {
					progressBar.setValue(7);
					logger.info("Game must be updated!");
					int confirmed = JOptionPane.showConfirmDialog(mainWindow,
							"Are you sure you want to update the game?",
							"Exit Program Message Box", JOptionPane.YES_NO_OPTION);
					if (confirmed == JOptionPane.YES_OPTION) {
						logger.info("Game Updating ...");
						progressBar.setStringPainted(true);
						progressBar.setToolTipText("Updating Game");
						progressBar.setString("Updating Game");
						progressBar.setValue(9);
					} else {
						logger.info("Game update cancelled!");
						return false;
					}
				} else {
					logger.info("Game is up to date");
					progressBar.setValue(20);
					logger.info("Starting game");
				}
				GameStarter gameStarter = new GameStarter();
				gameStarter.start(Presenter.this);
				logger.info("Game update failed!");
				return true;
			}
			
			@Override
			protected void done() {
				try {
					if (super.get()) {
						Thread.sleep(2000L);
						System.exit(0);
					}
				}
				catch (InterruptedException e) {
					logger.log(Level.SEVERE, e.toString(), e);
				}
				catch (ExecutionException e) {
					logger.log(Level.SEVERE, e.toString(), e);
				}
				gamePanel.getLaunchButton().setEnabled(true);
				JProgressBar progressBar = gamePanel.getProgressBar();
				progressBar.setValue(0);
				progressBar.setStringPainted(false);
			}
		};
		worker.execute();
	}
	
	public void pressedLogoutButton() {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				loginPanel.setVisible(true);
				loggedPanel.setVisible(false);
				logger.info("Logout!");
				return null;
			}
		};
		worker.execute();
	}
	
	public MainWindow getMainWindow() {
		return mainWindow;
	}
	
	public Color getBgColor() {
		return this.bgColor;
	}
	
	public JTextArea getConsole() {
		return this.console;
	}
	
	public ConfigHandler getConfigHandler() {
		return configHandler;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogger(BLog logger) {
		this.logger = logger;
	}
}
