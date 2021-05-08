package ru.home.telegram_bot.botapi;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.home.telegram_bot.botapi.handlers.Introduction_Case;
import ru.home.telegram_bot.botapi.BotState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.vdurmont.emoji.EmojiParser;

public class Button_handler {

    public static InlineKeyboardMarkup introductionButtons(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonTicTac = new InlineKeyboardButton().setText("Tic_Tac"); //emoji
        InlineKeyboardButton buttonMines = new InlineKeyboardButton().setText("Minesweeper"+EmojiParser.parseToUnicode(":bomb:")); //emoji

        buttonTicTac.setCallbackData("Game:tic_tac");
        buttonMines.setCallbackData("Game:mines");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonTicTac);
        keyboardButtonsRow1.add(buttonMines);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
    public static InlineKeyboardMarkup ticTacType(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonCross = new InlineKeyboardButton().setText(EmojiParser.parseToUnicode(":x:")); //emoji
        InlineKeyboardButton buttonCircle = new InlineKeyboardButton().setText(EmojiParser.parseToUnicode("&#11093;")); //emoji

        buttonCross.setCallbackData("Tic_tac_type:X");
        buttonCircle.setCallbackData("Tic_tac_type:O");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonCross);
        keyboardButtonsRow1.add(buttonCircle);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;

    }
    public static InlineKeyboardMarkup firstcrossMove() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton().setText(EmojiParser.parseToUnicode(":white_large_square:"));
                button.setCallbackData("Tic_tac_start:"+j+","+i);
                keyboardButtonsRow.add(button);
            }
            rowList.add(keyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup minesSizeButtons(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonSMALL = new InlineKeyboardButton().setText("SMALL");
        InlineKeyboardButton buttonMEDIUM = new InlineKeyboardButton().setText("MEDIUM");
        InlineKeyboardButton buttonLARGE = new InlineKeyboardButton().setText("LARGE");


        buttonSMALL.setCallbackData("Size:SMALL");
        buttonMEDIUM.setCallbackData("Size:MEDIUM");
        buttonLARGE.setCallbackData("Size:LARGE");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonSMALL);
        keyboardButtonsRow1.add(buttonMEDIUM);
        keyboardButtonsRow1.add(buttonLARGE);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
    public static InlineKeyboardMarkup complexityButtons(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonEASY = new InlineKeyboardButton().setText("EASY");
        InlineKeyboardButton buttonMEDIUM = new InlineKeyboardButton().setText("MEDIUM");
        InlineKeyboardButton buttonHARD = new InlineKeyboardButton().setText("HARD");


        buttonEASY.setCallbackData("Level:EASY");
        buttonMEDIUM.setCallbackData("Level:MEDIUM");
        buttonHARD.setCallbackData("Level:HARD");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonEASY);
        keyboardButtonsRow1.add(buttonMEDIUM);
        keyboardButtonsRow1.add(buttonHARD);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
    public static InlineKeyboardMarkup typeButtons(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonMINE = new InlineKeyboardButton().setText("MINE");
        InlineKeyboardButton buttonFREE= new InlineKeyboardButton().setText("FREE");

        buttonFREE.setCallbackData("Type:free");
        buttonMINE.setCallbackData("Type:mine");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonFREE);
        keyboardButtonsRow1.add(buttonMINE);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;

    }
    public static InlineKeyboardMarkup moveButtons(Field field){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        String[][] matrix = field.getPresent_matrix();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for(int j =0;j<field.Y_DIM;j++){
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for(int i=0;i<field.X_DIM;i++){
                InlineKeyboardButton button = new InlineKeyboardButton();
                if(matrix[j][i] == "."){button.setText(EmojiParser.parseToUnicode(":white_large_square:") );}
                else if(matrix[j][i] == "/"){button.setText(EmojiParser.parseToUnicode("::"));}
                else if(matrix[j][i] == "*"){button.setText(EmojiParser.parseToUnicode(":triangular_flag_on_post:"));}
                else if(matrix[j][i] == "X"){button.setText(EmojiParser.parseToUnicode(":bomb:"));}
                else if(matrix[j][i].equals("1")){button.setText(EmojiParser.parseToUnicode(":one:"));}
                else if(matrix[j][i].equals("2")){button.setText(EmojiParser.parseToUnicode(":two:"));}
                else if(matrix[j][i].equals("3")){button.setText(EmojiParser.parseToUnicode(":three:"));}
                else if(matrix[j][i].equals("4")){button.setText(EmojiParser.parseToUnicode(":four:"));}
                else if(matrix[j][i].equals("5")){button.setText(EmojiParser.parseToUnicode(":five:"));}
                //else button.setText(EmojiParser.parseToUnicode(":eye_in_speech_bubble:"));
                button.setCallbackData("Move:"+(i+1)+","+(j+1));
                keyboardButtonsRow.add(button);
                }
            rowList.add(keyboardButtonsRow);
        }

        InlineKeyboardButton buttonMINE = new InlineKeyboardButton().setText("MINE");
        InlineKeyboardButton buttonFREE= new InlineKeyboardButton().setText("FREE");

        buttonFREE.setCallbackData("Type:free");
        buttonMINE.setCallbackData("Type:mine");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonFREE);
        keyboardButtonsRow1.add(buttonMINE);

        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
    public static InlineKeyboardMarkup LostButtons(Field field, String info){ // FOR FINAL MESSAGING
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        String[][] matrix = field.getInfo_matrix();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for(int j =0;j<field.Y_DIM;j++){
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for(int i=0;i<field.X_DIM;i++){
                InlineKeyboardButton button = new InlineKeyboardButton();
                if(matrix[j][i] == "."){button.setText(EmojiParser.parseToUnicode(":white_large_square:") );}
                else if(matrix[j][i] == "/"){button.setText(EmojiParser.parseToUnicode("::"));}
                else if(matrix[j][i] == "*"){button.setText(EmojiParser.parseToUnicode(":triangular_flag_on_post:"));}
                else if(matrix[j][i] == "X"){button.setText(EmojiParser.parseToUnicode(":bomb:"));}
                else if(matrix[j][i].equals("1")){button.setText(EmojiParser.parseToUnicode(":one:"));}
                else if(matrix[j][i].equals("2")){button.setText(EmojiParser.parseToUnicode(":two:"));}
                else if(matrix[j][i].equals("3")){button.setText(EmojiParser.parseToUnicode(":three:"));}
                else if(matrix[j][i].equals("4")){button.setText(EmojiParser.parseToUnicode(":four:"));}
                else if(matrix[j][i].equals("5")){button.setText(EmojiParser.parseToUnicode(":five:"));}
                //else button.setText(EmojiParser.parseToUnicode(":eye_in_speech_bubble:"));

                button.setCallbackData("/new_mines_bot");

                keyboardButtonsRow.add(button);
            }
            rowList.add(keyboardButtonsRow);
        }

        InlineKeyboardButton buttonSMALL = new InlineKeyboardButton().setText("SMALL");
        InlineKeyboardButton buttonMEDIUM = new InlineKeyboardButton().setText("MEDIUM");
        InlineKeyboardButton buttonLARGE = new InlineKeyboardButton().setText("LARGE");


        buttonSMALL.setCallbackData("Size:SMALL");
        buttonMEDIUM.setCallbackData("Size:MEDIUM");
        buttonLARGE.setCallbackData("Size:LARGE");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonSMALL);
        keyboardButtonsRow1.add(buttonMEDIUM);
        keyboardButtonsRow1.add(buttonLARGE);

        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
    public static InlineKeyboardMarkup ticTacMove(TicTac ticTac , String type) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        String[][] field = ticTac.getField();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                if(field[j][i].equals("X")){
                    button.setText(EmojiParser.parseToUnicode(":x:"));
                }
                else if(field[j][i].equals("O") |field[j][i].equals("0") ) {
                    button.setText(EmojiParser.parseToUnicode("&#11093;"));
                }
                else {
                    button.setText(EmojiParser.parseToUnicode(":white_large_square:"));
                }
                button.setCallbackData("Tic_tac_move:"+j+","+i+","+type);
                keyboardButtonsRow.add(button);
            }
            rowList.add(keyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
    public static InlineKeyboardMarkup lost_field(TicTac ticTac) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        String[][] field = ticTac.getField();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                if(field[j][i].equals("X")){
                    button.setText(EmojiParser.parseToUnicode(":x:"));
                }
                else if(field[j][i].equals("O") |field[j][i].equals("0") ) {
                    button.setText(EmojiParser.parseToUnicode("&#11093;"));
                }
                else {
                    button.setText(EmojiParser.parseToUnicode(":white_large_square:"));
                }
                button.setCallbackData("/start");
                keyboardButtonsRow.add(button);
            }
            rowList.add(keyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

}
