import com.google.gson.Gson;

import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;

public class StoreServer {
    static String dbfile = "/Users/melanasmith/Downloads/Project2/data/store.db";

    public static void main(String[] args) {

        HashMap<Integer, UserModel> activeUsers = new HashMap<Integer, UserModel>();

        int totalActiveUsers = 0;

        int port = 8888;

        if (args.length > 0) {
            System.out.println("Running arguments: ");
            for (String arg : args)
                System.out.println(arg);
            port = Integer.parseInt(args[0]);
            dbfile = args[1];
        }

        try {
            SQLiteDataAdapter adapter = new SQLiteDataAdapter();
            Gson gson = new Gson();
            adapter.connect(dbfile);

            ServerSocket server = new ServerSocket(port);

            System.out.println("Server is listening at port = " + port);

            while (true) {
                Socket pipe = server.accept();
                PrintWriter out = new PrintWriter(pipe.getOutputStream(), true);
                Scanner in = new Scanner(pipe.getInputStream());

                MessageModel msg = gson.fromJson(in.nextLine(), MessageModel.class);
                System.out.println("CODE: " + msg.code);

                if (msg.code == MessageModel.GET_PRODUCT) {
                    System.out.println("GET product with id = " + msg.data);
                    ProductModel p = adapter.loadProduct(Integer.parseInt(msg.data));
                    if (p == null) {
                        msg.code = MessageModel.OPERATION_FAILED;
                    }
                    else {
                        msg.code = MessageModel.OPERATION_OK; // load successfully!!!
                        msg.data = gson.toJson(p);
                    }
                    out.println(gson.toJson(msg));
                }

                if (msg.code == MessageModel.PUT_PRODUCT) {
                    ProductModel p = gson.fromJson(msg.data, ProductModel.class);
                    System.out.println("PUT command with Product = " + p);
                    int res = adapter.saveProduct(p);
                    if (res == IDataAdapter.PRODUCT_SAVE_OK) {
                        msg.code = MessageModel.OPERATION_OK;
                    }
                    else {
                        msg.code = MessageModel.OPERATION_FAILED;
                    }
                    out.println(gson.toJson(msg));
                }

                if (msg.code == MessageModel.LOGIN) {
                    UserModel u = gson.fromJson(msg.data, UserModel.class);
                    System.out.println("LOGIN command with User = " + u);
                    UserModel user = adapter.loadUser(u.mUsername);
                    if (user != null && user.mPassword.equals(u.mPassword)) {
                        msg.code = MessageModel.OPERATION_OK;
                        totalActiveUsers++;
                        int accessToken = totalActiveUsers;
                        msg.ssid = accessToken;
                        msg.data = gson.toJson(user, UserModel.class);
                        activeUsers.put(accessToken, user);
                    }
                    else {
                        msg.code = MessageModel.OPERATION_FAILED;
                    }
                    out.println(gson.toJson(msg));  // answer login command!
                }

                if (msg.code == MessageModel.GET_PURCHASE_LIST_BY_CUSTOMER) {
                    int id = Integer.parseInt(msg.data);
                    PurchaseListModel res = adapter.loadPurchaseHistoryByCustomer(id);
                    msg.code = MessageModel.OPERATION_OK;
                    msg.data = gson.toJson(res);
                    out.println(gson.toJson(msg));  // answer get purchase history!!!
                }

                if (msg.code == MessageModel.GET_PURCHASE_LIST) {
                    PurchaseListModel res = adapter.loadPurchaseHistory();
                    msg.code = MessageModel.OPERATION_OK;
                    msg.data = gson.toJson(res);
                    out.println(gson.toJson(msg));  // answer get purchase history!!!
                }

                if (msg.code == MessageModel.SEARCH_PRODUCT) {
                    SearchProductParamModel model = new SearchProductParamModel();
                    model = gson.fromJson(msg.data, SearchProductParamModel.class);
                    ProductListModel res = adapter.searchProduct(model.mName, model.minPrice, model.maxPrice);
                    msg.code = MessageModel.OPERATION_OK;
                    msg.data = gson.toJson(res);
                    out.println(gson.toJson(msg));  // answer get purchase history!!!
                }


                // add responding to GET_USER, PUT_USER,...
                if(msg.code == MessageModel.PUT_USER) {
                    UserModel user = gson.fromJson(msg.data, UserModel.class);
                    int res = adapter.saveUser(user);

                    if (res == IDataAdapter.USER_SAVE_OK)
                        msg.code = MessageModel.OPERATION_OK;
                    else
                        msg.code = MessageModel.OPERATION_FAILED;

                    out.println(gson.toJson(msg));
                }

                if(msg.code == MessageModel.GET_USER) {
                    String username = msg.data;
                    UserModel res = adapter.loadUser(username);
                    msg.code = MessageModel.OPERATION_OK;
                    msg.data = gson.toJson(res);
                    out.println(gson.toJson(msg));
                }

                // Customer
                if(msg.code == MessageModel.PUT_CUSTOMER) {
                    CustomerModel customer = gson.fromJson(msg.data, CustomerModel.class);
                    int res = adapter.saveCustomer(customer);

                    if(res == IDataAdapter.CUSTOMER_SAVE_OK)
                        msg.code = MessageModel.OPERATION_OK;
                    else
                        msg.code = MessageModel.OPERATION_FAILED;

                    out.println(gson.toJson(msg));
                }

                if(msg.code == MessageModel.GET_CUSTOMER) {
                    int id = Integer.parseInt(msg.data);
                    CustomerModel res = adapter.loadCustomer(id);
                    msg.code = MessageModel.OPERATION_OK;
                    msg.data = gson.toJson(res);
                    out.println(gson.toJson(msg));
                }

                // Purchase
                if(msg.code == MessageModel.PUT_PURCHASE) {
                    PurchaseModel purchase = gson.fromJson(msg.data, PurchaseModel.class);
                    int res = adapter.savePurchase(purchase);

                    if(res == IDataAdapter.PURCHASE_SAVE_OK)
                        msg.code = MessageModel.OPERATION_OK;
                    else
                        msg.code = MessageModel.OPERATION_FAILED;

                    out.println(gson.toJson(msg));
                }

                if(msg.code == MessageModel.GET_PURCHASE) {
                    int id = Integer.parseInt(msg.data);
                    PurchaseModel res = adapter.loadPurchase(id);
                    msg.code = MessageModel.OPERATION_OK;
                    msg.data = gson.toJson(res);
                    out.println(gson.toJson(msg));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}