package tom.eyre.mpapp.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;

@Data
@Entity
public class MpEntity implements Serializable {

    public MpEntity(){}

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private Integer id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "party")
    private String party;

    @ColumnInfo(name = "mpFor")
    private String mpFor;

    @ColumnInfo(name = "active")
    private Boolean active;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "dateOfBirth")
    private String dateOfBirth;

    @ColumnInfo(name = "dateOfDeath")
    private String dateOfDeath;

    @ColumnInfo(name = "houseStartDate")
    private String houseStartDate;

//    @Embedded
//    private VoteEntity votes;
}
