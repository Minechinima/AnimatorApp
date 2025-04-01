package com.minechinima;

import com.minechinima.controller.ControllerResult;
import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.model.exceptions.UserException;

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