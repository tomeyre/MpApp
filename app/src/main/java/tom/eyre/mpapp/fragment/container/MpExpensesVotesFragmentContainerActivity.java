package tom.eyre.mpapp.fragment.container;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import lombok.SneakyThrows;
import tom.eyre.mpapp.R;
import tom.eyre.mpapp.entity.MpEntity;

public class MpExpensesVotesFragmentContainerActivity extends AppCompatActivity {

    // FOR NAVIGATION VIEW ITEM ICON COLOR
    int[][] statesIcon = new int[][]{
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_checked}, // unchecked

    };

    private MpEntity mp;
    private NavController navController;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mp = (MpEntity) getIntent().getSerializableExtra("mp");
        setContentView(R.layout.fragment_container_mp_expenses_votes_layout);
        BottomNavigationView navView = findViewById(R.id.mp_expenses_votes_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mpDetailsFragment, R.id.expensesFragment/*, R.id.navigation_notifications*/)
                .build();
        navController = Navigation.findNavController(this, R.id.mp_expenses_votes_nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        int[] colorsIcon = new int[]{
                Color.WHITE,
                getApplication().getResources().getColor(R.color.parlimentGreen, null)
        };

        navView.setItemTextColor(new ColorStateList(statesIcon, colorsIcon));
        navView.setItemIconTintList(new ColorStateList(statesIcon, colorsIcon));
        navView.setItemBackground(makeSelector(getApplication().getResources().getColor(R.color.parlimentGreen, null)));
        navView.setBackgroundColor(getResources().getColor(R.color.parlimentGreen, null));

        navController = Navigation.findNavController(this, R.id.mp_expenses_votes_nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.mp_expenses_votes_navigation);

        navGraph.findNode(R.id.mpDetailsFragment).addArgument("mp", new NavArgument.Builder().setDefaultValue(mp).build());
        navGraph.findNode(R.id.expensesFragment).addArgument("mp", new NavArgument.Builder().setDefaultValue(mp).build());
//        navController.getGraph().findNode(R.id.mpDetailsFragment) .addArgument("mp", new NavArgument.Builder() .setDefaultValue(mp) .build());

        navController.setGraph(navGraph);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent mainIntent = new Intent(MpExpensesVotesFragmentContainerActivity.this, MpPartyQuizFragmentContainerActivity.class);
                        MpExpensesVotesFragmentContainerActivity.this.startActivity(mainIntent);
                        MpExpensesVotesFragmentContainerActivity.this.finish();
                        break;
                    case R.id.mpDetailsFragment:
                        navController.navigate(R.id.mpDetailsFragment);
                        break;
                    case R.id.expensesFragment:
                        navController.navigate(R.id.expensesFragment);
                        break;
                    /*case R.id.navigation_notifications:
                        navController.navigate(R.id.navigation_notifications);
                        break;*/
                }
                return true;
            }
        });
    }

    private StateListDrawable makeSelector(int color) {
        StateListDrawable res = new StateListDrawable();
        res.setExitFadeDuration(400);
        res.setAlpha(45);
        res.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(Color.WHITE));
        res.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(Color.WHITE));
        res.addState(new int[]{android.R.attr.state_enabled}, new ColorDrawable(color));
        return res;
    }

    private int getColor() {
        return getResources().getColor(getResources().getIdentifier(mp.getParty().toLowerCase().trim()
                .replace("&", "and")
                .replace(" +", " ")
                .replace(" ", "_")
                .replace("-", "_")
                .replace(",", "")
                .replace("'", "")
                .replace(":", "")
                .replace(")", "")
                .replace("-", "")
                .replace("(", ""), "color", this.getPackageName()));
    }
}
