package ru.home.telegram_bot.botapi.handlers;

import ru.home.telegram_bot.botapi.Field;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Данные анкеты пользователя
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileData {
    Field field;
    int turn_count;
    public Field getField() {
        return field;
    }
    public void setField(Field field) {
        this.field = field;
    }


}