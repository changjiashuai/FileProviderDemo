package io.github.changjiashuai.fileproviderdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileSelectActivity extends AppCompatActivity implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {
  private Map<String, File[]> mGroupMap;
  private String[] mGroups = {"内部文件", "外部文件"};
  private File[] mIntFiles;
  private File[] mExtFiles;
  private ExpandableListView mFileList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_file_select);
    init();
    setResult(Activity.RESULT_CANCELED, null);
  }

  private void init() {
    mFileList = (ExpandableListView) findViewById(R.id.file_list);
    loadFiles();
    ExpandableListAdapter mFileListAdapter = new BaseExpandableListAdapter() {

      @Override
      public int getGroupCount() {
        return mGroups.length;
      }

      @Override
      public int getChildrenCount(int groupPosition) {
        int intFileNum, extFileNum;
        if (null == mIntFiles) {
          intFileNum = 0;
        } else
          intFileNum = mIntFiles.length;
        if (null == mExtFiles) {
          extFileNum = 0;
        } else
          extFileNum = mExtFiles.length;
        return (groupPosition == 0) ? intFileNum : extFileNum;
      }

      @Override
      public Object getGroup(int groupPosition) {
        return mGroupMap.get(mGroups[groupPosition]);
      }

      @Override
      public Object getChild(int groupPosition, int childPosition) {
        return ((File[]) mGroupMap.get(mGroups[groupPosition]))[childPosition];
      }

      @Override
      public long getGroupId(int groupPosition) {
        return groupPosition;
      }

      @Override
      public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
      }

      @Override
      public boolean hasStableIds() {
        return true;
      }

      @Override
      public View getGroupView(int groupPosition, boolean isExpanded,
                               View convertView, ViewGroup parent) {
        if (convertView == null) {
          convertView = View.inflate(FileSelectActivity.this, R.layout.file_select_group, null);
        }
//				if (!isExpanded) {
//					mFileList.expandGroup(groupPosition);
//				}
        TextView tvGroup = (TextView) convertView.findViewById(R.id.tv_group_title);
        tvGroup.setText(mGroups[groupPosition]);
        return convertView;
      }

      @Override
      public View getChildView(int groupPosition, int childPosition,
                               boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
          convertView = View.inflate(FileSelectActivity.this, R.layout.file_select_item, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tv_file_item);
        tv.setText(mGroupMap.get(mGroups[groupPosition])[childPosition].getName());

        return convertView;
      }

      @Override
      public boolean isChildSelectable(int groupPosition,
                                       int childPosition) {
        // TODO Auto-generated method stub
        return true;
      }

    };
    mFileList.setAdapter(mFileListAdapter);
    expandAll();
    mFileList.setOnGroupClickListener(this);
    mFileList.setOnChildClickListener(this);
  }

  private void expandAll() {
    for (int i = 0; i < mGroups.length; i++) {
      mFileList.expandGroup(i);
    }
  }

  private void loadFiles() {
    String sharedFileDir = "files";
    File intFileDir = new File(getFilesDir(), sharedFileDir);
    //File extFileDir = new File(Environment.getExternalStorageDirectory(), sharedFileDir);
    File extFileDir = new File(getExternalFilesDir(null), sharedFileDir);

    mIntFiles = intFileDir.listFiles();
    mExtFiles = extFileDir.listFiles();

    mGroupMap = new HashMap<>();
    mGroupMap.put(mGroups[0], mIntFiles);
    mGroupMap.put(mGroups[1], mExtFiles);
  }

  @Override
  public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
    if (mFileList.isGroupExpanded(groupPosition)) {
      mFileList.collapseGroup(groupPosition);
    } else {
      mFileList.expandGroup(groupPosition);
    }
    mFileList.collapseGroup(groupPosition);
    return false;
  }

  @Override
  public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
    File file = mGroupMap.get(mGroups[groupPosition])[childPosition];
    Toast.makeText(this, "U choosed " + file.getName(), Toast.LENGTH_SHORT).show();
    Uri fileUri;
    String authority = getResources().getString(R.string.fileprovider_authority);
    try {
      fileUri = FileProvider.getUriForFile(this, authority, file);
      Intent resultIntent = new Intent();
      if (fileUri != null) {
        resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Put the Uri and MIME type in the result Intent
        resultIntent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
        // Set the result
        setResult(Activity.RESULT_OK, resultIntent);
      } else {
        resultIntent.setDataAndType(null, "");
        setResult(RESULT_CANCELED, resultIntent);
      }
      finish();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      finish();
    }
    return true;
  }
}
