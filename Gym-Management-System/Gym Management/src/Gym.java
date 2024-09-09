import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Gym extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/gym_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root_123";

    private JTabbedPane tabbedPane;
    private JTextField memberIdSearchField;
    private JTextField memberIdField;
    private JTextField nameField;
    private JTextField mobileNumberField;
    private JTextField emailField;
    private JComboBox<String> genderComboBox;
    private JTextField ageField;
    private JComboBox<String> timingComboBox;
    private JTextField salaryField;
    private JComboBox<String> monthDropdown;
    private JTextField yearField;
    private JTextField totalPeopleField;
    private JTextField totalSalaryField;
    private DefaultTableModel memberTableModel;
    private JTable memberTable;
    private JTextField dateField;
    private JTextField monthField;
    private JTextField[] attendanceFields;
    private DefaultTableModel tableModel;

    public Gym() {
        setTitle("GYM MANAGEMENT");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        addNewMemberTab();
        addUpdateDeleteMemberTab();
        addSalGoerTab();  
        addListOfMembers();
        totalMoney();
        Attendance();
        addReloadTab();


        add(tabbedPane);

        setVisible(true);
    }

    private void addNewMemberTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(timeLabel, BorderLayout.NORTH);

        JTextField memberIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField mobileNumberField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
        JTextField ageField = new JTextField();

        String[] gymTimings = {"6am to 12noon", "12noon to 6pm", "6pm to 11:30pm"};
        JComboBox<String> timingComboBox = new JComboBox<>(gymTimings);

        JLabel salaryLabel = new JLabel("Salary Amount: ");
        JTextField salaryField = new JTextField();
        salaryField.setEditable(false);

        timingComboBox.addActionListener(e -> {
            String selectedTiming = (String) timingComboBox.getSelectedItem();
            updateSalaryBasedOnTiming(selectedTiming, salaryField);
        });

        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inputPanel.add(new JLabel("Member ID:"));
        inputPanel.add(memberIdField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Mobile Number:"));
        inputPanel.add(mobileNumberField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Gender:"));
        inputPanel.add(genderComboBox);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Gym Timing:"));
        inputPanel.add(timingComboBox);
        inputPanel.add(salaryLabel);
        inputPanel.add(salaryField);

        panel.add(inputPanel, BorderLayout.CENTER);

        JButton registerMemberButton = new JButton("Register Member");
        registerMemberButton.addActionListener(e -> registerMember(memberIdField, nameField, mobileNumberField, emailField, genderComboBox, ageField, timingComboBox, salaryField));
        panel.add(registerMemberButton, BorderLayout.SOUTH);

        tabbedPane.addTab("New Member", panel);
    }

    private void registerMember(JTextField memberIdField, JTextField nameField, JTextField mobileNumberField, JTextField emailField,
                                JComboBox<String> genderComboBox, JTextField ageField, JComboBox<String> timingComboBox, JTextField salaryField) {
        String memberId = memberIdField.getText();
        String name = nameField.getText();
        String mobileNumber = mobileNumberField.getText();
        String email = emailField.getText();
        String gender = (String) genderComboBox.getSelectedItem();
        String age = ageField.getText();
        String selectedTiming = (String) timingComboBox.getSelectedItem();
        String salary = salaryField.getText();

        insertDataIntoDatabase(memberId, name, mobileNumber, email, gender, age, selectedTiming, salary);

        String message = "Member ID: " + memberId + "\nName: " + name + "\nMobile Number: " + mobileNumber +
                "\nEmail: " + email + "\nGender: " + gender + "\nAge: " + age + "\nGym Timing: " + selectedTiming +
                "\nSalary Amount: " + salary;

        JOptionPane.showMessageDialog(this, message, "Registered Member Details", JOptionPane.INFORMATION_MESSAGE);

        clearNewMemberForm(memberIdField, nameField, mobileNumberField, emailField, ageField, salaryField);
    }

    private void insertDataIntoDatabase(String memberId, String name, String mobileNumber, String email,
                                        String gender, String age, String selectedTiming, String salary) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String query = "INSERT INTO members (member_id, name, mobile_number, email, gender, age, gym_timing, salary_amount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, memberId);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, mobileNumber);
                preparedStatement.setString(4, email);
                preparedStatement.setString(5, gender);
                preparedStatement.setString(6, age);
                preparedStatement.setString(7, selectedTiming);
                preparedStatement.setString(8, salary);

                preparedStatement.executeUpdate();
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addUpdateDeleteMemberTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(timeLabel, BorderLayout.NORTH);

        Timer timer = new Timer(1000, e -> updateTime(timeLabel));
        timer.start();

        // Input field for member ID search
        memberIdSearchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(200, 50));
        searchButton.addActionListener(e -> searchMemberById());

        // Input fields for member details
        memberIdField = new JTextField();
        nameField = new JTextField();
        mobileNumberField = new JTextField();
        emailField = new JTextField();
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
        ageField = new JTextField();

        // Dropdown list for gym timing
        String[] gymTimings = {"6am to 12noon", "12noon to 6pm", "6pm to 11:30pm"};
        timingComboBox = new JComboBox<>(gymTimings);

        salaryField = new JTextField();
        salaryField.setEditable(false);

        // Listener for timing selection
        timingComboBox.addActionListener(e -> {
            String selectedTiming = (String) timingComboBox.getSelectedItem();
            // Update salary based on selected timing
            updateSalaryBasedOnTiming(selectedTiming, salaryField);
        });

        // Organize the layout in a grid
        JPanel inputPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add components to the panel
        inputPanel.add(new JLabel("Member ID (Search):"));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(memberIdSearchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        inputPanel.add(searchPanel);

        // Continue with the rest of the components
        inputPanel.add(new JLabel("Member ID:"));
        inputPanel.add(memberIdField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Mobile Number:"));
        inputPanel.add(mobileNumberField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Gender:"));
        inputPanel.add(genderComboBox);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Gym Timing:"));
        inputPanel.add(timingComboBox);
        inputPanel.add(new JLabel("Salary Amount:"));
        inputPanel.add(salaryField);

        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton("Update Member");
        updateButton.addActionListener(e -> updateMember());

        JButton deleteButton = new JButton("Delete Member");
        deleteButton.addActionListener(e -> deleteMember());

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Add panels to the main panel
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Update and Delete Member", panel);
    }

    private void searchMemberById() {
        String memberId = memberIdSearchField.getText();

        if (memberId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Member ID to search.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM members WHERE member_id=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, memberId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        memberIdField.setText(resultSet.getString("member_id"));
                        nameField.setText(resultSet.getString("name"));
                        mobileNumberField.setText(resultSet.getString("mobile_number"));
                        emailField.setText(resultSet.getString("email"));
                        genderComboBox.setSelectedItem(resultSet.getString("gender"));
                        ageField.setText(resultSet.getString("age"));
                        timingComboBox.setSelectedItem(resultSet.getString("gym_timing"));
                        salaryField.setText(resultSet.getString("salary_amount"));
                    } else {
                        JOptionPane.showMessageDialog(this, "Member ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        //to clear the fields in case of a non-existent ID
                        clearMemberDetailsFields();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to clear member details fields
    private void clearMemberDetailsFields() {
        memberIdField.setText("");
        nameField.setText("");
        mobileNumberField.setText("");
        emailField.setText("");
        genderComboBox.setSelectedIndex(0); // Set to the default item
        ageField.setText("");
        timingComboBox.setSelectedIndex(0); // Set to the default item
        salaryField.setText("");
    }

    private void updateMember() {
        // Get the values from the input fields
        String memberId = memberIdField.getText();
        String name = nameField.getText();
        String mobileNumber = mobileNumberField.getText();
        String email = emailField.getText();
        String gender = (String) genderComboBox.getSelectedItem();
        String age = ageField.getText();
        String selectedTiming = (String) timingComboBox.getSelectedItem();
        String salary = salaryField.getText();
    
        // Validate the input if needed
    
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Prepare the update query
            String updateQuery = "UPDATE members SET name=?, mobile_number=?, email=?, gender=?, age=?, gym_timing=?, salary_amount=? WHERE member_id=?";
    
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                // Set the parameters for the update query
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, mobileNumber);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, gender);
                preparedStatement.setString(5, age);
                preparedStatement.setString(6, selectedTiming);
                preparedStatement.setString(7, salary);
                preparedStatement.setString(8, memberId);
    
                // Execute the update query
                int rowsAffected = preparedStatement.executeUpdate();
    
                // Check if the update was successful
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Member updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update member. Member ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating member. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteMember() {
        // Get the member ID from the text field
        String memberId = memberIdField.getText();
    
        // Check if the member ID is empty
        if (memberId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Member ID to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Confirm with the user before deletion
        int confirmResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this member?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        // If the user confirms the deletion
        if (confirmResult == JOptionPane.YES_OPTION) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // Prepare the delete query
                String deleteQuery = "DELETE FROM members WHERE member_id=?";
    
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                    // Set the member ID parameter
                    preparedStatement.setString(1, memberId);
    
                    // Execute the delete query
                    int rowsAffected = preparedStatement.executeUpdate();
    
                    // Check if deletion was successful
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Member deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Clear the member details fields after deletion
                        clearMemberDetailsFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete member. Member ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting member. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateSalaryBasedOnTiming(String selectedTiming, JTextField salaryField) {
        switch (selectedTiming) {
            case "6am to 12noon":
                salaryField.setText("1000rs");
                break;
            case "12noon to 6pm":
                salaryField.setText("800rs");
                break;
            case "6pm to 11:30pm":
                salaryField.setText("1250rs");
                break;
            default:
                salaryField.setText("");
        }
    }

    private void clearNewMemberForm(JTextField memberIdField, JTextField nameField,
                                     JTextField mobileNumberField, JTextField emailField,
                                     JTextField ageField, JTextField salaryField) {
        memberIdField.setText("");
        nameField.setText("");
        mobileNumberField.setText("");
        emailField.setText("");
        ageField.setText("");
        salaryField.setText("");
    }

    private void addSalGoerTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
    
        // Top panel for time display
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(timeLabel, BorderLayout.NORTH);
    
        Timer timer = new Timer(1000, e -> updateTime(timeLabel));
        timer.start();
    
        // Center panel for information text, month dropdown, year input, and total people input
        JPanel centerPanel = new JPanel(new BorderLayout());
    
        JTextArea infoText = new JTextArea("Gym goer money given marked here");
        infoText.setEditable(false);
        centerPanel.add(new JScrollPane(infoText), BorderLayout.CENTER);
    
        // Panel for month label and dropdown
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel monthLabel = new JLabel("Month: ");
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        monthDropdown = new JComboBox<>(months);
    
        monthPanel.add(monthLabel);
        monthPanel.add(monthDropdown);
    
        // Panel for year label and input
        JPanel yearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel yearLabel = new JLabel("Year: ");
        yearField = new JTextField(4); // Adjust the width as needed
    
        yearPanel.add(yearLabel);
        yearPanel.add(yearField);
    
        // Panel for total people label and input
        JPanel totalPeoplePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel totalPeopleLabel = new JLabel("Total People Joined: ");
        totalPeopleField = new JTextField(4); // Adjust the width as needed
    
        totalPeoplePanel.add(totalPeopleLabel);
        totalPeoplePanel.add(totalPeopleField);
    
        // Add the month, year, and total people panels to the center panel
        centerPanel.add(monthPanel, BorderLayout.NORTH);
        centerPanel.add(totalPeoplePanel, BorderLayout.WEST);  // Align totalPeoplePanel to the left
        centerPanel.add(yearPanel, BorderLayout.CENTER);
    
        // Bottom panel for salary marking button and total salary display
        JPanel bottomPanel = new JPanel(new BorderLayout());
    
        // Panel for total salary information
        JPanel totalSalaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel totalSalaryLabel = new JLabel("Total Salary: ");
        totalSalaryField = new JTextField(10); // Adjust the width as needed
        totalSalaryField.setEditable(false);  // Make it non-editable
    
        totalSalaryPanel.add(totalSalaryLabel);
        totalSalaryPanel.add(totalSalaryField);
    
        // Panel for buttons (Calculate Total Salary and Mark Salary)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    
        // Button to calculate total salary
        JButton calculateTotalSalaryButton = new JButton("Calculate Total Salary");
        calculateTotalSalaryButton.addActionListener(e -> {
            // logic to calculate and update total salary field
            
            int totalPeople = Integer.parseInt(totalPeopleField.getText());
            int totalSalary = totalPeople * 2000;  // Assuming 1 member = 2000 rupees
            totalSalaryField.setText(String.valueOf(totalSalary));
        });
    
        // Button to mark salary (using the provided markGoerSalButton function)
        JButton markGoerSalButton = new JButton("Mark Salary");
        markGoerSalButton.addActionListener(e -> markGoerSalButton());
    
        buttonPanel.add(calculateTotalSalaryButton);
        buttonPanel.add(markGoerSalButton);
    
        // Add components to bottom panel
        bottomPanel.add(totalSalaryPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
    
        // Add all panels to the main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
    
        tabbedPane.addTab("Salary-GymGoer", panel);
    }
    
    private void markGoerSalButton() {
        // Retrieve values from the UI components
        String month = (String) monthDropdown.getSelectedItem();
        String year = yearField.getText();
        int totalPeople;
        int totalSalary;
    
        try {
            totalPeople = Integer.parseInt(totalPeopleField.getText());
            totalSalary = Integer.parseInt(totalSalaryField.getText());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Invalid input for Total People or Total Salary.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Database connection and insertion logic
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // SQL query to insert data into salary_info table
            String sql = "INSERT INTO salary_info (month, year, total_people, total_salary) VALUES (?, ?, ?, ?)";
    
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // Set values in the prepared statement
                preparedStatement.setString(1, month);
                preparedStatement.setString(2, year);
                preparedStatement.setInt(3, totalPeople);
                preparedStatement.setInt(4, totalSalary);
    
                // Execute the query
                int rowsAffected = preparedStatement.executeUpdate();
    
                if (rowsAffected > 0) {
                    // Display success message
                    JOptionPane.showMessageDialog(this, "Salary marked successfully.", "Mark Salary", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Display error message if no rows were affected
                    JOptionPane.showMessageDialog(this, "Failed to mark salary. No rows affected.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error marking salary: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
private void addListOfMembers() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    JLabel timeLabel = new JLabel();
    timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
    panel.add(timeLabel, BorderLayout.NORTH);

    Timer timer = new Timer(1000, e -> updateTime(timeLabel));
    timer.start();

    // Create a table model to hold the data
    tableModel = new DefaultTableModel();
    tableModel.addColumn("Member ID");
    tableModel.addColumn("Name");
    tableModel.addColumn("Mobile Number");
    tableModel.addColumn("Email");
    tableModel.addColumn("Gender");
    tableModel.addColumn("Age");
    tableModel.addColumn("Gym Timing");
    tableModel.addColumn("Salary Amount");

    // Fetch data from the database and populate the table model
    fetchDataFromDatabase(tableModel);

    // Create the JTable with the table model
    memberTable = new JTable(tableModel);

    // Add the table to a scroll pane to enable scrolling if there are many rows
    JScrollPane scrollPane = new JScrollPane(memberTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Add a Refresh button
    JButton refreshButton = new JButton("Refresh");
    refreshButton.addActionListener(e -> refreshTableData());
    panel.add(refreshButton, BorderLayout.NORTH);

    // Add a Reload button
    JButton reloadButton = new JButton("Reload");
    reloadButton.addActionListener(e -> reloadDataFromDatabase());
    panel.add(reloadButton, BorderLayout.NORTH);

    tabbedPane.addTab("List Of Members", panel);
}

// Fetch data from the database and populate the table model
private void fetchDataFromDatabase(DefaultTableModel tableModel) {
    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        String query = "SELECT * FROM members";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String[] rowData = {
                        resultSet.getString("member_id"),
                        resultSet.getString("name"),
                        resultSet.getString("mobile_number"),
                        resultSet.getString("email"),
                        resultSet.getString("gender"),
                        resultSet.getString("age"),
                        resultSet.getString("gym_timing"),
                        resultSet.getString("salary_amount")
                };
                tableModel.addRow(rowData);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Refresh table data
private void refreshTableData() {
    // Remove existing rows from the table model
    tableModel.setRowCount(0);

    // Fetch and populate the table model with updated data
    fetchDataFromDatabase(tableModel);
}

// Reload table data
private void reloadDataFromDatabase() {
    // Clear existing data in the table model
    tableModel.setRowCount(0);

    // Fetch and populate the table model with updated data
    fetchDataFromDatabase(tableModel);
}

    private void totalMoney() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
    
        // Create a table model to hold the data
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Month");
        tableModel.addColumn("Year");
        tableModel.addColumn("Total Money Earned");
    
        // Fetch data from the database and populate the table model
        fetchDataFromSalaryInfoTable();
    
        // Create the JTable with the table model
        memberTable = new JTable(tableModel);
    
        // Add the table to a scroll pane to enable scrolling if there are many rows
        JScrollPane scrollPane = new JScrollPane(memberTable);
        panel.add(scrollPane, BorderLayout.CENTER);
    
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> fetchDataFromSalaryInfoTable());
        panel.add(refreshButton, BorderLayout.NORTH);
    
        tabbedPane.addTab("Total money earned", panel);
    }

    // Fetch data from the salary_info table and populate the table model
    private void fetchDataFromSalaryInfoTable() {
        // Clear existing data in the table model
        tableModel.setRowCount(0);
    
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM salary_info";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
    
                while (resultSet.next()) {
                    String[] rowData = {
                            resultSet.getString("month"),
                            resultSet.getString("year"),
                            String.valueOf(resultSet.getInt("total_salary"))
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void Attendance() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        // Left panel for member information
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        JLabel timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(timeLabel, BorderLayout.NORTH);
        // Input box for date
        dateField = new JTextField(2);
        // Input box for month
        monthField = new JTextField(2);
        // Input box for year
        yearField = new JTextField(4);
        // Add the input boxes to the left panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Date: "));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Month: "));
        inputPanel.add(monthField);
        inputPanel.add(new JLabel("Year: "));
        inputPanel.add(yearField);

        leftPanel.add(inputPanel, BorderLayout.NORTH);
        // Create a table model for member information
        memberTableModel = new DefaultTableModel();
        memberTableModel.addColumn("Member ID");
        memberTableModel.addColumn("Member Name");
        // Fetch data from the database and populate the member table model
        fetchMembersDataFromDatabase(memberTableModel);
        // Create a panel for each member with an input box
        JPanel membersPanel = new JPanel();
        membersPanel.setLayout(new GridLayout(memberTableModel.getRowCount(), 1)); // Set to 1 column
        // Create an array of input fields for attendance
        attendanceFields = new JTextField[memberTableModel.getRowCount()];
        for (int i = 0; i < memberTableModel.getRowCount(); i++) {
            String memberName = memberTableModel.getValueAt(i, 1).toString();
            // Create a panel for each member with a label and an input box
            JPanel memberPanel = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(memberName + ": ");
            nameLabel.setPreferredSize(new Dimension(150, 20)); // Set a fixed width for labels
            memberPanel.add(nameLabel, BorderLayout.WEST);
            attendanceFields[i] = new JTextField(5); // Set width of input boxes
            memberPanel.add(attendanceFields[i], BorderLayout.CENTER);
            membersPanel.add(memberPanel);
        }
        leftPanel.add(membersPanel, BorderLayout.CENTER);
        // Button to mark attendance
        JButton markAttendanceButton = new JButton("Mark attendance");
        markAttendanceButton.addActionListener(e -> markAttendance());
        leftPanel.add(markAttendanceButton, BorderLayout.SOUTH);
        // Add left panel to the main panel
        panel.add(leftPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Attendance", panel);
    }

    private void markAttendance() {
        String date = dateField.getText();
        String month = monthField.getText();
        String year = yearField.getText();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Iterate through the attendance fields to get attendance status for each member
            for (int i = 0; i < attendanceFields.length; i++) {
                String memberId = memberTableModel.getValueAt(i, 0).toString();
                String attendanceStatus = attendanceFields[i].getText();
                // Insert attendance data into the attendance_info table
                String insertQuery = "INSERT INTO attendance_info (name, date, month, year, attendance_status) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, memberId);
                    preparedStatement.setString(2, date);
                    preparedStatement.setString(3, month);
                    preparedStatement.setString(4, year);
                    preparedStatement.setString(5, attendanceStatus);
                    // Execute the query
                    preparedStatement.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(this, "Attendance marked successfully.", "Mark Attendance", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error marking attendance.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchMembersDataFromDatabase(DefaultTableModel tableModel) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT member_id, name FROM members";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    String memberId = resultSet.getString("member_id");
                    String name = resultSet.getString("name");

                    // Add a row with member ID and name
                    Object[] rowData = {memberId, name};
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addReloadTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        // Create a Reload button with a lambda expression
        JButton reloadButton = new JButton("Reload Application");
        reloadButton.addActionListener(e -> Reload());
        // Add the reload button to the panel
        panel.add(reloadButton, BorderLayout.CENTER);
        // Add the panel to the tabbedPane
        tabbedPane.addTab("Reload", panel);
    }

    private void Reload() {
        dispose();

        new Gym();
    }

    private void updateTime(JLabel label) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        label.setText(dateFormat.format(new Date()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Gym::new);
    }
}