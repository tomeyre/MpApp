package tom.eyre.mpapp.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;
import lombok.NonNull;

@Data
@Entity
public class VoteEntity {

    public VoteEntity(){}

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    private Long id;

    @ColumnInfo(name = "divisionUin")
    private String uin;

    @ColumnInfo(name = "mpId")
    private Integer mpId;

    @ColumnInfo(name = "party")
    private String party;

    @ColumnInfo(name = "result")
    private String result;

//    @SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
//    @Embedded()
//    private DivisionEntity divisionDetails;
}
