package euphoria.psycho.notes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import static euphoria.psycho.notes.Constants.EXTRA_ID;
import static euphoria.psycho.notes.Constants.EXTRA_SEARCH;

public class ViewActivity extends AppCompatActivity {

    private static final int MENU_EDIT = 0x3;
    private static final int REQUEST_EDIT_ACTIVITY = 0x2;
    private Note mNote;
    private ScrollView mScrollView;
    private TextView mTextView;

    private static void bringPointIntoView(TextView textView,
                                           ScrollView scrollView, int offset) {
        int line = textView.getLayout().getLineForOffset(offset);
        //+ 0.5
        int y = (int) ((line ) * textView.getLineHeight());
        scrollView.scrollTo(0, y);
    }

    private void editNote() {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EXTRA_ID, mNote.ID);
        startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);
    }

    private void highlightString(String input) {
        //Get the text from text view and create a spannable string
        SpannableString spannableString = new SpannableString(mTextView.getText());

        //Get the previous spans and remove them
        BackgroundColorSpan[] backgroundSpans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);

        for (BackgroundColorSpan span : backgroundSpans) {
            spannableString.removeSpan(span);
        }

        //Search for all occurrences of the keyword in the string
        int indexOfKeyword = spannableString.toString().indexOf(input);

        int firstPosition = 0;
        while (indexOfKeyword > 0) {
            if (firstPosition == 0) {
                firstPosition = indexOfKeyword;
            }
            //Create a background color span on the keyword
            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), indexOfKeyword, indexOfKeyword + input.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            //Get the next index of the keyword
            indexOfKeyword = spannableString.toString().indexOf(input, indexOfKeyword + input.length());
        }
        final int p = firstPosition;
        //Set the final text on TextView
        mTextView.setText(spannableString);
       // bringPointIntoView(mTextView, mScrollView, p);
        if (firstPosition > 0) {
            mScrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bringPointIntoView(mTextView, mScrollView, p);
                }
            },1000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_EDIT, 0, "编辑");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_EDIT:
                editNote();
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Intent intent = getIntent();
        long id = intent.getLongExtra(EXTRA_ID, 0);
        if (id == 0) finish();
        mNote = Databases.getInstance().fetchNote(id);
        mNote.ID = id;
        mTextView = (TextView) findViewById(R.id.text_view);
        mTextView.setText(mNote.Content);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String searchWord = intent.getStringExtra(EXTRA_SEARCH);
        if (searchWord==null) return;
        highlightString(searchWord);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_ACTIVITY && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
