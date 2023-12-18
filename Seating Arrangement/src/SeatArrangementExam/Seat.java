package SeatArrangementExam;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class Seat {
    private JFrame seatFrame;
    private JPanel frame;
    private JTable table1;
    private JTextField name;
    private JTextField rollno;
    private JButton GETSEATButton;
    private JComboBox<String> classDetail;
    private ArrayList<String> seat;

    public Seat() {
        seatFrame = new JFrame();
        frame = new JPanel();
        table1 = new JTable();
        name = new JTextField(10);
        rollno = new JTextField(10);
        GETSEATButton = new JButton("GET SEAT");
        classDetail = new JComboBox<>(new String[]{"Class A", "Class B", "Class C"});
        seat = new ArrayList<>();

        seatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        seatFrame.setSize(600, 400);
        seatFrame.setLayout(new BorderLayout());
        seatFrame.add(frame, BorderLayout.CENTER);

        frame.setLayout(new FlowLayout());

        frame.add(new JLabel("Name:"));
        frame.add(name);

        frame.add(new JLabel("Roll No:"));
        frame.add(rollno);

        frame.add(new JLabel("Class:"));
        frame.add(classDetail);

        GETSEATButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getSeatButtonActionPerformed();
            }
        });

        frame.add(GETSEATButton);

        // Create a JScrollPane to host the JTable
        JScrollPane scrollPane = new JScrollPane(table1);
        seatFrame.add(scrollPane, BorderLayout.SOUTH);

        // Set custom cell renderer for better appearance
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table1.setDefaultRenderer(Object.class, centerRenderer);

        // Set custom font for the table
        Font tableFont = new Font("Arial", Font.PLAIN, 14);
        table1.setFont(tableFont);

        // Set custom color for the table
        table1.setBackground(new Color(230, 240, 250));
        table1.setForeground(Color.BLACK);

        // Set grid color
        table1.setGridColor(Color.BLACK);

        // Set border
        table1.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Add KeyListener to allow only letters in the name field
        name.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        // Add KeyListener to allow only numbers in the rollno field
        rollno.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        declareSeats();
        tableData();

        seatFrame.setLocationRelativeTo(null);
        seatFrame.setVisible(true);
    }

    private void getSeatButtonActionPerformed() {
        if (name.getText().isEmpty() || rollno.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill all records to get a seat.");
        } else {
            try {
                Random rn = new Random();
                String seatNo = "";

                if (seat.size() == 0) {
                    JOptionPane.showMessageDialog(null, "THERE ARE NO SEATS AVAILABLE");
                } else {
                    seatNo = seat.get(rn.nextInt(seat.size()));
                }

                String sql = "INSERT INTO seat (NAME, ROLL_NO, CLASS, SEAT) VALUES (?, ?, ?, ?)";
                Class.forName("org.sqlite.JDBC");
                Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/jseab/Seating Arrangement/identifier.sqlite", "", "");
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, name.getText());
                statement.setString(2, rollno.getText());
                statement.setString(3, (String) classDetail.getSelectedItem());
                statement.setString(4, seatNo);

                seat.remove(seatNo);
                statement.executeUpdate();

                JOptionPane.showMessageDialog(null, "RECORD ADDED SUCCESSFULLY");
                name.setText("");
                rollno.setText("");

                updateData();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    public void declareSeats() {
        seat.clear();  // Clear existing seat data
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 10; j++) {
                String s = "R" + i + "S" + j;
                seat.add(s);
            }
        }

        try {
            String a = "SELECT SEAT FROM seat";
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/jseab/Seating Arrangement/identifier.sqlite", "", "");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(a);

            while (rs.next()) {
                seat.remove(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void tableData() {
        try {
            String a = "SELECT * FROM seat";
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/jseab/Seating Arrangement/identifier.sqlite", "", "");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(a);

            // Check if table1 is null before calling setModel
            if (table1 != null) {
                // Update the JTable with the fetched data
                table1.setModel(buildTableModel(rs));
            } else {
                System.out.println("table1 is null");
            }
        } catch (Exception ex1) {
            JOptionPane.showMessageDialog(null, ex1.getMessage());
        }
    }

    private void updateData() {
        try {
            String a = "SELECT * FROM seat";
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/jseab/Seating Arrangement/identifier.sqlite", "", "");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(a);

            if (table1 != null) {
                // Update the JTable with the fetched data
                table1.setModel(buildTableModel(rs));
            } else {
                System.out.println("table1 is null");
            }
        } catch (Exception ex1) {
            JOptionPane.showMessageDialog(null, ex1.getMessage());
        }
    }

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Seat());
    }
}
