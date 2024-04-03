package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.ResourceBundle;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    public GridLayout mineField; //用于布局按钮
    ImageButton resultButton; //显示表情
    TextView totalMinesTextView, remainingMinesTextView; //指示剩余和总共的雷的数目
    TextView textEndView; //指示剩余和总共的雷的数目
    final int ROWS = 9, COLUMNS = 9;//设置雷区大小和标记
    final int  S_MINE = 9; //标记每按钥的状态
    int totalMines, remainingMines, openedTiles;//总的雷的数目，剩余的数目，已经扫过的数目
    Button[] mineButtons = new Button[ROWS * COLUMNS];
    int[] mMines = new int[ROWS * COLUMNS];
    boolean EndPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initMine();
    }

    private void initViews() {//初始化
        Typeface face = ResourcesCompat.getFont(this, R.font.digital);
        mineField = findViewById(R.id.mineZone);
        totalMinesTextView = findViewById(R.id.textTotal);
        totalMinesTextView.setTypeface(face);
        remainingMinesTextView = findViewById(R.id.textLeft);
        remainingMinesTextView.setTypeface(face);
        textEndView = findViewById(R.id.textEnd);
        resultButton = findViewById(R.id.buttonFace);
        resultButton.setOnClickListener(v -> initMine());
        mineField.setColumnCount(COLUMNS);
        mineField.setRowCount(ROWS);
        mineField.setBackgroundColor(Color.rgb(211, 211, 211));
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(120, 120);
        for (int i = 0; i < ROWS * COLUMNS; i++) {
            mineButtons[i] = new Button(this);
            mineButtons[i].setLayoutParams(lp);
            mineButtons[i].setTag(i);
            mineButtons[i].setTextSize(24);
            mineButtons[i].setOnClickListener(this);
            mineButtons[i].setOnLongClickListener(this);
            mineField.addView(mineButtons[i]);
        }
    }

    //  0 ~  9 Touch
    // 10 ~ 19 noTouch
    // 20 ~ 29 flag
    private void initMine() {
        EndPlay = false;
        totalMines = remainingMines = 5; openedTiles = 0;
        textEndView.setText("");
        totalMinesTextView.setText(Integer.toString(totalMines));
        remainingMinesTextView.setText(Integer.toString(remainingMines));

        int count = totalMines;
        while (count > 0) {
            int x = new Random().nextInt(ROWS * COLUMNS);
            if (mMines[x] != S_MINE) {
                mMines[x] = S_MINE;
                count--;
            }
        }
        for (int i = 0; i < mMines.length; i++) { // sum mine
            if (mMines[i] == S_MINE) continue;
            mMines[i] = countAdjacentMines(i / COLUMNS, i % ROWS);
        }
        resultButton.setBackgroundResource(R.drawable.smile);
        for (int i = 0; i < ROWS * COLUMNS; i++) {
            mMines[i] += 10;
            mineButtons[i].setText("");
            mineButtons[i].setBackgroundResource(R.drawable.init);
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {
                count += isMineAt(row + r, col + c) ? 1 : 0;
            }
        }
        return count;
    }

    private boolean isMineAt(int row, int col) { // 判断第row行第col列是否为雷
        if (row < 0 || row >= ROWS || col < 0 || col >= COLUMNS)
            return false;
        return mMines[row * ROWS + col] == S_MINE;
    }

    @Override
    public void onClick(View view) {
        if (EndPlay) return;
        int i = (int) view.getTag();
        if (10 <= mMines[i] && mMines[i] <= 19) { // notouch
            if (mMines[i] == S_MINE + 10) {
                Toast.makeText(this, "FAIL!", Toast.LENGTH_SHORT).show();
                textEndView.setText("replay");
                EndPlay = true;
                resultButton.setBackgroundResource(R.drawable.surprise);
                printError(i);
                return;
            }
            printNum((i % ROWS), i / ROWS);
        }
    }

    private void printNum(int x, int y) {
        if (x < 0 || x >= ROWS || y < 0 || y >= COLUMNS)// 越界
            return;
        int t = x + y * ROWS;
        if (!(10 <= mMines[t] && mMines[t] <= 19)) {
            return;
        }
        mMines[t] -= 10;
        mineButtons[t].setBackgroundResource(0);
        openedTiles++;
        mineButtons[t].setText(Integer.toString(mMines[t]));
        if (openedTiles + totalMines == ROWS * COLUMNS ) {
            Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show();
            textEndView.setText("you win");
            EndPlay = true;
            return;
        }
        if (mMines[t] == 0) {
            printNum(x - 1, y);
            printNum(x + 1, y);
            printNum(x, y - 1);
            printNum(x, y + 1);
        }
    }

    private void printError(int t) {
        for (int i = 0; i < mineButtons.length; i++) {
            if (mMines[i] == S_MINE || mMines[i] == S_MINE + 10 || mMines[i] == S_MINE + 20) {
                resultButton.setBackgroundResource(R.drawable.surprise);
                mineButtons[i].setBackgroundResource(R.drawable.bomb);
            }
        }
        mineButtons[t].setBackgroundResource(R.drawable.bomb0);
    }

    @Override
    public boolean onLongClick(View view) {
        if (EndPlay) return false;
        int i = (int) view.getTag();
        if (10 <= mMines[i] && mMines[i] <= 19) { // notouch
            mMines[i] += 10;
            view.setBackgroundResource(R.drawable.flag);
            remainingMines--;
        } else if (20 <= mMines[i] && mMines[i] <= 29) {
            mMines[i] -= 10;
            view.setBackgroundResource(R.drawable.init);
            remainingMines++;
        }
        remainingMinesTextView.setText(Integer.toString(remainingMines));
        return false;
    }
}