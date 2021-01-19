package tom.eyre.mp2021.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@NoArgsConstructor
public class PostEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    private Long id;

    @ColumnInfo(name = "MpId")
    private Integer mpId;

    @ColumnInfo(name = "Position")
    private String Position;

    @ColumnInfo(name = "HansardName")
    private String hansardName;

    @ColumnInfo(name = "StartDate")
    private String startDate;

    @ColumnInfo(name = "EndDate")
    private String endDate;

    @ColumnInfo(name = "Note")
    private String note;

    @ColumnInfo(name = "EndNote")
    private String endNote;

    @ColumnInfo(name = "IsJoint")
    private Boolean isJoint;

    @ColumnInfo(name = "IsUnpaid")
    private Boolean isUnpaid;

    @ColumnInfo(name = "Email")
    private String email;

    @ColumnInfo(name = "Type")
    private String type;

    @ColumnInfo(name = "LayingMinisterName")
    private String layingMinisterName;
}
