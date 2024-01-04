package GUI_Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import main.Board;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.SwingConstants;

public class inMatchWithPlayer extends JFrame {
	public String ipServer;
	int portServer;
	public int idRoom;
	
	public matchFeaturesPanel matchFeaturesPanel;
	Board board;
	
	
	private JPanel contentPane;
	private JPanel panelDraw;
	// theme
	private JLabel lblNewLabelTheme = new JLabel("Theme:");
	JComboBox<String> comboBox = new JComboBox<String>();;
	DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
	// Label Name
	public JLabel lblYourName;
	public JLabel lblOpponentName;
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					inMatchWithPlayer frame = new inMatchWithPlayer();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 * @wbp.parser.constructor
	 */
	public inMatchWithPlayer(int idRoom, String ipServer, int portServer) {
		this.ipServer = ipServer;
		this.portServer = portServer;
		this.idRoom = idRoom;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1600, 1200);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(64, 128, 128));
//		contentPane.setBackground(new Color(72,61,139));
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Chess game");
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 30));
		lblNewLabel.setBounds(727, -8, 279, 70);
		contentPane.add(lblNewLabel);
		// label Name
		lblYourName = new JLabel("Name");
		lblYourName.setForeground(new Color(255, 128, 128));
		lblYourName.setFont(new Font("Arial", Font.BOLD, 20));
		lblYourName.setHorizontalAlignment(SwingConstants.CENTER);
		lblYourName.setBounds(10, 680, 105, 40);
		contentPane.add(lblYourName);
		
		lblOpponentName = new JLabel("Name");
		lblOpponentName.setForeground(new Color(255, 128, 128));
		lblOpponentName.setHorizontalAlignment(SwingConstants.CENTER);
		lblOpponentName.setFont(new Font("Arial", Font.BOLD, 20));
		lblOpponentName.setBounds(10, 90, 105, 40);
		contentPane.add(lblOpponentName);
		//

		// Board
		board = new Board(ipServer, portServer, this);
		board.setBounds(162, 53, 680, 680);
		contentPane.add(board);
		// Theme
		
		lblNewLabelTheme.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabelTheme.setBounds(162, 749, 61, 17);
		contentPane.add(lblNewLabelTheme);
		
		comboBox.setBounds(228, 743, 110, 32);
		contentPane.add(comboBox);
		
		comboBoxModel.addElement("Default");
		comboBoxModel.addElement("Red");
		comboBoxModel.addElement("Green");
		comboBoxModel.addElement("Blue");
		comboBox.setModel(comboBoxModel);
		
		JButton btnNewButton = new JButton("OK");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 8));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//						System.out.println(comboBox.getSelectedItem().toString());
				String themeOption = comboBox.getSelectedItem().toString();
				Color whiteSquare = new Color(227, 198, 181);
				Color blackSquare = new Color(157, 105, 53);
				switch (themeOption) {
					case "Red":
//								whiteSquare = new Color(227, 198, 181);
						blackSquare = new Color(153, 51, 51);
						break;
					case "Green":
//								whiteSquare = new Color(227, 198, 181);
						blackSquare = new Color(106, 153, 78);
						break;
					case "Blue":
						whiteSquare = new Color(255, 232, 214);
						blackSquare = new Color(26, 117, 159);
						break;
					default:
						break;
				}
				board.updateTheme(whiteSquare, blackSquare);
			}
		});
		btnNewButton.setBackground(new Color(192, 192, 192));
		btnNewButton.setBounds(348, 747, 45, 27);
		contentPane.add(btnNewButton);
		
		JButton btnDraw = new JButton("Draw");
		btnDraw.setBackground(new Color(255, 128, 128));
		btnDraw.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnDraw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelDraw.setVisible(true);
			}
		});
		btnDraw.setBounds(719, 743, 123, 32);
		contentPane.add(btnDraw);
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitMatch(idRoom);
			}
		});
		btnQuit.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnQuit.setBackground(new Color(250, 128, 114));
		btnQuit.setBounds(1378, 10, 123, 40);
		contentPane.add(btnQuit);
		// History
