package com.example;

import com.example.controllers.UserController;
import com.example.dao.UserDAO;
import com.example.service.UserService;
import com.example.util.HibernateUtil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserDAO dao = new UserDAO(HibernateUtil.getSessionFactory());
        UserService service = new UserService(dao);
        try(Scanner sc = new Scanner(System.in)) {
            UserController controller = new UserController(service, sc);
            boolean isRunning = true;
            System.out.println("Hello!");
            while(isRunning) {
                System.out.println("\nPlease select the operation:");
                System.out.println("1 - add user");
                System.out.println("2 - get user by id");
                System.out.println("3 - get all users");
                System.out.println("4 - update user");
                System.out.println("5 - delete user");
                System.out.println("6 - exit");

                int num = controller.readInt();
                switch (num) {
                    case 1 -> controller.create();
                    case 2 -> controller.getById();
                    case 3 -> controller.getAll();
                    case 4 -> controller.update();
                    case 5 -> controller.delete();
                    case 6 -> isRunning = false;
                    default -> System.out.println("Error: you must enter a number between 1 and 6.");
                }
            }
        }
        HibernateUtil.shutdown();
    }
}
