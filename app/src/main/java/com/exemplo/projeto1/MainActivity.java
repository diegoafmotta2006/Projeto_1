package com.exemplo.projeto1;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.WHITE);

        TextView texto = new TextView(this);
        texto.setText("Meu primeiro aplicativo!");
        texto.setTextSize(24);
        texto.setTextColor(Color.BLACK);
        texto.setGravity(Gravity.CENTER);

        Button botao = new Button(this);
        botao.setText("Clique aqui");

        botao.setOnClickListener(new android.view.View.OnClickListener() {
    @Override
    public void onClick(android.view.View v) {
        Toast.makeText(
            MainActivity.this,
            "Funcionou!",
            Toast.LENGTH_SHORT
        ).show();
    }
});

        layout.addView(texto);
        layout.addView(botao);

        setContentView(layout);
    }
}

