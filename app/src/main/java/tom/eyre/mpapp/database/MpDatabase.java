package tom.eyre.mpapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tom.eyre.yourvotematters.entity.DivisionEntity;
import tom.eyre.yourvotematters.entity.ExpenseEntity;
import tom.eyre.yourvotematters.entity.MpEntity;
import tom.eyre.yourvotematters.entity.VoteEntity;
import tom.eyre.yourvotematters.respository.MpDao;

@Database(entities = {MpEntity.class, DivisionEntity.class, VoteEntity.class, ExpenseEntity.class}, version = 1, exportSchema = false)
public abstract class MpDatabase extends RoomDatabase {
    public abstract MpDao mpDao();
}