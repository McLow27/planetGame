package frameGUI;

import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;

public class Frame1 {

	private JFrame frame;
	/**
	 * @wbp.nonvisual location=117,369
	 */


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame1 window = new Frame1();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Frame1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
	    frame.setTitle("MoonGame");
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(800,748);
	    frame.setResizable(false);
	    ImageIcon image = new ImageIcon("img/icon.png");
	    frame.setIconImage(image.getImage());
		frame.setBackground(new Color(255, 255, 255));
		JLabel label = new JLabel(new ImageIcon("img/wallpaper.jpg")); //comment out
		frame.setContentPane(label); // comment out
		frame.setBounds(450, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Join Lobby");
		btnNewButton.setBackground(new Color(255, 153, 255));
		btnNewButton.setBounds(350, 194, 116, 44);
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("");
		ImageIcon img = new ImageIcon(this.getClass().getResource("/globe-icon.png"));
		lblNewLabel.setIcon(img);
		lblNewLabel.setBounds(700, 479, 84, 61);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_2 = new JLabel("");
		ImageIcon img1 = new ImageIcon(this.getClass().getResource("/settings-icon.png"));
		lblNewLabel_2.setIcon(img1);
		lblNewLabel_2.setBounds(740, 11, 44, 44);
		frame.getContentPane().add(lblNewLabel_2);
		
		JButton btnOpenLobby = new JButton("Open Lobby");
		btnOpenLobby.setBackground(new Color(255, 153, 255));
		btnOpenLobby.setBounds(350, 293, 116, 44);
		frame.getContentPane().add(btnOpenLobby);
		
		
		
		
	}
}
