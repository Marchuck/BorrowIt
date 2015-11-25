package pl.edu.agh.borrowit;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by lukasz on 25.11.15.
 */
public class Model extends RealmObject {
    @PrimaryKey
    private String primaryKey;
    private String borrowerFilePath;
    private String imageFilePath;

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
