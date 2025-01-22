package com.example.projectiapi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.io.OutputStream;

public class MenuDialog {
    private final Activity activity;
    private OutputStream outputStream;

    public MenuDialog(Activity activity, OutputStream outputStream) {
        this.activity = activity;
        this.outputStream = outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void showMenuDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_menu, null);

        Button btnCloseMenu = dialogView.findViewById(R.id.topButton);
        if (btnCloseMenu != null) {
            btnCloseMenu.setOnClickListener(v -> {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
            });
        }

        SeekBar brightnessSlider = dialogView.findViewById(R.id.brightnessSlider);
        SharedPreferences preferences = activity.getSharedPreferences("MyAppPreferences", Activity.MODE_PRIVATE);
        int savedBrightness = preferences.getInt("saved_brightness", 128);

        if (brightnessSlider != null) {
            brightnessSlider.setMax(255);
            brightnessSlider.setProgress(savedBrightness);

            brightnessSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (outputStream != null) {
                        try {
                            String brightnessCommand = "B" + progress;
                            outputStream.write(brightnessCommand.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Ошибка отправки данных", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, "Bluetooth не подключен", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("saved_brightness", seekBar.getProgress());
                    editor.apply();
                }
            });
        }

        Button buttonRed = dialogView.findViewById(R.id.buttonRed);
        Button buttonBlue = dialogView.findViewById(R.id.buttonBlue);
        Button buttonGreen = dialogView.findViewById(R.id.buttonGreen);

        setupColorButton(buttonRed, "Z", "X");
        setupColorButton(buttonBlue, "M", "E");
        setupColorButton(buttonGreen, "Q", "N");

        BottomSheetBehavior<?> behavior = bottomSheetDialog.getBehavior();
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {}
        });

        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();
    }

    private void setupColorButton(Button button, String onCommand, String offCommand) {
        button.setOnClickListener(v -> {
            boolean isActivated = button.isSelected();
            button.setSelected(!isActivated);

            if (outputStream != null) {
                try {
                    String command = isActivated ? offCommand : onCommand;
                    outputStream.write(command.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity, "Bluetooth не подключен", Toast.LENGTH_SHORT).show();
            }
        });
    }
}