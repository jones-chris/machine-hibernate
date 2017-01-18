package com.cj.items;

import com.cj.items.model.Items;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;

/**
 * Created by chris&amy on 1/17/2017.
 */
public enum CustomerMenu {
    BUY,
    EXIT,
    MANAGER;

    public static boolean matchesEnum(String input) {
        for (CustomerMenu menuItem : CustomerMenu.values()) {
            if (menuItem.toString().equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static void printCustomerMenu() {
        System.out.println("Please choose one of the following: ");
        for (CustomerMenu choice : EnumSet.allOf(CustomerMenu.class)) {
            System.out.printf("%s%n", choice);
        }
        Scanner scanner = new Scanner(System.in);
        String customerChoice = scanner.next();
        if (!matchesEnum(customerChoice)) {
            System.out.println("That is not a valid choice.");
            printCustomerMenu(); //starts method over again.
        }

        if (customerChoice.equalsIgnoreCase("EXIT")) {
            System.out.println("Have a good day!");
            Application.getSession().close(); //close session
            System.exit(0); //close application
        }

        if (customerChoice.equalsIgnoreCase("BUY")) {
            System.out.println("Here are the available vending machine items: ");

            String hql = "SELECT new Items(location, name, price) FROM items WHERE quantity > 0 ORDER BY location";
            Query query = Application.getSession().createQuery(hql);
            List<Items> results = query.list(); //downcast from Object[] to List<Items>.  Suppressed unchecked warning.
            for (int i = 0; i < results.size() - 1; i++) {
                Items item = results.get(i);
                System.out.printf("%s     %s     %d", item.getLocation(), item.getName(),
                       item.getPrice());
            }

            Scanner sc = new Scanner(System.in);
            String location = sc.next();
            String buyHql = "UPDATE items SET quantity = (SELECT quantity FROM items WHERE location =" +
                    " '" + location + "') - 1 WHERE location = '" + location + "'";
            try {
                Transaction transaction = Application.getSession().beginTransaction();
                Query buyQuery = Application.getSession().createQuery(buyHql);
                buyQuery.executeUpdate();
                transaction.commit();
                System.out.printf("Enjoy!");
            } catch (Exception ex) {
                System.out.println("I'm sorry there was a problem vending your product.");
                //TODO:  cj subtract money from funds to refund customer.
            }
        }

        if (customerChoice.equalsIgnoreCase("MANAGER")) {
            //TODO:  cj build sign in method to start printManagerMenu method.
            ManagerMenu managerMenu = new ManagerMenu();
            managerMenu.printManagerMenu();
            //manager = new Manager("chris.jones", "chris1234", machine);
            //System.out.printf("Welcome back %s!  %n", manager.getUsernameFirstName());
            //printManagerMenu();
        }

        printCustomerMenu();
    }
}
