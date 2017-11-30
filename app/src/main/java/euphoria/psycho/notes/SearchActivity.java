package euphoria.psycho.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import static euphoria.psycho.notes.Constants.EXTRA_ID;
import static euphoria.psycho.notes.Constants.EXTRA_SEARCH;

public class SearchActivity extends AppCompatActivity {

    ListView mListView;
    EditText mEditText;
    NoteAdapter mNoteAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mListView = (ListView) findViewById(R.id.list_view);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mNoteAdapter = new NoteAdapter(new ArrayList<Note>(), this);
        mListView.setAdapter(mNoteAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this, ViewActivity.class);
                intent.putExtra(EXTRA_ID, mNoteAdapter.getItem(i).ID);
                intent.putExtra(EXTRA_SEARCH,mEditText.getText().toString().trim());
                startActivity(intent);
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && charSequence.toString().trim().length() > 0) {
                    mNoteAdapter.switchData(Databases.getInstance().searchTitle(charSequence.toString().trim()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && mEditText.getText().toString().trim().length() > 0) {

                    mNoteAdapter.switchData(Databases.getInstance().searchTitles(mEditText.getText().toString().trim()));

                    return true;
                }
                return false;
            }
        });
    }
}
