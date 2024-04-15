package bikeRental;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

class ConnectionSingleton {
	private static Connection con;

	public static Connection getConnection(String database) throws SQLException {
		String url = "jdbc:mysql://127.0.0.1:3307/" + database;
		String user = "alumno";
		String password = "alumno";
		if (con == null || con.isClosed()) {
			con = DriverManager.getConnection(url, user, password);
		}
		return con;
	}
}

public class BikeRental {

	private JFrame frmBikerental;
	static private Connection con;
	private JTable tableUsers;
	private JTable tableBikes;
	private JScrollPane scrollPaneUsers;
	private JScrollPane scrollPaneBikes;
	private JTextField textFieldName;
	private JTextField textFieldAge;
	private JTextField textFieldBankAccount;
	private static DefaultTableModel model1;
	private static DefaultTableModel model2;
	private JLabel lblCoduser;
	private JLabel lblCodbike;
	private JButton btnRentBike;
	private JButton btnReturnBike;
	private JButton btnUpdateUser;
	private JButton btnDeleteUser;
	private JButton btnDeleteBike;
	private int selectedUser;
	private int selectedBike;
	private static JComboBox comboBoxCodUserRent;
	private static JComboBox comboBoxCodBikeRent;
	private static JComboBox comboBoxCodUserReturn;
	private JLabel lblCodBikeReturn;

	public static void refresh() {
		Statement stmt;
		ResultSet rs;
		model1.setRowCount(0);
		model2.setRowCount(0);
		try {
			con = ConnectionSingleton.getConnection("bikeRental");
			stmt = con.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM users");
			while (rs.next()) {
				Object[] row = new Object[5];
				row[0] = rs.getInt("coduser");
				row[1] = rs.getString("name");
				row[2] = rs.getInt("age");
				row[3] = rs.getString("bankAccount");
				row[4] = rs.getString("bike");
				model1.addRow(row);
			}
			
			rs = stmt.executeQuery("SELECT * FROM bikes");
			while (rs.next()) {
				Object[] row2 = new Object[3];
				row2[0] = rs.getInt("codbike");
				row2[1] = rs.getBoolean("rented");
				row2[2] = rs.getInt("rating");
				model2.addRow(row2);
			}
			
			comboBoxCodUserRent.removeAllItems();
			comboBoxCodBikeRent.removeAllItems();
			comboBoxCodUserReturn.removeAllItems();
			
			rs = stmt.executeQuery("SELECT coduser FROM users");
			while (rs.next()) {
				int coduser = rs.getInt("coduser");
				comboBoxCodUserRent.addItem(coduser);
			}
			
			rs = stmt.executeQuery("SELECT codbike FROM bikes");
			while (rs.next()) {
				int codbike = rs.getInt("codbike");
				comboBoxCodBikeRent.addItem(codbike);
			}
			
			rs = stmt.executeQuery("SELECT coduser FROM users WHERE bike <> false");
			while (rs.next()) {
				int coduser = rs.getInt("coduser");
				comboBoxCodUserReturn.addItem(coduser);
			}
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.getErrorCode();
			e.printStackTrace();
		}
	}
	
