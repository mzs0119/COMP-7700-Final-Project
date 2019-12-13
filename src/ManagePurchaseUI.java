import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ManagePurchaseUI {
    public JFrame view;
    public JButton btnAdd = new JButton("Update");

    public JTextField txtPurchaseID = new JTextField(10);
    public JTextField txtCustomerID = new JTextField(10);
    public JTextField txtProductID = new JTextField(10);
    public JTextField txtQuantity = new JTextField(10);

    public JLabel labPrice = new JLabel("Product Price: ");

    public JLabel labCustomerName = new JLabel("Customer Name: ");
    public JLabel labProductName = new JLabel("Product Name: ");

    public JLabel labCost = new JLabel("Cost: $0.00 ");
    public JLabel labTax = new JLabel("Tax: $0.00");
    public JLabel labTotalCost = new JLabel("Total Cost: $0.00");

    ProductModel product;
    PurchaseModel purchase;
    CustomerModel customer;

    public ManagePurchaseUI() {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Add Purchase");
        view.setSize(600, 400);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel line = new JPanel(new FlowLayout());
        line.add(new JLabel("PurchaseID "));
        line.add(txtPurchaseID);
        view.getContentPane().add(line);

        line = new JPanel(new FlowLayout());
        line.add(new JLabel("CustomerID "));
        line.add(txtCustomerID);
        line.add(labCustomerName);
        view.getContentPane().add(line);

        line = new JPanel(new FlowLayout());
        line.add(new JLabel("ProductID "));
        line.add(txtProductID);
        line.add(labProductName);
        view.getContentPane().add(line);

        line = new JPanel(new FlowLayout());
        line.add(new JLabel("Quantity "));
        line.add(txtQuantity);
        line.add(labPrice);
        view.getContentPane().add(line);

        line = new JPanel(new FlowLayout());
        line.add(labCost);
        line.add(labTax);
        line.add(labTotalCost);
        view.getContentPane().add(line);

        line = new JPanel(new FlowLayout());
        line.add(btnAdd);
        view.getContentPane().add(line);

        txtPurchaseID.addFocusListener(new ManagePurchaseUI.PurchaseIDFocusListener());
        txtQuantity.getDocument().addDocumentListener(new ManagePurchaseUI.QuantityChangeListener());

        btnAdd.addActionListener(new ManagePurchaseUI.AddButtonListerner());
    }

    public void run() {
        purchase = new PurchaseModel();
        product = new ProductModel();
        customer = new CustomerModel();

        view.setVisible(true);
        txtCustomerID.setEnabled(false);
        txtProductID.setEnabled(false);
    }

    private class PurchaseIDFocusListener implements  FocusListener {

        @Override
        public void focusGained(FocusEvent e) {

        }

        @Override
        public void focusLost(FocusEvent e) {
            process();
        }

        private void process() {
            String s = txtPurchaseID.getText();
            if (s.length() == 0) {
                return;
            }

            try {
                int id = Integer.parseInt(s);
                PurchaseModel purchase = StoreClient.getInstance().getDataAdapter().loadPurchase(id);

                if(purchase == null) {
                    JOptionPane.showMessageDialog(null,
                            "Error: No purchase with id = " + id + " in store!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                product = StoreClient.getInstance().getDataAdapter().loadProduct(purchase.mProductID);
                txtProductID.setText(String.valueOf(purchase.mProductID));
                labProductName.setText("Product Name: " + product.mName);

                customer = StoreClient.getInstance().getDataAdapter().loadCustomer(purchase.mCustomerID);
                txtCustomerID.setText(String.valueOf(purchase.mCustomerID));
                labCustomerName.setText("Customer Name: " + customer.mName);

                txtQuantity.setText(String.valueOf(purchase.mQuantity));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error: Invalid PurchaseID", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

        }

    }

    private class QuantityChangeListener implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            process();
        }

        public void removeUpdate(DocumentEvent e) {
            process();
        }

        public void insertUpdate(DocumentEvent e) {
            process();
        }

        private void process() {
            String s = txtQuantity.getText();

            if (s.length() == 0) {
                //labCustomerName.setText("Customer Name: [not specified!]");
                return;
            }

            System.out.println("Quantity = " + s);

            try {
                purchase.mQuantity = Double.parseDouble(s);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error: Please enter an invalid quantity", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (purchase.mQuantity <= 0) {
                JOptionPane.showMessageDialog(null,
                        "Error: Please enter an invalid quantity", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (purchase.mQuantity > product.mQuantity) {
                JOptionPane.showMessageDialog(null,
                        "Not enough available products!", "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            purchase.mCost = purchase.mQuantity * product.mPrice;
            purchase.mTax = purchase.mCost * 0.09;
            purchase.mTotal = purchase.mCost + purchase.mTax;

            labCost.setText("Cost: $" + String.format("%8.2f", purchase.mCost).trim());
            labTax.setText("Tax: $" + String.format("%8.2f", purchase.mTax).trim());
            labTotalCost.setText("Total: $" + String.format("%8.2f", purchase.mTotal).trim());

        }
    }

    class AddButtonListerner implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            String id = txtPurchaseID.getText();

            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "PurchaseID cannot be null!");
                return;
            }

            try {
                purchase.mPurchaseID = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "PurchaseID is invalid!");
                return;
            }

            int res = StoreClient.getInstance().getDataAdapter().savePurchase(purchase);
            if (res == SQLiteDataAdapter.PURCHASE_SAVE_FAILED)
                JOptionPane.showMessageDialog(null, "Purchase NOT added successfully! Duplicate product ID!");
            else
                JOptionPane.showMessageDialog(null, "Purchase added successfully!" + purchase);
        }
    }

}
