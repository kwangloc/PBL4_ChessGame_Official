package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import GUI_Client.PlayerSession;
import GUI_Client.inMatchWithPlayer;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

public class Board extends JPanel implements MouseListener,MouseMotionListener,Runnable {
	public inMatchWithPlayer parentJFrame;
	
	public int tileSize = 85;
	public int cols = 8;
	public int rows = 8;
	public int off = 150;
	
	public Color whiteSquare = new Color(227, 198, 181);
	public Color blackSquare = new Color(157, 105, 53);
	
	ArrayList<Piece> pieceList = new ArrayList<>();
	public Piece selectedPiece; // the piece that you're moving
//	Input input = new Input(this);
	public CheckScanner checkScanner = new CheckScanner(this); // #7
	public int enPassantTile = -1;
	
	BufferedImage image;
	Graphics g;

	public Socket socket;
	
	public String myName;
	public String optName;
	//
	public boolean isWhitePlayer;
//	public static void main(String[] args) {
//		new Board();
//		
//	}
	public static SecretKey secretKey = new SecretKeySpec("MySecretKey12345".getBytes(), "AES");
	public String encryptMessage(String originalMessage) {
	   	 // Create Cipher instance for decryption
	   	try {
	   		Cipher cipher = Cipher.getInstance("AES");
	   		byte[] plaintext = originalMessage.getBytes(StandardCharsets.UTF_8);
	   		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedMessage = cipher.doFinal(plaintext);
	   		String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedMessage);
	        return encryptedBase64;
			} catch (Exception e) {
				
			}
	       return null;
	   }
	public Board(String ipServer, int portServer, inMatchWithPlayer parentJFrame) {
		this.parentJFrame = parentJFrame;
		
		image = new BufferedImage(cols * tileSize + 2*off, rows * tileSize + 2*off, 3);
		g = image.getGraphics();
		
		this.setPreferredSize(new Dimension(cols * tileSize, rows * tileSize));
		
//		this.setTitle("Chess Client");
//		this.setDefaultCloseOperation(3);
//		this.setSize(cols * tileSize + 2*off, rows * tileSize + 2*off);
		//
//		this.getContentPane().setBackground(Color.black);
//		this.setLayout(new GridBagLayout());
//		this.setMinimumSize(new Dimension(1000, 1000));
//		this.setLocationRelativeTo(null);
		
		this.setPreferredSize(new Dimension(cols * tileSize, rows * tileSize));
		//
//		addPieces();
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
//		this.setVisible(true);
//		this.add(this);
//		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		
//		this.setPreferredSize(new Dimension(cols * tileSize, rows * tileSize));
//		this.addMouseListener(input);
//		this.addMouseListener(this);
//		this.addMouseMotionListener(input);
//		this.addMouseMotionListener(this);
		
		
//		addPieces();
		//
		try {
//			this.soc = new Socket("localhost", 4567);
			System.out.println(ipServer + "," + portServer);
			this.socket = new Socket(ipServer, portServer);
			
			PlayerSession playerSession = PlayerSession.getExistedInstance();
			String name = playerSession.getNamePlayer(playerSession.idPlayer);
			System.out.println(isWhitePlayer + " Gui ten cho server: " + name);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(name);
			
			Thread t = new Thread(this); // luồng theo dõi tín hiệu từ server
			t.start();
		} catch (Exception e) {

			System.out.println("loi 103: " + e.getMessage());
		}
		
	}
	
	public void run() {
		// set color player (white or black)
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			//
			String colorPlayer = dis.readUTF();
			if(colorPlayer.equals("You are white player")) {
				isWhitePlayer = true;
			} else {
				isWhitePlayer = false;
			}
			String yourName = dis.readUTF();
			String opponentName = dis.readUTF();
			System.out.println((isWhitePlayer ? "White" : "Black") +", yourName: "+yourName+", opponentName: "+opponentName);
			
			this.myName = yourName;
			this.optName = opponentName;
			
			parentJFrame.putNamePlayer(myName, optName);
			addPieces();
			repaint();
		} catch (Exception e) {
			
		}
		boolean isWhiteTeam = true;
		while(true) {
			try {
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				int col = Integer.parseInt(dis.readUTF());
				int row = Integer.parseInt(dis.readUTF());
				int newCol = Integer.parseInt(dis.readUTF());
				int newRow = Integer.parseInt(dis.readUTF());
				
				
				if (col == -9999)
				{
					JOptionPane.showMessageDialog(parentJFrame, "The opponent quit. You win!");
//					System.exit(0);
					this.parentJFrame.quitMatch(this.parentJFrame.idRoom);
				}
				if (col == -8888) {
					responseDraw();
					continue;
				}
				if (col == -8885) {
					JOptionPane.showMessageDialog(parentJFrame, "The opponent accepted to Match Draw!");
					this.parentJFrame.quitMatch(this.parentJFrame.idRoom);
				}
				if (col == -8880) {
					String message = dis.readUTF();
					this.parentJFrame.updateMessage(message);
					continue;
				}
				System.out.println("Client get: " + col + "," + row + "," + newCol + "," + newRow);
				String colPos = convertColumnPos(col);
				String newColPos = convertColumnPos(newCol);
				int rowPos = convertRowPos(row);
				int newRowPos = convertRowPos(newRow);
				String player = isWhiteTeam == true ? "White" : "Black";
				if (isWhitePlayer == false) {
					col = 7 - col;
					row = 7 - row;
					newCol = 7 - newCol;
					newRow = 7 - newRow;
				}
//				dadanh.add(new Point(ix, iy));
				String nameClient = isWhitePlayer ? "white" : "black";
				System.out.println(nameClient + "Client converted: " + col + "," + row + "," + newCol + "," + newRow);
				makeMove(new Move(this, getPiece(col, row),	 newCol, newRow));
				this.repaint();
				this.parentJFrame.updateHistory(player + ":\t" + colPos + "" + rowPos + " --> " + newColPos + "" + newRowPos);
				isWhiteTeam = !isWhiteTeam;
			} catch (Exception e) {
				// trong trường hợp server ko phản hồi, ko để while(true) chạy vô hạn
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e.printStackTrace();
				}
			}
		}
	}
	public  void updateTheme(Color whiteSquare, Color blackSquare) {
		this.whiteSquare = whiteSquare;
		this.blackSquare = blackSquare;
		this.repaint();
	}
	public void requestDraw() {
		try {
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			
//			dos.writeUTF("-8888");
//			dos.writeUTF("-8888");
//			dos.writeUTF("-8888");
//			dos.writeUTF("-8888");
			dos.writeUTF(encryptMessage("-8888"));
			dos.writeUTF(encryptMessage("-8888"));
			dos.writeUTF(encryptMessage("-8888"));
			dos.writeUTF(encryptMessage("-8888"));
			
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\tKhong the gui yeu cau xin hoa");
		}
	}
	
	public void responseDraw() {
		try {
			int option = JOptionPane.showConfirmDialog(parentJFrame, "The opponent asked for a draw?", "Notification", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(parentJFrame, "Match Draw! Click OK to Exit!");
				
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				
//				dos.writeUTF("-8885");
//				dos.writeUTF("-8885");
//				dos.writeUTF("-8885");
//				dos.writeUTF("-8885");
				dos.writeUTF(encryptMessage("-8885"));
				dos.writeUTF(encryptMessage("-8885"));
				dos.writeUTF(encryptMessage("-8885"));
				dos.writeUTF(encryptMessage("-8885"));
				
				this.parentJFrame.quitMatch(this.parentJFrame.idRoom);
			}
			else {
				return;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\tKhong the nhan yeu cau xin hoa");
		}
	}
	
	public void sendMessage(String message) {
		try {
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			
//			dos.writeUTF("-8880");
//			dos.writeUTF("-8880");
//			dos.writeUTF("-8880");
//			dos.writeUTF("-8880");
//			dos.writeUTF(message);
			
			dos.writeUTF(encryptMessage("-8880"));
			dos.writeUTF(encryptMessage("-8880"));
			dos.writeUTF(encryptMessage("-8880"));
			dos.writeUTF(encryptMessage("-8880"));
			dos.writeUTF(message);
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\tKhong the gui tin nhan cho doi phuong Loi 843");
		}
	}
	
	public String convertColumnPos(int col) {
		switch (col) {
			case 0:
				return "a";
			case 1:
				return "b";
			case 2:
				return "c";
			case 3:
				return "d";
			case 4:
				return "e";
			case 5:
				return "f";
			case 6:
				return "g";
			case 7:
				return "h";
			default:
				return "";
		}
	}
	
	public int convertRowPos(int row) {
		return (8 - row);
	}
	
	public Piece getPiece(int col, int row) {
		for (Piece piece: pieceList) {
			if(piece.col == col && piece.row == row) {
				return piece;
			}
		}
		return null;
	}
	
	public void makeMove(Move move) {
		
		if (move.piece.name.equals("Pawn")) { // En Passant
			movePawn(move);
		} else if (move.piece.name.equals("King")) {
			moveKing(move);
		}
		move.piece.col = move.newCol;
		move.piece.row = move.newRow;
		move.piece.xPos = move.newCol*tileSize;
		move.piece.yPos = move.newRow*tileSize;
	
		move.piece.isFirstMove = false;
		
		capture(move.capture);
		
	}
	// 5/12
	private void moveKing(Move move) {
		if(Math.abs(move.piece.col - move.newCol) == 2) {
			Piece rook;
			if (isWhitePlayer) {
				if (move.piece.col < move.newCol) {
					rook = getPiece(7, move.piece.row);
					rook.col = 5;
				} else {
					rook = getPiece(0, move.piece.row);
					rook.col = 3;
				}
			} else {
				if (move.piece.col < move.newCol) {
					rook = getPiece(7, move.piece.row);
					rook.col = 4;
				} else {
					rook = getPiece(0, move.piece.row);
					rook.col = 2;
				}
			}
			rook.xPos = rook.col*tileSize;
		}
	}
	//
	private void movePawn(Move move) {
		// en passant
		
//		int colorIndex = move.piece.isWhite ? 1 : -1;
		int colorIndex = move.piece.isWhite ? 1 : 1;
		
		if (getTileNum(move.newCol, move.newRow) == enPassantTile) {
			move.capture = getPiece(move.newCol, move.newRow + colorIndex);
		}
		
		if(Math.abs(move.piece.row - move.newRow) == 2) {
			enPassantTile = getTileNum(move.newCol, move.newRow + colorIndex);
		} else {
			enPassantTile = -1;
		}
		
		// promotions
//		colorIndex = move.piece.isWhite ? 0 : 7;
		
		//
		if (isWhitePlayer) {
			colorIndex = move.piece.isWhite ? 0 : 7;
		} else {
			colorIndex = move.piece.isWhite ? 7 : 0;
		}
		//
		if (move.newRow == colorIndex) {
			promotionPawn(move);
		}
		
//		move.piece.col = move.newCol;
//		move.piece.row = move.newRow;
//		move.piece.xPos = move.newCol*tileSize;
//		move.piece.yPos = move.newRow*tileSize;
	
		move.piece.isFirstMove = false;
		
		capture(move.capture);
	}
	
	public void promotionPawn(Move move) {
		pieceList.add(new Queen(this, move.newCol, move.newRow, move.piece.isWhite));
		capture(move.piece);
	}
	
//	public void capture(Move move) {
//		pieceList.remove(move.capture);
//	}
	public void capture(Piece piece) {
		pieceList.remove(piece);
	}
	
	public boolean isValidMove(Move move) {
		if (sameTeam(move.piece, move.capture)) {
			return false;
		}
		
		if(!move.piece.isValidMovement(move.newCol, move.newRow)) {
			return false;
		}
		if(move.piece.moveCollidesWithPiece(move.newCol, move.newRow)) {
			return false;
		}
		// #7 king check and pinned pieces
		if (checkScanner.isKingChecked(move)) {
			return false;
		}
		//
		
		return true;
	}
	
	public boolean sameTeam(Piece p1, Piece p2) {
		if (p1 == null || p2 == null) {
			return false;
		}
		return p1.isWhite == p2.isWhite;
	}
	

	public int getTileNum(int col, int row) {
		return row*rows+col;
	}
	
	Piece findKing(boolean isWhite) {
		for(Piece piece:pieceList) {
			if (isWhite == piece.isWhite && piece.name.equals("King")) {
				return piece;
			}
		}
		return null;
	}
	
	public void addPieces() {
		// WHITE VIEW
		// Black tiles
		if (isWhitePlayer == true) {
			pieceList.add(new Rook(this, 0, 0, false));
			pieceList.add(new Knight(this, 1, 0, false));
			pieceList.add(new Bishop(this, 2, 0, false));
			pieceList.add(new Queen(this, 3, 0, false));
			pieceList.add(new King(this, 4, 0, false));
			pieceList.add(new Bishop(this, 5, 0, false));
			pieceList.add(new Knight(this, 6, 0, false));
			pieceList.add(new Rook(this, 7, 0, false));
			
			pieceList.add(new Pawn(this, 0, 1, false));
			pieceList.add(new Pawn(this, 1, 1, false));
			pieceList.add(new Pawn(this, 2, 1, false));
			pieceList.add(new Pawn(this, 3, 1, false));
			pieceList.add(new Pawn(this, 4, 1, false));
			pieceList.add(new Pawn(this, 5, 1, false));
			pieceList.add(new Pawn(this, 6, 1, false));
			pieceList.add(new Pawn(this, 7, 1, false));
			
			// White tiles
			pieceList.add(new Rook(this, 0, 7, true));
			pieceList.add(new Knight(this, 1, 7, true));
			pieceList.add(new Bishop(this, 2, 7, true));
			pieceList.add(new Queen(this, 3, 7, true));
			pieceList.add(new King(this, 4, 7, true));
			pieceList.add(new Bishop(this, 5, 7, true));
			pieceList.add(new Knight(this, 6, 7, true));
			pieceList.add(new Rook(this, 7, 7, true));
			
			pieceList.add(new Pawn(this, 0, 6, true));
			pieceList.add(new Pawn(this, 1, 6, true));
			pieceList.add(new Pawn(this, 2, 6, true));
			pieceList.add(new Pawn(this, 3, 6, true));
			pieceList.add(new Pawn(this, 4, 6, true));
			pieceList.add(new Pawn(this, 5, 6, true));
			pieceList.add(new Pawn(this, 6, 6, true));
			pieceList.add(new Pawn(this, 7, 6, true));
		} else { // BLACK PLAYER
			// Black tiles
			pieceList.add(new Rook(this, 7, 7, false));
			pieceList.add(new Knight(this, 6, 7, false));
			pieceList.add(new Bishop(this, 5, 7, false));
			pieceList.add(new Queen(this, 4, 7, false));
			pieceList.add(new King(this, 3, 7, false));
			pieceList.add(new Bishop(this, 2, 7, false));
			pieceList.add(new Knight(this, 1, 7, false));
			pieceList.add(new Rook(this, 0, 7, false));
			
			pieceList.add(new Pawn(this, 7, 6, false));
			pieceList.add(new Pawn(this, 6, 6, false));
			pieceList.add(new Pawn(this, 5, 6, false));
			pieceList.add(new Pawn(this, 4, 6, false));
			pieceList.add(new Pawn(this, 3, 6, false));
			pieceList.add(new Pawn(this, 2, 6, false));
			pieceList.add(new Pawn(this, 1, 6, false));
			pieceList.add(new Pawn(this, 0, 6, false));
			
			// White tiles
			pieceList.add(new Rook(this, 7, 0, true));
			pieceList.add(new Knight(this, 6, 0, true));
			pieceList.add(new Bishop(this, 5, 0, true));
			pieceList.add(new Queen(this, 4, 0, true));
			pieceList.add(new King(this, 3, 0, true));
			pieceList.add(new Bishop(this, 2, 0, true));
			pieceList.add(new Knight(this, 1, 0, true));
			pieceList.add(new Rook(this, 0, 0, true));
			
			pieceList.add(new Pawn(this, 7, 1, true));
			pieceList.add(new Pawn(this, 6, 1, true));
			pieceList.add(new Pawn(this, 5, 1, true));
			pieceList.add(new Pawn(this, 4, 1, true));
			pieceList.add(new Pawn(this, 3, 1, true));
			pieceList.add(new Pawn(this, 2, 1, true));
			pieceList.add(new Pawn(this, 1, 1, true));
			pieceList.add(new Pawn(this, 0, 1, true));
		}
	}
	
//	public void paintComponent(Graphics g) {
//		Graphics2D g2d = (Graphics2D) g;
//		// paint board
//		for (int r = 0; r < rows; r++) {
//			for (int c = 0; c < cols; c++) {
//				g2d.setColor((c + r)%2 == 0 ? new Color(227, 198, 181):new Color(157, 105, 53));
//				g2d.fillRect(c*tileSize, r*tileSize, tileSize, tileSize);
//			}
//		}
//		// paint highlight available moves of selectedPiece
//		if(selectedPiece != null) {
//			for (int r = 0; r < rows; r++) {
//				for (int c = 0; c < cols; c++) {
//					if (isValidMove(new Move(this, selectedPiece, c, r))) {
//						g2d.setColor(new Color(68, 180, 57, 190));
//						g2d.fillRect(c*tileSize, r*tileSize, tileSize, tileSize);
//					}
//				}
//			}
//		}
//		// paint pieces
//		for (Piece piece:pieceList) {
//			piece.paint(g2d);
//		}
//	}
	
	public void paint(Graphics g1) {
		Graphics2D g2d = (Graphics2D) g;
		// paint board
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
//				g2d.setColor((c + r)%2 == 0 ? new Color(227, 198, 181):new Color(157, 105, 53));
				g2d.setColor((c + r)%2 == 0 ? whiteSquare:blackSquare);
				g2d.fillRect(c*tileSize, r*tileSize, tileSize, tileSize);
			}
		}
		// paint highlight available moves of selectedPiece
		if(selectedPiece != null) {
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					if (isValidMove(new Move(this, selectedPiece, c, r))) {
						g2d.setColor(new Color(68, 180, 57, 190));
						g2d.fillRect(c*tileSize, r*tileSize, tileSize, tileSize);
					}
				}
			}
		}
		// paint pieces
		for (Piece piece:pieceList) {
			piece.paint(g2d);
		}
		
		g1.drawImage(image, 0, 0, null);
		//
