package tom.eyre.mpapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tom.eyre.mpapp.entity.BillsEntity;
import tom.eyre.mpapp.entity.DivisionEntity;
import tom.eyre.mpapp.entity.ExpenseEntity;
import tom.eyre.mpapp.entity.MpEntity;
import tom.eyre.mpapp.entity.QuestionEntity;
import tom.eyre.mpapp.entity.VoteEntity;
import tom.eyre.mpapp.respository.MpDao;

@Database(entities = {MpEntity.class, DivisionEntity.class, VoteEntity.class, ExpenseEntity.class, BillsEntity.class, QuestionEntity.class}, version = 1, exportSchema = false)
public abstract class MpDatabase extends RoomDatabase {
    public abstract MpDao mpDao();
}