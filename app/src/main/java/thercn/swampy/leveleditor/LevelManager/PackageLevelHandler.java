//
// Decompiled by Jadx (from NP Manager)
//
package thercn.swampy.leveleditor.LevelManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class PackageLevelHandler extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "/sdcard/SLE/water_levels.db";
  private static final int DATABASE_VERSION = 6;
  private static final String QUERY_LEVEL_TABLE_CREATE =
      "CREATE TABLE Level (uniqueId INTEGER AUTO_INCREMENT, packageId VARCHAR(30), packageName VARCHAR(30), levelId VARCHAR(30), levelName VARCHAR(30), like_count VARCHAR(30),  user_likes VARCHAR(30) , PRIMARY KEY(levelName, levelId));";
  private static final String QUERY_PACKAGE_LEVEL_TABLE_CREATE =
      "CREATE TABLE LevelPackage (uniqueId INTEGER AUTO_INCREMENT, packageId VARCHAR(30), packageName VARCHAR(30) , PRIMARY KEY(packageName, packageId));";
  private static final String TABLE_LEVEL = "Level";
  private static final String TABLE_LEVEL_PACKAGE = "LevelPackage";
  private static PackageLevelHandler databaseController = null;
  private static final String kLevelId = "levelId";
  private static final String kLevelName = "levelName";
  private static final String kLikeCount = "like_count";
  private static final String kPackageId = "packageId";
  private static final String kPackageName = "packageName";
  private static final String kUniqueId = "uniqueId";
  private static final String kUserLikes = "user_likes";
  private SQLiteDatabase dbo;

  public void saveLevel(LevelModel levelModel) {
    String str = "DB";
    if (existLevelIDWithLevelId(levelModel.getLevelId())) {
      updateLevelWithLevelID(levelModel.getLevelId(), levelModel);
      return;
    }
    SQLiteDatabase writableDatabase = getWritableDatabase();
    writableDatabase.beginTransaction();
    try {
      ContentValues contentValues = new ContentValues();
      contentValues.put(kPackageId, levelModel.getPackageId());
      contentValues.put(kPackageName, levelModel.getPackageName());
      contentValues.put(kLevelId, levelModel.getLevelId());
      contentValues.put(kLevelName, levelModel.getLevelName());
      contentValues.put(kLikeCount, levelModel.getNumberOfLikes());
      contentValues.put(kUserLikes, levelModel.getUserLikes());
      Log.i(str, "SAVE kPackageId: " + levelModel.getPackageId());
      Log.i(str, "SAVE kPackageName: " + levelModel.getPackageName());
      Log.i(str, "SAVE kLevelId: " + levelModel.getLevelId());
      Log.i(str, "SAVE kLevelName: " + levelModel.getLevelName());
      Log.i(str, "SAVE kLikeCount: " + levelModel.getNumberOfLikes());
      Log.i(str, "SAVE kUserLikes: " + levelModel.getUserLikes());
      writableDatabase.insert(TABLE_LEVEL, null, contentValues);
      writableDatabase.setTransactionSuccessful();
    } catch (Exception e) {
      Log.e("Error in transaction", e.toString());
    } catch (Throwable th) {
      writableDatabase.endTransaction();
      writableDatabase.close();
    }
    writableDatabase.endTransaction();
    writableDatabase.close();
  }

  public LevelModel getLevelObjectWithLevelName(String str, String str2) {
    LevelModel levelModel;
    Exception e;
    SQLiteDatabase writableDatabase = getWritableDatabase();
    synchronized (this) {
      try {
        Cursor query =
            writableDatabase.query(TABLE_LEVEL, null,
                                   "levelName LIKE '%" + str + "%' AND " +
                                       kPackageId + " = '" + str2 + "'",
                                   null, null, null, null);
        if (query.getCount() > 0) {
          query.moveToFirst();
          LevelModel levelModel2 = new LevelModel(
              query.getString(3), query.getString(1), query.getString(4),
              query.getString(2), query.getString(5),
              query.getString(DATABASE_VERSION));
          try {
            Log.i("DB", "LOAD kPackageId: " + query.getString(1));
            Log.i("DB", "LOAD kPackageName: " + query.getString(2));
            Log.i("DB", "LOAD kLevelId: " + query.getString(3));
            Log.i("DB", "LOAD kLevelName: " + query.getString(4));
            Log.i("DB", "LOAD kLikeCount: " + query.getString(5));
            Log.i("DB",
                  "LOAD kUserLikes: " + query.getString(DATABASE_VERSION));
            levelModel = levelModel2;
          } catch (Exception e2) {
            e = e2;
            levelModel = levelModel2;
            try {
              e.printStackTrace();
              writableDatabase.close();
              return levelModel;
            } catch (Throwable th) {
              writableDatabase.close();
            }
          }
        } else {
          levelModel = null;
        }
        try {
          query.deactivate();
          query.close();
        } catch (Exception e3) {

          e3.printStackTrace();
          writableDatabase.close();
          return levelModel;
        }
      } catch (Exception e4) {
        e = e4;
        levelModel = null;
        e.printStackTrace();
        writableDatabase.close();
        return levelModel;
      }
      try {
        writableDatabase.close();
      } catch (Throwable th2) {
      }
    }
    return levelModel;
  }

  public void saveLevelPackages(ArrayList<LevelPackageModel> arrayList,
                                boolean z) {
    if (arrayList.size() > 0 && z) {
      clearLevelPackages();
    }
    SQLiteDatabase writableDatabase = getWritableDatabase();
    writableDatabase.beginTransaction();
    try {
      int size = arrayList.size();
      for (int i = 0; i < size; i++) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(kPackageId,
                          ((LevelPackageModel)arrayList.get(i)).getPackageId());
        contentValues.put(
            kPackageName,
            ((LevelPackageModel)arrayList.get(i)).getPackageName());
        Log.i("DB", "saveLevelPackages: " + i + ". " +
                        ((LevelPackageModel)arrayList.get(i)).getPackageName());
        writableDatabase.insert(TABLE_LEVEL_PACKAGE, null, contentValues);
      }
      writableDatabase.setTransactionSuccessful();
    } catch (Exception e) {
      Log.e("Error in transaction", e.toString());
    } catch (Throwable th) {
      writableDatabase.endTransaction();
      writableDatabase.close();
    }
    writableDatabase.endTransaction();
    writableDatabase.close();
  }

  public String getLevelIDWithLevelNameAndPackageId(String str, String str2) {
    String str3;
    SQLiteDatabase writableDatabase = getWritableDatabase();
    synchronized (this) {
      str3 = null;
      try {
        Log.e("", "getLevelID query : levelName LIKE '%" + str + "%' AND " +
                      kPackageId + " = '" + str2 + "'");
        Cursor query =
            writableDatabase.query(TABLE_LEVEL, null,
                                   "levelName LIKE '%" + str + "%' AND " +
                                       kPackageId + " = '" + str2 + "'",
                                   null, null, null, null);
        if (query.getCount() > 0) {
          query.moveToFirst();
          str3 = query.getString(3);
          Log.i("DB", "levelId: " + str3 + " FOUND!");
        }
        query.deactivate();
        query.close();
      } catch (Exception e) {
        e.printStackTrace();
      } catch (Throwable th) {
        writableDatabase.close();
      }
      try {
        writableDatabase.close();
      } catch (Throwable th2) {
      }
    }
    return str3;
  }

  public boolean existLevelIDWithLevelId(String str) {
    String str2;
    SQLiteDatabase writableDatabase = getWritableDatabase();
    synchronized (this) {
      str2 = null;
      try {
        Cursor query =
            writableDatabase.query(TABLE_LEVEL, null, "levelId = '" + str + "'",
                                   null, null, null, null);
        if (query.getCount() > 0) {
          query.moveToFirst();
          str2 = query.getString(3);
          Log.i("DB", "levelId: " + str2 + " FOUND!");
        }
        query.deactivate();
        query.close();
      } catch (Exception e) {
        e.printStackTrace();
      } catch (Throwable th) {
        writableDatabase.close();
      }
      try {
        writableDatabase.close();
      } catch (Throwable th2) {
      }
    }
    return str2 != null;
  }

  public boolean existLevelsWithLevelPackageId(String str) {
    String str2;
    SQLiteDatabase writableDatabase = getWritableDatabase();
    synchronized (this) {
      str2 = null;
      try {
        Cursor query = writableDatabase.query(TABLE_LEVEL, null,
                                              "packageId = '" + str + "'", null,
                                              null, null, null);
        if (query.getCount() > 0) {
          query.moveToFirst();
          str2 = query.getString(3);
          Log.i("DB", "levelId: " + str2 + " FOUND!");
        }
        query.deactivate();
        query.close();
      } catch (Exception e) {
        e.printStackTrace();
      } catch (Throwable th) {
        writableDatabase.close();
      }
      try {
        writableDatabase.close();
      } catch (Throwable th2) {
      }
    }
    return str2 != null;
  }

  public String getLevelPackageIDWithIdOrName(String str) {
    String str2;
    SQLiteDatabase writableDatabase = getWritableDatabase();
    synchronized (this) {
      str2 = null;
      try {
        Cursor query = writableDatabase.query(
            TABLE_LEVEL_PACKAGE, null,
            "packageId = '" + str + "' OR " + kPackageName + " = '" + str + "'",
            null, null, null, null);
        if (query.getCount() > 0) {
          query.moveToFirst();
          str2 = query.getString(1);
          Log.i("DB", "packageId: " + str2 + " FOUND!");
        }
        query.deactivate();
        query.close();
      } catch (Exception e) {
        e.printStackTrace();
      } catch (Throwable th) {
        writableDatabase.close();
      }
      try {
        writableDatabase.close();
      } catch (Throwable th2) {
      }
    }
    return str2;
  }

  public void clearAll() {
    synchronized (this) {
      try {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
          writableDatabase.delete(TABLE_LEVEL_PACKAGE, null, null);
          Log.d("DB", "TABLE_LEVEL_PACKAGE cleared!");
          writableDatabase.delete(TABLE_LEVEL, null, null);
          Log.d("DB", "TABLE_LEVEL cleared!");
        } catch (Exception e) {
          e.printStackTrace();
        } catch (Throwable th) {
          writableDatabase.close();
        }
        writableDatabase.close();
      } catch (Throwable th2) {
      }
    }
  }

  public void updateLevelWithLevelID(String str, LevelModel levelModel) {
    synchronized (this) {
      try {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
          String str2 = "UPDATE Level SET like_count = '" +
                        levelModel.getNumberOfLikes() + "', " + kUserLikes +
                        " = '" + levelModel.getUserLikes() + "' WHERE " +
                        kLevelId + " = '" + levelModel.getLevelId() + "'";
          Log.d(getClass().getSimpleName(), "Query: " + str2);
          writableDatabase.execSQL(str2);
        } catch (Exception e) {
          e.printStackTrace();
        } catch (Throwable th) {
          writableDatabase.close();
        }
        writableDatabase.close();
      } catch (Throwable th2) {
      }
    }
  }

  public void clearLevelPackages() {
    synchronized (this) {
      try {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
          writableDatabase.delete(TABLE_LEVEL_PACKAGE, null, null);
          Log.d("DB", "TABLE_LEVEL_PACKAGE cleared!");
        } catch (Exception e) {
          e.printStackTrace();
        } catch (Throwable th) {
          writableDatabase.close();
        }
        writableDatabase.close();
      } catch (Throwable th2) {
      }
    }
  }

  public void clearLevels() {
    synchronized (this) {
      try {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
          writableDatabase.delete(TABLE_LEVEL, null, null);
          Log.d("DB", "TABLE_LEVEL cleared!");
        } catch (Exception e) {
          e.printStackTrace();
        } catch (Throwable th) {
          writableDatabase.close();
        }
        writableDatabase.close();
      } catch (Throwable th2) {
      }
    }
  }

  public void resetUserLikes() {
    synchronized (this) {
      try {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
          String str =
              "UPDATE Level SET user_likes = 'false'  WHERE user_likes = 'true'";
          Log.d(getClass().getSimpleName(), "Query: " + str);
          writableDatabase.execSQL(str);
        } catch (Exception e) {
          e.printStackTrace();
        } catch (Throwable th) {
          writableDatabase.close();
        }
        writableDatabase.close();
      } catch (Throwable th2) {
      }
    }
  }

  public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    Log.w(getClass().getSimpleName(),
          "Upgrading database from version " + i + " to " + i2);
    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS LevelPackage");
    sQLiteDatabase.execSQL(QUERY_PACKAGE_LEVEL_TABLE_CREATE);
    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS Level");
    sQLiteDatabase.execSQL(QUERY_LEVEL_TABLE_CREATE);
  }

  public void onCreate(SQLiteDatabase sQLiteDatabase) {
    Log.d(
        getClass().getSimpleName(),
        "Query: CREATE TABLE LevelPackage (uniqueId INTEGER AUTO_INCREMENT, packageId VARCHAR(30), packageName VARCHAR(30) , PRIMARY KEY(packageName, packageId));");
    sQLiteDatabase.execSQL(QUERY_PACKAGE_LEVEL_TABLE_CREATE);
    Log.d(
        getClass().getSimpleName(),
        "Query: CREATE TABLE Level (uniqueId INTEGER AUTO_INCREMENT, packageId VARCHAR(30), packageName VARCHAR(30), levelId VARCHAR(30), levelName VARCHAR(30), like_count VARCHAR(30),  user_likes VARCHAR(30) , PRIMARY KEY(levelName, levelId));");
    sQLiteDatabase.execSQL(QUERY_LEVEL_TABLE_CREATE);
  }

  public static PackageLevelHandler getInstance(Context context) {
    if (databaseController == null) {
      databaseController = new PackageLevelHandler(context);
    }
    return databaseController;
  }

  private PackageLevelHandler(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public void close() { databaseController.close(); }

  public PackageLevelHandler open() throws SQLException {
    this.dbo = databaseController.getWritableDatabase();
    return this;
  }

  public LevelModel getLevelObjectWithLevelId(String str) { return null; }
}
