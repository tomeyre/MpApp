package tom.eyre.mpapp.fragment.container;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import lombok.SneakyThrows;
import tom.eyre.mpapp.R;
import tom.eyre.mpapp.entity.MpEntity;
import tom.eyre.mpapp.fragment.ui.mppartyquiz.quiz.QuizFragment;

public class MpPartyQuizFragmentContainerActivity extends AppCompatActivity {
    // FOR NAVIGATION VIEW ITEM ICON COLOR
    int[][] statesIcon = new int[][]{
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_checked}, // unchecked

    };

    Boolean goToQuiz = false;
    private BottomNavigationView navView;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goToQuiz = getIntent().getBooleanExtra("goToQuiz", false);
        setContentView(R.layout.fragment_container_mp_party_quiz_layout);
        navView = findViewById(R.id.mp_party_quiz_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mpSelectFragment, R.id.mpSelectFragment/*, R.id.partySelectFragment*/, R.id.quizFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.mp_party_quiz_nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        int[] colorsIcon = new int[]{
                Color.WHITE,
                getResources().getColor(R.color.parlimentGreen,null)
        };

        navView.setItemTextColor(new ColorStateList(statesIcon, colorsIcon));
        navView.setItemIconTintList(new ColorStateList(statesIcon, colorsIcon));
        navView.setItemBackground(makeSelector());
        navView.setBackgroundColor(getResources().getColor(R.color.parlimentGreen, null));

        navController = Navigation.findNavController(this, R.id.mp_party_quiz_nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.mp_party_quiz_navigation);

//        navGraph.findNode(R.id.mpDetailsFragment).addArgument("mp", new NavArgument.Builder().setDefaultValue(mp).build());
//        navGraph.findNode(R.id.expensesFragment).addArgument("mp", new NavArgument.Builder().setDefaultValue(mp).build());
//        navController.getGraph().findNode(R.id.mpDetailsFragment) .addArgument("mp", new NavArgument.Builder() .setDefaultValue(mp) .build());

        navController.setGraph(navGraph);
        if(goToQuiz){
            switchToQuiz();
        }
    }

    public void switchToQuiz() {
        ((BottomNavigationView)this.findViewById(R.id.mp_party_quiz_nav_view)).setSelectedItemId(R.id.quizFragment);
    }

    public int getNavViewHeight(){
        return navView.getMeasuredHeight();
    }

    private StateListDrawable makeSelector() {
        StateListDrawable res = new StateListDrawable();
        res.setExitFadeDuration(400);
        res.setAlpha(45);
        res.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(Color.WHITE));
        res.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(Color.WHITE));
        res.addState(new int[]{android.R.attr.state_enabled}, new ColorDrawable(getResources().getColor(R.color.parlimentGreen,null)));
        return res;
    }
}
