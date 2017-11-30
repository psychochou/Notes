package euphoria.psycho.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;
import org.javia.arity.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static euphoria.psycho.notes.Constants.EXTRA_ID;
import static euphoria.psycho.notes.Constants.EXTRA_TAG;

public class EditNoteActivity extends AppCompatActivity {

    private static final int MENU_CALCULATE = 0x3;
    private EditText mEditText;
    private boolean mFinished = false;
    private Note mNote;
    private Symbols mSymbols;
    private String mTag;
    private boolean mUpdated = false;

    private void calculateExpression() {
        if (mSymbols == null) {
            mSymbols = new Symbols();
        }

        String input = mEditText.getText().toString();

        Pattern pattern = Pattern.compile("[0-9\\+\\-\\*\\.\\(\\)\\=/]+");
        Matcher matcher = pattern.matcher(input.split("\\={5}")[0]);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(input).append("\n\n\n=====\n\n\n");
        List<Double> results = new ArrayList<>();
        while (matcher.find()) {
            stringBuilder.append(matcher.group()).append(" => ");
            try {
                String result = Util.doubleToString(mSymbols.eval(matcher.group()), -1);
                results.add(Double.parseDouble(result));
                stringBuilder.append(result).append("\n\n");
            } catch (SyntaxException e) {
                stringBuilder.append(e.message);
            }
        }
        double addAll = 0;

        for (double i : results) {
            addAll += i;
        }
        stringBuilder.append("相加总结果：").append(addAll).append("\n\n\n");
        mEditText.setText(stringBuilder.toString());
    }

    private void updateNote() {
        String content = mEditText.getText().toString();
        if (content == null || content.trim().length() == 0) return;
        if (mNote == null) {
            mNote = new Note();
            mNote.Tag = mTag;
            mNote.Title = content.split("\n")[0].trim();
            mNote.Content = content;
            Databases.getInstance().insert(mNote);
        } else {
            mNote.Title = content.split("\n")[0].trim();
            mNote.Content = content;
            Databases.getInstance().update(mNote);
        }
        mUpdated = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        if (!mFinished)
            updateNote();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_CALCULATE, 0, "计算(公式)");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CALCULATE:
                calculateExpression();
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        updateNote();
        if (mUpdated) {
            setResult(RESULT_OK);
        }
        mFinished = true;
        super.finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mEditText = (EditText) findViewById(R.id.edit_text);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();

        long id = intent.getLongExtra(EXTRA_ID, 0);
        mTag = intent.getStringExtra(EXTRA_TAG);
        if (id != 0) {
            mNote = Databases.getInstance().fetchNote(id);
            mNote.ID = id;

            mEditText.setText(mNote.Content);

        }
    }
}
