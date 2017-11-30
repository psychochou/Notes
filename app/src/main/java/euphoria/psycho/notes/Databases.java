package euphoria.psycho.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Databases extends SQLiteOpenHelper {
    public static Databases sDatabases;
    private static final String TABLE_NAME = "note";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";
    private static final String TAGS = "tag";

    private static final String TAG = "Databases";

    public static Databases getInstance() {
        return sDatabases;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS note (\n" +
                "    _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    title VARCHAR(50) NOT NULL,\n" +
                "    content TEXT NOT NULL,\n" +
                "    tag TEXT NOT NULL,\n" +
                "    created_at BIGINT NOT NULL,\n" +
                "    updated_at BIGINT NOT NULL,\n" +
                "    UNIQUE (created_at)\n" +
                ");");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tags (\n" +
                "    _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    tag TEXT NOT NULL,\n" +
                "    UNIQUE (tag)\n" +

                ");");
        sqLiteDatabase.setLocale(Locale.CHINA);
    }

    public List<Note> searchTitle(String word) {

        List<Note> notes = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery("select _id,title from note where title like \"%" + word + "%\" order by Title collate localized", null);

        while (cursor.moveToNext()) {
            Note note = new Note();
            note.ID = cursor.getLong(0);
            note.Title = cursor.getString(1);

            notes.add(note);
        }
        cursor.close();
        return notes;

    }

    public void updateTag(String tag, String newTag) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TAGS, newTag);
        database.update("tags", values, "tag=?", new String[]{tag});

        database.update("note", values, "tag=?", new String[]{tag});
    }

    public void moveNote(Note note, String tag) {
        SQLiteDatabase database = getWritableDatabase();

        database.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(TAGS, tag);

            database.update(TABLE_NAME, values, "_id=?", new String[]{Long.toString(note.ID)});
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            database.endTransaction();

            database.close();
        }

    }

    public List<String> fetchTabList() {

        List<String> tabList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("select tag from tags order by tag collate localized", null);

        while (cursor.moveToNext()) {

            tabList.add(cursor.getString(0));
        }
        cursor.close();

        return tabList;
    }

    public void insertTab(String tab) {
        SQLiteDatabase database = getWritableDatabase();

        database.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put("tag", tab);
            database.insert("tags", null, values);
            database.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            database.endTransaction();

            database.close();
        }
    }

    public List<Note> searchTitles(String word) {

        List<Note> notes = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery("select _id,title from note where content like \"%" + word + "%\" order by Title collate localized", null);

        while (cursor.moveToNext()) {
            Note note = new Note();
            note.ID = cursor.getLong(0);
            note.Title = cursor.getString(1);

            notes.add(note);
        }
        cursor.close();
        return notes;

    }

    public List<Note> fetchTitles(String tag) {
        List<Note> notes = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery("select _id,title from note where tag=? order by title collate localized", new String[]{tag
        });

        while (cursor.moveToNext()) {
            Note note = new Note();
            note.ID = cursor.getLong(0);
            note.Title = cursor.getString(1);

            notes.add(note);
        }
        cursor.close();
        return notes;

    }


    public Note fetchNote(long id) {
        Note note = new Note();
        Cursor cursor = getReadableDatabase().rawQuery("select title,content from note where _id=?", new String[]{Long.toString(id)});

        if (cursor.moveToNext()) {

            note.ID = id;
            note.Title = cursor.getString(0);
            note.Content = cursor.getString(1);
        }
        cursor.close();

        return note;


    }

    public void insert(Note note) {
        SQLiteDatabase database = getWritableDatabase();

        database.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(CONTENT, note.Content);
            values.put(TITLE, note.Title);
            values.put(CREATED_AT, new Date().getTime());
            values.put(UPDATED_AT, new Date().getTime());
            values.put(TAGS, note.Tag);
            long rowId = database.insert(TABLE_NAME, null, values);
            note.ID = rowId;
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            database.endTransaction();

            database.close();
        }

    }

    public void deleteNote(Note note) {
        getWritableDatabase().delete(TABLE_NAME, "_id=?", new String[]{Long.toString(note.ID)});

    }

    public void update(Note note) {
        SQLiteDatabase database = getWritableDatabase();

        database.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(CONTENT, note.Content);
            values.put(TITLE, note.Title);
            values.put(CREATED_AT, new Date().getTime());
            values.put(CREATED_AT, new Date().getTime());
            database.update(TABLE_NAME, values, "_id=?", new String[]{Long.toString(note.ID)});
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            database.endTransaction();

            database.close();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS Notes USING fts4(Title, Content, UpdateTime, CreateTime, tokenize=icu zh_CN)");


    }

    public Databases(Context context, String name) {
        super(context, name, null, 1);
    }

    public static Databases newInstance(Context context, String fileName) {
        if (sDatabases == null) {
            sDatabases = new Databases(context, fileName);
        }
        return sDatabases;
    }
}
