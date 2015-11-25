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

import java.util.UUID;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int SELECT_KASZTAN_PHOTO = 100;
    public static final int SELECT_PENCIL_PHOTO = 200;

    /**stan aktualny wybranego pliku*/
    private IMAGE_SELECTED CURRENT = IMAGE_SELECTED.NONE;
    /**enum reprezentujący stany*/
    private enum IMAGE_SELECTED {
        NONE, KASZTAN, PENCIL
    }

    /**
     * widok listy z obrazkami
     */
    private RecyclerView lista;
    /**
     * przycisk dodający nową parę
     */
    private TextView group;
    /**
     * dwie zmienne: ścieżki do wybranych obrazów
     */
    private String temporaryPencil = null;
    private String temporaryKasztan = null;
    /**
     * czy można już dodać nową parę? (flaga)
     */
    private boolean enableGroup = false;
    /**
     * pośrednik między danymi a widokiem listy:
     */
    private AdapterDoListy adapterDoListy;
    /**
     * instancja Realma: obiekt z biblioteki realm.io, dzięki której możliwy jest dostęp do bazy danych
     */
    private Realm realm;

    /**
     * po wyłączeniu apki zwalniamy zasoby: zamykamy nieużywane instancje
     */
    @Override
    protected void onDestroy() {
        if (realm != null)
            realm.close();
        super.onDestroy();
    }

    /**
     * stworzenie widoku Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getInstance(this);
        setupList();
        setupButtons();
    }

    private void setupButtons() {
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

    private void setupList() {
        lista = (RecyclerView) findViewById(R.id.lista);
        lista.setLayoutManager(new LinearLayoutManager(this));
        adapterDoListy = new AdapterDoListy(this);
        lista.setAdapter(adapterDoListy);
    }

    /**
     * powrót do aktywności po wybraniu obrazu
     * @param requestCode
     * @param resultCode
     * @param imageReturnedIntent
     */
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
                    pokazOkno();
                }
            });
        }
    }

    private void pokazOkno() {
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
                dialog.dismiss();
            }
        });
        okno.show();
    }

    /**
     * zapis nowego rekordu do bazy danych
     */
    private void saveToDB() {
        Log.d(TAG, "saveToDB ");
        realm.beginTransaction(); //rozpoczęcie transakcji
        Model newRecord = realm.createObject(Model.class);
        newRecord.setPrimaryKey(UUID.randomUUID().toString()); //primary key musi być unikalny
        newRecord.setBorrowerFilePath(temporaryKasztan);
        newRecord.setImageFilePath(temporaryPencil);
        realm.commitTransaction(); //zakończenie transakcji
    }

    private void updateList() {
        Log.d(TAG, "updateList ");
        adapterDoListy.update();
        Toast.makeText(this,"List updated",Toast.LENGTH_LONG).show();
    }
}
