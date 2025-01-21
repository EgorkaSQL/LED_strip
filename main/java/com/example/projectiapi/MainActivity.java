package com.example.projectiapi;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.animation.ObjectAnimator;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private BluetoothDevice mDevice;
    private ImageButton btnToggleLED;
    private Button btnConnect;
    private Button btnAudioListener;
    private boolean ledState = false;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Button activeButton = null;
    private ModeController modeController;

    private final ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allPermissionsGranted = true;
                for (Boolean granted : permissions.values())
                {
                    if (!granted)
                    {
                        allPermissionsGranted = false;
                        break;
                    }
                }

                if (allPermissionsGranted)
                {
                    checkLocationPermission();
                }
                else
                {
                    Toast.makeText(this, "Не все разрешения были предоставлены.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> locationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result)
        {
            enableBluetooth();
        }
        else
        {
            Toast.makeText(this, "Требуется разрешение на фоновое местоположение.", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_color));
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, "Bluetooth не поддерживается на этом устройстве", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            permissionsLauncher.launch(new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_FINE_LOCATION
            });
        }
        else
        {
            checkLocationPermission();
        }

        btnToggleLED = findViewById(R.id.btnToggleLED);
        btnToggleLED.setOnClickListener(v -> toggleLED());

        btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(v -> connectToBluetoothDevice());

        btnAudioListener = findViewById(R.id.btnAudioListener);
        btnAudioListener.setOnClickListener(v -> showBottomSheet());

        Button btnMenu = findViewById(R.id.btnDummy1);
        btnMenu.setOnClickListener(v -> showMenuDialog());

        animateButtonsIn();
    }


    private void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);

        Button btnCloseMenu = bottomSheetView.findViewById(R.id.topButtonSheet);
        if (btnCloseMenu != null) {
            btnCloseMenu.setOnClickListener(v -> {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
            });
        }

        int[] buttonIds = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5};
        for (int buttonId : buttonIds) {
            Button button = bottomSheetView.findViewById(buttonId);
            if (button != null) {
                button.setOnClickListener(v -> handleButtonPress(buttonId, button, bottomSheetDialog));
            }
        }

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();
    }

    private void handleButtonPress(int buttonId, Button button, BottomSheetDialog dialog) {
        if (activeButton != null && activeButton != button) {
            Toast.makeText(dialog.getContext(), "Сначала отключите активную кнопку!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isActivated = button.isSelected();
        button.setSelected(!isActivated);

        if (modeController != null) {
            if (buttonId == R.id.button1) {
                modeController.toggleStripBlink();
            } else if (buttonId == R.id.button2) {
                modeController.toggleFadeEffect();
            } else if (buttonId == R.id.button3) {
                modeController.togglePhonkEffect();
            } else if (buttonId == R.id.button4) {
                modeController.toggleSoS();
            } else if (buttonId == R.id.button5) {
                modeController.toggleColorCycle();
            }
        } else {
            Toast.makeText(this, "Bluetooth не подключен", Toast.LENGTH_SHORT).show();
        }

        activeButton = isActivated ? null : button;
    }

    private void animateButtonsIn()
    {
        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
        int buttonCount = buttonLayout.getChildCount();

        for (int i = 0; i < buttonCount; i++)
        {
            View button = buttonLayout.getChildAt(i);
            button.setTranslationY(500);
            button.setAlpha(0f);

            ObjectAnimator animator = ObjectAnimator.ofFloat(button, "translationY", 500, 0);
            animator.setDuration(800);
            animator.setStartDelay(i * 200);

            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f);
            fadeIn.setDuration(800);
            fadeIn.setStartDelay(i * 200);

            animator.start();
            fadeIn.start();
        }
    }

    private void checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        else
        {
            enableBluetooth();
        }
    }

    private void enableBluetooth()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
        {
            if (!mBluetoothAdapter.isEnabled())
            {
                mBluetoothAdapter.enable();
                Toast.makeText(this, "Включаем Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Нет разрешения на использование Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectToBluetoothDevice() {
        String deviceAddress = "C0:5D:89:DC:BC:06";
        mDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);

        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mSocket.connect();
                mOutputStream = mSocket.getOutputStream();

                modeController = new ModeController(this, mOutputStream);
                Toast.makeText(this, "Подключено к устройству", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Нет разрешения на подключение к устройствам Bluetooth", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Не удалось подключиться к устройству", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void toggleLED() {
        ImageButton btnToggleLED = findViewById(R.id.btnToggleLED);

        if (mOutputStream != null) {
            if (ledState) {
                fadeInLightEffect();
                btnToggleLED.setImageResource(R.drawable.ic_led_off);
            } else {
                fadeOutLightEffect();
                btnToggleLED.setImageResource(R.drawable.ic_led_on);
            }
            ledState = !ledState;
        } else {
            Toast.makeText(this, "Bluetooth не подключен", Toast.LENGTH_SHORT).show();
        }
    }

    private void fadeInLightEffect()
    {
        View lightEffect = findViewById(R.id.lightEffect);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(lightEffect, "alpha", 0f, 1f);
        fadeIn.setDuration(300);
        fadeIn.start();
    }

    private void fadeOutLightEffect()
    {
        View lightEffect = findViewById(R.id.lightEffect);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(lightEffect, "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        fadeOut.start();
    }

    private void showMenuDialog()
    {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_menu);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        Button btnCloseMenu = bottomSheetDialog.findViewById(R.id.topButton);

        if (btnCloseMenu != null)
        {
            btnCloseMenu.setOnClickListener(v -> {
                if (bottomSheetDialog.isShowing())
                {
                    bottomSheetDialog.dismiss();
                }
            });
        }

        BottomSheetBehavior<?> behavior = bottomSheetDialog.getBehavior();
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                if (newState == BottomSheetBehavior.STATE_DRAGGING)
                {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });

        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        final View colorPicker = bottomSheetDialog.findViewById(R.id.colorPickerCircle);
        final View colorIndicator = bottomSheetDialog.findViewById(R.id.colorIndicator);
        if (colorPicker == null || colorIndicator == null) return;

        colorPicker.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                colorPicker.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int pickerWidth = colorPicker.getWidth();
                int pickerHeight = colorPicker.getHeight();
                int indicatorWidth = colorIndicator.getWidth();
                int indicatorHeight = colorIndicator.getHeight();

                colorIndicator.setX(colorPicker.getX() + pickerWidth / 2f - indicatorWidth / 2f);
                colorIndicator.setY(colorPicker.getY() + pickerHeight / 2f - indicatorHeight / 2f);
            }
        });

        colorPicker.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                float touchX = event.getX();
                float touchY = event.getY();
                int pickerWidth = colorPicker.getWidth();
                int pickerHeight = colorPicker.getHeight();
                int indicatorWidth = colorIndicator.getWidth();
                int indicatorHeight = colorIndicator.getHeight();
                float pickerX = colorPicker.getX();
                float pickerY = colorPicker.getY();
                float centerX = pickerX + pickerWidth / 2f;
                float centerY = pickerY + pickerHeight / 2f;
                float radius = pickerWidth / 2f;
                float dx = touchX - pickerWidth / 2f;
                float dy = touchY - pickerHeight / 2f;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance > radius)
                {
                    float ratio = radius / distance;
                    dx *= ratio;
                    dy *= ratio;
                }

                float newX = centerX + dx - indicatorWidth / 2f;
                float newY = centerY + dy - indicatorHeight / 2f;
                colorIndicator.setX(newX);
                colorIndicator.setY(newY);

                return true;
            }
        });

        bottomSheetDialog.show();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            if (mSocket != null)
            {
                mSocket.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}