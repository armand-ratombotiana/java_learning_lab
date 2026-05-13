package com.learning.lab.module01.project;

import java.util.Scanner;

public class InvoiceGenerator {
    private static final double TAX_RATE = 0.08;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Invoice Generator ===");
        System.out.print("Enter item name: ");
        String item = scanner.nextLine();
        
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        
        System.out.print("Enter unit price: $");
        double price = scanner.nextDouble();
        
        double subtotal = quantity * price;
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;
        
        System.out.println("\n=== INVOICE ===");
        System.out.println("Item: " + item);
        System.out.println("Quantity: " + quantity);
        System.out.println("Unit Price: $" + price);
        System.out.println("Subtotal: $" + subtotal);
        System.out.println("Tax (8%): $" + tax);
        System.out.println("Total: $" + total);
        
        scanner.close();
    }
}