package ru.home.telegram_bot.botapi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Field {
    private String[][] info_matrix;
    private String[][] present_matrix;
    private ArrayList<Integer> mines_loc;
    private int number_of_mines;
    private boolean active = false;
    final int X_DIM;
    final int Y_DIM;
    public boolean victory = true;
    int[] pos_seats;
    private int move_count = 0;

    public Field(int X_DIM, int Y_DIM) {
        this.X_DIM = X_DIM;
        this.Y_DIM = Y_DIM;
        this.number_of_mines = 10;
        //this.generate_mines_location();
        //this.fill_matrix();
        //System.out.println(Arrays.deepToString(this.info_matrix));
    }
    public SendMessage size_info(Message message) {
        String reply_text = "";
        reply_text+="Current X_DIM: " +Integer.toString(X_DIM) + ", Current Y_DIM: " + Integer.toString(Y_DIM);
        reply_text+="\n";
        reply_text+="In order to choose the level please write 'Level: HARD/MEDIUM/EASY' ";
        SendMessage reply = new SendMessage(message.getChatId(),reply_text);
        return reply;
    }

    public SendMessage set_complexity(Message message) {
        //choose amount of mines
        //generate_mines_location
        int number_of_mines;
        String message_text = message.getText();
        switch (message_text){
            case "EASY":
                number_of_mines = X_DIM*Y_DIM/10;
            case "MEDIUM":
                number_of_mines = X_DIM*Y_DIM/7;
            case "HARD":
                number_of_mines = X_DIM*Y_DIM/5;
            default:
                number_of_mines = 10;

        }
        setNumber_of_mines(number_of_mines);
        generate_mines_location();
        String answer = "";
        answer+= print_matrix(message);
        answer+='\n';
        answer+="In order to make a move please write 'Move: mine/free, x_coordinate , y_coordinate";
        SendMessage reply = new SendMessage(message.getChatId() , answer);
        active = true;
        System.out.println(print_info_matrix());
        return reply;
    }

    public boolean isActive() {
        return active;
    }
    public SendMessage end_message(Message message){
        if(isVictory()){
            SendMessage reply = new SendMessage(message.getChatId(),"You won! If you want to start new game - write 'Size: x_size , y_size' ");
        }
        SendMessage reply = new SendMessage(message.getChatId(),"You lost! If you want to start new game - write 'Size: x_size , y_size' ");
        return reply;
    }

    public SendMessage print_matrix(Message message) {
        String answer = "";
        answer+="   ";
        for (int i = 0; i < Y_DIM; i++) {
            answer+=(i + 1 + "  │  ");
        }
        answer+="\n";
        for (int i = 0; i < Y_DIM; i++) {
            if (i > 0) answer+="\n";
            answer+="   ";
            answer+=(i + 1 + " │ ");
            for (int j = 0; j < X_DIM; j++) {
                answer+=(present_matrix[i][j] + "  ");
            }
            answer+=("│");
        }
        answer+="\n";
        SendMessage reply = new SendMessage(message.getChatId(),answer);
        return reply;
    }
    private String print_info_matrix() {
        String answer = "";
        answer+=" │123456789│";
        answer+="\n";
        answer+=("—│—————————│");
        answer+="\n";
        for (int i = 0; i < Y_DIM; i++) {
            if (i > 0) answer+="\n";
            answer+=(i + 1 + "│");
            for (int j = 0; j < X_DIM; j++) {
                answer+=(info_matrix[i][j] + " ");
            }
            answer+=("│");
        }
        answer+="\n";
        answer+=("—│—————————│");
        answer+="\n";

        return answer;
    }

    public void setNumber_of_mines(int number_of_mines) {
        this.number_of_mines = number_of_mines;
    }

    public void generate_mines_location() {
        Random rand = new Random();
        mines_loc = new ArrayList<>(number_of_mines);
        int int_random;
        pos_seats = new int[X_DIM * Y_DIM];
        for (int i = 0; i < pos_seats.length; i++) {
            pos_seats[i] = i;
        }
        for (int i = 0; i < number_of_mines; i++) {
            int_random = rand.nextInt(pos_seats.length - 1);
            mines_loc.add(pos_seats[int_random]);
            pos_seats = delete(pos_seats, int_random);
        }
        fill_matrix();


    }

    public void fill_matrix() {
        int row_value;
        info_matrix = new String[Y_DIM][X_DIM];
        present_matrix = new String[Y_DIM][X_DIM];
        for (int i = 0; i < Y_DIM; i++) {
            for (int j = 0; j < X_DIM; j++) {
                row_value = i * Y_DIM + j;
                present_matrix[i][j] = ".";
                if (mines_loc.contains(row_value)) {
                    info_matrix[i][j] = "X";
                } else {
                    info_matrix[i][j] = ".";
                }
            }
        }
        // Count of mines
        for (int i = 0; i < Y_DIM; i++) {
            for (int j = 0; j < X_DIM; j++) {
                if (count_mines(i, j) > 0) {
                    info_matrix[i][j] = Integer.toString(count_mines(i, j));
                }
            }
        }
    }

    private int count_mines(int y, int x) {
        if (info_matrix[y][x] == "X") {
            return -1;
        }
        int count = 0;
        for (int j = -1; j < 2; j++) {
            for (int i = -1; i < 2; i++) {
                if ((y + j >= 0 & y + j < Y_DIM) & (x + i >= 0 & x + i < X_DIM)) {
                    if (info_matrix[y + j][x + i] == "X") {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public void update_matrix(int i_guess, int j_guess, String guess_type) {
        move_count++;
        System.out.println("USER TRYING TO UPDATE THE MATRIX");
        if ("free".equals(guess_type)) {
            if (present_matrix[j_guess][i_guess] == ".") {
                if (info_matrix[j_guess][i_guess] == ".") {
                    System.out.println("clearingr");
                    clearing(j_guess, i_guess);
                } else if (info_matrix[j_guess][i_guess] == "X") {
                    System.out.println("You stepped on a mine and failed!");
                    active = false;
                } else {
                    present_matrix[j_guess][i_guess] = info_matrix[j_guess][i_guess];
                }

            }
        } else if (guess_type.equals("mine")) {
            int guess_number = Y_DIM * j_guess + i_guess;
            System.out.println("guess number" + guess_number);
            if (present_matrix[j_guess][i_guess] == ".") {
                if (mines_loc.contains(guess_number)) {
                    mines_loc.remove(mines_loc.indexOf(guess_number));
                } else mines_loc.add(guess_number);
                present_matrix[j_guess][i_guess] = "*";
                System.out.println(present_matrix[j_guess][i_guess]);
            } else if (present_matrix[j_guess][i_guess] == "*") {
                if (mines_loc.contains(guess_number)) {
                    mines_loc.remove(mines_loc.indexOf(guess_number));
                } else mines_loc.add(guess_number);
                present_matrix[j_guess][i_guess] = ".";
            }
        } else {
            System.out.println(guess_type);
            System.out.println("Guess type should be either 'free' or 'mine'");
        }
        if (isVictory()) {
            active = false;
        }
    }

    public void clearing(int j_guess, int i_guess) {
        if (info_matrix[j_guess][i_guess] != ".") {
            present_matrix[j_guess][i_guess] = info_matrix[j_guess][i_guess];
            return;
        }
        if (present_matrix[j_guess][i_guess] != "." & present_matrix[j_guess][i_guess] != "*") {
            return;
        }
        present_matrix[j_guess][i_guess] = "/";
        for (int j = -1; j < 2; j++) {
            for (int i = -1; i < 2; i++) {
                if ((j_guess + j >= 0 & j_guess + j < Y_DIM) & (i_guess + i >= 0 & i_guess + i < X_DIM)) {
                    clearing(j_guess + j, i_guess + i);
                }
            }
        }
    }

    private boolean isVictory() {
        if (mines_loc.size() == 0) {
            return true;
        }
        return false;
    }

    public String[][] getPresent_matrix() {
        return present_matrix;
    }

    public static int[] delete(int[] array, int index) {
        int[] copy = new int[array.length - 1];

        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != index) {
                copy[j++] = array[i];
            }
        }
        return copy;
    }
}