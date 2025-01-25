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
                } else {
                    outputStream.write('3');
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
                } else {
                    outputStream.write('5');
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
                } else {
                    outputStream.write('7');
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
                } else {
                    outputStream.write('9');
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
                } else {
                    outputStream.write('C');
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
                } else {
                    outputStream.write('S');
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
                } else {
                    outputStream.write('G');
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
                } else {
                    outputStream.write('J');
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
                } else {
                    outputStream.write('L');
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
                } else {
                    outputStream.write('1');
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
                } else {
                    outputStream.write('U');
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
                } else {
                    outputStream.write('T');
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