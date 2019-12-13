import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CashierUI {
    public JFrame view;

    public JButton btnAddCustomer = new JButton("Add/Update Customer");
    public JButton btnAddPurchase = new JButton("Add New Purchase");
    public JButton btnUpdatePurchase = new JButton("Update Purchase");
    public JButton btnManageAccount = new JButton("Manage Account");

    public CashierUI() {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.setTitle("Store Management System - Cashier View");
        view.setSize(400, 300);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        JLabel title = new JLabel("Store Management System");

        title.setFont (title.getFont ().deriveFont (24.0f));
        view.getContentPane().add(title);

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnAddCustomer);
        panelButtons.add(btnAddPurchase);
        panelButtons.add(btnUpdatePurchase);
        panelButtons.add(btnManageAccount);

        view.getContentPane().add(panelButtons);

        btnAddCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageCustomerUI ui = new ManageCustomerUI();
                ui.run();
            }
        });

        btnAddPurchase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddPurchaseUI ui = new AddPurchaseUI();
                ui.run();
            }
        });

        btnUpdatePurchase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManagePurchaseUI ui = new ManagePurchaseUI();
                ui.run();
            }
        });

        btnManageAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageAccountUI ui = new ManageAccountUI();
                ui.run();
            }
        });
    }
}