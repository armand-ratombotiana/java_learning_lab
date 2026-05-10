package com.learning.selenide;

import com.codeborne.selenide.*;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.List;

public class SelenideSolution {

    public void openUrl(String url) {
        Selenide.open(url);
    }

    public void click(By selector) {
        Selenide.$(selector).click();
    }

    public void type(By selector, String text) {
        Selenide.$(selector).setValue(text);
    }

    public String getText(By selector) {
        return Selenide.$(selector).text();
    }

    public String getValue(By selector) {
        return Selenide.$(selector).val();
    }

    public boolean isVisible(By selector) {
        return Selenide.$(selector).isDisplayed();
    }

    public void waitForVisible(By selector, long seconds) {
        Selenide.$(selector).shouldBe(Condition.visible, Duration.ofSeconds(seconds));
    }

    public void waitForText(By selector, String text) {
        Selenide.$(selector).shouldHave(Condition.text(text));
    }

    public List<SelenideElement> findAll(By selector) {
        return Selenide.$$(selector);
    }

    public void selectOption(By dropdown, String option) {
        Selenide.$(dropdown).selectOption(option);
    }

    public void submitForm(By form) {
        Selenide.$(form).submit();
    }

    public void refresh() {
        Selenide.refresh();
    }

    public void close() {
        Selenide.closeWindow();
    }
}