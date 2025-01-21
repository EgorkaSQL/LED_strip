package com.example.projectiapi;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class ModeController {
    private final Context context;
    private final OutputStream outputStream;

    private boolean stripBlinkState = false;
    private boolean fadeEffectState = false;
    private boolean phonkEffectState = false;
    private boolean sosEffectState = false;

    public ModeController(Context context, OutputStream outputStream) {
        this.context = context;
        this.outputStream = outputStream;
    }

    public void toggleStripBlink() {
        if (outputStream != null) {
            try {
                if (stripBlinkState) {
                    outputStream.write('4');
                    showToast("Мигание выключено");
                } else {
                    outputStream.write('3');
                    showToast("Мигание включено");
                }
                stripBlinkState = !stripBlinkState;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bluetooth не подключен");
        }
    }

    public void toggleFadeEffect() {
        if (outputStream != null) {
            try {
                if (fadeEffectState) {
                    outputStream.write('6');
                    showToast("Эффект затухания выключен");
                } else {
                    outputStream.write('5');
                    showToast("Эффект затухания включён");
                }
                fadeEffectState = !fadeEffectState;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bluetooth не подключен");
        }
    }

    public void togglePhonkEffect() {
        if (outputStream != null) {
            try {
                if (phonkEffectState) {
                    outputStream.write('8');
                    showToast("Phonk-эффект выключен");
                } else {
                    outputStream.write('7');
                    showToast("Phonk-эффект включён");
                }
                phonkEffectState = !phonkEffectState;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bluetooth не подключен");
        }
    }

    public void toggleSoS() {
        if (outputStream != null) {
            try {
                if (sosEffectState) {
                    outputStream.write("10".getBytes("UTF-8"));
                    showToast("SOS выключен");
                } else {
                    outputStream.write('9');
                    showToast("SOS включён");
                }
                sosEffectState = !sosEffectState;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bluetooth не подключен");
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}