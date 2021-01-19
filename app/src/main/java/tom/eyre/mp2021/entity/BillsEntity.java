package tom.eyre.mp2021.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Entity
@Data
public class BillsEntity {

    public BillsEntity(){}

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @lombok.NonNull
    private Long id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "homePage")
    private String url;

    @ColumnInfo(name = "date")
    private String billDate;

    @ColumnInfo(name = "sponsorA")
    private String sponsorAId;

    @ColumnInfo(name = "sponsorB")
    private String sponsorBId;

    @ColumnInfo(name = "lastUpdatedTs")
    @NonNull
    private Long lastUpdatedTimestamp;
}
