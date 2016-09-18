package io.github.changjiashuai.fileproviderdemo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private Button btnInt;
  private Button btnExt;
  private Button btnRead;
  private static final String sharedFileDir = "files";
  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    btnInt = (Button) findViewById(R.id.btn_int);
    btnExt = (Button) findViewById(R.id.btn_ext);
    btnRead = (Button) findViewById(R.id.btn_read);
    btnInt.setOnClickListener(this);
    btnExt.setOnClickListener(this);
    btnRead.setOnClickListener(this);
  }

  private void createIntFiles(){
    String intFilePrefix = "内部文件";
    File intFileDir = new File(getFilesDir(), sharedFileDir);
    Log.i(TAG, "createIntFiles: path=" + intFileDir.getAbsolutePath());
    if (!intFileDir.exists()){
      intFileDir.mkdir();
    }

    for (int i=0; i<10; i++){
      File file = new File(intFileDir, intFilePrefix+i+".txt");
      if (!file.exists()){
        try {
          file.createNewFile();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void createExtFiles(){
    String extFilePrefix = "外部文件";
    File extFileDir = new File(Environment.getExternalStorageDirectory(), sharedFileDir);
    //File extFileDir = new File(getExternalFilesDir(null), sharedFileDir);
    Log.i(TAG, "createExtFiles: External Storage Dir=" + Environment.getExternalStorageDirectory().getAbsolutePath());
    Log.i(TAG, "createExtFiles: extFileDir=" + extFileDir.getAbsolutePath());
    if (!extFileDir.exists()){
     boolean c = extFileDir.mkdirs();
      Log.i(TAG, "createExtFiles: " +c);
    }
    for (int i=0; i<10; i++){
      File file = new File(extFileDir, extFilePrefix+i+".txt");
      if (!file.exists()){
        try {
          file.createNewFile();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.btn_int:
        createIntFiles();
        break;
      case R.id.btn_ext:
        createExtFiles();
        break;
      case R.id.btn_read:
        requestFile();
        break;
    }
  }

  private void requestFile(){
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("text/plain");
    startActivityForResult(intent, 0);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK){
      Toast.makeText(MainActivity.this, "get file failed", Toast.LENGTH_SHORT).show();
    }else if (requestCode==0){
      Uri uri = data.getData();
      Log.i(TAG, "onActivityResult: uri=" + uri);
      readFile(uri);
    }
  }

  private void readFile(Uri uri){
    try {
      ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
      Cursor cursor = getContentResolver().query(uri, null, null, null, null);
      int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
      int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
      cursor.moveToFirst();
      cursor.close();
      String content = "";
      FileReader fileReader;
      char[] buffer = new char[1024];
      StringBuilder stringBuilder = new StringBuilder();
      fileReader = new FileReader(parcelFileDescriptor.getFileDescriptor());
      try {
        while (fileReader.read(buffer) != -1){
          stringBuilder.append(buffer);
        }
        fileReader.close();
        content = stringBuilder.toString();
        Log.i(TAG, "readFile: content=" + content);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
