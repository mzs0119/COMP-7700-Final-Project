import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageCustomerUI {

    public JFrame view;

    public JButton btnLoad = new JButton("Load Customer");
    public JButton btnSave = new JButton("Save Customer");

    public JTextField txtCustomerID = new JTextField(20);
    public JTextField txtName = new JTextField(20);
    public JTextField txtPhone = new JTextField(20);
    public JTextField txtAddress = new JTextField(20);

    public CustomerModel customer;

    public ManageCustomerUI() {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Manage Customer Information");
        view.setSize(600, 400);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnLoad);
        panelButtons.add(btnSave);
        view.getContentPane().add(panelButtons);

        JPanel line1 = new JPanel(new FlowLayout());
        line1.add(new JLabel("CustomerID "));
        line1.add(txtCustomerID);
        view.getContentPane().add(line1);

        JPanel line2 = new JPanel(new FlowLayout());
        line2.add(new JLabel("Name "));
        line2.add(txtName);
        view.getContentPane().add(line2);

        JPanel line3 = new JPanel(new FlowLayout());
        line3.add(new JLabel("Phone "));
        line3.add(txtPhone);
        view.getContentPane().add(line3);

        JPanel line4 = new JPanel(new FlowLayout());
        line4.add(new JLabel("Address "));
        line4.add(txtAddress);
        view.getContentPane().add(line4);


        btnLoad.addActionListener(new LoadButtonListerner());

        btnSave.addActionListener(new SaveButtonListener());

    }

    public void run() {
        view.setVisible(true);
    }

    class LoadButtonListerner implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String id = txtCustomerID.getText();

            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "CustomerID cannot be null!");
                return;
            }

            try {
                int i = Integer.parseInt(id);
                System.out.println("CustomerID = " + i);

                try {
                    customer = StoreClient.getInstance().getDataAdapter().loadCustomer(i);

                    if (customer == null) {
                        JOptionPane.showMessageDialog(null,
                                "Error: No customer with the id = " + i + " in store!", "Error Message",
                                JOptionPane.ERROR_MESSAGE);

                        return;
                    }

                    txtName.setText(customer.mName);
                    txtAddress.setText(customer.mAddress);
                    txtPhone.setText(customer.mPhone);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "CustomerID is invalid!");
                return;
            }
        }
    }

    class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String id = txtCustomerID.getText();
            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "CustomerID cannot be null!");
                return;
            }

            try {
                int i = Integer.parseInt(id);
                customer.mCustomerID = i;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "CustomerID is invalid!");
                return;
            }

            String name = txtName.getText();
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(null, "Name cannot be empty!");
                return;
            }
            customer.mName = name;

            String address = txtAddress.getText();
            if(address.length() == 0) {
                JOptionPane.showMessageDialog(null, "Address cannot be empty!");
                return;
            }
            customer.mAddress = address;

            String phone = txtPhone.getText();
            if(phone.length() == 0) {
                JOptionPane.showMessageDialog(null, "Phone cannot be empty!");
                return;
            }
            customer.mPhone = phone;

            try {
                int res = StoreClient.getInstance().getDataAdapter().saveCustomer(customer);

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