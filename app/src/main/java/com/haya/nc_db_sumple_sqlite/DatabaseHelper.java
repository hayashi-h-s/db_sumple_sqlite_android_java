package com.haya.nc_db_sumple_sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// SQLiteOpenHelperを継承
public class DatabaseHelper extends SQLiteOpenHelper {

  // データベースファイル名の定数フィールド
  private static final String DATABASE_NAME = "cocktailmemo.db";

  // バージョン情報の定数フィールド
  private static final int DATABASE_VERSION = 1;

  // コンストラクタ
  public DatabaseHelper(Context content) {

    // 親クラスのコンストラクタの呼び出し
    super(content, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // 端末内部にデータベースが存在しない時に、一度だけ実行する
  // 初期設定に必要なSQLはここで実行する
  @Override
  public void onCreate(SQLiteDatabase db){

    // テーブル作成用のSQL文字列の作成
    StringBuilder sb = new StringBuilder();
    sb.append("CREATE TABLE cocktailmemo(");
      // _idだと、OSが自動で主キーと判断する
    sb.append("_id INTEGER PRIMARY KEY,");
    sb.append("name TEXT,");
    sb.append("note TEXT");
    // エラー解消 → : → ; に変更
    sb.append(");");
    String sql = sb.toString();

    // SQLの実行
    db.execSQL(sql);
  }


  // 内部のバージョン番号と、コンストラクタの番号が違う時に実行される
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
