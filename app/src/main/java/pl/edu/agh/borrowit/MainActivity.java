package pl.edu.agh.borrowit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    public static final int SELECT_KASZTAN_PHOTO = 100;
    public static final int SELECT_PENCIL_PHOTO = 200;
    public static final String TAG = MainActivity.class.getSimpleName();
    private enum IMAGE_SELECTED {
        NONE, KASZTAN, PENCIL
    }

    private RecyclerView lista;
    private TextView group;
    private IMAGE_SELECTED CURRENT = IMAGE_SELECTED.NONE;
    private String temporaryPencil = null;
    private String temporaryKasztan = null;
    private boolean enableGroup = false;
    private AdapterDoListy adapterDoListy;
    private Realm realm;

    @Override
    protected void onDestroy() {
        if (realm != null)
            realm.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getInstance(this);

        lista = (RecyclerView) findViewById(R.id.lista);
        lista.setLayoutManager(new LinearLayoutManager(this));
        adapterDoListy = new AdapterDoListy(this);
        lista.setAdapter(adapterDoListy);

        group = (TextView) findViewById(R.id.group);
        findViewById(R.id.item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PENCIL_PHOTO);
            }
        });
        findViewById(R.id.borrower).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_KASZTAN_PHOTO);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_KASZTAN_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    temporaryKasztan = cursor.getString(columnIndex);
                    cursor.close();
                    enableGroup = CURRENT == IMAGE_SELECTED.PENCIL;
                    CURRENT = IMAGE_SELECTED.KASZTAN;
                }
                break;
            case SELECT_PENCIL_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    temporaryPencil = cursor.getString(columnIndex);
                    cursor.close();

                    ///   final Bitmap pencilImage = BitmapFactory.decodeFile(filePath);
                    enableGroup = CURRENT == IMAGE_SELECTED.KASZTAN;
                    CURRENT = IMAGE_SELECTED.PENCIL;
                }
                break;
        }
        checkGroupEnabled();
    }

    private void checkGroupEnabled() {
        if (!enableGroup) {
            group.setTextColor(Color.DKGRAY);
            Toast.makeText(this, "Select second photo", Toast.LENGTH_SHORT).show();
        } else {
            group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder okno = new AlertDialog.Builder(MainActivity.this);
                    okno.setMessage("Do you want to add this pair?");
                    okno.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    okno.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CURRENT = IMAGE_SELECTED.NONE;
                            saveToDB();
                            updateList();
                        }
                    });
                    okno.show();
                }
            });
        }
    }

    private void saveToDB() {
        Log.d(TAG, "saveToDB ");
        realm.beginTransaction();
        Model newRecord = realm.createObject(Model.class);
        newRecord.setPrimaryKey(UUID.randomUUID().toString());
        newRecord.setBorrowerFilePath(temporaryKasztan);
        newRecord.setImageFilePath(temporaryPencil);
        realm.commitTransaction();
    }

    private void updateList() {
        List<Model> list =new ArrayList<>();
        realm.beginTransaction();
        RealmResults<Model> results = realm.where(Model.class).findAll();
        list.addAll(results);
        realm.commitTransaction();
        adapterDoListy.updateWith(list);
        Toast.makeText(this,"List updated",Toast.LENGTH_LONG).show();
    }
}
