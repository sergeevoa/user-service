package com.example.controllers;

import com.example.domain.User;
import com.example.service.UserService;

import java.util.List;
import java.util.Scanner;

public class UserController {
    private final UserService service;
    private final Scanner sc;

    public UserController(UserService service, Scanner sc) {
        this.service = service;
        this.sc = sc;
    }

    public void create() {
        System.out.println("Enter name:");
        String name = sc.nextLine();
        name = name.isBlank() ? null : name;
        System.out.println("Enter email:");
        String email = sc.nextLine();
        email = email.isBlank() ? null : email;
        System.out.println("Enter age:");
        int age = readInt();
        service.create(name, email, age);
    }

    private User getUser() {
        System.out.println("Enter user id:");
        User user = service.get(readLong());
        return user;
    }

    public void getById() {
        User user = getUser();
        if (user != null)
            user.print();
        else
            System.out.println("User is not found");
    }

    public void getAll() {
        List<User> userList = service.getAll();
        if (userList != null && userList.size() != 0) {
            for (User u : userList) {
                u.print();
            }
        } else {
            System.out.println("User list is empty");
        }
    }

    public void update() {
        User user = getUser();
        if (user != null) {
            System.out.println("Update rules: enter new value to the line to update field, leave the line blank to keep " +
                    "it's old value.");
            System.out.println("Enter new name:");
            String newName = sc.nextLine();
            newName = newName.isBlank() ? null : newName;
            System.out.println("Enter new email:");
            String newEmail = sc.nextLine();
            newEmail = newEmail.isBlank() ? null : newEmail;
            System.out.println("Enter new age:");
            String ageLine = sc.nextLine();
            Integer newAge = ageLine.isBlank() ? null: Integer.valueOf(ageLine);
            service.update(user.getId(), newName, newEmail, newAge);
        } else {
            System.out.println("User is not found");
        }

    }

    public void delete() {
        User user = getUser();
        if (user != null)
            service.delete(getUser().getId());
        else
            System.out.println("User is not found");
    }

    public Integer readInt() {
        while (true) {
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer");
            }
        }
    }

    private Long readLong() {
        while (true) {
            String input = sc.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid Long");
            }
        }
    }
}
