package tom.eyre.mp2021.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;

@Entity
@Data
public class QuestionEntity implements Serializable {

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

    @ColumnInfo(name = "id")
    private Integer id;
}
