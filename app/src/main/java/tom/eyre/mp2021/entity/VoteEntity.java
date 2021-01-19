package tom.eyre.mp2021.entity;


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

    @ColumnInfo(name = "lastUpdatedTs")
    @androidx.annotation.NonNull
    private Long lastUpdatedTimestamp;

}
