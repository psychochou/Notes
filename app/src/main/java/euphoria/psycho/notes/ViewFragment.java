package euphoria.psycho.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static euphoria.psycho.notes.Constants.EXTRA_ID;
import static euphoria.psycho.notes.Constants.EXTRA_TAG;

public class ViewFragment extends Fragment implements UpdateFragement {

    private static final int MENU_ADD_NOTE = 10;
    private static final int MENU_DELETE_NOTE = 12;
    private static final int MENU_EDIT_CLASSIFY = 15;
    private static final int MENU_EDIT_NOTE = 11;
    private static final int MENU_MOVE_NOTE = 13;
    private static final int REQUEST_EDIT_ACTIVITY = 10;
    private static final int REQUEST_VIEW_ACTIVITY = 11;
    private ListView mListView;
    private NoteAdapter mNoteAdapter;
    private String mTag;

    private void addNote() {
        Intent intent = new Intent(this.getContext(), EditNoteActivity.class);
        intent.putExtra(EXTRA_TAG, mTag);
        startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);
    }

    private void deleteNote(Note note) {

        Databases.getInstance().deleteNote(note);
        mNoteAdapter.switchData(Databases.getInstance().fetchTitles(mTag));

    }

    private void moveNote(final Note item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getContext());

        View convertView = (View) LayoutInflater.from(this.getContext()).inflate(R.layout.activity_fragment, null);
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.list_view);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), R.layout.list_item, Databases.getInstance().fetchTabList());

        final AlertDialog dialog = alertDialog.create();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Databases.getInstance().moveNote(item, adapter.getItem(i));
                List<Fragment> fragments = ViewFragment.this.getFragmentManager().getFragments();
                for (Fragment fragment : fragments) {
                    ((UpdateFragement) fragment).updateFragement();
                }
                dialog.dismiss();
            }
        });
        lv.setAdapter(adapter);
        dialog.show();
    }

    private void updateClassfiy() {
        final EditText editText = new EditText(this.getContext());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getContext())
                .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tag = editText.getText().toString();
                        if (tag.trim().equals(mTag)) {
                            dialogInterface.dismiss();
                            return;
                        }
                        Databases.getInstance().updateTag(mTag, tag);
                        mTag=tag;
                        dialogInterface.dismiss();
                        //ViewFragment.this.getFragmentManager().getFragments().clear();
                        ((MainActivity) ViewFragment.this.getActivity()).updateTabLayout();
                        ((MainActivity) ViewFragment.this.getActivity()).goTo(tag);
                        //mNoteAdapter.switchData(Databases.getInstance().fetchTitles(mTag));

                        //ViewFragment.this.getFragmentManager().popBackStackImmediate();
                        List<Fragment> fragments = ViewFragment.this.getFragmentManager().getFragments();
                        for (Fragment fragment : fragments) {
                            ((UpdateFragement) fragment).updateFragement();
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                mNoteAdapter.switchData(Databases.getInstance().fetchTitles(mTag));
            }
        } else if (requestCode == REQUEST_VIEW_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                mNoteAdapter.switchData(Databases.getInstance().fetchTitles(mTag));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        mTag = bundle.getString("tag");
        View view = inflater.inflate(R.layout.activity_fragment, container, false);

        mListView = view.findViewById(R.id.list_view);

        registerForContextMenu(mListView);
        mNoteAdapter = new NoteAdapter(Databases.getInstance().fetchTitles(mTag), this.getContext());
        mListView.setAdapter(mNoteAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ViewFragment.this.getContext(), ViewActivity.class);
                intent.putExtra(EXTRA_ID, mNoteAdapter.getItem(i).ID);
                startActivityForResult(intent, REQUEST_VIEW_ACTIVITY);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_EDIT_CLASSIFY, 0, "修改分类");
        menu.add(0, MENU_ADD_NOTE, 0, "添加笔记");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ADD_NOTE:
                addNote();
                return true;
            case MENU_EDIT_CLASSIFY:
                updateClassfiy();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(0, MENU_EDIT_NOTE, 0, "编辑");
        menu.add(0, MENU_MOVE_NOTE, 0, "移动到");

        menu.add(0, MENU_DELETE_NOTE, 0, "删除");


        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            switch (item.getItemId()) {
                case MENU_EDIT_NOTE:
                    editNote(mNoteAdapter.getItem(menuInfo.position));
                    return true;
                case MENU_DELETE_NOTE:
                    deleteNote(mNoteAdapter.getItem(menuInfo.position));
                    return true;
                case MENU_MOVE_NOTE:
                    moveNote(mNoteAdapter.getItem(menuInfo.position));
                    return true;

            }
        }
        return super.onContextItemSelected(item);

    }

    private void editNote(Note item) {
        Intent intent = new Intent(ViewFragment.this.getContext(), EditNoteActivity.class);
        intent.putExtra(EXTRA_ID, item.ID);
        startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);
    }

    @Override
    public void updateFragement() {
        mNoteAdapter.switchData(Databases.getInstance().fetchTitles(mTag));

    }
}
