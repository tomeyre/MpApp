package tom.eyre.mpapp.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

import lombok.Data;
import lombok.NonNull;

@Data
@Entity
public class ExpenseEntity {

    public ExpenseEntity(){}

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    private Long id;

    @ColumnInfo(name = "mpId")
    private Integer mpId;

    @ColumnInfo(name = "year")
    private String year;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "claimNo")
    private String claimNo;

    @ColumnInfo(name = "mPsName")
    private String mpName;

    @ColumnInfo(name = "mPsConstituency")
    private String mpsConstituency;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "expenseType")
    private String expenseType;

    @ColumnInfo(name = "shortDescription")
    private String shortDescription;

    @ColumnInfo(name = "details")
    private String details;

    @ColumnInfo(name = "journeyType")
    private String journeyType;

    @ColumnInfo(name = "from")
    private String from;

    @ColumnInfo(name = "to")
    private String to;

    @ColumnInfo(name = "travel")
    private String travel;

    @ColumnInfo(name = "nights")
    private Integer nights;

    @ColumnInfo(name = "mileage")
    private Integer mileage;

    @ColumnInfo(name = "amountClaimed")
    private Double amountClaimed;

    @ColumnInfo(name = "amountPaid")
    private Double amountPaid;

    @ColumnInfo(name = "amountNotPaid")
    private Double amountNotPaid;

    @ColumnInfo(name = "amountRepaid")
    private Double amountRepaid;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "reasonIfNotPaid")
    private String reasonIfNotPaid;

    @ColumnInfo(name = "supplyMonth")
    private Integer supplyMonth;

    @ColumnInfo(name = "supplyPeriod")
    private Integer supplyPeriod;

    @ColumnInfo(name = "lastUpdatedTs")
    @androidx.annotation.NonNull
    private Long lastUpdatedTimestamp;
}
