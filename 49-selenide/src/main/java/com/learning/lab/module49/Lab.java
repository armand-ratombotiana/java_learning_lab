package com.learning.lab.module49;

import com.codeborne.selenide.*;
import com.codeborne.selenide.conditions.*;
import com.codeborne.selenide.logevents.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 49: Selenide Lab ===\n");

        System.out.println("1. Selenide Configuration:");
        Configuration.browser = "chrome";
        Configuration.timeout = 10000;
        Configuration.headless = false;
        Configuration.reportsFolder = "target/reports";

        System.out.println("\n2. Element Selection:");
        elementSelectionDemo();

        System.out.println("\n3. Conditions:");
        conditionsDemo();

        System.out.println("\n4. Actions:");
        actionsDemo();

        System.out.println("\n5. Waiting:");
        waitingDemo();

        System.out.println("\n6. Dynamic Elements:");
        dynamicElementsDemo();

        System.out.println("\n7. Collections:");
        collectionsDemo();

        System.out.println("\n8. Windows and Frames:");
        windowsFramesDemo();

        System.out.println("\n9. Screenshot and Debugging:");
        screenshotDemo();

        System.out.println("\n=== Selenide Lab Complete ===");
    }

    static void elementSelectionDemo() {
        System.out.println("   By CSS:");
        SelenideElement element = $("#username");
        element = $(".login-form");
        element = $("input[type='text']");

        System.out.println("\n   By XPath:");
        element = $x("//button[@class='submit']");
        element = $x("//div[@id='content']//span");

        System.out.println("\n   By Text:");
        element = $(byText("Login"));
        element = $(byPartialText("Log"));

        System.out.println("\n   By Attribute:");
        element = $(byAttribute("name", "email"));
        element = $(byAttribute("type", "password"));
    }

    static void conditionsDemo() {
        System.out.println("   Visible Conditions:");
        System.out.println("   element.shouldBe(visible)");
        System.out.println("   element.shouldNotBe(visible)");
        System.out.println("   element.shouldHave(text(\"Hello\"))");
        System.out.println("   element.shouldHave(exist)");

        System.out.println("\n   Other Conditions:");
        System.out.println("   .shouldHave(attribute(\"type\", \"text\"))");
        System.out.println("   .shouldHave(cssClass(\"active\"))");
        System.out.println("   .shouldHave(value(\"default\"))");
        System.out.println("   .shouldBe(enabled)");
        System.out.println("   .shouldBe(disabled)");
        System.out.println("   .shouldBe(selected)");
    }

    static void actionsDemo() {
        System.out.println("   Click Operations:");
        System.out.println("   $(\"#btn\").click()");
        System.out.println("   $(\"#btn\").doubleClick()");
        System.out.println("   $(\"#btn\").contextClick()");

        System.out.println("\n   Input Operations:");
        System.out.println("   $(\"#input\").setValue(\"text\")");
        System.out.println("   $(\"#input\").append(\"more\")");
        System.out.println("   $(\"#input\").clear()");
        System.out.println("   $(\"#input\").pressEnter()");
        System.out.println("   $(\"#input\").pressEscape()");

        System.out.println("\n   Hover and Drag:");
        System.out.println("   $(\"#menu\").hover()");
        System.out.println("   $(\"#draggable\").dragAndDropTo(\"#dropzone\")");
    }

    static void waitingDemo() {
        System.out.println("   Automatic Waiting:");
        System.out.println("   Selenide waits for elements to be visible");
        System.out.println("   Default timeout: 4 seconds");
        System.out.println("   Configurable: Configuration.timeout");

        System.out.println("\n   Explicit Wait:");
        System.out.println("   $(\"#element\").should(appear)");
        System.out.println("   $(\"#element\").shouldNot(vanish)");

        System.out.println("\n   Collection Wait:");
        System.out.println("   $$\".items\").shouldHave(size(5))");
        System.out.println("   $$\".items\").shouldBe(empty)");
    }

    static void dynamicElementsDemo() {
        System.out.println("   Dynamic Element Handling:");
        System.out.println("   $(\"#dynamic\").waitUntil(visible, 10000)");
        System.out.println("   $(\"#dynamic\").shouldHave(text(\"Loaded\"))");

        System.out.println("\n   Ajax Loading:");
        System.out.println("   $(\".loader\").shouldNot(visible)");
        System.out.println("   $(\".result\").shouldBe(visible)");

        System.out.println("\n   Lazy Loading:");
        System.out.println("   $(\".item\").scrollIntoView(true)");
    }

    static void collectionsDemo() {
        System.out.println("   Collection Methods:");
        System.out.println("   $$(\".items\").size()");
        System.out.println("   $$(\".items\").filter(visible)");
        System.out.println("   $$(\".items\").first()");
        System.out.println("   $$(\".items\").last()");
        System.out.println("   $$(\".items\").get(2)");

        System.out.println("\n   Collection Conditions:");
        System.out.println("   $$(\".items\").shouldHave(size(5))");
        System.out.println("   $$(\".items\").shouldBe(empty)");
        System.out.println("   $$(\".items\").filterBy(text(\"Item 1\"))");
    }

    static void windowsFramesDemo() {
        System.out.println("   Window Operations:");
        System.out.println("   Switch to window: switchTo().window(index)");
        System.out.println("   Close window: switchTo().window(index).close()");
        System.out.println("   Current window: switchTo().defaultContent()");

        System.out.println("\n   Frame Operations:");
        System.out.println("   switchTo().frame(\"iframeName\")");
        System.out.println("   switchTo().frame(element)");
        System.out.println("   switchTo().defaultContent()");
    }

    static void screenshotDemo() {
        System.out.println("   Screenshots:");
        System.out.println("   Selenide takes screenshot on failure");
        System.out.println("   Configuration.reportsFolder");
        System.out.println("   Screenshots saved as .png files");

        System.out.println("\n   Manual Screenshot:");
        System.out.println("   File screenshot = Selenide.screenshot();");
        System.out.println("   String base64 = Selenide.screenshotAsBase64();");

        System.out.println("\n   Debug Mode:");
        System.out.println("   Configuration.startMaximized = true;");
        System.out.println("   Configuration.holdBrowserOpen = true;");
    }
}