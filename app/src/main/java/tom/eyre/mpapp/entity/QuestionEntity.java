package tom.eyre.mpapp.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Entity
@Data
public class QuestionEntity {

    public QuestionEntity(){}

    @PrimaryKey
    @ColumnInfo(name = "uin")
    @NonNull
    private String uin;

    @ColumnInfo(name = "date")
    private String dateOfDivision;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "question")
    private String question;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "opinion")
    private Boolean opinion;

    @ColumnInfo(name = "lastUpdatedTs")
    @androidx.annotation.NonNull
    private Long lastUpdatedTimestamp;
}
