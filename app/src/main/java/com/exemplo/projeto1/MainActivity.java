
package com.exemplo.projeto1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class MainActivity extends Activity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView anosMeses, dias, horas, minutos, segundos, millis;

    private static final LocalDateTime INICIO_ESCURO =
            LocalDateTime.of(2025, 5, 22, 0, 0, 0);
    private static final LocalDateTime INICIO_CLARO =
            LocalDateTime.of(2025, 8, 12, 0, 0, 0);

    private LocalDateTime inicio = INICIO_ESCURO;
    private boolean modoClaro = false;

    private final Runnable atualizar = new Runnable() {
        @Override
        public void run() {
            atualizarContador();
            handler.postDelayed(this, 10);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        montarTela();
    }

    private void montarTela() {
        int corFundo = modoClaro ? Color.WHITE : Color.BLACK;
        String corPrincipal = modoClaro ? "#000000" : "#FFFFFF";
        String corSeparador = modoClaro ? "#999999" : "#666666";
        String corLegenda = modoClaro ? "#6E6E73" : "#8E8E93";
        String corMilli = modoClaro ? "#6E6E73" : "#8E8E93";
        String corMilliLegenda = modoClaro ? "#8E8E93" : "#666666";

        getWindow().setStatusBarColor(corFundo);
        getWindow().setNavigationBarColor(corFundo);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | (modoClaro ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0)
        );

        LinearLayout tela = new LinearLayout(this);
        tela.setOrientation(LinearLayout.VERTICAL);
        tela.setGravity(Gravity.CENTER);
        tela.setPadding(16, 20, 16, 20);
        tela.setBackgroundColor(corFundo);
        tela.setClickable(true);
        tela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alternarModo();
            }
        });

        anosMeses = criarTexto(26, corPrincipal);
        adicionar(tela, anosMeses, 0, 0, 10);

        dias = criarTexto(23, corPrincipal);
        adicionar(tela, dias, 0, 0, 32);

        LinearLayout linha = new LinearLayout(this);
        linha.setOrientation(LinearLayout.HORIZONTAL);
        linha.setGravity(Gravity.CENTER);

        horas = criarTexto(29, corPrincipal);
        minutos = criarTexto(29, corPrincipal);
        segundos = criarTexto(29, corPrincipal);

        adicionar(linha, horas, 1, 0, 0);
        adicionar(linha, criarTextoCor(":", 27, corSeparador), 0, 0, 0);
        adicionar(linha, minutos, 1, 0, 0);
        adicionar(linha, criarTextoCor(":", 27, corSeparador), 0, 0, 0);
        adicionar(linha, segundos, 1, 0, 0);

        tela.addView(linha, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout legendas = new LinearLayout(this);
        legendas.setOrientation(LinearLayout.HORIZONTAL);
        legendas.setGravity(Gravity.CENTER);

        adicionar(legendas, criarTextoCor("HORAS", 10, corLegenda), 1, 0, 0);
        adicionar(legendas, criarTextoCor("MINUTOS", 10, corLegenda), 1, 0, 0);
        adicionar(legendas, criarTextoCor("SEGUNDOS", 10, corLegenda), 1, 0, 0);

        tela.addView(legendas, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        millis = criarTextoCor(".000", 21, corMilli);
        adicionar(tela, millis, 0, 16, 0);

        adicionar(tela, criarTextoCor("MILISSEGUNDOS", 10, corMilliLegenda),
                0, 4, 0);

        setContentView(tela);
    }

    private void alternarModo() {
        modoClaro = !modoClaro;

        inicio = modoClaro ? INICIO_CLARO : INICIO_ESCURO;

        montarTela();
        atualizarContador();
    }

    private TextView criarTexto(float tamanho, String cor) {
        return criarTextoCor("", tamanho, cor);
    }

    private TextView criarTextoCor(String valor, float tamanho, String cor) {
        TextView t = new TextView(this);
        t.setText(valor);
        t.setTextColor(Color.parseColor(cor));
        t.setTextSize(tamanho);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        t.setIncludeFontPadding(false);
        return t;
    }

    private void adicionar(LinearLayout pai, View v,
                           int peso, int topo, int baixo) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                peso == 1 ? 0 : LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                peso
        );
        p.topMargin = topo;
        p.bottomMargin = baixo;
        pai.addView(v, p);
    }

    private void atualizarContador() {
        LocalDateTime agora = LocalDateTime.now();

        if (agora.isBefore(inicio)) {
            anosMeses.setText("00 anos, 00 meses");
            dias.setText("00 dias");
            horas.setText("00");
            minutos.setText("00");
            segundos.setText("00");
            millis.setText(".000");
            return;
        }

        long anos = ChronoUnit.YEARS.between(inicio, agora);
        LocalDateTime marco = inicio.plusYears(anos);

        long meses = ChronoUnit.MONTHS.between(marco, agora);
        marco = marco.plusMonths(meses);

        long d = ChronoUnit.DAYS.between(marco, agora);
        marco = marco.plusDays(d);

        long h = ChronoUnit.HOURS.between(marco, agora);
        marco = marco.plusHours(h);

        long m = ChronoUnit.MINUTES.between(marco, agora);
        marco = marco.plusMinutes(m);

        long s = ChronoUnit.SECONDS.between(marco, agora);
        marco = marco.plusSeconds(s);

        long ms = ChronoUnit.MILLIS.between(marco, agora);

        anosMeses.setText(String.format(Locale.getDefault(),
                "%02d anos, %02d meses", anos, meses));
        dias.setText(String.format(Locale.getDefault(), "%02d dias", d));
        horas.setText(String.format(Locale.getDefault(), "%02d", h));
        minutos.setText(String.format(Locale.getDefault(), "%02d", m));
        segundos.setText(String.format(Locale.getDefault(), "%02d", s));
        millis.setText(String.format(Locale.getDefault(), ".%03d", ms));
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.removeCallbacks(atualizar);
        handler.post(atualizar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(atualizar);
    }
}

