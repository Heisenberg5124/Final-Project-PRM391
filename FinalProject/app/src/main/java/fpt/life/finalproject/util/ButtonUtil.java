package fpt.life.finalproject.util;

import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;

import fpt.life.finalproject.R;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ButtonUtil {

    private Button button;
    private boolean filled;

    public static boolean isEditTextFilled(EditText ...editTexts) {
        for (EditText editText : editTexts) {
            if (editText.getText().toString().equals(""))
                return false;
        }
        return true;
    }

    public static boolean isChipGroupSelected(int checkedId) {
        return checkedId != -1;
    }

    public void setButtonWhenFilled() {
        int background = filled
                ? R.drawable.background_button_clicked
                : R.drawable.background_button_un_clicked;
        button.setBackgroundResource(background);

        String color = filled ? "#000000" : "#A3A3A3";
        button.setTextColor(Color.parseColor(color));

        button.setEnabled(filled);
    }
}
