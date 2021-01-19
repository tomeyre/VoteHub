package tom.eyre.mp2021.database.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tom.eyre.mp2021.entity.BillsEntity;
import tom.eyre.mp2021.entity.DivisionEntity;
import tom.eyre.mp2021.entity.ExpenseEntity;
import tom.eyre.mp2021.entity.MpEntity;
import tom.eyre.mp2021.entity.PartyEntity;
import tom.eyre.mp2021.entity.PostEntity;
import tom.eyre.mp2021.entity.QuestionEntity;
import tom.eyre.mp2021.entity.VoteEntity;
import tom.eyre.mp2021.respository.LocalDatabaseDao;

@Database(entities = {MpEntity.class,
        DivisionEntity.class,
        VoteEntity.class,
        ExpenseEntity.class,
        BillsEntity.class,
        QuestionEntity.class,
        PartyEntity.class,
        PostEntity.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract LocalDatabaseDao localDatabaseDao();
}