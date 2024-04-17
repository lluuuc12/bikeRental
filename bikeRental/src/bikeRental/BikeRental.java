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
import java.awt.Font;
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
	private static JComboBox<Integer> comboBoxCodUserRent;
	private static JComboBox<Integer> comboBoxCodBikeRent;
	private static JComboBox<Integer> comboBoxCodUserReturn;
	private static JLabel lblCodBikeReturn;
	private JLabel lblBikes;

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

			rs = stmt.executeQuery("SELECT coduser FROM users where bike is null");
			while (rs.next()) {
				int coduser = rs.getInt("coduser");
				comboBoxCodUserRent.addItem(coduser);
			}

			rs = stmt.executeQuery("SELECT codbike FROM bikes WHERE rented is false");
			while (rs.next()) {
				int codbike = rs.getInt("codbike");
				comboBoxCodBikeRent.addItem(codbike);
			}

			rs = stmt.executeQuery("SELECT coduser FROM users WHERE bike IS NOT NULL");
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

		comboBoxCodUserRent = new JComboBox<Integer>();
		comboBoxCodUserRent.setBounds(605, 338, 117, 22);
		frmBikerental.getContentPane().add(comboBoxCodUserRent);

		comboBoxCodBikeRent = new JComboBox<Integer>();
		comboBoxCodBikeRent.setBounds(605, 369, 117, 22);
		frmBikerental.getContentPane().add(comboBoxCodBikeRent);
		
		comboBoxCodUserReturn = new JComboBox<Integer>();
		comboBoxCodUserReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (comboBoxCodUserReturn.getSelectedItem() != null) {
				try {
					con = ConnectionSingleton.getConnection("bikeRental");
					PreparedStatement stmt = con.prepareStatement("SELECT bike FROM users WHERE coduser = ?");
					stmt.setInt(1, (int) comboBoxCodUserReturn.getSelectedItem());
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						String bike = String.valueOf(rs.getInt("bike"));
						lblCodBikeReturn.setText(bike);
					}
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
				} else {
					lblCodBikeReturn.setText("");
				}
			}
		});
		comboBoxCodUserReturn.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				
			}
		});
		comboBoxCodUserReturn.setBounds(756, 338, 117, 22);
		frmBikerental.getContentPane().add(comboBoxCodUserReturn);

		model1 = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		model1.addColumn("Coduser");
		model1.addColumn("Name");
		model1.addColumn("Age");
		model1.addColumn("Bank Account");
		model1.addColumn("Bike");

		model2 = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		model2.addColumn("Codbike");
		model2.addColumn("Rented");
		model2.addColumn("Rating");

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
					if ((checkER(textFieldBankAccount.getText(), "^[A-Z]{2}[0-9]{22}$") && (checkER(textFieldAge.getText(), "^[0-9]+$")))) {
						ins_pstmt.setInt(2, Integer.parseInt(textFieldAge.getText()));
						ins_pstmt.setString(3, textFieldBankAccount.getText());
						ins_pstmt.executeUpdate();
						ins_pstmt.close();
						refresh();
					} else {
						JOptionPane.showMessageDialog(frmBikerental,
								"The bank account number or age does not comply with the required format.", "Error",
								JOptionPane.ERROR_MESSAGE);
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
					ins_pstmt.executeUpdate();
					ins_pstmt.close();
					refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnAddBike.setBounds(525, 301, 117, 25);
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
					PreparedStatement updBike_pstmt = con
							.prepareStatement("UPDATE bikes SET rented = true WHERE codbike = ?");
					updBike_pstmt.setInt(1, (int) comboBoxCodBikeRent.getSelectedItem());
					updBike_pstmt.executeUpdate();

					PreparedStatement upd_user_pstmt = con
							.prepareStatement("UPDATE users SET bike = ?, rentalStartTime = ? WHERE coduser = ?");
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
					PreparedStatement sel_pstmt = con
							.prepareStatement("SELECT bike FROM users WHERE coduser = ?");
					sel_pstmt.setInt(1, (int) comboBoxCodUserReturn.getSelectedItem());
					ResultSet rs = sel_pstmt.executeQuery();
					if (rs.next()) {
						selectedBike = rs.getInt("bike");
					}
					
					PreparedStatement updUser_pstmt = con
							.prepareStatement("UPDATE users SET bike = null WHERE coduser = ?");
					updUser_pstmt.setInt(1, (int) comboBoxCodUserReturn.getSelectedItem());
					updUser_pstmt.executeUpdate();
					updUser_pstmt.close();
					PreparedStatement updBike_pstmt = con
							.prepareStatement("UPDATE bikes SET rented = false WHERE codbike = ?");
					updBike_pstmt.setInt(1, selectedBike);
					updBike_pstmt.executeUpdate();
					updBike_pstmt.close();

					sel_pstmt = con
							.prepareStatement("SELECT rentalStartTime FROM users WHERE coduser = ?");
					sel_pstmt.setInt(1, (int) comboBoxCodUserReturn.getSelectedItem());
					rs = sel_pstmt.executeQuery();
					LocalDateTime rentalStartTime = null;
					if (rs.next()) {
						rentalStartTime = LocalDateTime.parse(rs.getString("rentalStartTime"));
					}
					sel_pstmt.close();

					long durationSeconds = Duration.between(rentalStartTime, LocalDateTime.now()).getSeconds();
					double rentalCost = durationSeconds * 0.02;
					lblCost.setText(rentalCost + "€");

					String[] options = { "1", "2", "3", "4", "5" };
					JComboBox<String> comboBox = new JComboBox<>(options);
					int option = JOptionPane.showOptionDialog(null, comboBox, "Select Rating",
							JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
					if (option == JOptionPane.OK_OPTION) {
						int selectedRating = Integer.parseInt((String) comboBox.getSelectedItem());

						PreparedStatement upd_pstmt = con
								.prepareStatement("UPDATE bikes SET rating = ? WHERE codbike = ?");
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
					PreparedStatement upd_pstmt = con
							.prepareStatement("UPDATE users SET name = ?, age = ?, bankAccount = ? WHERE coduser = ?");
					upd_pstmt.setString(1, textFieldName.getText());
					if ((checkER(textFieldBankAccount.getText(), "^[A-Z]{2}[0-9]{22}$") && (checkER(textFieldAge.getText(), "^[0-9]+$")))) {
						upd_pstmt.setInt(2, Integer.parseInt(textFieldAge.getText()));
						upd_pstmt.setString(3, textFieldBankAccount.getText());
						upd_pstmt.setInt(4, selectedUser);
						upd_pstmt.executeUpdate();
						upd_pstmt.close();
						refresh();
					}
					JOptionPane.showMessageDialog(frmBikerental,
							"The bank account number or age does not comply with the required format.", "Error",
							JOptionPane.ERROR_MESSAGE);
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
					PreparedStatement checkStmt = con.prepareStatement("SELECT bike FROM users WHERE coduser = ?");
		            checkStmt.setInt(1, selectedUser);
		            ResultSet rs = checkStmt.executeQuery();
		            if (rs.next()) {
		                int bikeId = rs.getInt("bike");
		                if (bikeId != 0) {
		                    JOptionPane.showMessageDialog(frmBikerental, "This user has a rented bike and cannot be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
		                    return; 
		                }
		            }
		            checkStmt.close();
					
					PreparedStatement dele_pstmt = con.prepareStatement("DELETE FROM users WHERE coduser = ?");
					dele_pstmt.setInt(1, selectedUser);
					dele_pstmt.executeUpdate();
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
					PreparedStatement checkStmt = con.prepareStatement("SELECT rented FROM bikes WHERE codbike = ?");
		            checkStmt.setInt(1, selectedBike);
		            ResultSet rs = checkStmt.executeQuery();
		            if (rs.next()) {
		                boolean rented = rs.getBoolean("rented");
		                if (rented) {
		                    JOptionPane.showMessageDialog(frmBikerental, "This bike is currently rented and cannot be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
		                    return; 
		                }
		            }
		            checkStmt.close();
					
					PreparedStatement dele_pstmt = con.prepareStatement("DELETE FROM bikes WHERE codbike = ?");
					dele_pstmt.setInt(1, selectedBike);
					dele_pstmt.executeUpdate();
					dele_pstmt.close();
					refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnDeleteBike.setBounds(813, 301, 117, 25);
		frmBikerental.getContentPane().add(btnDeleteBike);
		
		JLabel lblUser = new JLabel("Users");
		lblUser.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblUser.setBounds(205, 36, 114, 14);
		frmBikerental.getContentPane().add(lblUser);
		
		lblBikes = new JLabel("Bikes");
		lblBikes.setHorizontalAlignment(SwingConstants.CENTER);
		lblBikes.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblBikes.setBounds(681, 36, 93, 14);
		frmBikerental.getContentPane().add(lblBikes);
		
		JButton btnUpdateBike = new JButton("Update Bike");
		btnUpdateBike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String[] options = { "1", "2", "3", "4", "5" };
					JComboBox<String> comboBox = new JComboBox<>(options);
					int option = JOptionPane.showOptionDialog(null, comboBox, "Select Rating",
							JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
					if (option == JOptionPane.OK_OPTION) {
						int selectedRating = Integer.parseInt((String) comboBox.getSelectedItem());

						PreparedStatement upd_pstmt = con
								.prepareStatement("UPDATE bikes SET rating = ? WHERE codbike = ?");
						upd_pstmt.setInt(1, selectedRating);
						upd_pstmt.setInt(2, selectedBike);
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
		btnUpdateBike.setBounds(665, 301, 128, 25);
		frmBikerental.getContentPane().add(btnUpdateBike);
		
		refresh();
	}
}
