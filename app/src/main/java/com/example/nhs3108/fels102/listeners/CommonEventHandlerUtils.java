package com.example.nhs3108.fels102.listeners;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by nhs3108 on 1/20/16.
 */
public class CommonEventHandlerUtils {
    public static void clickBack(final Activity activity, ImageButton backButton) {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }
}
