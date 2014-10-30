package pl.grm.boll;

import javax.swing.SwingUtilities;

public class Launcher {
	private static Presenter presenter;
	private static MainWindow mainWindow;

	public static void main(String[] args) {
		presenter = new Presenter();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainWindow = new MainWindow(presenter);
				mainWindow.setVisible(true);
			}
		});
	}
}