//		moveHistoryPanel moveHistoryPanel = new moveHistoryPanel();
//		moveHistoryPanel.setBounds(750, 100, 600, 315);
//		contentPane.add(moveHistoryPanel);
		// Chat
		matchFeaturesPanel = new matchFeaturesPanel();
		matchFeaturesPanel.setBounds(922, 53, 579, 700);
		
		matchFeaturesPanel.btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = matchFeaturesPanel.textField.getText();
				PlayerSession playerSession = PlayerSession.getExistedInstance();
				
				String namePlayer = playerSession.getNamePlayer(playerSession.idPlayer);
				board.sendMessage(namePlayer+": "+message);
				matchFeaturesPanel.textField.setText("");
			}
		});
		contentPane.add(matchFeaturesPanel);
		// draw
		panelDraw = new JPanel();
		panelDraw.setVisible(false);
		panelDraw.setBounds(397, 743, 312, 50);
		contentPane.add(panelDraw);
		panelDraw.setBackground(new Color(211, 211, 211));
		
		JButton btnYes = new JButton("Yes");
		btnYes.setBounds(134, 11, 81, 32);
		btnYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.requestDraw();
				panelDraw.setVisible(false);
			}
		});
		panelDraw.setLayout(null);
		btnYes.setBackground(new Color(0, 255, 0));
		btnYes.setFont(new Font("Arial", Font.PLAIN, 14));
		panelDraw.add(btnYes);
		
		JButton btnNo = new JButton("No");
		btnNo.setBounds(221, 11, 81, 32);
		btnNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelDraw.setVisible(false);
			}
		});
		btnNo.setFont(new Font("Arial", Font.PLAIN, 14));
		btnNo.setBackground(Color.GREEN);
		panelDraw.add(btnNo);
		
		JLabel lblNewLabel_1 = new JLabel("Are you sure?");
		lblNewLabel_1.setBounds(0, 10, 137, 32);
		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 18));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		panelDraw.add(lblNewLabel_1);
		
		
		setLocationRelativeTo(null);
		//
		this.setVisible(true);
	}
	
	public inMatchWithPlayer(String ipServer, int portServer) {
		this.ipServer = ipServer;
		this.portServer = portServer;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1600, 1200);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(64, 128, 128));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Chess game");
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 30));
		lblNewLabel.setBounds(718, -8, 279, 70);
		contentPane.add(lblNewLabel);
		// label Name
		lblYourName = new JLabel("Name");
		lblYourName.setForeground(new Color(255, 128, 128));
		lblYourName.setFont(new Font("Arial", Font.BOLD, 20));
		lblYourName.setHorizontalAlignment(SwingConstants.CENTER);
		lblYourName.setBounds(10, 680, 105, 40);
		contentPane.add(lblYourName);
		
		lblOpponentName = new JLabel("Name");
		lblOpponentName.setForeground(new Color(255, 128, 128));
		lblOpponentName.setHorizontalAlignment(SwingConstants.CENTER);
		lblOpponentName.setFont(new Font("Arial", Font.BOLD, 20));
		lblOpponentName.setBounds(10, 90, 105, 40);
		contentPane.add(lblOpponentName);
		
		// Board
		board = new Board(ipServer, portServer, this);
		board.setBounds(162, 53, 680, 680);
		contentPane.add(board);
		// History
//		moveHistoryPanel moveHistoryPanel = new moveHistoryPanel();
//		moveHistoryPanel.setBounds(750, 100, 600, 315);
//		contentPane.add(moveHistoryPanel);
		// Chat
		matchFeaturesPanel = new matchFeaturesPanel();
		matchFeaturesPanel.setBounds(901, 53, 600, 700);
		
		matchFeaturesPanel.btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = matchFeaturesPanel.textField.getText();
				PlayerSession playerSession = PlayerSession.getExistedInstance();
				
				String namePlayer = playerSession.getNamePlayer(playerSession.idPlayer);
				board.sendMessage(namePlayer+": "+message);
				matchFeaturesPanel.textField.setText("");
			}
		});
		contentPane.add(matchFeaturesPanel);
		// Theme
		JLabel lblNewLabelTheme = new JLabel("Theme:");
		lblNewLabelTheme.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabelTheme.setBounds(162, 749, 61, 17);
		contentPane.add(lblNewLabelTheme);
		
		comboBox.setBounds(228, 743, 110, 32);
		contentPane.add(comboBox);
		
		comboBoxModel.addElement("Default");
		comboBoxModel.addElement("Red");
		comboBoxModel.addElement("Green");
		comboBoxModel.addElement("Blue");
		comboBox.setModel(comboBoxModel);
		
		JButton btnNewButton = new JButton("OK");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 8));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//								System.out.println(comboBox.getSelectedItem().toString());
				String themeOption = comboBox.getSelectedItem().toString();
				Color whiteSquare = new Color(227, 198, 181);
				Color blackSquare = new Color(157, 105, 53);
				switch (themeOption) {
					case "Red":
//						whiteSquare = new Color(227, 198, 181);
						blackSquare = new Color(153, 51, 51);
						break;
					case "Green":
//										whiteSquare = new Color(227, 198, 181);
						blackSquare = new Color(106, 153, 78);
						break;
					case "Blue":
						whiteSquare = new Color(255, 232, 214);
						blackSquare = new Color(26, 117, 159);
						break;
					default:
						break;
				}
				board.updateTheme(whiteSquare, blackSquare);
			}
		});
		btnNewButton.setBackground(new Color(192, 192, 192));
		btnNewButton.setBounds(348, 747, 45, 27);
		contentPane.add(btnNewButton);
		// draw