	public static boolean checkER(String text, String er) {
		Pattern pat = Pattern.compile(er);
		Matcher mat = pat.matcher(text);
		return mat.matches();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BikeRental window = new BikeRental();
					window.frmBikerental.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BikeRental() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmBikerental = new JFrame();
		frmBikerental.setTitle("BikeRental");
		frmBikerental.setBounds(100, 100, 1000, 500);
		frmBikerental.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBikerental.getContentPane().setLayout(null);
		
		lblCodBikeReturn = new JLabel("");
		lblCodBikeReturn.setBounds(756, 373, 70, 15);
		frmBikerental.getContentPane().add(lblCodBikeReturn);
		
		comboBoxCodUserRent = new JComboBox();
		comboBoxCodUserRent.setBounds(605, 338, 117, 22);
		frmBikerental.getContentPane().add(comboBoxCodUserRent);
		
		comboBoxCodBikeRent = new JComboBox();
		comboBoxCodBikeRent.setBounds(605, 369, 117, 22);
		frmBikerental.getContentPane().add(comboBoxCodBikeRent);
		
		comboBoxCodUserReturn = new JComboBox();
		comboBoxCodUserReturn.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
					PreparedStatement sel_pstmt = con.prepareStatement("Select bike from users where coduser = ?");
					sel_pstmt.setInt(1, (int) comboBoxCodUserReturn.getSelectedItem());
					ResultSet rs = sel_pstmt.executeQuery();
					if (rs.next()) {
						lblCodBikeReturn.setText(rs.getString("bike"));	
					}
					sel_pstmt.close();
					refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		comboBoxCodUserReturn.setBounds(756, 338, 117, 22);
		frmBikerental.getContentPane().add(comboBoxCodUserReturn);

		model1 = new DefaultTableModel();
		model1.addColumn("Coduser");
		model1.addColumn("Name");
		model1.addColumn("Age");
		model1.addColumn("Bank Account");
		model1.addColumn("Bike");

		model2 = new DefaultTableModel();
		model2.addColumn("Codbike");
		model2.addColumn("Rented");
		model2.addColumn("Rating");

		try {
			con = ConnectionSingleton.getConnection("bikeRental");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM users");
			
			while (rs.next()) {
				Object[] row = new Object[5];
				row[0] = rs.getInt("coduser");
				row[1] = rs.getString("name");
				row[2] = rs.getInt("age");
				row[3] = rs.getString("bankAccount");
				row[4] = rs.getString("bike");
				model1.addRow(row);
			}
			
			rs = stmt.executeQuery("SELECT * FROM bikes");
			while (rs.next()) {
				Object[] row2 = new Object[3];
				row2[0] = rs.getInt("codbike");
				row2[1] = rs.getBoolean("rented");
				row2[2] = rs.getInt("rating");
				model2.addRow(row2);
			}
			comboBoxCodUserRent.removeAllItems();
			comboBoxCodBikeRent.removeAllItems();
			comboBoxCodUserReturn.removeAllItems();
			rs = stmt.executeQuery("SELECT coduser FROM users");
			while (rs.next()) {
				int coduser = rs.getInt("coduser");
				comboBoxCodUserRent.addItem(coduser);
			}
			
			rs = stmt.executeQuery("SELECT codbike FROM bikes");
			while (rs.next()) {
				int codbike = rs.getInt("codbike");
				comboBoxCodBikeRent.addItem(codbike);
			}
			
			rs = stmt.executeQuery("SELECT coduser FROM users WHERE bike <> false");
			while (rs.next()) {
				int coduser = rs.getInt("coduser");
				comboBoxCodUserReturn.addItem(coduser);
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.getErrorCode();
			e.printStackTrace();
		}
		
		
		
		tableUsers = new JTable(model1);
		tableUsers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int i = tableUsers.getSelectedRow();
				selectedUser = (int) model1.getValueAt(i, 0);
				textFieldName.setText(model1.getValueAt(i, 1).toString());
				textFieldAge.setText(model1.getValueAt(i, 2).toString());
				textFieldBankAccount.setText(model1.getValueAt(i, 3).toString());
			}
		});
		tableUsers.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		scrollPaneUsers = new JScrollPane(tableUsers);
		scrollPaneUsers.setBounds(60, 61, 405, 220);
		frmBikerental.getContentPane().add(scrollPaneUsers);

		tableBikes = new JTable(model2);
		tableBikes.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        int i = tableBikes.getSelectedRow();
		        selectedBike = (int) model2.getValueAt(i, 0);
		    }
		});
		tableBikes.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		scrollPaneBikes = new JScrollPane(tableBikes);
		scrollPaneBikes.setBounds(525, 61, 405, 220);
		frmBikerental.getContentPane().add(scrollPaneBikes);

		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(110, 306, 70, 15);
		frmBikerental.getContentPane().add(lblName);

		JLabel lblNewLabel = new JLabel("Age:");
		lblNewLabel.setBounds(110, 342, 70, 15);
		frmBikerental.getContentPane().add(lblNewLabel);

		textFieldName = new JTextField();
		textFieldName.setBounds(237, 304, 160, 19);
		frmBikerental.getContentPane().add(textFieldName);
		textFieldName.setColumns(10);

		textFieldAge = new JTextField();
		textFieldAge.setBounds(237, 340, 160, 19);
		frmBikerental.getContentPane().add(textFieldAge);
		textFieldAge.setColumns(10);

		JLabel lblBankAccount = new JLabel("Bank Account:");
		lblBankAccount.setBounds(110, 373, 100, 15);
		frmBikerental.getContentPane().add(lblBankAccount);

		textFieldBankAccount = new JTextField();
		textFieldBankAccount.setBounds(237, 371, 160, 19);
		frmBikerental.getContentPane().add(textFieldBankAccount);
		textFieldBankAccount.setColumns(10);

		JButton btnAddUser = new JButton("Add User");
		btnAddUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					con = ConnectionSingleton.getConnection("bikeRental");
					PreparedStatement ins_pstmt = con
							.prepareStatement("insert into users (name, age, bankAccount) VALUES (?, ?, ?)");
					ins_pstmt.setString(1, textFieldName.getText());
					ins_pstmt.setInt(2, Integer.parseInt(textFieldAge.getText()));
					if (checkER(textFieldBankAccount.getText(), "^[A-Z]{2}[0-9]{22}$")) {
						ins_pstmt.setString(3, textFieldBankAccount.getText());
						int rowsInserted = ins_pstmt.executeUpdate();
						ins_pstmt.close();
						refresh();
					} else {
		                JOptionPane.showMessageDialog(frmBikerental, "The bank account number does not comply with the required format.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnAddUser.setBounds(60, 417, 125, 25);
		frmBikerental.getContentPane().add(btnAddUser);

		JButton btnAddBike = new JButton("Add Bike");
		btnAddBike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					con = ConnectionSingleton.getConnection("bikeRental");
					PreparedStatement ins_pstmt = con.prepareStatement("insert into bikes () values ();");
					int rowsInserted = ins_pstmt.executeUpdate();
					ins_pstmt.close();
					refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnAddBike.setBounds(605, 301, 117, 25);
		frmBikerental.getContentPane().add(btnAddBike);

		lblCoduser = new JLabel("CodUser:");
		lblCoduser.setBounds(525, 342, 70, 15);
		frmBikerental.getContentPane().add(lblCoduser);

		lblCodbike = new JLabel("CodBike:");
		lblCodbike.setBounds(525, 373, 70, 15);
		frmBikerental.getContentPane().add(lblCodbike);

		btnRentBike = new JButton("Rent Bike");
		btnRentBike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
		                	PreparedStatement updBike_pstmt = con.prepareStatement("UPDATE bikes SET rented = true WHERE codbike = ?");
		                	updBike_pstmt.setInt(1, (int) comboBoxCodBikeRent.getSelectedItem());
		                	updBike_pstmt.executeUpdate();
		                	
		                	PreparedStatement upd_user_pstmt = con.prepareStatement("UPDATE users SET bike = ?, rentalStartTime = ? WHERE coduser = ?");
		                	upd_user_pstmt.setInt(1, (int) comboBoxCodBikeRent.getSelectedItem());
		                	upd_user_pstmt.setString(2, LocalDateTime.now().toString());
		                	upd_user_pstmt.setInt(3, (int) comboBoxCodUserRent.getSelectedItem());
		                	upd_user_pstmt.executeUpdate();
		                	upd_user_pstmt.close();
		                	updBike_pstmt.close();
							refresh();
		                
		            
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnRentBike.setBounds(605, 402, 117, 25);
		frmBikerental.getContentPane().add(btnRentBike);
		
		JLabel lblCost = new JLabel("");
		lblCost.setForeground(Color.RED);
		lblCost.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCost.setBounds(746, 439, 114, 15);
		frmBikerental.getContentPane().add(lblCost);

		btnReturnBike = new JButton("Return Bike");
		btnReturnBike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					PreparedStatement updUser_pstmt = con
							.prepareStatement("UPDATE users SET bike = null WHERE coduser = ?");
					updUser_pstmt.setInt(1, (int) comboBoxCodUserReturn.getSelectedItem());
					updUser_pstmt.executeUpdate();
					updUser_pstmt.close();
					PreparedStatement updBike_pstmt = con.prepareStatement("UPDATE bikes SET rented = false WHERE codbike = ?");
					updBike_pstmt.setInt(1, selectedBike);
					updBike_pstmt.executeQuery();
					updBike_pstmt.close();
					
					
					PreparedStatement sel_pstmt = con.prepareStatement("SELECT rentalStartTime FROM users WHERE coduser = ?");
		            sel_pstmt.setInt(1, (int) comboBoxCodUserReturn.getSelectedItem());
		            ResultSet rs = sel_pstmt.executeQuery();
		            LocalDateTime rentalStartTime = null;
		            if (rs.next()) {
		                rentalStartTime = LocalDateTime.parse(rs.getString("rentalStartTime"));
		            }
		            sel_pstmt.close();
		         
		            long durationSeconds = Duration.between(rentalStartTime, LocalDateTime.now()).getSeconds();
		            double rentalCost = durationSeconds * 0.02;
		            lblCost.setText(rentalCost + "€");
					
					String[] options = {"1", "2", "3", "4", "5"};
		            JComboBox<String> comboBox = new JComboBox<>(options);
		            int option = JOptionPane.showOptionDialog(null, comboBox, "Select Rating",
		                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		            if (option == JOptionPane.OK_OPTION) {
		                int selectedRating = Integer.parseInt((String) comboBox.getSelectedItem());
		                
		                PreparedStatement upd_pstmt = con.prepareStatement("UPDATE bikes SET rating = ? WHERE codbike = ?");
		                upd_pstmt.setInt(1, selectedRating);
		                upd_pstmt.setInt(2, (int) comboBoxCodUserReturn.getSelectedItem());
		                upd_pstmt.executeUpdate();
		                upd_pstmt.close();
		            }
		            refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnReturnBike.setBounds(756, 402, 117, 25);
		frmBikerental.getContentPane().add(btnReturnBike);

		btnUpdateUser = new JButton("Update User");
		btnUpdateUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					PreparedStatement upd_pstmt = con.prepareStatement("UPDATE users SET name = ?, age = ?, bankAccount = ? WHERE coduser = ?");
					upd_pstmt.setString(1, textFieldName.getText());
					upd_pstmt.setInt(2, Integer.parseInt(textFieldAge.getText()));
					if (checkER(textFieldBankAccount.getText(), "^[A-Z]{2}[0-9]{22}$")) {
						upd_pstmt.setString(3, textFieldBankAccount.getText());
						upd_pstmt.setInt(4, selectedUser);
						int rowsUpdated = upd_pstmt.executeUpdate();
						upd_pstmt.close();
						refresh();
					}
	                JOptionPane.showMessageDialog(frmBikerental, "The bank account number does not comply with the required format.", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnUpdateUser.setBounds(201, 417, 125, 25);
		frmBikerental.getContentPane().add(btnUpdateUser);

		btnDeleteUser = new JButton("Delete User");
		btnDeleteUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					PreparedStatement dele_pstmt = con.prepareStatement("DELETE FROM users WHERE coduser = ?");
					dele_pstmt.setInt(1, selectedUser);
					int rowsDeleted =dele_pstmt.executeUpdate();
					dele_pstmt.close();
					refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnDeleteUser.setBounds(342, 417, 123, 25);
		frmBikerental.getContentPane().add(btnDeleteUser);

		btnDeleteBike = new JButton("Delete Bike");
		btnDeleteBike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					PreparedStatement dele_pstmt = con.prepareStatement("DELETE FROM bikes WHERE codbike = ?");
					dele_pstmt.setInt(1, selectedBike);
					int rowsDeleted =dele_pstmt.executeUpdate();
					dele_pstmt.close();
					refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnDeleteBike.setBounds(756, 301, 117, 25);
		frmBikerental.getContentPane().add(btnDeleteBike);
	}
}
