package tom.eyre.mp2021.entity;


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

    @ColumnInfo(name = "DisplayAs")
    private String fullName;

    @ColumnInfo(name = "forename")
    private String forename;

    @ColumnInfo(name = "surname")
    private String surname;

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

    @ColumnInfo(name = "houseEndDate")
    private String houseEndDate;

    @ColumnInfo(name = "bio")
    private String bio;

    @ColumnInfo(name = "twitter")
    private String twitterUrl;

    @ColumnInfo(name = "homePage")
    private String homePage;

    @ColumnInfo(name = "wikiLink")
    private String wikiLink;

    @ColumnInfo(name = "lastUpdatedTs")
    @androidx.annotation.NonNull
    private Long lastUpdatedTimestamp;

}
