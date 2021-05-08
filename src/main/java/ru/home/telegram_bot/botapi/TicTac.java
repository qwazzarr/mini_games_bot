package ru.home.telegram_bot.botapi;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

public class TicTac {

    int player1_id;
    String player1_type;
    int player2_id;
    String player2_type;
    long player1_chat_id;
    long player2_chat_id;
    boolean tic_tac_move1 = true;
    boolean tic_tac_move2 = true;
    private String[][] field = new String[3][3];
    public TicTac(int j_move,int i_move,int id,String type,long player1_chat_id) {
        for (int j =0;j<3;j++){
            for(int i =0; i<3;i++){
                if (j == j_move && i == i_move) {
                    field[i][j] = type;
                }
                else {
                    field[i][j] = ".";
                }
            }
        }
        this.player1_type = type;
        this.player1_id = id;
        this.player1_chat_id = player1_chat_id;
    }

    public void setTic_tac_move2(boolean tic_tac_move2) {
        this.tic_tac_move2 = tic_tac_move2;
    }

    public void setTic_tac_move1(boolean tic_tac_move1) {
        this.tic_tac_move1 = tic_tac_move1;
    }

    public int OpponentID(int player_id) {
        if (player_id == player1_id){
            return player2_id;
        }
        else if (player_id == player2_id){
            return player1_id;
        }
        else {
            System.out.println("произошла попа");
            return 0;
        }
    }

    public int getPlayer1_id() {
        return player1_id;
    }
    public long opponentChatID(long player_chat_id) {
        if(player_chat_id == player1_chat_id){
            return player2_chat_id;
        }
        if(player_chat_id == player2_chat_id){
            return player1_chat_id;
        }
        else {
            System.out.println("произошла еще попа");
            return 0;
        }
    }

    public void setPlayer2_chat_id(long player2_chat_id) {
        this.player2_chat_id = player2_chat_id;
    }

    public int getPlayer2_id() {
        return player2_id;
    }

    public String[][] getField() {
        return this.field;
    }
    public void setField(String[][] field){
        this.field = field;
    }

    public void setOpponent(int id){
        this.player2_id = id;
        if (player1_type.equals("X")) {
            player2_type = "O";
        }
        else {
            player2_type = "X";
        }
    }
    public SendMessage makeMove(int j_i , int i_i , int user_id , long chat_id){
        if (!field[j_i][i_i].equals(".")){
            return new SendMessage(chat_id,"This square is already taken ,try again");
        }
        if(user_id == player2_id){
            if (!tic_tac_move2){
                return new SendMessage(chat_id,"Please wait for your opponent move");
            }
            this.field[j_i][i_i] = player2_type;
            tic_tac_move2 = false;
            tic_tac_move1 = true;

        }
        else {
            if (!tic_tac_move1){
                return new SendMessage(chat_id,"Wait for your opponent move");
            }
            this.field[j_i][i_i] = player1_type;
            tic_tac_move2 = true;
            tic_tac_move1 = false;
        }
        SendMessage reply = new SendMessage(chat_id , "You made your move , waiting for your opponent");
        return reply;
    }
    public String isFinished(){
        int blank = 0;
        for (int j =0;j<3;j++) {
            for (int i = 0; i < 3; i++) {
                if (field[j][i].equals(".")) {
                    blank++;
                }
            }
        }
        if (blank == 0) {
            System.out.println("Status : not");
            return "draw";
        }

        int cross;
        int circle;
        int[][] y_diagonals ={{0,1,2},{0,0,0},{0,1,2},{2,2,2},{2,1,0},{0,1,2},{0,1,2},{1,1,1}};
        int[][] x_diagonals = {{0,0,0},{0,1,2},{0,1,2},{0,1,2},{0,1,2},{2,2,2},{1,1,1},{0,1,2}};

        for (int diag = 0;diag<8;diag++){
            cross = 0;
            circle =0;
            for(int ind =0;ind<3;ind++) {
                if (field[y_diagonals[diag][ind]][x_diagonals[diag][ind]].equals("X")) {
                    cross++;
                }
                if (field[y_diagonals[diag][ind]][x_diagonals[diag][ind]].equals("O") | field[y_diagonals[diag][ind]][x_diagonals[diag][ind]].equals("0")) {
                    circle++;
                }
            }
            if (circle == 3) {
                System.out.println("Status check: "+"circle");
                return "circle";
            }
            else if(cross == 3){
                System.out.println("Status check: "+"cross");
                return "cross";
            }

        }
        System.out.println("Status check: "+"not");
        return "not";
    }
}
