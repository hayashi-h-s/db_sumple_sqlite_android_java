package com.haya.nc_db_sumple_sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CocktailMemoActivity extends AppCompatActivity {

//  カクテルの主キー

  int _cocktailld = -1;

// 選択されたカクテル名を表すフィールド

  String _cocktailName = "";

// カクテル名を表示するTextViewフィールド

  TextView _tvCocktailName;
  TextView _tvLibResult;

  Button _btnSave;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cocktail_memo);

    // 画面要素を取得
    _tvCocktailName = findViewById(R.id.tvCocktailName);
    _btnSave = findViewById(R.id.btnSave);
    ListView lvCocktail = findViewById(R.id.lvCocktail);

    lvCocktail.setOnItemClickListener(new ListItemClickListener());

  }

  public void onSaveButtonClick(View view) {
    // 感想を取得
    EditText etNote = findViewById(R.id.etNote);
    String note = etNote.getText().toString();


    // DB作成の大まかな手順
    /*
      ・DBヘルパーオブジェクトの作成
      ・↑から、データベース接続オブジェクトをもらう
      ・SQl文字列を作成
      ・ステートメントオブジェクトをもらう
      ・変数をバインドする
      ・SQLを実行
    */

    // ここからデータベースの処理
    // データベースヘルパーオブジェクトの作成
    DatabaseHelper helper = new DatabaseHelper(CocktailMemoActivity.this);
    // データベースヘルパーオブジェクトの作成

    // getReadableDatabase は読み取り専用
    SQLiteDatabase db = helper.getWritableDatabase();


    try {

      // リストで選択されたメモデータを削除。その後インサートを行う。
      // 削除用のSQL文字列を用意
      String sqlDelete = "DELETE FROM cocktailmemo WHERE _id = ?";
      SQLiteStatement stmt = db.compileStatement(sqlDelete);

      stmt.bindLong(1, _cocktailld);
      // 削除SQLの実行
      stmt.executeUpdateDelete();

      // インサート用SQL文字列の用意

      // VALUES (?, ?, ?)" の部分は 変数によって値が変わる場所
      // INSERTはデータ入力
      String sqlInsert = "INSERT INTO cocktailmemo (_id, name, note) VALUES (?, ?, ?)";
      // SQL文字列を元にプリペアドステートメントを取得
      // ステートメント = SQLオブジェクト
      stmt = db.compileStatement(sqlInsert);
      // 変数のバインド
      // bind~~ を利用して、VALUES (?, ?, ?)に変数を埋め込む
      stmt.bindLong(1, _cocktailld);
      stmt.bindString(2, _cocktailName);
      stmt.bindString(3, note);

      // インサートSQLの実行 → DELETEは削除
      stmt.executeInsert();

    } finally {
      // データベース接続オブジェクトの解放 → データ処理が終わったら必ず行う。
      // try-with-resources なら 解放処理をしなくていい
      db.close();
    }
    _tvCocktailName.setText(getString(R.string.tv_name));
    etNote.setText("");
    _btnSave.setEnabled(false);
  }

  // AdapterView<?>
  // 呼び出し元からAdapterView<なんらかの型>を呼び出す働きをしている

  private class ListItemClickListener implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

      // タップされた行番号をフィールドの主キーIDに保存
      _cocktailld = position;

      // タップされた行のデータを取得。これがカクテル名となるので、フィールドに代入。
      _cocktailName = (String) parent.getItemAtPosition(position);

      _tvCocktailName.setText(_cocktailName);

      _btnSave.setEnabled(true);

      // データベースヘルパーオブジェクトの作成
      DatabaseHelper helper = new DatabaseHelper(CocktailMemoActivity.this);
      // データベースヘルパーオブジェクトの作成
      SQLiteDatabase db = helper.getWritableDatabase();

      try {
        // 主キーによる検索SQL文字列の用意
        // SELECTはデータ取得
//        String sql = "SELECT * FROM cocktailmemo WHERE _id = " + _cocktailld;

        // SELECT文を実行するには、rawQueryを使用する
        // 第二引数はバインド変数 "SELECT * FROM cocktailmemo WHERE _id = ?"; で ? に変数を入れる
        // Cursor はSELECT文の実行結果が丸々格納されているオブジェクト
//        Cursor cursor = db.rawQuery(sql, null);

        // SQLを使わなくても下記でも実行できる→ SQLiteの
        String[] sql = {String.valueOf(_cocktailld)};
        Cursor cursor = db.query("cocktailmemo",null,"_id = ?",sql,null,null,null);

        // 初期化
        String note = "";

        // moveToNext で レコードが１つ進む。falseになるまで実行する。
        while (cursor.moveToNext()) {

          // positionで取得した_cocktailldの"note"カラムの値を取得
          int idxNote = cursor.getColumnIndex("note");

          // データを取得する
          note = cursor.getString(idxNote);
        }
        // 感想のEditTextの各画面部分を取得しデータベースの値を反映
        EditText etNote = findViewById(R.id.etNote);
        etNote.setText(note);
      } finally {
        // データベース接続オブジェクトの解放
        db.close();
      }
    }
  }
}