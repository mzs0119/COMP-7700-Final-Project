import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ManageUserUI {

    public JFrame view;

    public JButton btnSave = new JButton("Save User");

    public JLabel labFullName = new JLabel("Full Name:");
    public JTextField txtUserName = new JTextField(20);

    String s1[] = { "Customer", "Cashier", "Manager", "Admin" };
    public JComboBox txtUserType = new JComboBox(s1);

    UserModel user;

    public ManageUserUI() {
        this.view = new JFrame();
        txtUserType.setPrototypeDisplayValue("sdlfkhsdlkfdefault text heresdlk"); //sets the width of the combo

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Manage Customer Information");
        view.setSize(600, 400);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel line = new JPanel(new FlowLayout());
        line.add(new JLabel("Username "));
        line.add(txtUserName);
        view.getContentPane().add(line);


        line = new JPanel(new FlowLayout());
        line.add(labFullName);
        view.getContentPane().add(line);

        line = new JPanel(new FlowLayout());
        line.add(new JLabel("User Type "));
        line.add(txtUserType);
        view.getContentPane().add(line);

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnSave);
        view.getContentPane().add(panelButtons);

        btnSave.addActionListener(new ManageUserUI.SaveButtonListener());

        txtUserName.addFocusListener(new UserNameFocusListener());
    }

    public void run() {
        view.setVisible(true);
    }



    class  UserNameFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent e) {

        }
        @Override
        public void focusLost(FocusEvent focusEvent) {
            process();
        }

        private void process() {
            String userName = txtUserName.getText();

            if (userName.length() == 0) {
                txtUserName.setText("Username: [not specified!]");
                return;
            }

            System.out.println("Username = " + userName);

            user = StoreClient.getInstance().getDataAdapter().loadUser(userName);

            if (user == null) {
                JOptionPane.showMessageDialog(null,
                        "Error: No user with the username = " + userName + " in store!", "Error Message",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            labFullName.setText("Full Name: " + user.mFullname);
            txtUserType.setSelectedIndex(user.mUserType);

        }

    }

    class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String userName = txtUserName.getText();
            if (userName.length() == 0) {
                txtUserName.setText("Username: [not specified!]");
                JOptionPane.showMessageDialog(null, "Username cannot be empty!");
                return;
            }

            user = StoreClient.getInstance().getDataAdapter().loadUser(userName);
            if (user == null) {
                JOptionPane.showMessageDialog(null,
                        "Error: No user with the username = " + userName + " in store!", "Error Message",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            int userType = txtUserType.getSelectedIndex();
            if (txtUserType.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "User type cannot be empty!");
                return;
            }
            user.mUserType = userType;

            try {
                int res = StoreClient.getInstance().getDataAdapter().saveUser(user);

                if (res == IDataAdapter.USER_SAVE_FAILED)
                    JOptionPane.showMessageDialog(null, "User is NOT saved successfully!");
                else
                    JOptionPane.showMessageDialog(null, "User is SAVED successfully!");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
