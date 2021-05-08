package ru.home.telegram_bot.botapi.handlers;

import ru.home.telegram_bot.botapi.Field;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.home.telegram_bot.botapi.TicTac;

/**
 * Данные анкеты пользователя
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileData {
    Field field;
    private String next_move = "";
    TicTac ticTac;
    boolean active_tic_tac = false;

    public void setActive_tic_tac(boolean active_tic_tac) {
        this.active_tic_tac = active_tic_tac;
    }

    public boolean isActive_tic_tac() {
        return active_tic_tac;
    }

    public String getNext_move(){
        return next_move;
    }
    public void setNext_move(String move){
        next_move = move;
    }
    int turn_count;
    public Field getField() {
        return field;
    }
    public void setField(Field field) {
        this.field = field;
    }
    public TicTac getTicTac(){
        return ticTac;
    }
    public void setTicTac(TicTac ticTac){this.ticTac = ticTac;}


}