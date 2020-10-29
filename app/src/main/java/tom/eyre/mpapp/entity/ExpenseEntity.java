package tom.eyre.mpapp.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;
import lombok.NonNull;

@Data
@Entity
public class ExpenseEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    private Long id;

    @ColumnInfo(name = "mpId")
    private Integer mpId;

    @ColumnInfo(name = "Year")
    private String year;

    @ColumnInfo(name = "Date")
    private String date;

    @ColumnInfo(name = "Claim No.")
    private String claimNo;

    @ColumnInfo(name = "MP's Name")
    private String mpName;

    @ColumnInfo(name = "MP's Constituency")
    private String mpsConstituency;

    @ColumnInfo(name = "Category")
    private String category;

    @ColumnInfo(name = "Expense Type")
    private String expenseType;

    @ColumnInfo(name = "Short Description")
    private String shortDescription;

    @ColumnInfo(name = "Details")
    private String details;

    @ColumnInfo(name = "Journey Type")
    private String journeyType;

    @ColumnInfo(name = "From")
    private String from;

    @ColumnInfo(name = "To")
    private String to;

    @ColumnInfo(name = "Travel")
    private String travel;

    @ColumnInfo(name = "Nights")
    private Integer nights;

    @ColumnInfo(name = "Mileage")
    private Integer mileage;

    @ColumnInfo(name = "Amount Claimed")
    private Double amountClaimed;

    @ColumnInfo(name = "Amount Paid")
    private Double amountPaid;

    @ColumnInfo(name = "Amount Not Paid")
    private Double amountNotPaid;

    @ColumnInfo(name = "Amount Repaid")
    private Double amountRepaid;

    @ColumnInfo(name = "Status")
    private String status;

    @ColumnInfo(name = "Reason If Not Paid")
    private String reasonIfNotPaid;

    @ColumnInfo(name = "Supply Month")
    private Integer supplyMonth;

    @ColumnInfo(name = "Supply Period")
    private Integer supplyPeriod;
}
