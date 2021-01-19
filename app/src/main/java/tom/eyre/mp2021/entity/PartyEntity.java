package tom.eyre.mp2021.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;

@Data
@Entity
public class PartyEntity implements Serializable {

    public PartyEntity(){}

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    private Long id;

    @ColumnInfo(name = "MpId")
    private Integer mpId;

    @ColumnInfo(name = "Party")
    private String party;

    @ColumnInfo(name = "StartDate")
    private String startDate;

    @ColumnInfo(name = "EndDate")
    private String endDate;
}
