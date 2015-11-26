package pl.edu.agh.borrowit;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by lukasz on 25.11.15.
 * Tabela trzymająca ścieżki do obrazów
 * Każdy rekord bazy musi mieć unikalny primaryKey, zawsze jest generowany losowo,
 * bo stworzyć 2 identyczne UUIDy jest o wiele mniej prawdopodobne niż trafienie 6 w totka.
 *
 * więcej na realm.io
 *
 */
public class Model extends RealmObject {
    @PrimaryKey
    private String primaryKey;
    private String borrowerFilePath;
    private String imageFilePath;
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Model() {
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getBorrowerFilePath() {
        return borrowerFilePath;
    }

    public void setBorrowerFilePath(String borrowerFilePath) {
        this.borrowerFilePath = borrowerFilePath;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }
}
