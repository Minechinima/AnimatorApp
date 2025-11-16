package com.minechinima;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        try {
            throw new Exception();
        } catch (Exception e) {
            return;
        } finally {
            System.out.println("finally");
        }

    }
}