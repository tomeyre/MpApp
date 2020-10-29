package tom.eyre.mpapp.respository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tom.eyre.yourvotematters.entity.DivisionEntity;
import tom.eyre.yourvotematters.entity.ExpenseEntity;
import tom.eyre.yourvotematters.entity.MpEntity;
import tom.eyre.yourvotematters.entity.VoteEntity;

@Dao
public interface MpDao {

    @Insert
    void insertAllMps(List<MpEntity> mps);

    @Query("SELECT * FROM MpEntity")
    List<MpEntity> getAllMps();

    @Query("SELECT * FROM MpEntity WHERE id = :id")
    MpEntity findMpById(Integer id);

    @Insert
    void insertAllDivisions(List<DivisionEntity> divisions);

    @Query("SELECT * FROM DivisionEntity")
    List<DivisionEntity> getAllDivisions();

    @Insert
    void insertAllVotes(List<VoteEntity> votes);

    @Query("SELECT * FROM VoteEntity")
    List<VoteEntity> getAllVotes();

    @Insert
    void insertAllExpenses(List<ExpenseEntity> expenses);

    @Query("SELECT * FROM ExpenseEntity WHERE mpId = :id")
    List<ExpenseEntity> getExpensesByMpId(Integer id);
}
