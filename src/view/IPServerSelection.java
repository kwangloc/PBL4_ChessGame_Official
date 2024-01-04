package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class IPServerSelection extends JFrame {

	public String serverAddress;
	
	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					IPServerSelection frame = new IPServerSelection();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public IPServerSelection() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1600, 900);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(240, 240, 240));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("SELECT SERVER");
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 30));
		lblNewLabel.setBounds(693, 155, 344, 91);
		contentPane.add(lblNewLabel);
		
		JLabel lblIpAddress = new JLabel("IP address:");
		lblIpAddress.setFont(new Font("Arial", Font.BOLD, 24));
		lblIpAddress.setBounds(556, 274, 141, 37);
		contentPane.add(lblIpAddress);
		
		textField = new JTextField();
		textField.setFont(new Font("Arial", Font.BOLD, 24));
		textField.setBounds(708, 274, 350, 37);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Choose");
		btnNewButton.setForeground(new Color(255, 255, 255));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				serverAddress = textField.getText();
				new FormLogin(serverAddress);
			}
		});
		btnNewButton.setBackground(new Color(64, 128, 128));
		btnNewButton.setFont(new Font("Arial", Font.BOLD, 18));
		btnNewButton.setBounds(678, 374, 149, 37);
		contentPane.add(btnNewButton);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnExit.setBackground(new Color(64, 128, 128));
		btnExit.setForeground(new Color(255, 255, 255));
		btnExit.setFont(new Font("Arial", Font.BOLD, 18));
		btnExit.setBounds(860, 374, 149, 37);
		contentPane.add(btnExit);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
