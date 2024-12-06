package com.example.projectiapi;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import android.animation.ObjectAnimator;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    private Button btnToggleLED;
    private Button btnConnect;
    private Button btnDummy1;
    private boolean ledState = false;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Button btnDummy2;
    private boolean stripBlinkState = false;

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
                    Toast.makeText(this, "Для работы с Bluetooth требуется разрешение на фоновое местоположение.", Toast.LENGTH_SHORT).show();
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

        Button btnMenu = findViewById(R.id.btnDummy1);
        btnMenu.setOnClickListener(v -> showMenuDialog());

        btnDummy2 = findViewById(R.id.btnDummy2);
        btnDummy2.setOnClickListener(v -> toggleStripBlink());
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

    private void connectToBluetoothDevice()
    {
        String deviceAddress = "AC:15:18:E9:F9:56";
        mDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);

        try
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
            {
                mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mSocket.connect();
                mOutputStream = mSocket.getOutputStream();
                Toast.makeText(this, "Подключено к устройству", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Нет разрешения на подключение к устройствам Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e)
        {
            Toast.makeText(this, "Не удалось подключиться к устройству", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void toggleStripBlink()
    {
        if (mOutputStream != null)
        {
            try
            {
                if (stripBlinkState)
                {
                    mOutputStream.write('3');
                    btnDummy2.setText("Пустышка 2 (Включить ленту)");
                }
                else
                {
                    mOutputStream.write('2');
                    btnDummy2.setText("Пустышка 2 (Выключить ленту)");
                }
                stripBlinkState = !stripBlinkState;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this, "Bluetooth не подключен", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleLED()
    {
        if (mOutputStream != null)
        {
            try
            {
                if (ledState)
                {
                    mOutputStream.write('0');
                    btnToggleLED.setText("Включить светодиод");
                    fadeInLightEffect();
                }
                else
                {
                    mOutputStream.write('1');
                    btnToggleLED.setText("Выключить светодиод");
                    fadeOutLightEffect();
                }
                ledState = !ledState;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
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

    //тестирование всплывающего меню с изменением цветов и всякой другой штуки
    private void showMenuDialog()
    {
        int[] buttonIds = {
                R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8,
                R.id.btn9, R.id.btn10, R.id.btn11, R.id.btn12
        };
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_menu);

        for (int i = 0; i < buttonIds.length; i++)
        {
            Button button = bottomSheetDialog.findViewById(buttonIds[i]);
            if (button != null)
            {
                int finalI = i;
                button.setOnClickListener(v -> {
                    Toast.makeText(this, "Нажата кнопка: " + (finalI + 1), Toast.LENGTH_SHORT).show();
                });
            }
        }
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
