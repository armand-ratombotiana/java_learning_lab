package com.learning.lab.module01.mini;

public class TemperatureConverter {
    public double celsiusToFahrenheit(double c) {
        return (c * 9/5) + 32;
    }
    
    public double fahrenheitToCelsius(double f) {
        return (f - 32) * 5/9;
    }
    
    public static void main(String[] args) {
        TemperatureConverter conv = new TemperatureConverter();
        System.out.println("0°C = " + conv.celsiusToFahrenheit(0) + "°F");
        System.out.println("32°F = " + conv.fahrenheitToCelsius(32) + "°C");
    }
}