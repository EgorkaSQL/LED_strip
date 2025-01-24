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
    private boolean colorCycleState = false;

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
                    outputStream.write('W');
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

    public void toggleColorCycle() {
        if (outputStream != null) {
            try {
                if (colorCycleState) {
                    outputStream.write('D');
                    showToast("Режим смены цветов выключен");
                } else {
                    outputStream.write('C');
                    showToast("Режим смены цветов включён");
                }
                colorCycleState = !colorCycleState;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bluetooth не подключен");
        }
    }

    public void toogleStrobe()
    {
        if (outputStream != null) {
            try {
                if (colorCycleState) {
                    outputStream.write('A');
                    showToast("Режим Strobe выключен");
                } else {
                    outputStream.write('S');
                    showToast("Режим Strobe включён");
                }
                colorCycleState = !colorCycleState;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bluetooth не подключен");
        }
    }

    public void toogleCold()
    {
        if (outputStream != null) {
            try {
                if (colorCycleState) {
                    outputStream.write('F');
                    showToast("Режим Cold Winter выключен");
                } else {
                    outputStream.write('G');
                    showToast("Режим Cold Winter включён");
                }
                colorCycleState = !colorCycleState;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bluetooth не подключен");
        }
    }

    public void toogleRomantic()
    {
        if (outputStream != null) {
            try {
                if (colorCycleState) {
                    outputStream.write('H');
                    showToast("Режим Romantic выключен");
                } else {
                    outputStream.write('J');
                    showToast("Режим Romantic включён");
                }
                colorCycleState = !colorCycleState;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bluetooth не подключен");
        }
    }

    public void toogleRelax()
    {
        if (outputStream != null) {
            try {
                if (colorCycleState) {
                    outputStream.write('K');
                    showToast("Режим Relax выключен");
                } else {
                    outputStream.write('L');
                    showToast("Режим Relax включён");
                }
                colorCycleState = !colorCycleState;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bluetooth не подключен");
        }
    }

    public void toogleChill()
    {
        if (outputStream != null) {
            try {
                if (colorCycleState) {
                    outputStream.write('P');
                    showToast("Режим Phonk 2 выключен");
                } else {
                    outputStream.write('1');
                    showToast("Режим Phonk 2 включён");
                }
                colorCycleState = !colorCycleState;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bluetooth не подключен");
        }
    }

    public void toogleEpileptics()
    {
        if (outputStream != null) {
            try {
                if (colorCycleState) {
                    outputStream.write('I');
                    showToast("Режим Evening выключен");
                } else {
                    outputStream.write('U');
                    showToast("Режим Evening включён");
                }
                colorCycleState = !colorCycleState;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showToast("Bluetooth не подключен");
        }
    }

    public void tooglePolice()
    {
        if (outputStream != null) {
            try {
                if (colorCycleState) {
                    outputStream.write('Y');
                    showToast("Режим Police выключен");
                } else {
                    outputStream.write('T');
                    showToast("Режим Police включён");
                }
                colorCycleState = !colorCycleState;
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
