package com.ocwvar.darkpurple.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ocwvar.darkpurple.Adapters.MusicFolderAdapter;
import com.ocwvar.darkpurple.AppConfigs;
import com.ocwvar.darkpurple.R;
import com.ocwvar.darkpurple.Units.BaseBlurActivity;
import com.ocwvar.darkpurple.Units.Logger;

import java.io.File;

/**
 * Created by 区成伟
 * Package: com.ocwvar.darkpurple.Activities
 * Data: 2016/7/26 22:34
 * Project: DarkPurple
 * 歌曲扫描目录设置
 */
public class FolderSelectorActivity extends BaseBlurActivity implements MusicFolderAdapter.OnPathChangedCallback {

    public static final int DATA_CHANGED = 1;
    public static final int DATA_UNCHANGED = 0;
    MusicFolderAdapter adapter;
    RecyclerView recyclerView;
    TextView openUI;
    ImageButton addPath;
    EditText editText;

    @Override
    protected boolean onPreSetup() {
        return true;
    }

    @Override
    protected int setActivityView() {
        return R.layout.activity_musicfloder;
    }

    @Override
    protected int onSetToolBar() {
        return R.id.toolbar;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onSetupViews() {
        openUI = (TextView) findViewById(R.id.openUI);
        addPath = (ImageButton) findViewById(R.id.imageButton_addPath);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new MusicFolderAdapter(this);

        editText = (EditText) findViewById(R.id.editText);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(FolderSelectorActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        openUI.setOnClickListener(this);
        addPath.setOnClickListener(this);

        openUI.setBackgroundColor(AppConfigs.Color.ToolBar_color);
        openUI.setTextColor(AppConfigs.Color.ToolBar_title_color);
        editText.setTextColor(AppConfigs.Color.ToolBar_title_color);
        editText.setHintTextColor(AppConfigs.Color.ToolBar_subtitle_color);
        findViewById(R.id.second_lay).setBackgroundColor(AppConfigs.Color.ToolBar_color);

        setResult(DATA_UNCHANGED);
    }

    @Override
    protected void onViewClick(View clickedView) {
        switch (clickedView.getId()) {
            case R.id.openUI:
                FolderSelectorUI.startBlurActivityForResult(10, Color.TRANSPARENT, false, FolderSelectorActivity.this, FolderSelectorUI.class, null, 777);
                break;
            case R.id.imageButton_addPath:
                String string = editText.getText().toString();
                //先检查路径是否有效
                if (isPathVaild(string)) {
                    adapter.addPath(string, false);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), R.string.info_wrongPath, Snackbar.LENGTH_LONG).show();
                }
                editText.getText().clear();
                break;
        }
    }

    @Override
    protected boolean onViewLongClick(View holdedView) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 777 && resultCode == FolderSelectorUI.RESULT_CODE) {
            //从浏览器返回时检查数据
            if (data != null) {
                try {
                    Object[] result = (Object[]) data.getExtras().getSerializable("Data");
                    if (result != null) {
                        for (Object folder : result) {
                            if (isPathVaild(((File) (folder)).getPath())) {
                                adapter.addPath(((File) (folder)).getPath(), true);
                            }
                        }
                    }
                } catch (Exception e) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.ERROR_SIMPLE, Snackbar.LENGTH_SHORT).show();
                    Logger.error("音频目录设置界面", e.getMessage());
                }
            }
        }
    }

    /**
     * 检测路径是否合法
     *
     * @param path 用户输入的路径文字
     * @return 合法性
     */
    private boolean isPathVaild(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        } else {
            File folder = new File(path);
            if (folder.exists() && folder.canRead()) {
                folder = null;
                return true;
            } else {
                folder = null;
                return false;
            }
        }
    }

    /**
     * 移除路径成功
     */
    @Override
    public void onRemovedPath() {
        AppConfigs.updatePathSet(adapter.getPaths());
        Snackbar.make(findViewById(android.R.id.content), R.string.info_removedPath, Snackbar.LENGTH_LONG).show();
        setResult(DATA_CHANGED);
    }

    /**
     * 添加路径成功
     */
    @Override
    public void onAddedPath() {
        AppConfigs.updatePathSet(adapter.getPaths());
        Snackbar.make(findViewById(android.R.id.content), R.string.info_addedPath, Snackbar.LENGTH_LONG).show();
        setResult(DATA_CHANGED);
    }

    /**
     * 添加数据失败
     */
    @Override
    public void onAddedFailed() {
        Snackbar.make(findViewById(android.R.id.content), R.string.info_existPath, Snackbar.LENGTH_LONG).show();
    }

}
