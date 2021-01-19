package tom.eyre.mp2021.utility;

import android.content.Context;

import androidx.room.Room;

import lombok.Data;
import tom.eyre.mp2021.database.local.LocalDatabase;

@Data
public class DatabaseUtil {

    private static DatabaseUtil instance;
    public static LocalDatabase localDatabase;

    private DatabaseUtil(){}

    public static DatabaseUtil getInstance(Context context){
        if(instance == null){
            instance = new DatabaseUtil();
            localDatabase = Room.databaseBuilder(context,
                    LocalDatabase.class, "localDb").build();
        }
        return instance;
    }
}
