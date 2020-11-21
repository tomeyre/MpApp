package tom.eyre.mpapp.util;

import android.content.Context;

import androidx.room.Room;

import lombok.Data;
import tom.eyre.mpapp.database.MpDatabase;

@Data
public class DatabaseUtil {

    private static DatabaseUtil instance;
    public static MpDatabase mpDB;

    private DatabaseUtil(){}

    public static DatabaseUtil getInstance(Context context){
        if(instance == null){
            instance = new DatabaseUtil();
            mpDB = Room.databaseBuilder(context,
                    MpDatabase.class, "mpDb").build();
        }
        return instance;
    }
}
