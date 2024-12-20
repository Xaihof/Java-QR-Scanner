package com.xoksis.qrscannner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.xoksis.qrscannner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String scannedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.scanQrButton.setOnClickListener(v -> {

            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan a QR Code");
            options.setCameraId(0);  // Use a specific camera of the device
            options.setBeepEnabled(false);
            options.setBarcodeImageEnabled(true);
            options.setOrientationLocked(false);
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);

            barcodeLauncher.launch(options);

        });

        binding.resultText.setOnClickListener(v -> {

            if (scannedText == null) {
                Toast.makeText(this, "Please Scan A Qr Code First", Toast.LENGTH_SHORT).show();
            } else {

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copied Text", binding.resultText.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(this, "Text Copied to clipboard.", Toast.LENGTH_SHORT).show();
            }

        });

    }

    // Register the launcher and result handler
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Scanned Successfully", Toast.LENGTH_SHORT).show();
                    scannedText = result.getContents();
                    binding.resultText.setText(result.getContents());

                   // Check if the scanned text is a URL
                    if (scannedText.startsWith("http://") || scannedText.startsWith("https://")) {
                        // Open the URL in a browser
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedText));
                        startActivity(intent);
                    } else {
                        // Not a URL, display the content in TextView as before
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("Copied Text", result.getContents());
                                clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(this, "Text Copied to clipboard.", Toast.LENGTH_SHORT).show();
                    }


                }
            });

    // Launch
    public void onButtonClick(View view) {
        barcodeLauncher.launch(new ScanOptions());
    }

}