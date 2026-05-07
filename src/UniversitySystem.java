import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class UniversitySystem extends JFrame {
    // Users
    private enum UserType { ADMIN, COORDINATOR }

    // Current user
    private UserType currentUserType;

    // Main ui
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // data Files
    private final String PROGRAMS_FILE = "programs.txt";
    private final String STUDENTS_FILE = "students.txt";
    private final String USERS_FILE = "users.txt";

    // Data maps
    private Map<String, String> programsMap = new HashMap<>();
    private Map<String, String> usersMap = new HashMap<>();

    public UniversitySystem() {
        setTitle("University System");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load data
        loadPrograms();
        loadUsers();

        // main panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "Login");
        mainPanel.add(createCoordinatorPanel(), "Coordinator");
        mainPanel.add(createAdminPanel(), "Admin");

        add(mainPanel);
        showLogin();
    }

    private void showLogin() {
        cardLayout.show(mainPanel, "Login");
    }

    //  Login ui 
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblTitle = new JLabel("Select User Type to Login");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnCoordinator = new JButton("Student Coordinator");
        JButton btnAdmin = new JButton("Admin");

        btnCoordinator.addActionListener(e -> {
            currentUserType = UserType.COORDINATOR;
            loadPrograms(); // Refresh programs
            cardLayout.show(mainPanel, "Coordinator");
        });
        btnAdmin.addActionListener(e -> {
            currentUserType = UserType.ADMIN;
            loadPrograms(); // Refresh programs
            loadUsers(); // Refresh users
            cardLayout.show(mainPanel, "Admin");
        });

        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);
        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(btnCoordinator, gbc);
        gbc.gridx = 1;
        panel.add(btnAdmin, gbc);

        return panel;
    }

    //  Coordinator ui 
    private JPanel coordinatorPanel;
    private JComboBox<String> programComboBox;
    private JTextArea searchResultsArea;


    private JPanel createCoordinatorPanel() {
        coordinatorPanel = new JPanel(new BorderLayout());

        // Top buttons
        JPanel topPanel = new JPanel();
        JButton btnAddProgram = new JButton("Add Program");
        JButton btnRegisterStudent = new JButton("Register Student");
        JButton btnSearchStudent = new JButton("Search Student");
        JButton btnLogout = new JButton("Logout");

        topPanel.add(btnAddProgram);
        topPanel.add(btnRegisterStudent);
        topPanel.add(btnSearchStudent);
        topPanel.add(btnLogout);

        // Card layout for functionalities
        JPanel funcPanel = new JPanel(new CardLayout());

        // Add Program ui
        JPanel addProgramPanel = new JPanel(new GridBagLayout());
        JTextField txtProgram = new JTextField(15);
        JTextField txtMajor = new JTextField(15);
        JButton btnSaveProgram = new JButton("Save Program");
        JLabel lblProgramStatus = new JLabel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0;
        addProgramPanel.add(new JLabel("Program Name:"), gbc);
        gbc.gridx = 1; 
        addProgramPanel.add(txtProgram, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        addProgramPanel.add(new JLabel("Major:"), gbc);
        gbc.gridx = 1;
        addProgramPanel.add(txtMajor, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        addProgramPanel.add(btnSaveProgram, gbc);
        gbc.gridy = 3;
        addProgramPanel.add(lblProgramStatus, gbc);

        btnSaveProgram.addActionListener(e -> {
            String prog = txtProgram.getText().trim();
            String major = txtMajor.getText().trim();
            if (!prog.isEmpty() && !major.isEmpty()) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(PROGRAMS_FILE, true))) {
                    bw.write(prog + "," + major);
                    bw.newLine();
                    programsMap.put(prog, major);
                    lblProgramStatus.setText("Program added successfully.");
                    refreshProgramComboBoxes();
                } catch (IOException ex) {
                    lblProgramStatus.setText("Error saving program.");
                }
            } else {
                lblProgramStatus.setText("Please fill all fields.");
            }
        });

        // Register Student ui
        JPanel registerStudentPanel = new JPanel(new GridBagLayout());
        JTextField txtStudentID = new JTextField(15);
        JTextField txtStudentName = new JTextField(15);
        JComboBox<String> programRegisterCombo = new JComboBox<>();
        JButton btnRegister = new JButton("Register Student");
        JLabel lblRegisterStatus = new JLabel();

        // Populate program  
        for (String prog : programsMap.keySet()) {
            programRegisterCombo.addItem(prog);
        }

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0;
        registerStudentPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1; 
        registerStudentPanel.add(txtStudentID, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        registerStudentPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; 
        registerStudentPanel.add(txtStudentName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        registerStudentPanel.add(new JLabel("Program:"), gbc);
        gbc.gridx = 1; 
        registerStudentPanel.add(programRegisterCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        registerStudentPanel.add(btnRegister, gbc);
        gbc.gridy = 4;
        registerStudentPanel.add(lblRegisterStatus, gbc);

        btnRegister.addActionListener(e -> {
            String studentID = txtStudentID.getText().trim();
            String name = txtStudentName.getText().trim();
            String program = (String) programRegisterCombo.getSelectedItem();
            if (!studentID.isEmpty() && !name.isEmpty() && program != null) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(STUDENTS_FILE, true))) {
                    bw.write(studentID + "," + name + "," + program);
                    bw.newLine();
                    lblRegisterStatus.setText("Student registered successfully.");
                } catch (IOException ex) {
                    lblRegisterStatus.setText("Error registering student.");
                }
            } else {
                lblRegisterStatus.setText("Please fill all fields.");
            }
        });

        // Search Student 
        JPanel searchPanel = new JPanel(new BorderLayout());
        JPanel searchInputPanel = new JPanel();
        JTextField txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        searchInputPanel.add(new JLabel("Search (by ID or Name): "));
        searchInputPanel.add(txtSearch);
        searchInputPanel.add(btnSearch);

        searchResultsArea = new JTextArea(10, 50);
        searchResultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(searchResultsArea);

        searchPanel.add(searchInputPanel, BorderLayout.NORTH);
        searchPanel.add(scrollPane, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> {
            String query = txtSearch.getText().trim().toLowerCase();
            searchResultsArea.setText("");
            try (BufferedReader br = new BufferedReader(new FileReader(STUDENTS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String id = parts[0];
                        String name = parts[1];
                        String program = parts[2];
                        if (id.toLowerCase().contains(query) || name.toLowerCase().contains(query)) {
                            searchResultsArea.append("ID: " + id + ", Name: " + name + ", Program: " + program + "\n");
                        }
                    }
                }
            } catch (IOException ex) {
                searchResultsArea.setText("Error reading student data.");
            }
        });

        // functional card layout
        CardLayout cl = (CardLayout) (funcPanel.getLayout());
        JPanel addProgramContainer = new JPanel(new BorderLayout());
        addProgramContainer.add(addProgramPanel, BorderLayout.CENTER);
        JPanel registerStudentContainer = new JPanel(new BorderLayout());
        registerStudentContainer.add(registerStudentPanel, BorderLayout.CENTER);
        JPanel searchStudentContainer = new JPanel(new BorderLayout());
        searchStudentContainer.add(searchPanel, BorderLayout.CENTER);

        funcPanel.add(addProgramContainer, "AddProgram");
        funcPanel.add(registerStudentContainer, "RegisterStudent");
        funcPanel.add(searchStudentContainer, "SearchStudent");

        // Button actions
        btnAddProgram.addActionListener(e -> {
            ((CardLayout) (funcPanel.getLayout())).show(funcPanel, "AddProgram");
        });
        btnRegisterStudent.addActionListener(e -> {
            ((CardLayout) (funcPanel.getLayout())).show(funcPanel, "RegisterStudent");
        });
        btnSearchStudent.addActionListener(e -> {
            ((CardLayout) (funcPanel.getLayout())).show(funcPanel, "SearchStudent");
        });
        btnLogout.addActionListener(e -> {
            showLogin();
        });

        // Helper to refresh program 
        refreshProgramComboBoxes();

        // Assemble main coordinator ui
        coordinatorPanel.add(topPanel, BorderLayout.NORTH);
        coordinatorPanel.add(funcPanel, BorderLayout.CENTER);

        return coordinatorPanel;
    }

    private void refreshProgramComboBoxes() {
        // When called, update all combo boxes that list programs
        // For simplicity, you can recreate combo boxes or update their models
        // In this example, you might need to recreate panels or keep references
        // For now, assume you re-setup combo boxes each time
    }

    //  Admin ui
    private JPanel adminPanel;
    private JTextArea adminSearchResultsArea;

    private JPanel createAdminPanel() {
        adminPanel = new JPanel(new BorderLayout());

        // Top buttons
        JPanel topPanel = new JPanel();
        JButton btnAddUser = new JButton("Add User");
        JButton btnAddProgram = new JButton("Add Program");
        JButton btnRegisterStudent = new JButton("Register Student");
        JButton btnSearchStudent = new JButton("Search Student");
        JButton btnLogout = new JButton("Logout");

        topPanel.add(btnAddUser);
        topPanel.add(btnAddProgram);
        topPanel.add(btnRegisterStudent);
        topPanel.add(btnSearchStudent);
        topPanel.add(btnLogout);

        // Card layout for functionalities
        JPanel funcPanel = new JPanel(new CardLayout());

        // Add User ui
        JPanel addUserPanel = new JPanel(new GridBagLayout());
        JTextField txtNewUsername = new JTextField(15);
        JPasswordField txtNewPassword = new JPasswordField(15);
        String[] roles = { "Student Coordinator", "Admin" };
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        JButton btnSaveUser = new JButton("Create User");
        JLabel lblUserStatus = new JLabel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0;
        addUserPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; 
        addUserPanel.add(txtNewUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        addUserPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        addUserPanel.add(txtNewPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        addUserPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        addUserPanel.add(roleCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        addUserPanel.add(btnSaveUser, gbc);
        gbc.gridy = 4;
        addUserPanel.add(lblUserStatus, gbc);

        btnSaveUser.addActionListener(e -> {
            String username = txtNewUsername.getText().trim();
            String password = new String(txtNewPassword.getPassword()).trim();
            String role = (String) roleCombo.getSelectedItem();
            if (!username.isEmpty() && !password.isEmpty()) {
                if (usersMap.containsKey(username)) {
                    lblUserStatus.setText("Username already exists.");
                } else {
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
                        bw.write(username + "," + password);
                        bw.newLine();
                        usersMap.put(username, password);
                        lblUserStatus.setText("User created successfully.");
                    } catch (IOException ex) {
                        lblUserStatus.setText("Error creating user.");
                    }
                }
            } else {
                lblUserStatus.setText("Fill all fields.");
            }
        });

        // Register Student ui
        JPanel registerStudentPanel = createAdminRegisterStudentPanel();

        // Search Student ui
        JPanel searchStudentPanel = createAdminSearchStudentPanel();

        //  functional card layout
        CardLayout cl = (CardLayout) (funcPanel.getLayout());
        JPanel addUserContainer = new JPanel(new BorderLayout());
        addUserContainer.add(addUserPanel, BorderLayout.CENTER);
        JPanel registerStudentContainer = new JPanel(new BorderLayout());
        registerStudentContainer.add(registerStudentPanel, BorderLayout.CENTER);
        JPanel searchStudentContainer = new JPanel(new BorderLayout());
        searchStudentContainer.add(searchStudentPanel, BorderLayout.CENTER);

        funcPanel.add(addUserContainer, "AddUser");
        funcPanel.add(registerStudentContainer, "RegisterStudent");
        funcPanel.add(searchStudentContainer, "SearchStudent");

        // Button actions
        btnAddUser.addActionListener(e -> {
            ((CardLayout) (funcPanel.getLayout())).show(funcPanel, "AddUser");
        });
        btnAddProgram.addActionListener(e -> {
            ((CardLayout) (funcPanel.getLayout())).show(funcPanel, "AddProgram");
        });
        btnRegisterStudent.addActionListener(e -> {
            ((CardLayout) (funcPanel.getLayout())).show(funcPanel, "RegisterStudent");
        });
        btnSearchStudent.addActionListener(e -> {
            ((CardLayout) (funcPanel.getLayout())).show(funcPanel, "SearchStudent");
        });
        btnLogout.addActionListener(e -> {
            showLogin();
        });

        // Assemble main admin panel
        adminPanel.add(topPanel, BorderLayout.NORTH);
        adminPanel.add(funcPanel, BorderLayout.CENTER);

        return adminPanel;
    }

    //  Register Student panel for Admin
    private JPanel createAdminRegisterStudentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JTextField txtStudentID = new JTextField(15);
        JTextField txtStudentName = new JTextField(15);
        JComboBox<String> programRegisterCombo = new JComboBox<>();
        JButton btnRegister = new JButton("Register Student");
        JLabel lblStatus = new JLabel();

        // Load programs
        for (String prog : programsMap.keySet()) {
            programRegisterCombo.addItem(prog);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1; 
        panel.add(txtStudentID, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; 
        panel.add(txtStudentName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Program:"), gbc);
        gbc.gridx = 1; 
        panel.add(programRegisterCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnRegister, gbc);
        gbc.gridy = 4;
        panel.add(lblStatus, gbc);

        btnRegister.addActionListener(e -> {
            String studentID = txtStudentID.getText().trim();
            String name = txtStudentName.getText().trim();
            String program = (String) programRegisterCombo.getSelectedItem();
            if (!studentID.isEmpty() && !name.isEmpty() && program != null) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(STUDENTS_FILE, true))) {
                    bw.write(studentID + "," + name + "," + program);
                    bw.newLine();
                    lblStatus.setText("Student registered successfully.");
                } catch (IOException ex) {
                    lblStatus.setText("Error registering student.");
                }
            } else {
                lblStatus.setText("Fill all fields.");
            }
        });

        return panel;
    }

    //  Search Student panel for Admin
    private JPanel createAdminSearchStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        JTextField txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        inputPanel.add(new JLabel("Search (by ID or Name): "));
        inputPanel.add(txtSearch);
        inputPanel.add(btnSearch);

        JTextArea resultsArea = new JTextArea(10, 50);
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> {
            String query = txtSearch.getText().trim().toLowerCase();
            resultsArea.setText("");
            try (BufferedReader br = new BufferedReader(new FileReader(STUDENTS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String id = parts[0];
                        String name = parts[1];
                        String program = parts[2];
                        if (id.toLowerCase().contains(query) || name.toLowerCase().contains(query)) {
                            resultsArea.append("ID: " + id + ", Name: " + name + ", Program: " + program + "\n");
                        }
                    }
                }
            } catch (IOException ex) {
                resultsArea.setText("Error reading student data.");
            }
        });

        return panel;
    }

    //  Utility methods 
  private void loadPrograms() {
    programsMap.clear();
    File file = new File(PROGRAMS_FILE);
    if (!file.exists()) {
        try {
            file.createNewFile();
        } catch (IOException e) { e.printStackTrace(); }
    }
    try (BufferedReader br = new BufferedReader(new FileReader(PROGRAMS_FILE))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                programsMap.put(parts[0], parts[1]);
            }
        }
    } catch (IOException e) { e.printStackTrace(); }
}
    private void loadUsers() {
        usersMap.clear();
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
                bw.write("admin,admin123");
                bw.newLine();
            } catch (IOException e) { e.printStackTrace(); }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    usersMap.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UniversitySystem().setVisible(true);
        });
    }
}