package euphoria.psycho.notes;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MENU_ADD_TAG = 101;
    private static final int MENU_SEARCH = 102;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private void addClassify() {
        final EditText editText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(editText)

                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (editText.getText().toString().trim().length() > 0) {
                            Databases.getInstance().insertTab(editText.getText().toString().trim());
//                            if (getSupportFragmentManager().getFragments() != null)
//                                getSupportFragmentManager().getFragments().clear();
                            mPagerAdapter.switchData(Databases.getInstance().fetchTabList());
                            //   getSupportFragmentManager().getFragments().clear();
                            goTo(editText.getText().toString().trim());


                        }
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    private void initialize() {
        setContentView(R.layout.activity_main);

        initializeDatabase();
        initializeViewPager();
    }

    private void initializeDatabase() {

        if (Databases.getInstance() != null) return;
        File databaseFile = SharedUtils.getExternalStorageDirectoryFile(Constants.DATABASE_FILENAME);
        Databases.newInstance(this, databaseFile.getAbsolutePath());

    }


    public void goTo(String title) {
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            if (mPagerAdapter.getPageTitle(i).equals(title)) {
                mViewPager.setCurrentItem(i);
            }
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        initializeDatabase();
    }

    public void updateTabLayout() {

        mPagerAdapter.switchData(Databases.getInstance().fetchTabList());
    }

    private void initializeViewPager() {

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        List<String> tabList = Databases.getInstance().fetchTabList();
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabList);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

    }

    private void searchNote() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ADD_TAG, 0, "添加分类");
        menu.add(0, MENU_SEARCH, 0, "搜索笔记");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ADD_TAG:
                addClassify();
                return true;
            case MENU_SEARCH:
                searchNote();
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initialize();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, Constants.REQUEST_PERIMISSION);
        } else {
            initialize();
        }
    }


}
