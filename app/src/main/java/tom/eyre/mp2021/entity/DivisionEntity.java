package tom.eyre.mp2021.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity
public class DivisionEntity {
    public DivisionEntity() {
    }

    @PrimaryKey
    @ColumnInfo(name = "uin")
    @NonNull
    private String uin;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "lastUpdatedTs")
    @NonNull
    private Long lastUpdatedTimestamp;
}