//		AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
//		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//		tx.translate(-image.getWidth(null), -image.getHeight(null));
//		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//		image = op.filter(image, null);
//		g1.drawImage(image, 0, 0, null);
	}
	
	
	
	// Mouse event handling
	@Override
	public void mousePressed(MouseEvent e) {
		int col = e.getX()/this.tileSize;
		int row = e.getY()/this.tileSize;
//		if (isWhitePlayer == false) {
//			col = 7 - col;
//			row = 7 - row;
//		}
		
		Piece pieceXY = this.getPiece(col, row);
		if (pieceXY != null && pieceXY.isWhite == isWhitePlayer) {
			this.selectedPiece = pieceXY;
		}
		this.repaint();
	}
	
	public void mouseDragged(MouseEvent e) {
		if(this.selectedPiece != null) {
			// cập nhật xPos, yPos của selectedPiece
			this.selectedPiece.xPos = e.getX() - this.tileSize/2; // the piece is centered on the mouse
			this.selectedPiece.yPos = e.getY() - this.tileSize/2;
			
			this.repaint();
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		int newCol = e.getX()/this.tileSize;
		int newRow = e.getY()/this.tileSize;
		if(this.selectedPiece != null) {
			Move move = new Move(this, this.selectedPiece, newCol, newRow);
			
			if(this.isValidMove(move)) {
				// thay vi makeMove, gui move cho server
				int oldCol = this.selectedPiece.col;
				int oldRow = this.selectedPiece.row;
				if (isWhitePlayer == false) {
					newCol = 7 - newCol;
					newRow = 7 - newRow;
					oldCol = 7 - oldCol;
					oldRow = 7 - oldRow;
				}
				try {
					DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//					dos.writeUTF(oldCol + "");
//					dos.writeUTF(oldRow + "");
//					dos.writeUTF(newCol + "");
//					dos.writeUTF(newRow + "");
					
					dos.writeUTF(encryptMessage(oldCol+ ""));
					dos.writeUTF(encryptMessage(oldRow+ ""));
					dos.writeUTF(encryptMessage(newCol+ ""));
					dos.writeUTF(encryptMessage(newRow+ ""));
				} catch (Exception e1) {
					
				}
//				this.makeMove(move);
				
				//
			} else {
				// nếu nước đi không hợp lệ, reset xPos, yPos lại như ban đầu
				this.selectedPiece.xPos = this.selectedPiece.col * this.tileSize;
				this.selectedPiece.yPos = this.selectedPiece.row * this.tileSize;
			}
		}
		// nếu nước đi không hợp lệ, reset xPos, yPos lại như ban đầu
		if (this.selectedPiece != null) {
			this.selectedPiece.xPos = this.selectedPiece.col * this.tileSize;
			this.selectedPiece.yPos = this.selectedPiece.row * this.tileSize;
			this.selectedPiece = null;
		}
		this.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}