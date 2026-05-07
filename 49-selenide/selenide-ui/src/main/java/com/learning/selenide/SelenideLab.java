package com.learning.selenide;

public class SelenideLab {

    public static void main(String[] args) {
        System.out.println("=== Selenide UI Testing Lab ===\n");

        System.out.println("1. Selenide Test Example:");
        System.out.println("   open(\"http://localhost:8080/login\");");
        System.out.println("   $(By.name(\"username\")).val(\"user\");");
        System.out.println("   $(By.name(\"password\")).val(\"pass\");");
        System.out.println("   $(\"button[type=submit]\").click();");
        System.out.println("   $(\".welcome\").shouldHave(text(\"Welcome, user\"));");

        System.out.println("\n2. Page Object Pattern:");
        System.out.println("   - LoginPage: Login form interactions");
        System.out.println("   - DashboardPage: Dashboard verification");
        System.out.println("   - SettingsPage: Settings management");

        System.out.println("\n3. Selenide Advantages:");
        System.out.println("   - Auto-waiting for elements");
        System.out.println("   - Concise, fluent API");
        System.out.println("   - Automatic screenshots on failure");
        System.out.println("   - Built-in Ajax support");

        System.out.println("\n=== Selenide UI Testing Lab Complete ===");
    }
}