//		panelDraw = new JPanel();
//		panelDraw.setBackground(new Color(211, 211, 211));
//		panelDraw.setVisible(false);
//		panelDraw.setBounds(125, 261, 356, 139);
//		matchFeaturesPanel.add(panelDraw);
//		panelDraw.setLayout(null);
//		
//		JButton btnYes = new JButton("Yes");
//		btnYes.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				board.requestDraw();
//				panelDraw.setVisible(false);
//			}
//		});
//		btnYes.setBackground(new Color(0, 255, 0));
//		btnYes.setFont(new Font("Arial", Font.PLAIN, 14));
//		btnYes.setBounds(50, 97, 108, 32);
//		panelDraw.add(btnYes);
//		
//		JButton btnNo = new JButton("No");
//		btnNo.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				panelDraw.setVisible(false);
//			}
//		});
//		btnNo.setFont(new Font("Arial", Font.PLAIN, 14));
//		btnNo.setBackground(Color.GREEN);
//		btnNo.setBounds(204, 97, 108, 32);
//		panelDraw.add(btnNo);
//		
//		JLabel lblNewLabel_1 = new JLabel("Are you sure to draw?");
//		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 18));
//		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
//		lblNewLabel_1.setBounds(81, 33, 201, 32);
//		panelDraw.add(lblNewLabel_1);
//		
//		JButton btnDraw = new JButton("Draw");
//		btnDraw.setBackground(new Color(0, 191, 255));
//		btnDraw.setFont(new Font("Tahoma", Font.PLAIN, 18));
//		btnDraw.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				panelDraw.setVisible(true);
//			}
//		});
//		btnDraw.setBounds(719, 743, 123, 32);
//		contentPane.add(btnDraw);
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitMatchAI();
			}
		});
		btnQuit.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnQuit.setBackground(new Color(250, 128, 114));
		btnQuit.setBounds(1378, 10, 123, 40);
		contentPane.add(btnQuit);
		// Background
//		ImageIcon backgroundIcon = new ImageIcon(selectMode.class.getResource("/res/bg2.jpg")); // Replace with the actual path to your image
//		JLabel backgroundLabel = new JLabel(backgroundIcon);
//        backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
//        getContentPane().add(backgroundLabel);
//        setSize(backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
     
		//
		setLocationRelativeTo(null);
		setResizable(true);
		setVisible(true);
	}
	
	public void updateHistory(String move) {
		matchFeaturesPanel.updateHistory(move);
	}
	
	public void updateMessage(String msg) {
		matchFeaturesPanel.updateMessage(msg);
	}
	
	public void quitMatch(int idRoom) {
		dispose();
		try {
			board.socket.close();
		} catch (Exception e2) {
			System.out.println(e2.getMessage()+"\tThoat");
		}
		PlayerSession playerSession = PlayerSession.getExistedInstance();
		playerSession.emptyRoom(idRoom);
		new selectMode(ipServer);
	}
	
	public void quitMatchAI() {
		dispose();
		try {
			board.socket.close();
		} catch (Exception e2) {
			System.out.println(e2.getMessage()+"\tThoat");
		}
		new selectMode(ipServer);
	}
	
	public void putNamePlayer(String yourName, String opponentName) {
		lblYourName.setText(yourName);
		lblOpponentName.setText(opponentName);
	}
}