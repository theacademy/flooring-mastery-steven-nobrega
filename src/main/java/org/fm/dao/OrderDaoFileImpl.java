package org.fm.dao;

import org.fm.dto.Order;
import org.fm.dto.Product;
import org.fm.dto.TaxInfo;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrderDaoFileImpl implements OrderDao {

    private static final String ORDERS_DIR = "Orders/";
    private static final String EXPORT_FILE = "Backup/DataExport.txt";
    private static final String DELIMITER = ",";
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("MMddyyyy");
    private static final DateTimeFormatter EXPORTALL_DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    //Map containing every order per day
    private Map<String, List<Order>> orders = new HashMap<>();

    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws OrderPersistenceException {
        loadOrdersForDate(date);
        String key = date.format(FILE_DATE_FORMAT);
        List<Order> result = orders.get(key);
        if (result == null)
            return new ArrayList<>();
        return new ArrayList<>(result);
    }

    @Override
    public Order addOrder(LocalDate date, Order order) throws OrderPersistenceException {
        loadOrdersForDate(date);
        String key = date.format(FILE_DATE_FORMAT);
        // Get the list for this key. If none exists, create a new ArrayList and store it.
        orders.computeIfAbsent(key, k-> new ArrayList<>()).add(order);
        writeOrdersForDate(date);
        return order;
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws OrderPersistenceException {
        loadOrdersForDate(date);
        String key = date.format(FILE_DATE_FORMAT);
        List<Order> list = orders.get(key);
        if (list == null)
            throw new OrderPersistenceException("No orders for that date.");
        for (int i = 0; i < list.size(); i++) {
            //If order number matches
            if (list.get(i).getOrderNumber() == order.getOrderNumber()) {
                //Update the order
                list.set(i, order);
                writeOrdersForDate(date);
                return order;
            }
        }
        throw new OrderPersistenceException("Order not found.");
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws OrderPersistenceException {
        loadOrdersForDate(date);
        String key = date.format(FILE_DATE_FORMAT);
        List<Order> list = orders.get(key);
        if (list == null)
            throw new OrderPersistenceException("No orders for that date.");
        Order orderToRemove = list.stream()
                .filter(o -> o.getOrderNumber() == orderNumber)
                .findFirst()
                .orElseThrow(() -> new OrderPersistenceException("Order not found."));
        list.remove(orderToRemove);
        writeOrdersForDate(date);
        return orderToRemove;
    }

    @Override
    public void exportAllData() throws OrderPersistenceException {
        loadAllOrders();
        new File("Backup").mkdirs();

        try (PrintWriter out = new PrintWriter(new FileWriter(EXPORT_FILE))) {
            out.println("OrderNumber,CustomerName,State,TaxRate,ProductType,Area,"
                    + "CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total,OrderDate");

            for (Map.Entry<String, List<Order>> entry : orders.entrySet()) {
                LocalDate date = LocalDate.parse(entry.getKey(), FILE_DATE_FORMAT);
                String exportDateStr = date.format(EXPORTALL_DATE_FORMAT);

                for (Order o : entry.getValue()) {
                    out.println(marshallOrder(o) + "," + exportDateStr);
                }
            }
        } catch (IOException e) {
            throw new OrderPersistenceException("Could not export data.", e);
        }
    }

    @Override
    public int getMaxOrderNumber() throws OrderPersistenceException {
        loadAllOrders();
        return orders.values().stream()
                .flatMap(List::stream) // puts all lists in a single list
                .mapToInt(Order::getOrderNumber) // convert to a list with only the order numbers
                .max()
                .orElse(0);
    }

    private void loadAllOrders() throws OrderPersistenceException {
        File dir = new File(ORDERS_DIR);
        File[] files = dir.listFiles((d, name) -> name.startsWith("Orders_") && name.endsWith(".txt"));
        if (files == null) return;

        for (File file : files) {
            String datePart = file.getName().replace("Orders_", "").replace(".txt", "");
            LocalDate date = LocalDate.parse(datePart, FILE_DATE_FORMAT);
            loadOrdersForDate(date); // populates the orders map for each order file
        }
    }

    private String getFileName(LocalDate date) {
        return ORDERS_DIR + "Orders_" + date.format(FILE_DATE_FORMAT) + ".txt";
    }

    private void loadOrdersForDate(LocalDate date) throws OrderPersistenceException {
        String key = date.format(FILE_DATE_FORMAT);
        orders.put(key, new ArrayList<>());
        File file = new File(getFileName(date));
        if (!file.exists())
            return;
        try (Scanner sc = new Scanner(new FileReader(file))) {
            if (sc.hasNextLine())
                sc.nextLine(); //skip header
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    orders.get(key).add(unmarshallOrder(line));
                }
            }
        } catch (FileNotFoundException e) {
            throw new OrderPersistenceException("Could not load orders file.", e);
        }
    }

    private Order unmarshallOrder(String line) {
        String[] orderTokens = line.split(DELIMITER);
        //Index 1 - Order Number
        Order orderFromFile = new Order(Integer.parseInt(orderTokens[0]));
        //Index 2 - Customer Name
        orderFromFile.setCustomerName(orderTokens[1]);

        TaxInfo taxInfoFromFile = new TaxInfo();
        //Index 3 - State
        taxInfoFromFile.setStateAbbreviation(orderTokens[2]);
        //Index 4 - Tax Rate
        taxInfoFromFile.setTaxRate(new BigDecimal(orderTokens[3]));
        orderFromFile.setTaxInfo(taxInfoFromFile);

        Product productFromFile = new Product();
        //Index 5 - Product Type
        productFromFile.setProductType(orderTokens[4]);
        //Index 6 - Area
        orderFromFile.setArea(new BigDecimal(orderTokens[5]));
        //Index 7 - CostPerSquareFoot
        productFromFile.setCostPerSquareFoot(new BigDecimal(orderTokens[6]));
        //Index 8 - LaborCostPerSquareFoot
        productFromFile.setLaborCostPerSquareFoot(new BigDecimal(orderTokens[7]));
        orderFromFile.setProduct(productFromFile);

        //Index 9 - Material Cost
        orderFromFile.setMaterialCost(new BigDecimal(orderTokens[8]));
        //Index 10 - Labor Cost
        orderFromFile.setLaborCost(new BigDecimal(orderTokens[9]));
        //Index 11 - Tax
        orderFromFile.setTax(new BigDecimal(orderTokens[10]));
        //Index 12 - Total
        orderFromFile.setTotal(new BigDecimal(orderTokens[11]));

        return orderFromFile;
    }

    private void writeOrdersForDate(LocalDate date) throws OrderPersistenceException {
        // Create folder just in case it's absent
        new File(ORDERS_DIR).mkdirs();
        try (PrintWriter out = new PrintWriter(new FileWriter(getFileName(date)))){
            out.println("OrderNumber,CustomerName,State,TaxRate,ProductType,Area,"
                    + "CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total");
            orders.get(date.format(FILE_DATE_FORMAT)).forEach(o -> out.println(marshallOrder(o)));
        } catch (IOException e) {
            throw new OrderPersistenceException("Could not write orders file.", e);
        }
    }

    private String marshallOrder(Order o) {
        return o.getOrderNumber() + DELIMITER + o.getCustomerName() + DELIMITER
                + o.getTaxInfo().getStateAbbreviation() + DELIMITER + o.getTaxInfo().getTaxRate() + DELIMITER
                + o.getProduct().getProductType() + DELIMITER + o.getArea() + DELIMITER
                + o.getProduct().getCostPerSquareFoot() + DELIMITER + o.getProduct().getLaborCostPerSquareFoot() + DELIMITER
                + o.getMaterialCost() + DELIMITER + o.getLaborCost() + DELIMITER
                + o.getTax() + DELIMITER + o.getTotal();
    }
}
