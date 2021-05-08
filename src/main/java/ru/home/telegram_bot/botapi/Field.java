package ru.home.telegram_bot.botapi;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.*;
import ru.home.telegram_bot.botapi.Button_handler;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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

    public Field(String complexity) {
        if(complexity.contains("SMALL")){
            this.X_DIM = 8;
            this.Y_DIM = 8;
        }
        else if(complexity.contains("MEDIUM")){
            this.X_DIM = 8;
            this.Y_DIM = 10;
        }
        else if(complexity.contains("LARGE")){
            this.X_DIM = 8;
            this.Y_DIM = 12;
        }
        else { System.out.println("Error , not found size"); this.X_DIM = 8;this.Y_DIM = 8;}
        this.number_of_mines = 10;

        //this.generate_mines_location();
        //this.fill_matrix();
        //System.out.println(Arrays.deepToString(this.info_matrix));
    }
    public SendMessage size_info(String message, long chatId) {
        String reply_text = "";
        reply_text+="Current field is: " +Integer.toString(X_DIM) + "x" + Integer.toString(Y_DIM);
        reply_text+="\n";
        reply_text+="Please choose a complexity";
        SendMessage reply = new SendMessage(chatId,reply_text);
        reply.setReplyMarkup(Button_handler.complexityButtons());
        return reply;
    }

    public String[][] getInfo_matrix() {
        return info_matrix;
    }

    public SendMessage set_complexity(String message, long chatId) {
        //choose amount of mines
        //generate_mines_location
        switch (message){
            case "EASY":
                this.number_of_mines = (X_DIM*Y_DIM)/9;
                break;
            case "MEDIUM":
                this.number_of_mines = (X_DIM*Y_DIM)/8;
                break;
            case "HARD":
                this.number_of_mines = (X_DIM*Y_DIM)/7;
                break;

        }
        setNumber_of_mines(this.number_of_mines);
        generate_mines_location();
        String answer = "";
        answer+='\n';
        answer+="Please choose a type of your next move:";
        answer+='\n';
        answer+="FREE - uncover the bracket";
        answer+='\n';
        answer+="MINE - flag the mine/unflag ";
        SendMessage reply = new SendMessage(chatId , answer);
        reply.setReplyMarkup(Button_handler.typeButtons());
        active = true;
        System.out.println(print_info_matrix());
        return reply;
    }

    public boolean isActive() {
        return active;
    }
    public SendMessage end_message(String message,long chatId , Field field){
        SendMessage reply;
        if(isVictory()){
            reply = new SendMessage(chatId,"You won! If you want to start new game choose your new size ");
            reply.setReplyMarkup(Button_handler.introductionButtons());
        }
        else {
            reply = new SendMessage(chatId,"You lost! If you want to start new game choose your new size");
            reply.setReplyMarkup(Button_handler.LostButtons(field,"info"));
            //reply.setReplyMarkup(Button_handler.introductionButtons());
        }
        return reply;
    }

    public SendMessage print_matrix(String message, long chatId) {
        String answer = "";
        answer+="To make your move - tap on the desired square";
        answer+="\n";
        answer+= EmojiParser.parseToUnicode(":white_large_square:")+ "-> not_opened " + EmojiParser.parseToUnicode(":triangular_flag_on_post:") + "-> flag , "+EmojiParser.parseToUnicode(":1234:") + "- number of mines around the bracket";
        answer+="\n";
        SendMessage reply = new SendMessage(chatId,answer);
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
        mines_loc.clear();
        int int_random;
        pos_seats = new int[X_DIM * Y_DIM];
        for (int i = 0; i < pos_seats.length; i++) {
            pos_seats[i] = i;
        }
        for (int i = 0; i < this.number_of_mines; i++) {
            int_random = rand.nextInt(pos_seats.length - 1);
            mines_loc.add(pos_seats[int_random]);
            pos_seats = delete(pos_seats, int_random);
        }
        System.out.println(mines_loc);
        fill_matrix();



    }

    public void fill_matrix() {
        int row_value;
        info_matrix = new String[Y_DIM][X_DIM];
        present_matrix = new String[Y_DIM][X_DIM];
        for (int i = 0; i < Y_DIM; i++) {
            for (int j = 0; j < X_DIM; j++) {
                row_value = i * X_DIM + j;
                present_matrix[i][j] = ".";
                if (mines_loc.contains(row_value)) {
                    System.out.println("row_value: "+row_value);
                    System.out.println("Y:" +i+" X:" + j);
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
            int guess_number = X_DIM * j_guess + i_guess;
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
            System.out.println(mines_loc);
        } else {
            System.out.println(guess_type);
            System.out.println("Guess type should be either 'free' or 'mine'");
        }
        if (isVictory()) {
            active = false;
        }
    }

    public void clearing(int j_guess, int i_guess) {
        if  (present_matrix[j_guess][i_guess].equals("*")) {
            int guess_number = Y_DIM * j_guess + i_guess;
            System.out.println("Clearing flag");
            update_matrix(i_guess,j_guess,"mine");
        }
        else if (info_matrix[j_guess][i_guess] != ".") {
            present_matrix[j_guess][i_guess] = info_matrix[j_guess][i_guess];
            return;
        }
        else if (present_matrix[j_guess][i_guess] == "/"){
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

    public int getNumber_of_mines() {
        return number_of_mines;
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
