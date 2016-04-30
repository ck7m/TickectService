package com.ticketingservice;

import com.ticketingservice.exception.TicketServiceException;
import com.ticketingservice.factories.StageFactory;
import com.ticketingservice.model.SeatHold;
import com.ticketingservice.model.Stage;
import com.ticketingservice.service.TicketService;
import com.ticketingservice.service.TicketServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.Scanner;

/**
 * Created by lva833 on 4/30/16.
 */
@Component
public class ApplicationStarter implements CommandLineRunner {
    private static final String INSTRUCTIONS = "1) Press 1 : Find the number of seats in the requested level that are neither held nor reserved\n" +
            "2) Press 2: Find and hold the best available seats for a customer \n" +
            "3) Press 3: Commit seats held for a specific customer  \n" +
            "Anytime type exit to quit.";
    @Resource
    private TicketService ticketService;

    @Resource
    Environment env;

    @Override
    public void run(String... args) throws Exception {

        Stage stage = StageFactory.createDefaultStage();
        ((TicketServiceImpl) ticketService).setStage(stage);

        System.out.println("\n\n Welcome to Ticketing Service system \n");
        Scanner scanner = new Scanner(System.in);
        String userInputStr = readInput(scanner, INSTRUCTIONS);
        int userInput = parseInt(userInputStr);
        do {
            switch (userInput) {
                case 1:
                    numOfAvailableSeats(scanner);
                    break;
                case 2:
                    findAndHoldSeats(scanner);
                    String email;
                    break;
                case 3:
                    reserveSeats(scanner);
                    break;
                default:
                    System.out.println("Please enter valid number or 'exit'. ");
                    break;
            }
            userInputStr = readInput(scanner, INSTRUCTIONS);
            userInput = parseInt(userInputStr);
        } while (!"exit".equalsIgnoreCase(userInputStr));

    }

    private void numOfAvailableSeats(Scanner scanner) {
        try {
            Optional<Integer> level = levelToOptional("Enter Level between 1-4 (Optional): ", scanner);
            int numOfAvailableTickets = ticketService.numSeatsAvailable(level);
            System.out.println(String.format("Total number of Available Tickets : %d", numOfAvailableTickets));
        } catch (Exception e) {
            System.err.println("Sorry something went wrong. ");
        }
    }

    private void reserveSeats(Scanner scanner) throws TicketServiceException {
        String userInputStr;
        String email;
        userInputStr = readInput(scanner, "Enter the seat hold id : ");
        int confirmationId = parseInt(userInputStr);
        email = readInput(scanner, "Enter the customer email : ");
        try {
            String status = ticketService.reserveSeats(confirmationId, email);
            System.out.println(String.format("Booking Status : %s", status));
        } catch (IllegalArgumentException | TicketServiceException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Sorry something went wrong. ");
        }
    }

    private void findAndHoldSeats(Scanner scanner) throws TicketServiceException {
        String userInputStr = readInput(scanner, "Enter the number of seats to find and hold: ");
        int numOfSeats = parseInt(userInputStr);
        Optional<Integer> minLevel = levelToOptional("Enter min Level between 1-4 (Optional): ", scanner);
        Optional<Integer> maxLevel = levelToOptional("Enter max Level between 1-4 (Optional): ", scanner);
        String email = readInput(scanner, "Enter the customer email : ");

        try {
            SeatHold seatHold = ticketService.findAndHoldSeats(numOfSeats, minLevel, maxLevel, email);
            System.out.println(String.format("Status of the request : %s", seatHold));
        } catch (IllegalArgumentException | TicketServiceException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Sorry something went wrong. ");
        }
    }

    private int parseInt(String userInputStr) {
        int i = -1;
        if (userInputStr == null || userInputStr.trim().isEmpty()) return i;
        try {
            i = Integer.parseInt(userInputStr);
        } catch (NumberFormatException e) {
        }
        return i;
    }

    private String readInput(Scanner scanner, String msg) {
        System.out.println(msg);
        String userInputStr = scanner.nextLine();
        if ("exit".equalsIgnoreCase(userInputStr)) System.exit(1);
        return userInputStr;
    }

    private Optional<Integer> levelToOptional(String msg, Scanner scanner) {
        String userInputStr = readInput(scanner, msg);
        int i = parseInt(userInputStr);
        return i < 0 ? Optional.empty() : Optional.of(i);
    }
}
