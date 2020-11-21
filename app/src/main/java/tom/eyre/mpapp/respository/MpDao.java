package tom.eyre.mpapp.respository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

import lombok.Synchronized;
import tom.eyre.mpapp.entity.BillsEntity;
import tom.eyre.mpapp.entity.DivisionEntity;
import tom.eyre.mpapp.entity.ExpenseEntity;
import tom.eyre.mpapp.entity.MpEntity;
import tom.eyre.mpapp.entity.QuestionEntity;
import tom.eyre.mpapp.entity.VoteEntity;

@Dao
public interface MpDao {

    //MpEntity----------------------------------------------------
    @Insert
    void insertAllMps(List<MpEntity> mps);

    @Query("SELECT * FROM MpEntity WHERE mpFor <> 'Life peer'")
    List<MpEntity> getAllMps();

    @Query("SELECT * FROM MpEntity WHERE id = :id")
    MpEntity findMpById(Integer id);

    @Query("UPDATE MpEntity SET bio = :bio WHERE id = :id")
    void updateBioById(Integer id, String bio);

    @Query("UPDATE MpEntity SET homePage = :homePage WHERE id = :id")
    void updateHomePage(Integer id, String homePage);

    @Query("UPDATE MpEntity SET twitter = :twitterUrl WHERE id = :id")
    void updateTwitterUrl(Integer id, String twitterUrl);

    @Query("UPDATE MpEntity SET lastUpdatedTs = :lastUpdatedTs WHERE id = :id")
    void updateLastUpdatedTs(Integer id, Long lastUpdatedTs);

    //DivisionEntity------------------------------------------------
    @Insert
    void insertAllDivisions(List<DivisionEntity> divisions);

    @Query("SELECT * FROM DivisionEntity")
    List<DivisionEntity> getAllDivisions();

    //VoteEntity-----------------------------------------------------
    @Insert
    void insertAllVotes(List<VoteEntity> votes);

    @Query("SELECT * FROM VoteEntity WHERE mpId = :id")
    List<VoteEntity> getAllVotesByMpId(Integer id);

    @Query("SELECT * FROM VoteEntity")
    List<VoteEntity> getAllVotes();

    @Insert
    void insertAllExpenses(List<ExpenseEntity> expenses);

    @Query("SELECT t1.uin FROM DivisionEntity t1 " +
            "LEFT JOIN VoteEntity t2 on t1.uin = t2.divisionUin " +
            "WHERE t2.divisionUin IS NULL " +
            "ORDER BY t1.uin DESC")
    public List<String> getAllNewDivisionUinsWithNoVoteResults();

    //ExpenseEntity---------------------------------------------------
    @Query("SELECT * FROM ExpenseEntity WHERE mpId = :id")
    List<ExpenseEntity> getExpensesByMpId(Integer id);

    @Query("SELECT lastUpdatedTs FROM ExpenseEntity WHERE mpId = :id")
    Long getExpenseDate(Integer id);

    //BillsEntity-----------------------------------------------------

    @Insert
    void insertAllBills(List<BillsEntity> entities);

    @Query("SELECT * FROM BillsEntity")
    List<BillsEntity> getAllBills();

    @Query("SELECT * FROM BillsEntity WHERE sponsorA = :fullName OR sponsorA = :name OR sponsorB = :fullName OR sponsorB = :name")
    List<BillsEntity> getAllBillsByName(String fullName, String name);

    //QuestionsEntity-----------------------------------------------------

    @Insert
    void insertAllQuestions(List<QuestionEntity> entities);

    @Query("SELECT * FROM QuestionEntity")
    List<QuestionEntity> getAllQuestions();

    @Query("SELECT lastUpdatedTs FROM QuestionEntity ORDER BY lastUpdatedTs desc LIMIT 1")
    Long getMostRecentQuestionTime();

    @Query("UPDATE QuestionEntity SET opinion = :opinion WHERE question = :question")
    void updateQuestionOpinion(String question, Boolean opinion);

    @Query("SELECT uin FROM QuestionEntity")
    List<String> getAllQuestionUins();

}
