import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Calendar;

public class ViewSalesUI {

    public JFrame view;
    public JTable purchaseTable;

    public ViewSalesUI() {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Sales Report");
        view.setSize(600, 400);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));


        PurchaseListModel list = StoreClient.getInstance().getDataAdapter().loadPurchaseHistory();

        DefaultTableModel tableData = new DefaultTableModel();

        tableData.addColumn("PurchaseID");
        tableData.addColumn("ProductID");
        tableData.addColumn("Product Name");
        tableData.addColumn("Total");

        Double totalSales = 0.00;
        for (PurchaseModel purchase : list.purchases) {
            Object[] row = new Object[tableData.getColumnCount()];
            row[0] = purchase.mPurchaseID;
            row[1] = purchase.mProductID;
            ProductModel product = StoreClient.getInstance().getDataAdapter().loadProduct(purchase.mProductID);
            if(product != null) {
                row[2] = product.mName;
                row[3] = purchase.mTotal;
            }
            tableData.addRow(row);

            totalSales += purchase.mTotal;
        }

        purchaseTable = new JTable(tableData);

        JScrollPane scrollableList = new JScrollPane(purchaseTable);

        DecimalFormat dec = new DecimalFormat("#.##");
        JLabel title = new JLabel("Sales Summary - Total Sales: $" + dec.format(totalSales));

        title.setFont (title.getFont().deriveFont (24.0f));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        view.getContentPane().add(title);
        view.getContentPane().add(scrollableList);
    }

    public void run() {
        view.setVisible(true);
    }

}
