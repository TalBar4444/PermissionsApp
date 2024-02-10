package com.myapps.permissionsapp;

import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private AppCompatTextView main_LBL_secondTitle, main_LBL_hint;
    private AppCompatEditText main_EDT_password;
    private MaterialButton main_BTN_start;
    private SensorManager sensorManager;
    private AudioManager audioManager;
    private BatteryManager batteryManager;
    private Sensor rotationVectorSensor;
    private boolean pointingEast = false;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        //Read volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        //Read battery
        batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);

        //Read direction degrees
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        main_BTN_start.setOnClickListener(view -> isPasswordValid());

        main_LBL_secondTitle.setOnClickListener(view -> main_LBL_hint.setVisibility(View.VISIBLE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(rotationVectorSensor != null){
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void findViews() {
        main_EDT_password = findViewById(R.id.main_EDT_password);
        main_BTN_start = findViewById(R.id.main_BTN_start);
        main_LBL_secondTitle = findViewById(R.id.main_LBL_secondTitle);
        main_LBL_hint = findViewById(R.id.main_LBL_hint);
    }

    private int getCurrentVolumeLevel(String func){
        if(func.equals("max"))
            return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        else
            return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

    }
    // Read battery percentage
    private String getBatteryPercentage() {

        int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return Integer.valueOf((int) batteryLevel).toString();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            float[] orientationAngles = new float[3];

            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, rotationMatrix);
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            float azimuthRadians = orientationAngles[0];
            float azimuthDegrees = (float) Math.toDegrees(azimuthRadians);
            int roundDegrees = Math.round(azimuthDegrees);

            if (roundDegrees < 0) {
                roundDegrees += 360;
            }

            if ((roundDegrees >= 80 && roundDegrees <= 100)) {
                pointingEast = true;
            } else {
                pointingEast = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void isPasswordValid() {
        String password = main_EDT_password.getText().toString().trim();
        if(TextUtils.isEmpty(password)){
            main_EDT_password.setError("Required*");
        }
        else{
            checkContent(password);
        }
    }

    private void checkContent(String password) {
        if(password.equals(getBatteryPercentage()) && getCurrentVolumeLevel("curr") == getCurrentVolumeLevel("max") && pointingEast)
            openSuccessActivity();
        else{
            Toast.makeText(MainActivity.this, "Wrong. Please try again.", Toast.LENGTH_SHORT).show();
            main_EDT_password.setText("");
        }
    }

    private void openSuccessActivity() {
        Intent intent = new Intent(this, SuccessActivity.class);
        startActivity(intent);
    }
}
