package com.example.rakna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Objects;
import java.util.Random;

import jp.wasabeef.blurry.Blurry;


public class BookingQRActivity extends AppCompatActivity {
    ImageView qrcode_image;
    ImageButton backButton;
    TextView desc;
    DatabaseReference reference;
    FirebaseAuth auth;
    RelativeLayout relativeLayout;
    private final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_qr);
        initComponent();
        getCodeFromFirebase();
        setBackButtonListener();
    }

    private void initComponent() {
        qrcode_image = findViewById(R.id.QrCode);
        desc = findViewById(R.id.description);
        backButton = findViewById(R.id.button_qr_back);
        relativeLayout = findViewById(R.id.qr_relative_layout);
        reference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    private void setBackButtonListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getCodeFromFirebase() {
        reference.child("Users").child(auth.getCurrentUser().getUid()).child("BookCode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (Objects.equals(snapshot.getValue(String.class), "None")) {
                        blurQRCode();
                    } else {
                        String code = snapshot.getValue(String.class);
                        generateCode(code);
                        desc.setText(R.string.Your_booking_code);
                    }
                } else {
                    blurQRCode();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void blurQRCode() {
        generateCode(generateRandomKey());
        Blurry.with(BookingQRActivity.this).radius(20).sampling(2).onto(relativeLayout);
        desc.setText(R.string.youhavenotBook);
        desc.setTextSize(20);
    }

    private String generateRandomKey() {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(16);

        for (int i = 0; i < 16; ++i) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        return sb.toString();
    }

    private void generateCode(String code) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(code, BarcodeFormat.QR_CODE, 550, 550);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(matrix);
            qrcode_image.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }


    }
}